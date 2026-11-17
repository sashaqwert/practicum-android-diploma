package ru.practicum.android.diploma.filter.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFilterBinding

class FilterFragment : Fragment() {
    private var _viewBinding: FragmentFilterBinding? = null

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
        _viewBinding = FragmentFilterBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()

        showButtons(1)
    }

    private fun setListeners() {
        viewBinding.apply {
            salaryCheckBox.setOnClickListener {
                salaryCheckBox.toggle()
            }

            salaryEditTextContainer.setOnClickListener {
                salaryEditText.requestFocus()

                WindowCompat.getInsetsController(requireActivity().window, salaryEditText)
                    .show(WindowInsetsCompat.Type.ime())
            }

            industryValue.setOnClickListener {
                findNavController().navigate(R.id.action_filterFragment_to_industryFragment)
            }

            workPlaceValue.setOnClickListener {
                findNavController().navigate(R.id.action_filterFragment_to_locationFragment)
            }
        }

        parentFragmentManager.setFragmentResultListener(
            IndustriesFragment.INDUSTRY_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            val result =
                bundle.getString(IndustriesFragment.INDUSTRY_ARG_KEY) ?: return@setFragmentResultListener

            // вот это нужно будет доработать, чтобы вью модель шла в интернет и искала отрасль по айди
            viewBinding.industryValue.setText(result)
        }
    }

    private fun showButtons(isFiltersChanged: Int) {
        viewBinding.apply {
            applyFilter.clipToOutline = true
            cancelButton.clipToOutline = true
            applyFilter.visibility = isFiltersChanged
            cancelButton.visibility = isFiltersChanged
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }

    companion object {
    }
}
