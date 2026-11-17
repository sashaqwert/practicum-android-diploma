package ru.practicum.android.diploma.filter.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import ru.practicum.android.diploma.databinding.FragmentCountryBinding

class CountryFragment : Fragment() {
    private var _viewBinding: FragmentCountryBinding? = null
    private val viewBinding get() = _viewBinding!!

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

    private fun showContent() {
        handleVisibilities(
            isContent = true,
            isLoading = false,
            isError = false
        )
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
            countriesRecyclerView.isVisible = isContent
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }
}
