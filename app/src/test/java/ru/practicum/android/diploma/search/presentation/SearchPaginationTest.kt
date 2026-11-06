package ru.practicum.android.diploma.search.presentation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.ViewModelStore
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ru.practicum.android.diploma.common.domain.Resource
import ru.practicum.android.diploma.common.domain.models.VacancyCard
import ru.practicum.android.diploma.search.domain.model.SearchResult
import ru.practicum.android.diploma.search.domain.usecase.SearchInteractor
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class SearchPaginationTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val dispatcher = StandardTestDispatcher()
    private val store = ViewModelStore()
    private val vacancy = VacancyCard("1", "Android developer", null, null, null, null)
    private lateinit var interactor: RecordingInteractor
    private lateinit var viewModel: SearchViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        interactor = RecordingInteractor().apply {
            answer = { query, page -> pageResult(query, page) }
        }
        viewModel = SearchViewModel(interactor)
        store.put("search", viewModel)
    }

    @After
    fun tearDown() {
        store.clear()
        Dispatchers.resetMain()
    }

    @Test
    fun appendsUniqueVacanciesAndKeepsPreviousSnapshot() = runTest(dispatcher) {
        val second = vacancy.copy(id = "2")
        val third = vacancy.copy(id = "3")
        interactor.answer = { _, page ->
            val cards = if (page == 0) listOf(vacancy, vacancy, second) else listOf(second, third, third)
            Resource.Success(SearchResult(cards, 3, page, 2))
        }
        viewModel.onQueryChanged("android")
        advanceUntilIdle()
        val previous = content()

        viewModel.onBottomReached()
        advanceUntilIdle()

        assertEquals(listOf("1", "2", "3"), content().vacancies.map { it.id })
        assertEquals(3, content().found)
        assertEquals(listOf("1", "2"), previous.vacancies.map { it.id })
        assertEquals(listOf("android" to 0, "android" to 1), interactor.requests)
    }

    @Test
    fun repeatedBottomEventsDoNotStartParallelRequests() = runTest(dispatcher) {
        val nextPage = CompletableDeferred<Resource<SearchResult>>()
        interactor.answer = { query, page -> if (page == 0) pageResult(query, page) else nextPage.await() }
        viewModel.onQueryChanged("android")
        advanceUntilIdle()
        viewModel.onBottomReached()
        viewModel.onBottomReached()
        runCurrent()
        viewModel.onBottomReached()

        assertEquals(listOf("android" to 0, "android" to 1), interactor.requests)
        assertTrue(content().isLoadingMore)
        viewModel.onPaginationErrorShown()
        assertTrue(content().isLoadingMore)
        assertEquals(listOf("android-0"), content().vacancies.map { it.id })
        assertEquals(42, content().found)

        nextPage.complete(pageResult("android", 1))
        runCurrent()

        assertFalse(content().isLoadingMore)
        assertEquals(listOf("android-0", "android-1"), content().vacancies.map { it.id })
    }

    @Test
    fun stopsAtLastPageUsingUpdatedPageCount() = runTest(dispatcher) {
        interactor.answer = { query, page -> pageResult(query, page, if (page == 0) 3 else 2) }
        viewModel.onQueryChanged("android")
        advanceUntilIdle()
        viewModel.onBottomReached()
        advanceUntilIdle()
        repeat(3) { viewModel.onBottomReached() }
        advanceUntilIdle()

        assertEquals(listOf("android" to 0, "android" to 1), interactor.requests)
        assertFalse(content().isLoadingMore)
    }

    @Test
    fun loadsEveryPageOnlyOnceForDifferentPageCounts() = runTest(dispatcher) {
        for (pageCount in 1..3) {
            val query = "android$pageCount"
            val previousRequests = interactor.requests.size
            interactor.answer = { text, page -> pageResult(text, page, pageCount) }
            viewModel.onQueryChanged(query)
            advanceUntilIdle()
            repeat(pageCount + 1) {
                viewModel.onBottomReached()
                advanceUntilIdle()
            }

            assertEquals((0 until pageCount).map { query to it }, interactor.requests.drop(previousRequests))
            assertEquals((0 until pageCount).map { "$query-$it" }, content().vacancies.map { it.id })
            assertFalse(content().isLoadingMore)
        }
    }

    @Test
    fun ignoresBottomEventsUntilFirstPageFinishes() = runTest(dispatcher) {
        val firstPage = CompletableDeferred<Resource<SearchResult>>()
        interactor.answer = { _, _ -> firstPage.await() }
        viewModel.onBottomReached()
        viewModel.onQueryChanged("android")
        viewModel.onBottomReached()
        runCurrent()
        advanceTimeBy(1999)
        viewModel.onBottomReached()
        runCurrent()

        assertTrue(interactor.requests.isEmpty())

        advanceTimeBy(1)
        runCurrent()
        viewModel.onBottomReached()
        runCurrent()
        firstPage.complete(pageResult("android", 0))
        runCurrent()

        assertEquals(listOf("android" to 0), interactor.requests)
    }

    @Test
    fun newQueryStartsAtZeroAndDiscardsLatePage() = runTest(dispatcher) {
        val oldPage = CompletableDeferred<Resource<SearchResult>>()
        interactor.answer = { query, page ->
            if (query == "old" && page == 1) {
                withContext(NonCancellable) { oldPage.await() }
            } else {
                pageResult(query, page)
            }
        }
        viewModel.onQueryChanged("old")
        advanceUntilIdle()
        viewModel.onBottomReached()
        runCurrent()
        viewModel.onQueryChanged("new")
        viewModel.onBottomReached()
        oldPage.complete(pageResult("old", 1))
        runCurrent()
        advanceUntilIdle()
        viewModel.onBottomReached()
        advanceUntilIdle()

        assertEquals(listOf("old" to 0, "old" to 1, "new" to 0, "new" to 1), interactor.requests)
        assertEquals(listOf("new-0", "new-1"), content().vacancies.map { it.id })
    }

    @Test
    fun clearingQueryDiscardsLatePageError() = runTest(dispatcher) {
        val nextPage = CompletableDeferred<Resource<SearchResult>>()
        interactor.answer = { query, page ->
            if (page == 0) pageResult(query, page) else withContext(NonCancellable) { nextPage.await() }
        }
        viewModel.onQueryChanged("android")
        advanceUntilIdle()
        viewModel.onBottomReached()
        runCurrent()
        viewModel.onQueryChanged("")
        viewModel.onBottomReached()
        nextPage.complete(Resource.Error(500))
        runCurrent()

        assertEquals(SearchState.Default, viewModel.state.value)
        assertEquals(listOf("android" to 0, "android" to 1), interactor.requests)
    }

    @Test
    fun failedPagePreservesContentAndRetriesSamePage() = runTest(dispatcher) {
        for (code in listOf(500, -1)) {
            val query = "query$code"
            interactor.answer = { text, page ->
                if (page == 0) {
                    pageResult(text, page, 2)
                } else if (code == -1) {
                    throw IOException("Disconnected")
                } else {
                    Resource.Error(code)
                }
            }
            viewModel.onQueryChanged(query)
            advanceUntilIdle()
            viewModel.onBottomReached()
            advanceUntilIdle()

            assertEquals(listOf("$query-0"), content().vacancies.map { it.id })
            assertEquals(42, content().found)
            assertFalse(content().isLoadingMore)
            assertEquals(code, content().paginationErrorCode)

            viewModel.onPaginationErrorShown()
            assertEquals(null, content().paginationErrorCode)
            interactor.answer = { text, page -> pageResult(text, page, 2) }
            viewModel.onBottomReached()
            advanceUntilIdle()

            assertEquals(listOf("$query-0", "$query-1"), content().vacancies.map { it.id })
            assertEquals(listOf(query to 0, query to 1, query to 1), interactor.requests.takeLast(3))
        }
    }

    @Test
    fun emptyResultHasNoNextPage() = runTest(dispatcher) {
        interactor.answer = { _, _ -> Resource.Success(SearchResult(emptyList(), 0, 0, 0)) }
        viewModel.onQueryChanged("missing")
        advanceUntilIdle()
        viewModel.onBottomReached()
        advanceUntilIdle()

        assertEquals(SearchState.Content(emptyList(), 0), viewModel.state.value)
        assertEquals(listOf("missing" to 0), interactor.requests)
    }

    private fun content(): SearchState.Content = viewModel.state.value as SearchState.Content

    private fun pageResult(query: String, page: Int, pages: Int = 3): Resource<SearchResult> = Resource.Success(
        SearchResult(listOf(vacancy.copy(id = "$query-$page")), found = 42, page = page, pages = pages)
    )

    private class RecordingInteractor : SearchInteractor {
        val requests = mutableListOf<Pair<String, Int>>()
        var answer: suspend (String, Int) -> Resource<SearchResult> = { _, page ->
            Resource.Success(SearchResult(emptyList(), 0, page, 1))
        }

        override suspend fun searchVacancies(query: String, page: Int): Resource<SearchResult> {
            requests.add(query to page)
            return answer(query, page)
        }
    }
}
