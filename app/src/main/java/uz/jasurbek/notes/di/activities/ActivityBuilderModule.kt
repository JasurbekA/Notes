package uz.jasurbek.notes.di.activities

import dagger.Module
import dagger.android.ContributesAndroidInjector
import uz.jasurbek.notes.di.fragments.NoteListFragmentModule
import uz.jasurbek.notes.di.fragments.NoteOperationsFragmentModule
import uz.jasurbek.notes.ui.MainActivity
import uz.jasurbek.notes.ui.list.NotesListFragment
import uz.jasurbek.notes.ui.operation.NoteOperationsFragment


@Module
abstract class ActivityBuilderModule {

    @ContributesAndroidInjector
    abstract fun provideDependenciesToActivity(): MainActivity


    @ContributesAndroidInjector(modules = [NoteListFragmentModule::class])
    abstract fun provideDependenciesToListFragment(): NotesListFragment

    @ContributesAndroidInjector(modules = [NoteOperationsFragmentModule::class])
    abstract fun provideDependenciesToOperationsFragment() : NoteOperationsFragment

}