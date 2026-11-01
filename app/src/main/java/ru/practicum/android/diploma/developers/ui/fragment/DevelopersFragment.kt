package ru.practicum.android.diploma.developers.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ru.practicum.android.diploma.R


/**
 * A simple [Fragment] subclass.
 * Use the [DevelopersFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DevelopersFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_developers, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment DevelopersFragment.
         */
        @JvmStatic
        fun newInstance() =
            DevelopersFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
