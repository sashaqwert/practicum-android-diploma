package ru.practicum.android.diploma.details.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.practicum.android.diploma.databinding.FragmentDetailsBinding
import ru.practicum.android.diploma.details.domain.model.VacancyDetail
import ru.practicum.android.diploma.details.presentation.state.DetailsState
import ru.practicum.android.diploma.details.presentation.view_model.DetailsViewModel

class DetailsFragment : Fragment() {

    private var vacancyId: String? = null
    private var _viewBinding: FragmentDetailsBinding? = null
    private val viewBinding get() = _viewBinding!!
    private val viewModel by viewModel<DetailsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            vacancyId = it.getString(CURRENT_VACANCY_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _viewBinding = FragmentDetailsBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setID(vacancyId ?: "0")

        viewModel.observeState().observe(viewLifecycleOwner) { state ->
            when (state) {
                is DetailsState.Content -> setContent(state.vacancyDetail)
                is DetailsState.Error -> {
                    setLayoutVisibilites(
                        isVacancyLayoutVisible = false,
                        isErrorLayoutVisible = true,
                        isLoadingBarVisible = true
                    )

                    setError(state.code)
                }
                DetailsState.Loading -> {
                    setLayoutVisibilites(
                        isVacancyLayoutVisible = false,
                        isErrorLayoutVisible = false,
                        isLoadingBarVisible = true
                    )
                }
            }
        }
    }

    private fun setLayoutVisibilites(
        isVacancyLayoutVisible: Boolean,
        isErrorLayoutVisible: Boolean,
        isLoadingBarVisible: Boolean
    ) {
        viewBinding.apply {
            vacancyLayout.visibility = if (isVacancyLayoutVisible) View.VISIBLE else View.GONE
            errorLayout.visibility = if (isErrorLayoutVisible) View.VISIBLE else View.GONE
            loadingBar.visibility = if (isLoadingBarVisible) View.VISIBLE else View.GONE
        }
    }

    private fun setContent(vacancyDetail: VacancyDetail) {

    }

    private fun setError(code: Int) {

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }

    companion object {
        private const val CURRENT_VACANCY_ID = "CURRENT_VACANCY_ID"

        fun putId(id: String): Bundle {
            return Bundle().apply {
                putString(CURRENT_VACANCY_ID, id)
            }
        }

        private const val NO_DETAIL_RESPONSE = -2
        private const val NOT_FOUND = 404
        private const val UNKNOWN_ERROR = -3
    }
}
