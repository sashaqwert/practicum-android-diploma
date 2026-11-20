package ru.practicum.android.diploma.filter.ui.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.color.MaterialColors
import com.google.android.material.textfield.TextInputLayout
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentLocationBinding

class LocationFragment : Fragment() {
    private var _viewBinding: FragmentLocationBinding? = null
    private val viewBinding get() = _viewBinding!!
    private var countryId: Int? = null
    private var regionId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _viewBinding = FragmentLocationBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getCountry()
        getRegion()

        viewBinding.chosenCountry.setOnClickListener {
            findNavController().navigate(R.id.action_locationFragment_to_countryFragment)
        }
        viewBinding.countryClear.setOnClickListener {
            viewBinding.chosenCountry.text = null
            viewBinding.countryClear.isVisible = !viewBinding.chosenCountry.text.isNullOrEmpty()
        }
        viewBinding.chosenRegion.setOnClickListener {
            if (countryId != null && countryId != 0) {
                val result = Bundle().apply {
                    putInt(SEND_COUNTRY_ID, countryId ?: 0)
                }
                parentFragmentManager.setFragmentResult(SEND_COUNTRY_RESULT, result)
            }
            findNavController().navigate(R.id.action_locationFragment_to_regionFragment)
        }
        viewBinding.regionClear.setOnClickListener {
            regionId = null
            viewBinding.chosenRegion.text = null
            viewBinding.regionClear.isVisible = !viewBinding.chosenRegion.text.isNullOrEmpty()
            viewBinding.workplaceChoseButton.isVisible = false
        }
        setWorkplaceChooseListener()
        viewBinding.fromWorkPlace.setOnClickListener {
            findNavController().navigateUp()
        }

        setLocationsListeners()
    }

    private fun setWorkplaceChooseListener() {
        viewBinding.workplaceChoseButton.isVisible = regionId != null
        viewBinding.workplaceChoseButton.setOnClickListener {
            val resultToFilter = Bundle().apply {
                putString(COUNTRY, viewBinding.chosenCountry.text.toString())
                putInt(CURRENT_ID, regionId ?: countryId ?: 0)
                putString(REGION, viewBinding.chosenRegion.text.toString())
            }
            parentFragmentManager.setFragmentResult(LOCATION, resultToFilter)
            findNavController().popBackStack(R.id.filterFragment, false)
        }
    }

    private fun getCountry() {
        parentFragmentManager.setFragmentResultListener(GET_COUNTRY, viewLifecycleOwner) { requestKey, result ->
            val countryName = result.getString(CURRENT_COUNTRY) ?: ""
            countryId = result.getInt(CURRENT_COUNTRY_ID)
            val editable = Editable.Factory.getInstance().newEditable(countryName)
            viewBinding.chosenCountry.text = editable
            viewBinding.countryClear.isVisible = !viewBinding.chosenCountry.text.isNullOrEmpty()
        }
    }

    private fun getRegion() {
        parentFragmentManager.setFragmentResultListener(GET_REGION, viewLifecycleOwner) { requestKey, result ->
            val region = result.getString(CURRENT_REGION) ?: ""
            regionId = result.getInt(CURRENT_REGION_ID).takeIf { it != 0 && region.isNotBlank() }
            val editable = Editable.Factory.getInstance().newEditable(region)
            viewBinding.chosenRegion.text = editable
            viewBinding.regionClear.isVisible = !viewBinding.chosenRegion.text.isNullOrEmpty()
            if (viewBinding.chosenCountry.text.isNullOrEmpty()) {
                val country = result.getString(CURRENT_COUNTRY) ?: ""
                val editable = Editable.Factory.getInstance().newEditable(country)
                viewBinding.chosenCountry.text = editable
            }
            viewBinding.workplaceChoseButton.isVisible = regionId != null
        }
    }

    private fun setLocationsListeners() {
        with(viewBinding) {
            updateHintColor(chosenCountryLayout, chosenCountry.text)
            updateHintColor(chosenRegionLayout, chosenRegion.text)

            chosenCountry.doOnTextChanged { text, _, _, _ ->
                updateHintColor(chosenCountryLayout, text)
            }

            chosenRegion.doOnTextChanged { text, _, _, _ ->
                updateHintColor(chosenRegionLayout, text)
            }
        }
    }

    private fun updateHintColor(layout: TextInputLayout, text: CharSequence?) {
        val color = if (text.isNullOrEmpty()) {
            ContextCompat.getColor(requireContext(), R.color.gray)
        } else {
            MaterialColors.getColor(layout, com.google.android.material.R.attr.colorOnPrimary)
        }
        layout.defaultHintTextColor = ColorStateList.valueOf(color)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
        countryId = null
        regionId = null
    }

    companion object {
        private const val LOCATION = "LOCATION"
        private const val COUNTRY = "COUNTRY"
        private const val REGION = "REGION"
        const val GET_COUNTRY = "GET_COUNTRY"
        const val CURRENT_COUNTRY = "CURRENT_COUNTRY"
        const val CURRENT_COUNTRY_ID = "CURRENT_COUNTRY_ID"
        const val CURRENT_ID = "CURRENT_ID"
        const val CURRENT_REGION_ID = "CURRENT_REGION_ID"
        const val GET_REGION = "GET_REGION"
        const val CURRENT_REGION = "CURRENT_REGION"
        private const val SEND_COUNTRY_ID = "SEND_COUNTRY_ID"
        private const val SEND_COUNTRY_RESULT = "SEND_COUNTRY_RESULT"
    }
}
