package ru.practicum.android.diploma.search.ui.fragment

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.common.domain.models.VacancyCard
import ru.practicum.android.diploma.common.ui.forrecyclerview.VacancyAdapter
import ru.practicum.android.diploma.databinding.FragmentSearchBinding
import ru.practicum.android.diploma.details.ui.fragment.DetailsFragment
import ru.practicum.android.diploma.search.presentation.state.SearchState
import ru.practicum.android.diploma.search.presentation.viewmodel.SearchViewModel

class SearchFragment : Fragment() {
    private var _viewBinding: FragmentSearchBinding? = null
    private var vacancies: List<VacancyCard> = emptyList()
    private val viewBinding get() = _viewBinding!!

    private val viewModel: SearchViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _viewBinding = FragmentSearchBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding.searchEditText.doOnTextChanged { s, _, _, _ ->
            viewBinding.clearButton.isVisible = !s.isNullOrEmpty()
            viewModel.onQueryChanged(s?.toString().orEmpty())
        }

        val adapter = VacancyAdapter(vacancies) { vacancy ->
            findNavController().navigate(
                R.id.action_searchFragment_to_detailsFragment,
                DetailsFragment.putId(vacancy.id)
            )
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is SearchState.Content -> {
                    adapter.updateList(state.vacancies)
                    content(state.found)
                    viewBinding.paginationProgressBar.isVisible = state.isLoadingMore
                }

                SearchState.Default -> default()
                SearchState.Error.NoInternet -> noInternet()
                SearchState.Error.Server -> error()
                SearchState.Loading -> loading()
            }
        }

        viewBinding.clearButton.setOnClickListener {
            viewBinding.searchEditText.setText("")
            viewBinding.clearButton.isVisible = false
            closeKeyboard()
            default()
        }

        viewBinding.vacancyList.adapter = adapter
        viewBinding.vacancyList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    val pos =
                        (viewBinding.vacancyList.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                    val itemsCount = adapter.itemCount
                    if (pos >= itemsCount - 1) {
                        val currentState = viewModel.state.value as? SearchState.Content
                        if (currentState != null) {
                            if (currentState.paginationError == null) {
                                val nextPage = currentState.page + 1
                                viewModel.loadPage(nextPage)
                            } else {
                                Toast.makeText(requireContext(), R.string.no_wifi, Toast.LENGTH_LONG).show()
                                viewModel.onPaginationErrorShown()
                            }
                        }
                    }
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }

    private fun closeKeyboard() {
        val inputMethodManager =
            requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
    }

    private fun loading() {
        closeKeyboard()
        viewBinding.progressBar.isVisible = true
        viewBinding.spaceForImgInSearch.isVisible = false
        viewBinding.spaceForVacancyCount.isVisible = false
        viewBinding.vacancyList.isVisible = false
        viewBinding.textStateSpace.isVisible = false
    }

    private fun content(found: Int) {
        viewBinding.spaceForImgInSearch.apply {
            setImageResource(R.drawable.img_error_search_state_328)
            isVisible = found == 0
        }
        viewBinding.spaceForVacancyCount.apply {
            if (found == 0) {
                setText(R.string.empty_vacancy)
            } else {
                text = getString(R.string.found_vacancy, found)
            }
            isVisible = true
        }
        viewBinding.vacancyList.isVisible = found > 0
        viewBinding.progressBar.isVisible = false
        viewBinding.textStateSpace.apply {
            if (found > 0) {
                isVisible = false
            } else {
                setText(R.string.vacancy_error)
                isVisible = true
            }
        }
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
