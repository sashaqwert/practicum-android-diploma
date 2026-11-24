package ru.practicum.android.diploma.search.ui.fragment

import android.content.Context.INPUT_METHOD_SERVICE
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.MaterialColors
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.common.domain.models.VacancyCard
import ru.practicum.android.diploma.common.ui.forrecyclerview.VacancyAdapter
import ru.practicum.android.diploma.databinding.FragmentSearchBinding
import ru.practicum.android.diploma.details.ui.fragment.DetailsFragment
import ru.practicum.android.diploma.filter.ui.fragments.FilterFragment
import ru.practicum.android.diploma.search.presentation.state.SearchState
import ru.practicum.android.diploma.search.presentation.viewmodel.SearchViewModel

class SearchFragment : Fragment() {
    private var _viewBinding: FragmentSearchBinding? = null
    private var vacancies: List<VacancyCard> = emptyList()
    private val viewBinding get() = _viewBinding!!
    private val viewModel: SearchViewModel by viewModel()

    @Suppress("LateinitUsage")
    private lateinit var adapter: VacancyAdapter

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
            viewBinding.spaceForImgInSearch.isVisible = false
            viewBinding.textStateSpace.isVisible = false
            viewModel.onQueryChanged(s?.toString().orEmpty())
        }

        initAdapter()
        observeViewModelState()

        viewBinding.clearButton.setOnClickListener {
            viewBinding.searchEditText.setText("")
            viewBinding.clearButton.isVisible = false
            closeKeyboard()
            default()
        }
        viewBinding.filerButton.setOnClickListener {
            findNavController().navigate(R.id.action_searchFragment_to_filterFragment)
        }

        viewBinding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.retrySearch()
                closeKeyboard()
                true
            } else {
                false
            }
        }

        viewBinding.vacancyList.adapter = adapter
        viewBinding.vacancyList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy >= 0) recyclerView.post { loadPageAtListEnd(recyclerView, dy > 0) }
            }
        })

        setFilterListener()
    }

    override fun onResume() {
        super.onResume()
        val hasFilters = viewModel.hasFilters()
        with(viewBinding.filerButton) {
            isSelected = hasFilters
            if (hasFilters) {
                iconTint = null
                setIconResource(R.drawable.ic_filter_active_24)
            } else {
                val color = MaterialColors.getColor(this, com.google.android.material.R.attr.colorOnPrimary)
                iconTint = ColorStateList.valueOf(color)
                setIconResource(R.drawable.ic_filter_18)
            }
        }
    }

    private fun loadPageAtListEnd(recyclerView: RecyclerView, scrolledDown: Boolean) {
        val currentState = viewModel.state.value as? SearchState.Content ?: return
        if (_viewBinding?.vacancyList != recyclerView || !isAtListEnd(recyclerView, scrolledDown)) return
        if (currentState.paginationError == null) {
            viewModel.loadPage(currentState.page + 1)
        } else if (scrolledDown) {
            Toast.makeText(requireContext(), R.string.no_wifi, Toast.LENGTH_LONG).show()
            viewModel.onPaginationErrorShown()
        }
    }

    private fun isAtListEnd(recyclerView: RecyclerView, scrolledDown: Boolean): Boolean {
        if (!scrolledDown && (recyclerView.canScrollVertically(-1) || recyclerView.canScrollVertically(1))) return false
        val manager = recyclerView.layoutManager as LinearLayoutManager
        val lastVisible = manager.findLastVisibleItemPosition()
        return lastVisible != RecyclerView.NO_POSITION && lastVisible == adapter.itemCount - 1
    }

    private fun initAdapter() {
        adapter = VacancyAdapter(vacancies) { vacancy ->
            findNavController().navigate(
                R.id.action_searchFragment_to_detailsFragment,
                DetailsFragment.putId(vacancy.id)
            )
        }
    }

    private fun observeViewModelState() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            viewBinding.paginationProgressBar.isVisible = (state as? SearchState.Content)?.isLoadingMore == true
            when (state) {
                is SearchState.Content -> {
                    if (vacancies !== state.vacancies) {
                        vacancies = state.vacancies
                        adapter.updateList(vacancies)
                    }
                    content(state.found)
                }

                SearchState.Default -> default()
                SearchState.Error.NoInternet -> noInternet()
                SearchState.Error.Server -> error()
                SearchState.Loading -> loading()
            }
        }
    }

    private fun setFilterListener() {
        parentFragmentManager
            .setFragmentResultListener(
                FilterFragment.SEND_FILTERS,
                viewLifecycleOwner
            ) { _, _ ->
                viewModel.retrySearch()
            }
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
        viewBinding.progressBar.isVisible = true
        viewBinding.spaceForImgInSearch.isVisible = false
        viewBinding.spaceForVacancyCount.isVisible = false
        viewBinding.vacancyList.isVisible = false
        viewBinding.textStateSpace.isVisible = false
        viewBinding.vacancyList.scrollToPosition(0)
    }

    private fun content(found: Int) {
        if (viewBinding.progressBar.isVisible) closeKeyboard()
        viewBinding.spaceForImgInSearch.apply {
            if (found == 0) setImageResource(R.drawable.img_empty_search_state_328)
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
                setText(R.string.vacancy_empty)
                isVisible = true
            }
        }
    }

    private fun noInternet() {
        closeKeyboard()
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
        viewModel.retrySearchWithDebounce()
    }

    private fun error() {
        closeKeyboard()
        viewBinding.spaceForVacancyCount.isVisible = false
        viewBinding.vacancyList.isVisible = false
        viewBinding.progressBar.isVisible = false
        viewBinding.spaceForImgInSearch.apply {
            setImageResource(R.drawable.img_error_search_state_328)
            isVisible = true
        }
        viewBinding.textStateSpace.apply {
            setText(R.string.server_error)
            isVisible = true
        }
    }

    private fun default() {
        closeKeyboard()
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
