package ru.practicum.android.diploma

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin
import ru.practicum.android.diploma.details.di.data.detailsDataModule
import ru.practicum.android.diploma.details.di.domain.detailsDomainModule
import ru.practicum.android.diploma.details.di.ui.detailsViewModelModule
import ru.practicum.android.diploma.favorite.di.data.favoriteDataModuleDI
import ru.practicum.android.diploma.favorite.di.domain.favoriteDomainModuleDI
import ru.practicum.android.diploma.favorite.di.ui.favoriteViewModelModuleDI
import ru.practicum.android.diploma.filter.di.data.filterDataModule
import ru.practicum.android.diploma.filter.di.ui.filterViewModelModule
import ru.practicum.android.diploma.search.di.data.searchDataModule
import ru.practicum.android.diploma.search.di.domain.searchDomainModule
import ru.practicum.android.diploma.search.di.ui.searchViewModelModule

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(detailsDataModule, detailsDomainModule, detailsViewModelModule,
                favoriteDataModuleDI, favoriteDomainModuleDI, favoriteViewModelModuleDI,
                filterDataModule, filterDataModule, filterViewModelModule,
                searchDataModule, searchDomainModule, searchViewModelModule
            )
        }
    }
}
