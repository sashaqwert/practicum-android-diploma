package ru.practicum.android.diploma.filter.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.common.domain.models.FilterArea
import ru.practicum.android.diploma.databinding.FragmentCountryBinding
import ru.practicum.android.diploma.filter.presentation.state.CountryState
import ru.practicum.android.diploma.filter.presentation.state.IndustriesState
import ru.practicum.android.diploma.filter.presentation.viewmodels.CountryViewModel
import ru.practicum.android.diploma.filter.ui.adapter.AreaAdapter

class CountryFragment : Fragment() {
    private var _viewBinding: FragmentCountryBinding? = null
    private val viewBinding get() = _viewBinding!!
    private val viewModel by viewModel<CountryViewModel>()
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
        _viewBinding = FragmentCountryBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).setSupportActionBar(viewBinding.toolBar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(true)
        viewBinding.toolBar.setNavigationOnClickListener { requireActivity().onBackPressedDispatcher.onBackPressed() }

        viewModel.fillData()

        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is CountryState.Content -> {
                    showContent(state.filterAreas)
                }

                is CountryState.Error -> {
                    showError(state.code)
                }

                is CountryState.Loading -> {
                    showLoading()
                }
            }
        }

//        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, enabled = true) {
//            parentFragmentManager.setFragmentResult(
//                GET_COUNTRY,
//                Bundle().apply {
//                    putString(CURRENT_COUNTRY, area?.id ?: "")
//                    putString(CURRENT_COUNTRY_NAME, area?.name ?: "")
//                }
//            )
//
//            findNavController().navigateUp()
//        }
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
                GET_COUNTRY,
                Bundle().apply {
                    putString(CURRENT_COUNTRY, area?.name ?: "")
                    putString(CURRENT_COUNTRY_ID, area?.id ?: "")
                }
            )

            findNavController().navigateUp()
        }
        viewBinding.countriesRecyclerView.adapter = areaAdapter
        areaAdapter!!.submitAreas(filterAreas)
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
                NO_AREA_RESPONSE, NO_INTERNET -> {
                    drawable = R.drawable.img_not_internet_search_state_328
                    text = R.string.no_internet
                }

                else -> {
                    drawable = R.drawable.img_server_error
                    text = R.string.server_error
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
            loadingBar.isVisible = isLoading
            errorLayout.isVisible = isError
            countriesRecyclerView.isVisible = isContent
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }

    companion object {
        private const val GET_COUNTRY = "GET_COUNTRY"
        private const val CURRENT_COUNTRY = "CURRENT_COUNTRY"
        private const val CURRENT_COUNTRY_ID = "CURRENT_COUNTRY_NAME"
        private const val NO_INTERNET = -1
        private const val NO_AREA_RESPONSE = -2
    }
}
