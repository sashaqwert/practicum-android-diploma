package ru.practicum.android.diploma.favorite.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.R
import ru.practicum.android.diploma.common.ui.forrecyclerview.VacancyAdapter
import ru.practicum.android.diploma.databinding.FragmentFavoriteBinding
import ru.practicum.android.diploma.details.ui.fragment.DetailsFragment
import ru.practicum.android.diploma.favorite.presentation.state.FavoriteState
import ru.practicum.android.diploma.favorite.presentation.viewmodel.FavoriteViewModel

class FavoriteFragment : Fragment() {
    private var _viewBinding: FragmentFavoriteBinding? = null
    private val viewBinding get() = _viewBinding!!
    private val viewModel by viewModel<FavoriteViewModel>()

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

        viewModel.fillData()
        val adapter = VacancyAdapter(emptyList()) { vacancy ->
            findNavController().navigate(
                R.id.action_favoriteFragment_to_detailsFragment,
                DetailsFragment.putId(vacancy.id)
            )
        }

        viewBinding.favoriteList.adapter = adapter
        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is FavoriteState.Content -> {
                    adapter.updateList(state.vacancies)
                    viewBinding.favoriteList.isVisible = true
                    viewBinding.favoritePlaceholderImage.isVisible = false
                    viewBinding.favoritePlaceholder.isVisible = false
                    viewBinding.favoriteLoading.isVisible = false
                }
                FavoriteState.Empty -> showPlaceholder(R.drawable.img_favorite_empty, R.string.favorite_list_empty)
                FavoriteState.Error -> showPlaceholder(R.drawable.img_error_search_state_328, R.string.vacancy_error)
                FavoriteState.Loading -> {
                    viewBinding.favoriteLoading.isVisible = true
                    viewBinding.favoriteList.isVisible = false
                    viewBinding.favoritePlaceholderImage.isVisible = false
                    viewBinding.favoritePlaceholder.isVisible = false
                }
            }
        }

    }

    private fun renderPlaceholder(state: FavoriteState) {

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
}
