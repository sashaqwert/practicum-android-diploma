package ru.practicum.android.diploma.filter.ui.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.ViewCompat.getRootWindowInsets
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
    private var industryId = 0

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

            ViewCompat.setOnApplyWindowInsetsListener(root) { _, insets ->
                val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
                salaryEditTextHeader.setTextColor(getSalaryColor(salaryEditText.text, imeVisible))
                insets
            }

            salaryEditText.doOnTextChanged { text, _, _, _ ->
                viewModel.setSalary(text?.toString()?.toLongOrNull())
                val imeVisible = getRootWindowInsets(root)
                    ?.isVisible(WindowInsetsCompat.Type.ime()) == true

                salaryEditTextHeader.setTextColor(getSalaryColor(text, imeVisible))
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
            val id = bundle.getInt(LocationFragment.CURRENT_ID)
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

            industryId = state.filterIndustry?.id ?: 0

            setApplyButtonListener(state)

            setLocationsListeners(state)
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

    private fun setApplyButtonListener(state: FilterState) {
        viewBinding.applyFilter.setOnClickListener {
            val area = state.filterArea
            val industry = state.filterIndustry
            val salary = state.salary
            val doNotShowWithSalary = state.doNotShowWithoutSalary

            parentFragmentManager.setFragmentResult(SEND_FILTERS, Bundle().apply {
                putInt(SEND_AREA, area?.id ?: 0)
                putInt(SEND_INDUSTRY, industry?.id ?: 0)
                putLong(SEND_SALARY, salary ?: 0L)
                putBoolean(SEND_SALARY_CHECKBOX, doNotShowWithSalary)
            })

            findNavController().popBackStack()
        }
    }

    private fun setLocationsListeners(state: FilterState) {
        with(viewBinding) {
            industryValue.setOnClickListener {
                parentFragmentManager.setFragmentResult(
                    SEND_INDUSTRY_ID,
                    Bundle().apply {
                        putInt(SEND_INDUSTRY_ID_ARG, state.filterIndustry?.id ?: 0)
                    }
                )
                findNavController().navigate(R.id.action_filterFragment_to_industryFragment)
            }

            workPlaceValue.setOnClickListener {
                if (state.filterArea?.parentName != null) {
                    parentFragmentManager.setFragmentResult(
                        LocationFragment.GET_COUNTRY,
                        Bundle().apply {
                            putString(LocationFragment.CURRENT_COUNTRY, state.filterArea.parentName)
                            putInt(LocationFragment.CURRENT_COUNTRY_ID, state.filterArea.id)
                        }
                    )
                } else {
                    parentFragmentManager.setFragmentResult(
                        LocationFragment.GET_REGION,
                        Bundle().apply {
                            putString(LocationFragment.CURRENT_REGION, state.filterArea?.name)
                            putInt(LocationFragment.CURRENT_REGION_ID, state.filterArea?.id ?: 0)
                        }
                    )
                }
                findNavController().navigate(R.id.action_filterFragment_to_locationFragment)
            }
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

    private fun getSalaryColor(text: CharSequence?, imeVisible: Boolean): Int {
        return if (text.isNullOrEmpty()) {
            MaterialColors.getColor(
                viewBinding.salaryEditTextHeader,
                com.google.android.material.R.attr.colorOnContainer
            )
        } else if (imeVisible) {
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
        // с экрана выбора работы
        private const val LOCATION = "LOCATION"
        private const val COUNTRY = "COUNTRY"
        private const val REGION = "REGION"

        // на экран отраслей
        const val SEND_INDUSTRY_ID = "SEND_INDUSTRY_ID"
        const val SEND_INDUSTRY_ID_ARG = "SEND_INDUSTRY_ID_ARG"

        // на экран поиска
        const val SEND_FILTERS = "SEND_FILTERS"
        const val SEND_AREA = "SEND_AREA"
        const val SEND_INDUSTRY = "SEND_INDUSTRY"
        const val SEND_SALARY = "SEND_SALARY"
        const val SEND_SALARY_CHECKBOX = "SEND_SALARY_CHECKBOX"
    }
}
