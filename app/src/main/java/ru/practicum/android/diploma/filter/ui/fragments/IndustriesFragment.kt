package ru.practicum.android.diploma.filter.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.common.domain.models.FilterIndustry
import ru.practicum.android.diploma.databinding.FragmentIndustriesBinding
import ru.practicum.android.diploma.filter.presentation.state.IndustriesState
import ru.practicum.android.diploma.filter.presentation.viewmodels.IndustriesViewModel

class IndustriesFragment : Fragment() {
    private var _viewBinding: FragmentIndustriesBinding? = null
    private val viewBinding get() = _viewBinding!!
    private val viewModel by viewModel<IndustriesViewModel>()

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
        viewBinding.toolBar.setNavigationOnClickListener { findNavController().popBackStack() }

        viewModel.fillData()

        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is IndustriesState.Content -> {
                    showContent(state.industries)
                }

                is IndustriesState.Error -> {
                    showError()
                }

                is IndustriesState.Loading -> {
                    showLoading()
                }
            }
        }
    }

    private fun showContent(industries: List<FilterIndustry>) {
        handleVisibilities(
            isContent = true,
            isLoading = false,
            isError = false
        )

        with(viewBinding) {
            // здесь потом нужно прописать примерно эти строки, когда адаптер будет готов:
            // industriesRecyclerView.adapter = industriesAdapter
            // industriesAdapter.updateList()
        }
    }

    private fun showError() {
        handleVisibilities(
            isContent = false,
            isLoading = false,
            isError = true
        )
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
}
