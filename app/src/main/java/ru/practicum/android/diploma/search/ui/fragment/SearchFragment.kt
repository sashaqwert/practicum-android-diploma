package ru.practicum.android.diploma.search.ui.fragment

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.common.domain.models.VacancyCard
import ru.practicum.android.diploma.common.ui.adapters.VacancyAdapter
import ru.practicum.android.diploma.databinding.FragmentSearchBinding
import ru.practicum.android.diploma.search.presentation.SearchState
import ru.practicum.android.diploma.search.presentation.SearchViewModel

class SearchFragment : Fragment() {
    private var _viewBinding: FragmentSearchBinding? = null
    private val viewBinding get() = _viewBinding!!
    private val viewModel: SearchViewModel by viewModel()
    private val vacancies = mutableListOf<VacancyCard>()
    private val vacancyAdapter = VacancyAdapter(vacancies) { vacancy ->
        val navController = findNavController()
        if (navController.currentDestination?.id == R.id.searchFragment) {
            navController.navigate(
                R.id.action_searchFragment_to_detailsFragment,
                Bundle().apply { putString("vacancyId", vacancy.id) }
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _viewBinding = FragmentSearchBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.vacancyList.adapter = vacancyAdapter
        viewBinding.searchEditText.doOnTextChanged { text, _, _, _ ->
            viewBinding.clearButton.isVisible = !text.isNullOrEmpty()
            viewModel.onQueryChanged(text?.toString().orEmpty())
        }
        viewBinding.clearButton.setOnClickListener {
            viewBinding.searchEditText.setText("")
            val inputMethodManager = requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(viewBinding.searchEditText.windowToken, 0)
        }
        viewBinding.filerButton.setOnClickListener {
            val navController = findNavController()
            if (navController.currentDestination?.id == R.id.searchFragment) {
                navController.navigate(R.id.action_searchFragment_to_filterFragment)
            }
        }
        viewModel.state.observe(viewLifecycleOwner, ::render)
    }

    override fun onDestroyView() {
        viewBinding.vacancyList.adapter = null
        _viewBinding = null
        super.onDestroyView()
    }

    private fun render(state: SearchState) {
        when (state) {
            SearchState.Default -> default()
            SearchState.Typing -> typing()
            SearchState.Loading -> loading()
            is SearchState.Content -> {
                vacancies.clear()
                vacancies.addAll(state.vacancies)
                vacancyAdapter.notifyDataSetChanged()
                viewBinding.spaceForVacancyCount.text = resources.getQuantityString(
                    R.plurals.vacancies_found, state.found, state.found
                )
                content()
            }
            is SearchState.Error -> when (state.type) {
                SearchState.ErrorType.NO_INTERNET -> noInternet()
                SearchState.ErrorType.SERVER -> error()
            }
        }
    }

    private fun typing() {
        viewBinding.spaceForImgInSearch.isVisible = false
        viewBinding.spaceForVacancyCount.isVisible = false
        viewBinding.vacancyList.isVisible = false
        viewBinding.progressBar.isVisible = false
        viewBinding.textStateSpace.isVisible = false
    }

    private fun loading() {
        viewBinding.spaceForImgInSearch.isVisible = false
        viewBinding.spaceForVacancyCount.isVisible = false
        viewBinding.vacancyList.isVisible = false
        viewBinding.progressBar.isVisible = true
        viewBinding.textStateSpace.isVisible = false
    }

    private fun content() {
        viewBinding.spaceForImgInSearch.isVisible = false
        viewBinding.spaceForVacancyCount.isVisible = true
        viewBinding.vacancyList.isVisible = true
        viewBinding.progressBar.isVisible = false
        viewBinding.textStateSpace.isVisible = false
    }

    private fun noInternet() {
        viewBinding.spaceForVacancyCount.isVisible = false
        viewBinding.vacancyList.isVisible = false
        viewBinding.progressBar.isVisible = false
        viewBinding.spaceForImgInSearch.apply {
            setImageResource(R.drawable.img_not_internet_search_state_328)
            isVisible = true
        }
        viewBinding.textStateSpace.apply {
            setText(R.string.no_internet)
            isVisible = true
        }
    }

    private fun error() {
        viewBinding.spaceForVacancyCount.isVisible = false
        viewBinding.vacancyList.isVisible = false
        viewBinding.progressBar.isVisible = false
        viewBinding.spaceForImgInSearch.apply {
            setImageResource(R.drawable.img_error_search_state_328)
            isVisible = true
        }
        viewBinding.textStateSpace.apply {
            setText(R.string.vacancy_error)
            isVisible = true
        }
    }

    private fun default() {
        viewBinding.vacancyList.isVisible = false
        viewBinding.spaceForVacancyCount.isVisible = false
        viewBinding.progressBar.isVisible = false
        viewBinding.textStateSpace.isVisible = false
        viewBinding.spaceForImgInSearch.apply {
            setImageResource(R.drawable.img_default_search_state_328)
            isVisible = true
        }
    }

}
