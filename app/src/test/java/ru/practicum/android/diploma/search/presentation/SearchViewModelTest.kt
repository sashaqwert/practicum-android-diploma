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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import ru.practicum.android.diploma.common.domain.Resource
import ru.practicum.android.diploma.common.domain.models.VacancyCard
import ru.practicum.android.diploma.common.domain.models.VacancyCardSalary
import ru.practicum.android.diploma.search.domain.model.SearchResult
import ru.practicum.android.diploma.search.domain.usecase.SearchInteractor
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val dispatcher = StandardTestDispatcher()
    private val viewModelStore = ViewModelStore()
    private lateinit var interactor: RecordingSearchInteractor
    private lateinit var viewModel: SearchViewModel
    private val vacancy = VacancyCard(
        id = "123",
        name = "Android developer",
        company = "Company",
        city = "Moscow",
        logo = null,
        salary = VacancyCardSalary(from = 100000, to = null, currency = "RUR")
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        interactor = RecordingSearchInteractor()
        viewModel = SearchViewModel(interactor)
        viewModelStore.put("search", viewModel)
    }

    @After
    fun tearDown() {
        viewModelStore.clear()
        Dispatchers.resetMain()
    }

    @Test
    fun startsWithDefaultStateWithoutSearch() = runTest(dispatcher) {
        advanceUntilIdle()

        assertEquals(SearchState.Default, viewModel.state.value)
        assertTrue(interactor.queries.isEmpty())
    }

    @Test
    fun newInputImmediatelyLeavesDefaultStateBeforeDebounce() = runTest(dispatcher) {
        viewModel.onQueryChanged("android")

        assertEquals(SearchState.Typing, viewModel.state.value)
        assertTrue(interactor.queries.isEmpty())
        viewModel.onQueryChanged("")
    }

    @Test
    fun searchesLatestQueryAfterTwoSecondsWithoutInput() = runTest(dispatcher) {
        viewModel.onQueryChanged("kot")
        runCurrent()
        advanceTimeBy(1500)
        viewModel.onQueryChanged("kotlin")
        runCurrent()
        advanceTimeBy(1999)
        runCurrent()

        assertTrue(interactor.queries.isEmpty())

        advanceTimeBy(1)
        runCurrent()

        assertEquals(listOf("kotlin"), interactor.queries)
    }

    @Test
    fun identicalTextDoesNotRestartDebounceOrRepeatCompletedSearch() = runTest(dispatcher) {
        viewModel.onQueryChanged("kotlin")
        runCurrent()
        advanceTimeBy(1000)
        viewModel.onQueryChanged("kotlin")
        advanceTimeBy(1000)
        runCurrent()

        assertEquals(listOf("kotlin"), interactor.queries)

        viewModel.onQueryChanged("kotlin")
        advanceUntilIdle()

        assertEquals(listOf("kotlin"), interactor.queries)
    }

    @Test
    fun changedWhitespaceRestartsDebounceAndRequestUsesTrimmedQuery() = runTest(dispatcher) {
        viewModel.onQueryChanged("kotlin")
        runCurrent()
        advanceTimeBy(1500)
        viewModel.onQueryChanged(" kotlin ")
        runCurrent()
        advanceTimeBy(1999)
        runCurrent()

        assertTrue(interactor.queries.isEmpty())

        advanceTimeBy(1)
        runCurrent()

        assertEquals(listOf("kotlin"), interactor.queries)
    }

    @Test
    fun clearingTextCancelsPendingSearch() = runTest(dispatcher) {
        viewModel.onQueryChanged("kotlin")
        runCurrent()
        advanceTimeBy(1000)
        viewModel.onQueryChanged("")
        advanceUntilIdle()

        assertTrue(interactor.queries.isEmpty())
        assertEquals(SearchState.Default, viewModel.state.value)
    }

    @Test
    fun blankTextClearsCompletedResultsWithoutAnotherSearch() = runTest(dispatcher) {
        interactor.response = { Resource.Success(SearchResult(listOf(vacancy), 42)) }
        viewModel.onQueryChanged("kotlin")
        advanceUntilIdle()
        assertEquals(SearchState.Content(listOf(vacancy), 42), viewModel.state.value)

        viewModel.onQueryChanged("   ")
        advanceUntilIdle()

        assertEquals(SearchState.Default, viewModel.state.value)
        assertEquals(listOf("kotlin"), interactor.queries)
    }

    @Test
    fun showsLoadingThenVacanciesWithTotalFoundCount() = runTest(dispatcher) {
        val response = CompletableDeferred<SearchResult>()
        interactor.response = { Resource.Success(response.await()) }
        viewModel.onQueryChanged("kotlin")
        runCurrent()
        advanceTimeBy(2000)
        runCurrent()

        assertEquals(SearchState.Loading, viewModel.state.value)

        response.complete(SearchResult(listOf(vacancy), 42))
        runCurrent()

        assertEquals(SearchState.Content(listOf(vacancy), 42), viewModel.state.value)
    }

    @Test
    fun emptyResponseFinishesLoading() = runTest(dispatcher) {
        viewModel.onQueryChanged("kotlin")
        advanceUntilIdle()

        assertEquals(SearchState.Content(emptyList(), 0), viewModel.state.value)
    }

    @Test
    fun oldResponseCannotReplaceStateAfterNewInput() = runTest(dispatcher) {
        val oldResponse = CompletableDeferred<SearchResult>()
        interactor.response = { query ->
            if (query == "old") {
                Resource.Success(withContext(NonCancellable) { oldResponse.await() })
            } else {
                Resource.Success(SearchResult(listOf(vacancy), 42))
            }
        }
        viewModel.onQueryChanged("old")
        runCurrent()
        advanceTimeBy(2000)
        runCurrent()
        viewModel.onQueryChanged("new")
        runCurrent()
        oldResponse.complete(SearchResult(emptyList(), 0))
        runCurrent()

        assertEquals(SearchState.Typing, viewModel.state.value)

        advanceUntilIdle()

        assertEquals(SearchState.Content(listOf(vacancy), 42), viewModel.state.value)
        assertEquals(listOf("old", "new"), interactor.queries)
    }

    @Test
    fun clearedSearchCannotRestoreResultsAfterLateResponse() = runTest(dispatcher) {
        val response = CompletableDeferred<SearchResult>()
        interactor.response = { Resource.Success(withContext(NonCancellable) { response.await() }) }
        viewModel.onQueryChanged("kotlin")
        runCurrent()
        advanceTimeBy(2000)
        runCurrent()
        viewModel.onQueryChanged("")
        response.complete(SearchResult(listOf(vacancy), 42))
        runCurrent()

        assertEquals(SearchState.Default, viewModel.state.value)
    }

    @Test
    fun failedRequestFinishesLoadingAndNextQueryCanSucceed() = runTest(dispatcher) {
        interactor.response = { throw IOException("Unavailable") }
        viewModel.onQueryChanged("old")
        advanceUntilIdle()

        assertEquals(SearchState.Error(SearchState.ErrorType.SERVER), viewModel.state.value)

        interactor.response = { Resource.Success(SearchResult(listOf(vacancy), 42)) }
        viewModel.onQueryChanged("new")
        advanceUntilIdle()

        assertEquals(SearchState.Content(listOf(vacancy), 42), viewModel.state.value)
    }

    @Test
    fun clearingViewModelCancelsPendingSearch() = runTest(dispatcher) {
        viewModel.onQueryChanged("kotlin")
        runCurrent()
        viewModelStore.clear()
        advanceUntilIdle()

        assertTrue(interactor.queries.isEmpty())
    }

    @Test
    fun noInternetResponseHasItsOwnErrorType() = runTest(dispatcher) {
        interactor.response = { Resource.Error(-1) }
        viewModel.onQueryChanged("android")
        advanceUntilIdle()

        assertEquals(SearchState.Error(SearchState.ErrorType.NO_INTERNET), viewModel.state.value)

        viewModel.onQueryChanged("")
        assertEquals(SearchState.Default, viewModel.state.value)
    }

    @Test
    fun serverResponseHasServerErrorType() = runTest(dispatcher) {
        interactor.response = { Resource.Error(503) }
        viewModel.onQueryChanged("android")
        advanceUntilIdle()

        assertEquals(SearchState.Error(SearchState.ErrorType.SERVER), viewModel.state.value)
    }

    private class RecordingSearchInteractor : SearchInteractor {
        val queries = mutableListOf<String>()
        var response: suspend (String) -> Resource<SearchResult> = { Resource.Success(SearchResult(emptyList(), 0)) }

        override suspend fun searchVacancies(query: String): Resource<SearchResult> {
            queries.add(query)
            return response(query)
        }
    }
}
