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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.common.domain.models.VacancyCard
import ru.practicum.android.diploma.common.ui.for_recycler_view.VacancyAdapter
import ru.practicum.android.diploma.databinding.FragmentSearchBinding
import ru.practicum.android.diploma.details.ui.fragment.DetailsFragment


private const val ARG_PARAM1 = "param1"

private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class SearchFragment : Fragment() {
    private var param1: String? = null

    private var param2: String? = null
    private var _viewBinding: FragmentSearchBinding? = null
    private var vacancies: List<VacancyCard> = emptyList()

    private val viewBinding get() = _viewBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
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
        }


        viewBinding.clearButton.setOnClickListener {
            viewBinding.searchEditText.setText("")
            viewBinding.clearButton.isVisible = false
            default()
            val inputMethodManager =
                requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        }

        val adapter = VacancyAdapter(vacancies) { vacancy ->
            findNavController().navigate(
                R.id.action_searchFragment_to_detailsFragment,
                DetailsFragment.putId(vacancy.id)
            )
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
                        //вызов метода viewModel, который вызовет интерактор для отправки поискового запроса
                    }
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment SearchFragment.
         */
        @JvmStatic
        fun newInstance() =
            SearchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
