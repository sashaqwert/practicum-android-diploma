package ru.practicum.android.diploma.filter.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentLocationBinding
class LocationFragment : Fragment() {
    private var _viewBinding: FragmentLocationBinding? = null
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
            findNavController().navigate(R.id.action_locationFragment_to_regionFragment)
        }
        viewBinding.regionClear.setOnClickListener {
            viewBinding.chosenRegion.text = null
            viewBinding.regionClear.isVisible = !viewBinding.chosenRegion.text.isNullOrEmpty()
        }
        viewBinding.workplaceChoseButton.setOnClickListener {
            val resultToFilter = Bundle().apply {
                putString(COUNTRY, viewBinding.chosenCountry.text.toString())
                putString(REGION, viewBinding.chosenRegion.text.toString())
            }
            parentFragmentManager.setFragmentResult(LOCATION, resultToFilter)
            findNavController().popBackStack(R.id.filterFragment, false)
        }
        viewBinding.fromWorkPlace.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun getCountry() {
        parentFragmentManager.setFragmentResultListener(GET_COUNTRY, viewLifecycleOwner) { requestKey, result ->
            val country = result.getString(CURRENT_COUNTRY)
            val editable = Editable.Factory.getInstance().newEditable(country)
            viewBinding.chosenCountry.text = editable
            viewBinding.countryClear.isVisible = !viewBinding.chosenCountry.text.isNullOrEmpty()
        }
    }

    private fun getRegion() {
        parentFragmentManager.setFragmentResultListener(GET_REGION, viewLifecycleOwner) { requestKey, result ->
            val region = result.getString(CURRENT_REGION)
            val editable = Editable.Factory.getInstance().newEditable(region)
            viewBinding.chosenRegion.text = editable
            viewBinding.regionClear.isVisible = !viewBinding.chosenRegion.text.isNullOrEmpty()
            if (viewBinding.chosenCountry.text.isNullOrEmpty()) {
                val country = result.getString(REGION_COUNTRY)
                val editable = Editable.Factory.getInstance().newEditable(country)
                viewBinding.chosenCountry.text = editable
            }
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
        private const val GET_COUNTRY = "GET_COUNTRY"
        private const val CURRENT_COUNTRY = "CURRENT_COUNTRY"
        private const val GET_REGION = "GET_REGION"
        private const val CURRENT_REGION = "CURRENT_REGION"
        private const val REGION_COUNTRY = "REGION_COUNTRY"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment LocationFragment.
         */
       /* @JvmStatic
        fun newInstance() =
            LocationFragment().apply {
                arguments = Bundle().apply {
                }
            }

        */
    }
}
