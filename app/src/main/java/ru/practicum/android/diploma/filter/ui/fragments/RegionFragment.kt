package ru.practicum.android.diploma.filter.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.common.domain.models.FilterArea
import ru.practicum.android.diploma.databinding.FragmentRegionBinding
import ru.practicum.android.diploma.filter.presentation.state.CountryState
import ru.practicum.android.diploma.filter.presentation.state.RegionState
import ru.practicum.android.diploma.filter.presentation.viewmodels.RegionViewModel
import ru.practicum.android.diploma.filter.ui.adapter.AreaAdapter


/**
 * A simple [Fragment] subclass.
 * Use the [RegionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegionFragment : Fragment() {
    private var _viewBinding: FragmentRegionBinding? = null
    private val viewBinding get() = _viewBinding!!
    private val viewModel by viewModel<RegionViewModel>()

    private var areaAdapter: AreaAdapter? = null
    private var area: FilterArea? = null

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
        _viewBinding = FragmentRegionBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).setSupportActionBar(viewBinding.toolBar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(true)
        viewBinding.toolBar.setNavigationOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }

        val countryId = arguments?.getInt(LocationFragment.CURRENT_COUNTRY_ID)?.takeIf { it != 0 }
        viewModel.fillData(countryId)
        viewBinding.searchEditText.doOnTextChanged { text, _, _, _ ->
            viewModel.filterRegions(text?.toString().orEmpty())
        }

        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is RegionState.Content -> {
                    showContent(state.regions)
                }

                is RegionState.Empty -> {
                    showEmpty()
                }

                is RegionState.Error -> {
                    showError(NO_INTERNET)
                }

                is RegionState.Loading -> {
                    showLoading()
                }
            }
        }
    }

    private fun showContent(filterAreas: List<FilterArea>) {
        handleVisibilities(
            isContent = true,
            isLoading = false,
            isError = false
        )

        areaAdapter = AreaAdapter { filterArea ->
            area = filterArea

            parentFragmentManager.setFragmentResult(
                LocationFragment.GET_REGION,
                Bundle().apply {
                    putString(LocationFragment.CURRENT_REGION, area?.name ?: "")
                    putInt(LocationFragment.CURRENT_REGION_ID, area?.id ?: 0)
                    putString(LocationFragment.CURRENT_COUNTRY, area?.parentName)
                    putInt(LocationFragment.CURRENT_COUNTRY_ID, area?.countryId ?: 0)
                }
            )

            findNavController().navigateUp()
        }
        viewBinding.industriesRecyclerView.adapter = areaAdapter
        areaAdapter!!.submitAreas(filterAreas)
    }

    private fun showEmpty() {
        showError(NO_AREA_RESPONSE)
    }

    private fun showError(code: Int) {
        handleVisibilities(
            isContent = false,
            isLoading = false,
            isError = true
        )

        with(viewBinding) {
            val drawable: Int

            val text: Int

            when (code) {
                NO_INTERNET -> {
                    drawable = R.drawable.cant_get_region_list
                    text = R.string.cant_get_region_list
                }

                else -> {
                    drawable = R.drawable.img_empty_search_state_328
                    text = R.string.region_not_found
                }
            }

            errorImg.setImageResource(drawable)
            errorText.setText(text)
        }
    }

    private fun showLoading() {
        handleVisibilities(
            isContent = false,
            isLoading = true,
            isError = false
        )
    }

    private fun handleVisibilities(isContent: Boolean, isLoading: Boolean, isError: Boolean) {
        with(viewBinding) {
            viewBinding.loadingBar.isVisible = isLoading
            viewBinding.errorLayout.isVisible = isError
            viewBinding.industriesRecyclerView.isVisible = isContent
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }

    companion object {
        private const val NO_INTERNET = -1
        private const val NO_AREA_RESPONSE = -2

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment RegionFragment.
         */
        @JvmStatic
        fun newInstance() =
            RegionFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
