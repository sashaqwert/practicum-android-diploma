package ru.practicum.android.diploma.search.domain.usecaseimpl

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.practicum.android.diploma.common.domain.Resource
import ru.practicum.android.diploma.common.domain.models.VacancyCard
import ru.practicum.android.diploma.filter.domain.usecase.RegionInteractor
import ru.practicum.android.diploma.search.domain.model.SearchPage
import ru.practicum.android.diploma.search.domain.model.SearchResult
import ru.practicum.android.diploma.search.domain.repository.SearchRepository
import ru.practicum.android.diploma.search.domain.usecase.SearchInteractor
import java.io.IOException

class SearchInteractorImpl(
    private val searchRepository: SearchRepository,
    private val regionInteractor: RegionInteractor
) : SearchInteractor {
    private var countrySearch: CountrySearch? = null

    override suspend fun searchVacancies(options: Map<String, String>, countryId: Int?): Resource<SearchPage> {
        if (countryId == null) {
            countrySearch = null
            return searchArea(options)
        }

        val page = options["page"]?.toIntOrNull()?.coerceAtLeast(FIRST_PAGE) ?: FIRST_PAGE
        val countryOptions = options - "page" + ("area" to countryId.toString())
        return searchCountry(countryOptions, countryId, page)
    }

    override fun countCountryVacancies(): Flow<Int> {
        val search = countrySearch ?: return emptyFlow()
        return channelFlow {
            for (batch in search.regions.chunked(PARALLEL_COUNT_REQUESTS)) {
                coroutineScope {
                    batch.forEach { region ->
                        launch { loadRegionCount(search, region)?.let { send(it) } }
                    }
                }
            }
        }.distinctUntilChanged()
    }

    private suspend fun loadRegionCount(search: CountrySearch, region: RegionSearch): Int? {
        return try {
            loadFirstRegionPage(search.options, region)
            search.found
        } catch (_: IOException) {
            null
        }
    }

    private suspend fun searchArea(options: Map<String, String>): Resource<SearchPage> {
        return when (val result = searchRepository.searchVacancies(options)) {
            is Resource.Error -> result
            is Resource.Success -> {
                val data = result.data
                Resource.Success(SearchPage(data.vacancies, data.found, data.page, data.page < data.pages))
            }
        }
    }

    private suspend fun searchCountry(
        options: Map<String, String>,
        countryId: Int,
        page: Int
    ): Resource<SearchPage> {
        val cached = countrySearch
        if (page > FIRST_PAGE && cached != null && cached.options == options) {
            return loadCountryPage(cached, page)
        }

        countrySearch = null
        return when (val areas = regionInteractor.getRegions(countryId)) {
            is Resource.Error -> areas
            is Resource.Success -> {
                currentCoroutineContext().ensureActive()
                val regions = (areas.data.map { it.id } + countryId).distinct().map(::RegionSearch)
                val search = CountrySearch(options, regions)
                countrySearch = search
                loadCountryPage(search, page)
            }
        }
    }

    private suspend fun loadCountryPage(search: CountrySearch, page: Int): Resource<SearchPage> {
        return when (val result = loadNextRegionVacancies(search)) {
            is Resource.Error -> result
            is Resource.Success -> {
                val hasNextPage = search.pending.any { it.canLoadPage() }
                Resource.Success(SearchPage(result.data, search.found, page, hasNextPage))
            }
        }
    }

    private suspend fun loadNextRegionVacancies(search: CountrySearch): Resource<List<VacancyCard>> {
        var vacancies = emptyList<VacancyCard>()
        while (vacancies.isEmpty() && search.pending.isNotEmpty()) {
            val region = search.pending.first()
            val result = if (region.page == FIRST_PAGE) {
                loadFirstRegionPage(search.options, region)
            } else {
                loadRegionPage(search.options, region.id, region.page)
            }
            when (result) {
                is Resource.Error -> return result
                is Resource.Success -> {
                    currentCoroutineContext().ensureActive()
                    vacancies = result.data.vacancies
                    search.advance(region, result.data)
                }
            }
        }
        return Resource.Success(vacancies)
    }

    private suspend fun loadFirstRegionPage(
        options: Map<String, String>,
        region: RegionSearch
    ): Resource<SearchResult> = region.firstPageMutex.withLock {
        val cached = region.firstPage
        if (cached != null) {
            Resource.Success(cached)
        } else {
            val result = loadRegionPage(options, region.id, FIRST_PAGE)
            if (result is Resource.Success) region.firstPage = result.data
            result
        }
    }

    private suspend fun loadRegionPage(
        options: Map<String, String>,
        areaId: Int,
        page: Int
    ): Resource<SearchResult> {
        return searchRepository.searchVacancies(options + mapOf("area" to areaId.toString(), "page" to page.toString()))
    }

    private class RegionSearch(val id: Int) {
        val firstPageMutex = Mutex()
        var page = FIRST_PAGE

        @Volatile
        var firstPage: SearchResult? = null

        fun canLoadPage(): Boolean = page > FIRST_PAGE || firstPage?.vacancies?.isEmpty() != true
    }

    private class CountrySearch(val options: Map<String, String>, val regions: List<RegionSearch>) {
        val pending = ArrayDeque(regions)
        val found: Int get() = regions.sumOf { it.firstPage?.found ?: 0 }

        fun advance(region: RegionSearch, result: SearchResult) {
            pending.removeFirst()
            if (region.page < result.pages && result.vacancies.isNotEmpty()) {
                region.page++
                pending.addLast(region)
            }
        }
    }

    private companion object {
        const val FIRST_PAGE = 1
        const val PARALLEL_COUNT_REQUESTS = 4
    }
}
