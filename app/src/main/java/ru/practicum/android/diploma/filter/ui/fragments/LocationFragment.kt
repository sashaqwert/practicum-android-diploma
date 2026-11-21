package ru.practicum.android.diploma.filter.ui.fragments

import android.content.res.ColorStateList
import android.os.Bundle
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
    private var countryName: String? = null
    private var regionId: Int? = null
    private var regionName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val selection = savedInstanceState ?: arguments
        countryId = selection?.getInt(CURRENT_COUNTRY_ID)?.takeIf { it != 0 }
        countryName = selection?.getString(CURRENT_COUNTRY)
        regionId = selection?.getInt(CURRENT_REGION_ID)?.takeIf { it != 0 }
        regionName = selection?.getString(CURRENT_REGION)
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
            setCountry(null, null)
        }
        viewBinding.chosenRegion.setOnClickListener {
            findNavController().navigate(
                R.id.action_locationFragment_to_regionFragment,
                Bundle().apply { putInt(CURRENT_COUNTRY_ID, countryId ?: 0) }
            )
        }
        viewBinding.regionClear.setOnClickListener {
            regionId = null
            regionName = null
            renderSelection()
        }
        setWorkplaceChooseListener()
        viewBinding.fromWorkPlace.setOnClickListener {
            findNavController().navigateUp()
        }

        setLocationsListeners()
        renderSelection()
    }

    private fun setWorkplaceChooseListener() {
        viewBinding.workplaceChoseButton.setOnClickListener {
            val resultToFilter = selectionBundle()
            parentFragmentManager.setFragmentResult(LOCATION, resultToFilter)
            findNavController().popBackStack(R.id.filterFragment, false)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putAll(selectionBundle())
    }

    private fun selectionBundle(): Bundle = Bundle().apply {
        putInt(CURRENT_COUNTRY_ID, countryId ?: 0)
        putString(CURRENT_COUNTRY, countryName)
        putInt(CURRENT_REGION_ID, regionId ?: 0)
        putString(CURRENT_REGION, regionName)
    }

    private fun getCountry() {
        parentFragmentManager.setFragmentResultListener(GET_COUNTRY, viewLifecycleOwner) { _, result ->
            val id = result.getInt(CURRENT_COUNTRY_ID)
            setCountry(id.takeIf { it != 0 }, result.getString(CURRENT_COUNTRY))
        }
    }

    private fun getRegion() {
        parentFragmentManager.setFragmentResultListener(GET_REGION, viewLifecycleOwner) { _, result ->
            regionId = result.getInt(CURRENT_REGION_ID).takeIf { it != 0 }
            regionName = result.getString(CURRENT_REGION)
            if (countryId == null) {
                countryId = result.getInt(CURRENT_COUNTRY_ID).takeIf { it != 0 }
                countryName = result.getString(CURRENT_COUNTRY)
            }
            renderSelection()
        }
    }

    private fun setCountry(id: Int?, name: String?) {
        if (countryId != id) {
            regionId = null
            regionName = null
        }
        countryId = id
        countryName = name
        renderSelection()
    }

    private fun renderSelection() = with(viewBinding) {
        chosenCountry.setText(countryName)
        chosenRegion.setText(regionName)
        countryClear.isVisible = countryId != null
        regionClear.isVisible = regionId != null
        workplaceChoseButton.isVisible = countryId != null || regionId != null
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
    }

    companion object {
        const val LOCATION = "LOCATION"
        const val GET_COUNTRY = "GET_COUNTRY"
        const val CURRENT_COUNTRY = "CURRENT_COUNTRY"
        const val CURRENT_COUNTRY_ID = "CURRENT_COUNTRY_ID"
        const val CURRENT_REGION_ID = "CURRENT_REGION_ID"
        const val GET_REGION = "GET_REGION"
        const val CURRENT_REGION = "CURRENT_REGION"
    }
}
