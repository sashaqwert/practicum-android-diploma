package ru.practicum.android.diploma.filter.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.common.domain.models.FilterIndustry
import ru.practicum.android.diploma.databinding.FragmentIndustriesBinding
import ru.practicum.android.diploma.filter.presentation.state.IndustriesState
import ru.practicum.android.diploma.filter.presentation.viewmodels.IndustriesViewModel
import ru.practicum.android.diploma.filter.ui.adapter.IndustryAdapter

class IndustriesFragment : Fragment() {
    private var _viewBinding: FragmentIndustriesBinding? = null
    private val viewBinding get() = _viewBinding!!
    private val viewModel by viewModel<IndustriesViewModel>()
    private var industryAdapter: IndustryAdapter? = null
    private var selectedIndustryId: String? = null

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
        _viewBinding = FragmentIndustriesBinding.inflate(inflater, container, false)
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
                is IndustriesState.Content -> {
                    showContent(state.industries)
                }

                is IndustriesState.Error -> {
                    showError(state.code)
                }

                is IndustriesState.Loading -> {
                    showLoading()
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, enabled = true) {
            val filter = industryAdapter?.getIndustryById(selectedIndustryId ?: "")
            parentFragmentManager.setFragmentResult(
                INDUSTRY_KEY,
                Bundle().apply {
                    putString(INDUSTRY_ARG_KEY, filter?.id)
                    putString(INDUSTRY_NAME_ARG_KEY, filter?.name)
                }
            )

            findNavController().navigateUp()
        }
    }

    private fun showContent(industries: List<FilterIndustry>) {
        handleVisibilities(
            isContent = true,
            isLoading = false,
            isError = false
        )

        setAdapter(industries)

        viewBinding.searchEditText.doOnTextChanged { text, start, before, count ->
            industryAdapter!!.filterIndustries(text.toString())
        }
    }

    private fun setAdapter(industries: List<FilterIndustry>) {
        with(viewBinding) {
            industryAdapter = IndustryAdapter { filterIndustry ->
                selectedIndustryId = filterIndustry.id
                industryAdapter!!.submitIndustries(industries, selectedIndustryId)
            }

            industriesRecyclerView.adapter = industryAdapter
            industryAdapter!!.submitIndustries(industries, selectedIndustryId)
        }
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
                NO_INDUSTRY_RESPONSE, NO_INTERNET -> {
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
            industriesRecyclerView.isVisible = isContent
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }

    companion object {
        private const val NO_INTERNET = -1
        private const val NO_INDUSTRY_RESPONSE = -2
        const val INDUSTRY_KEY = "industry_key"
        const val INDUSTRY_ARG_KEY = "industry_arg_key"
        const val INDUSTRY_NAME_ARG_KEY = "industry_name"
    }
}
