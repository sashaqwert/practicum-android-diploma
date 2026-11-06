package ru.practicum.android.diploma.common.di.data

import org.koin.dsl.module
import ru.practicum.android.diploma.common.data.converters.Converters

val commonDataModuleDI = module {

    single {
        Converters()
    }

}
