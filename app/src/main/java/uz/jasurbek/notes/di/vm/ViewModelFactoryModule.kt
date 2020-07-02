package uz.jasurbek.notes.di.vm

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelFactoryModule {
    @Binds
    abstract fun bindViewModuleFactory(providerFactory: ViewModelProviderFactory): ViewModelProvider.Factory
}