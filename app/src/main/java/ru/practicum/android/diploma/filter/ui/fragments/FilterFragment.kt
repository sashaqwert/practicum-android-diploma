package ru.practicum.android.diploma.filter.ui.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.color.MaterialColors
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFilterBinding
import ru.practicum.android.diploma.filter.presentation.state.FilterState
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

        observeState()
        setListeners()
        setFragmentResultListeners()
    }

    private fun setListeners() {
        viewBinding.apply {
            salaryCheckBox.setOnClickListener {
                salaryCheckBox.toggle()
                viewModel.setDoNotShowWithoutSalary(salaryCheckBox.isChecked)
            }

            salaryEditTextContainer.setOnClickListener {
                salaryEditText.requestFocus()

                WindowCompat.getInsetsController(requireActivity().window, salaryEditText)
                    .show(WindowInsetsCompat.Type.ime())
            }

            salaryEditText.doOnTextChanged { text, _, _, _ ->
                viewModel.setSalary(text?.toString()?.toLongOrNull())
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

            cancelButton.setOnClickListener {
                viewModel.resetButtonClicked()
            }
        }
    }

    private fun setFragmentResultListeners() {
        parentFragmentManager.setFragmentResultListener(
            IndustriesFragment.INDUSTRY_KEY,
            viewLifecycleOwner
        ) { _, bundle ->
            val id =
                bundle.getInt(IndustriesFragment.INDUSTRY_ARG_KEY)

            val name =
                bundle.getString(IndustriesFragment.INDUSTRY_NAME_ARG_KEY) ?: ""

            if (id == 0) {
                return@setFragmentResultListener
            }


            viewModel.setFilterIndustry(id, name)
        }

        parentFragmentManager.setFragmentResultListener(LOCATION, viewLifecycleOwner) { _, bundle ->
            val id = bundle.getInt(CURRENT_COUNTRY_ID)
            val countryName = bundle.getString(COUNTRY) ?: ""
            val regionName = bundle.getString(REGION) ?: ""

            if (id == 0) {
                return@setFragmentResultListener
            }

            viewModel.setFilterArea(id, regionName, countryName)
        }
    }

    private fun observeState() {
        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            setIndustryName(state)
            setLocationName(state)

            setSalary(state)
            viewBinding.salaryCheckBox.isChecked = state.doNotShowWithoutSalary

            showButtons()

            setIcons(state)
        }
    }

    private fun setLocationName(state: FilterState) {
        val countryName = state.filterArea?.parentName ?: ""
        val regionName = state.filterArea?.name ?: ""

        if (countryName.isNotEmpty() && countryName.isNotBlank()) {
            val text = "$countryName, $regionName"
            viewBinding.workPlaceValue.setText(text)
        } else {
            viewBinding.workPlaceValue.text?.clear()
        }
    }

    private fun setIndustryName(state: FilterState) {
        val industryName = state.filterIndustry?.name ?: ""

        if (industryName.isNotEmpty() && industryName.isNotBlank()) {
            viewBinding.industryValue.setText(industryName)
        } else {
            viewBinding.industryValue.text?.clear()
        }
    }

    private fun setSalary(state: FilterState) {
        with(viewBinding) {
            val salary = state.salary ?: 0
            if (salary == 0L) {
                salaryEditText.text.clear()
            } else if (state.salary == salary) {
                return
            } else {
                salaryEditText.setText(salary.toString())
            }
        }
    }

    private fun setIcons(state: FilterState) {
        with(viewBinding) {
            val area = state.filterArea
            val areaDrawable = if (area != null) {
                workPlaceIcon.setOnClickListener {
                    viewModel.deleteLocation()
                }
                R.drawable.ic_cancel
            } else {
                R.drawable.ic_select
            }
            workPlaceIcon.setImageResource(areaDrawable)

            val industry = state.filterIndustry

            val industryDrawable = if (industry != null) {
                industryIcon.setOnClickListener {
                    viewModel.deleteIndustry()
                }
                R.drawable.ic_cancel
            } else {
                R.drawable.ic_select
            }

            industryIcon.setImageResource(industryDrawable)
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

    private fun getSalaryColor(text: CharSequence?): Int {
        val isKeyboardVisible =
            ViewCompat.getRootWindowInsets(viewBinding.root)
                ?.isVisible(WindowInsetsCompat.Type.ime()) == true

        return if (text.isNullOrEmpty()) {
            MaterialColors.getColor(
                viewBinding.salaryEditTextHeader,
                com.google.android.material.R.attr.colorOnContainer
            )
        } else if (isKeyboardVisible) {
            ContextCompat.getColor(requireContext(), R.color.blue)
        } else {
            ContextCompat.getColor(requireContext(), R.color.black)
        }
    }

    private fun showButtons() {
        viewBinding.apply {
            applyFilter.clipToOutline = true
            cancelButton.clipToOutline = true
            applyFilter.isVisible = viewModel.compareWithLast()
            cancelButton.isVisible = viewModel.compareWithDefault()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }

    companion object {
        private const val LOCATION = "LOCATION"
        private const val COUNTRY = "COUNTRY"
        private const val REGION = "REGION"
        private const val CURRENT_COUNTRY_ID = "CURRENT_COUNTRY_ID"
    }
}
