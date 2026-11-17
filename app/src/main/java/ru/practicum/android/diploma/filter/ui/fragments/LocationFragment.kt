package ru.practicum.android.diploma.filter.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentLocationBinding

/**
 * A simple [Fragment] subclass.
 * Use the [LocationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
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

        viewBinding.chosenCountryLayout.setEndIconOnClickListener {
            findNavController().navigate(R.id.action_locationFragment_to_countryFragment)
        }
        viewBinding.countryClear.setOnClickListener {
            viewBinding.chosenCountry.text = null
        }
        viewBinding.chosenRegionLayout.setEndIconOnClickListener {
            findNavController().navigate(R.id.action_locationFragment_to_regionFragment)
        }
        viewBinding.regionClear.setOnClickListener {
            viewBinding.chosenRegion.text = null
        }
        viewBinding.workplaceChoseButton.setOnClickListener {
            findNavController().popBackStack(R.id.filterFragment, false)
        }
        viewBinding.fromWorkPlace.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment LocationFragment.
         */
        //вот тут как вариант можно создавать экран с последним выбором пользователя
        @JvmStatic
        fun newInstance() =
            LocationFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
