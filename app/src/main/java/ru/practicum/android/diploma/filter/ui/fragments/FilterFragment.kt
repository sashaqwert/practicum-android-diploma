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

        observeState()
        setListeners()
        setFragmentResultListeners()
        setApplyButtonListener()
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
                salaryClear.isVisible = !text.isNullOrEmpty()
                viewModel.setSalary(text?.toString()?.toLongOrNull())
                val imeVisible = getRootWindowInsets(root)
                    ?.isVisible(WindowInsetsCompat.Type.ime()) == true

                salaryEditTextHeader.setTextColor(getSalaryColor(text, imeVisible))
            }
            salaryClear.setOnClickListener { salaryEditText.text.clear() }

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

        parentFragmentManager.setFragmentResultListener(LocationFragment.LOCATION, viewLifecycleOwner) { _, bundle ->
            val countryId = bundle.getInt(LocationFragment.CURRENT_COUNTRY_ID)
            val regionId = bundle.getInt(LocationFragment.CURRENT_REGION_ID)
            if (countryId != 0 || regionId != 0) {
                viewModel.setFilterArea(
                    id = regionId.takeIf { it != 0 } ?: countryId,
                    name = bundle.getString(LocationFragment.CURRENT_REGION).orEmpty(),
                    parentName = bundle.getString(LocationFragment.CURRENT_COUNTRY),
                    countryId = countryId.takeIf { it != 0 }
                )
            }
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

            setLocationsListeners(state)
        }
    }

    private fun setLocationName(state: FilterState) {
        val countryName = state.filterArea?.parentName ?: ""

        if (countryName.isNotEmpty() && countryName.isNotBlank()) {
            val text = getLocationName(state, countryName)
            viewBinding.workPlaceValue.setText(text)
        } else {
            viewBinding.workPlaceValue.setText(state.filterArea?.name?.takeIf { it.isNotBlank() })
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
            if (salaryEditText.text.toString().toLongOrNull() != state.salary) {
                salaryEditText.setText(state.salary?.toString().orEmpty())
            }
            salaryClear.isVisible = !salaryEditText.text.isNullOrEmpty()
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
                workPlaceIcon.setOnClickListener { workPlaceValue.performClick() }
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
                industryIcon.setOnClickListener { industryValue.performClick() }
                R.drawable.ic_select
            }

            industryIcon.setImageResource(industryDrawable)
        }
    }

    private fun setApplyButtonListener() {
        viewBinding.applyFilter.setOnClickListener {
            parentFragmentManager.setFragmentResult(SEND_FILTERS, Bundle.EMPTY)
            findNavController().popBackStack()
        }
    }

    private fun setLocationsListeners(state: FilterState) {
        with(viewBinding) {
            industryValue.setOnClickListener {
                findNavController().navigate(
                    R.id.action_filterFragment_to_industryFragment,
                    Bundle().apply {
                        putInt(SEND_INDUSTRY_ID_ARG, industryId)
                    }
                )
            }

            workPlaceValue.setOnClickListener {
                val area = state.filterArea
                val regionId = area?.takeIf { it.name.isNotEmpty() }?.id ?: 0
                findNavController().navigate(
                    R.id.action_filterFragment_to_locationFragment,
                    Bundle().apply {
                        putString(LocationFragment.CURRENT_COUNTRY, area?.parentName)
                        putInt(LocationFragment.CURRENT_COUNTRY_ID, area?.countryId ?: 0)
                        putString(LocationFragment.CURRENT_REGION, area?.name)
                        putInt(LocationFragment.CURRENT_REGION_ID, regionId)
                    }
                )
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
            applyFilter.isVisible = viewModel.compareWithDefault()
            cancelButton.isVisible = viewModel.compareWithDefault()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }

    companion object {
        // на экран отраслей
        const val SEND_INDUSTRY_ID_ARG = "SEND_INDUSTRY_ID_ARG"

        // на экран поиска
        const val SEND_FILTERS = "SEND_FILTERS"
    }
}
