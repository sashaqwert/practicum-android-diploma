package ru.practicum.android.diploma.favorite.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.databinding.FragmentFavoriteBinding
import ru.practicum.android.diploma.favorite.presentation.state.FavoriteState
import ru.practicum.android.diploma.favorite.presentation.viewmodel.FavoriteViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [FavoriteFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class FavoriteFragment : Fragment() {
    private var _viewBinding: FragmentFavoriteBinding? = null
    private val viewBinding get() = _viewBinding!!
    private val viewModel by viewModel<FavoriteViewModel>()

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
        _viewBinding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            renderPlaceholder(state)
        }
    }

    private fun renderPlaceholder(state: FavoriteState) {
        when (state) {
            FavoriteState.Empty -> showPlaceholder(R.drawable.img_favorite_empty, R.string.favorite_list_empty)
            FavoriteState.Error -> showPlaceholder(R.drawable.img_error_search_state_328, R.string.vacancy_error)
            FavoriteState.Loading, is FavoriteState.Content -> viewBinding.favoritePlaceholder.isVisible = false
        }
    }

    private fun showPlaceholder(@DrawableRes image: Int, @StringRes message: Int) {
        with(viewBinding) {
            favoritePlaceholderImage.setImageResource(image)
            favoritePlaceholderMessage.setText(message)
            favoritePlaceholder.isVisible = true
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
         * @return A new instance of fragment FavoriteFragmant.
         */
        @JvmStatic
        fun newInstance() =
            FavoriteFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
