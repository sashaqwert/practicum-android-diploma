package ru.practicum.android.diploma.filter.ui.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
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
import ru.practicum.android.diploma.common.domain.models.FilterArea
import ru.practicum.android.diploma.common.domain.models.FilterIndustry
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

        (requireActivity() as AppCompatActivity).setSupportActionBar(viewBinding.toolBar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(true)
        viewBinding.toolBar.setNavigationOnClickListener { findNavController().popBackStack() }

        acceptFiltersFromSearch()

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

        if (countryName.isNotEmpty() && countryName.isNotBlank()) {
            val text = getLocationName(state, countryName)
            viewBinding.workPlaceValue.setText(text)
        } else {
            viewBinding.workPlaceValue.text?.clear()
        }
    }

    private fun getLocationName(state: FilterState, countryName: String): String {
        val regionName = state.filterArea?.name ?: ""
        val regionText = if (regionName.isNotBlank() && regionName.isNotEmpty()) {
            ", $regionName"
        } else {
            ""
        }

        return "$countryName$regionText"
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
            val salaryText = state.salary?.takeIf { it != 0L }?.toString().orEmpty()
            if (salaryEditText.text.toString() != salaryText) {
                salaryEditText.setText(salaryText)
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
            parentFragmentManager.setFragmentResult(SEND_FILTERS, createFilterBundle(state))
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
                            putString(LocationFragment.CURRENT_COUNTRY, state.filterArea.parentName ?: "")
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

    private fun acceptFiltersFromSearch() {
        parentFragmentManager.setFragmentResultListener(RESTORE_FILTERS, viewLifecycleOwner) { _, bundle ->
            viewModel.fillData(readFilterBundle(bundle))
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

        // с или на экран поиска
        const val SEND_FILTERS = "SEND_FILTERS"
        const val RESTORE_FILTERS = "RESTORE_FILTERS"
        private const val SEND_AREA = "SEND_AREA"
        private const val SEND_INDUSTRY = "SEND_INDUSTRY"
        private const val SEND_SALARY = "SEND_SALARY"
        private const val SEND_SALARY_CHECKBOX = "SEND_SALARY_CHECKBOX"
        private const val SEND_COUNTRY_NAME = "SEND_COUNTRY_NAME"
        private const val SEND_REGION_NAME = "SEND_REGION_NAME"
        private const val SEND_INDUSTRY_NAME = "SEND_INDUSTRY_NAME"

        fun createFilterBundle(state: FilterState): Bundle = Bundle().apply {
            putInt(SEND_AREA, state.filterArea?.id ?: 0)
            putString(SEND_COUNTRY_NAME, state.filterArea?.parentName)
            putString(SEND_REGION_NAME, state.filterArea?.name)
            putInt(SEND_INDUSTRY, state.filterIndustry?.id ?: 0)
            putString(SEND_INDUSTRY_NAME, state.filterIndustry?.name)
            putLong(SEND_SALARY, state.salary ?: 0L)
            putBoolean(SEND_SALARY_CHECKBOX, state.doNotShowWithoutSalary)
        }

        fun readFilterBundle(bundle: Bundle): FilterState {
            val area = bundle.getInt(SEND_AREA).takeIf { it != 0 }?.let { id ->
                FilterArea(
                    id = id,
                    name = bundle.getString(SEND_REGION_NAME).orEmpty(),
                    parentId = null,
                    parentName = bundle.getString(SEND_COUNTRY_NAME)
                )
            }
            val industry = bundle.getInt(SEND_INDUSTRY).takeIf { it != 0 }?.let { id ->
                FilterIndustry(id, bundle.getString(SEND_INDUSTRY_NAME).orEmpty())
            }
            return FilterState(
                filterArea = area,
                filterIndustry = industry,
                salary = bundle.getLong(SEND_SALARY).takeIf { it != 0L },
                doNotShowWithoutSalary = bundle.getBoolean(SEND_SALARY_CHECKBOX)
            )
        }
    }
}
