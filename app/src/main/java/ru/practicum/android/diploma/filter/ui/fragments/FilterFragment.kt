package ru.practicum.android.diploma.filter.ui.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.color.MaterialColors
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.common.domain.models.FilterArea
import ru.practicum.android.diploma.common.domain.models.FilterIndustry
import ru.practicum.android.diploma.databinding.FragmentFilterBinding
import ru.practicum.android.diploma.filter.presentation.viewmodels.FilterViewModel

class FilterFragment : Fragment() {
    private var _viewBinding: FragmentFilterBinding? = null

    private val viewBinding get() = _viewBinding!!

    private val viewModel by viewModel<FilterViewModel>()

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

            industryValue.doOnTextChanged { text, _, _, _ ->
                val color = getColor(text)

                industry.defaultHintTextColor =
                    ColorStateList.valueOf(color)
            }

            workPlaceValue.doOnTextChanged { text, _, _, _ ->
                val color = getColor(text)

                workPlace.defaultHintTextColor =
                    ColorStateList.valueOf(color)
            }
        }

        parentFragmentManager.setFragmentResultListener(
            IndustriesFragment.INDUSTRY_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            val id =
                bundle.getString(IndustriesFragment.INDUSTRY_ARG_KEY) ?: ""

            val name =
                bundle.getString(IndustriesFragment.INDUSTRY_NAME_ARG_KEY) ?: ""

            val filterIndustry = FilterIndustry(id, name)
            viewModel.setFilterIndustry(filterIndustry)

            viewBinding.industryValue.setText(name)
        }
    }

    private fun getColor(text: CharSequence?): Int {
        return if (text.isNullOrEmpty()) {
            ContextCompat.getColor(requireContext(), R.color.gray)
        } else {
            MaterialColors.getColor(
                viewBinding.industry,
                com.google.android.material.R.attr.colorOnPrimary
            )
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
