package ru.practicum.android.diploma.search

import android.os.SystemClock
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.dsl.module
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.common.data.network.client.NetworkClient
import ru.practicum.android.diploma.common.data.network.client.RequestIntent
import ru.practicum.android.diploma.common.data.network.client.Response
import ru.practicum.android.diploma.root_activity.RootActivity
import ru.practicum.android.diploma.search.data.dto.VacancyCardDto
import ru.practicum.android.diploma.search.data.repository_impl.SearchRepositoryImpl
import ru.practicum.android.diploma.search.data.response.VacancyResponse
import ru.practicum.android.diploma.search.domain.repository.SearchRepository

@RunWith(AndroidJUnit4::class)
class SearchIntegrationTest {
    @Test
    fun searchesClearsNavigatesAndDistinguishesErrors() {
        val client = TestNetworkClient()
        val testModule = module {
            single<NetworkClient> { client }
            single<SearchRepository> { SearchRepositoryImpl(get()) { true } }
        }
        loadKoinModules(testModule)
        try {
            ActivityScenario.launch(RootActivity::class.java).use { scenario ->
                scenario.onActivity { activity ->
                    assertTrue(activity.findViewById<View>(R.id.space_for_img_in_search).isShown)
                    activity.findViewById<EditText>(R.id.search_edit_text).setText("android")
                    assertFalse(activity.findViewById<View>(R.id.space_for_img_in_search).isShown)
                    assertFalse(activity.findViewById<View>(R.id.progress_bar).isShown)
                }
                await(scenario) { activity ->
                    activity.findViewById<TextView>(R.id.space_for_vacancy_count).text ==
                        activity.resources.getQuantityString(R.plurals.vacancies_found, 42, 42)
                }
                scenario.recreate()
                await(scenario) { activity ->
                    activity.findViewById<RecyclerView>(R.id.vacancy_list).childCount == 1
                }
                scenario.onActivity { activity ->
                    val list = activity.findViewById<RecyclerView>(R.id.vacancy_list)
                    assertEquals(1, list.adapter?.itemCount)
                    assertEquals("android", client.query)
                    assertEquals("android", activity.findViewById<EditText>(R.id.search_edit_text).text.toString())
                    list.getChildAt(0).performClick()
                    val controller = navHost(activity).navController
                    assertEquals(R.id.detailsFragment, controller.currentDestination?.id)
                    assertEquals("123", controller.currentBackStackEntry?.arguments?.getString("vacancyId"))
                    controller.popBackStack()
                }
                await(scenario) { activity -> activity.findViewById<View>(R.id.clear_button)?.isShown == true }
                scenario.onActivity { activity ->
                    activity.findViewById<View>(R.id.clear_button).performClick()
                    assertFalse(activity.findViewById<View>(R.id.space_for_vacancy_count).isShown)
                    assertTrue(activity.findViewById<View>(R.id.space_for_img_in_search).isShown)
                    client.response = Response().apply { code = -1 }
                    activity.findViewById<EditText>(R.id.search_edit_text).setText("offline")
                }
                await(scenario) { activity ->
                    activity.findViewById<TextView>(R.id.text_state_space).text ==
                        activity.getString(R.string.no_internet)
                }
                scenario.onActivity { activity ->
                    client.response = Response().apply { code = 503 }
                    activity.findViewById<EditText>(R.id.search_edit_text).setText("server")
                }
                await(scenario) { activity ->
                    activity.findViewById<TextView>(R.id.text_state_space).text ==
                        activity.getString(R.string.vacancy_error)
                }
                scenario.onActivity { activity ->
                    activity.findViewById<View>(R.id.filer_button).performClick()
                    assertEquals(R.id.filterFragment, navHost(activity).navController.currentDestination?.id)
                }
            }
        } finally {
            unloadKoinModules(testModule)
        }
    }

    private fun navHost(activity: RootActivity): NavHostFragment =
        activity.supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment

    private fun await(scenario: ActivityScenario<RootActivity>, condition: (RootActivity) -> Boolean) {
        val deadline = SystemClock.uptimeMillis() + 10000
        var matched = false
        while (!matched && SystemClock.uptimeMillis() < deadline) {
            scenario.onActivity { matched = condition(it) }
            if (!matched) SystemClock.sleep(50)
        }
        assertTrue("Search screen did not reach the expected state", matched)
    }

    private class TestNetworkClient : NetworkClient {
        var query: String? = null
        var response: Response = VacancyResponse(
            found = 42,
            vacancyCards = listOf(VacancyCardDto(null, "Company", "123", null, "Android developer", null)),
            page = 0,
            pages = 3
        ).apply { code = 200 }

        override suspend fun doRequest(intent: RequestIntent, isConnected: Boolean): Response {
            query = (intent as RequestIntent.GetFilteredVacancies).options["text"]
            return response
        }
    }
}
