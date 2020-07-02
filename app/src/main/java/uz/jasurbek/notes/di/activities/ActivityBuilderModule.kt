package uz.jasurbek.notes.di.activities

import dagger.Module
import dagger.android.ContributesAndroidInjector
import uz.jasurbek.notes.di.fragments.NoteListFragmentModule
import uz.jasurbek.notes.ui.MainActivity
import uz.jasurbek.notes.ui.list.NotesListFragment


@Module
abstract class ActivityBuilderModule {

    @ContributesAndroidInjector
    abstract fun provideDependenciesToActivity(): MainActivity


    @ContributesAndroidInjector(modules = [NoteListFragmentModule::class])
    abstract fun provideDependenciesToFragment(): NotesListFragment

}