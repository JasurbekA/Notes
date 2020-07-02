package uz.jasurbek.notes.di.fragments

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import uz.jasurbek.notes.date.local.NoteDatabase
import uz.jasurbek.notes.di.vm.ViewModelKey
import uz.jasurbek.notes.ui.list.NotesListViewModel
import javax.inject.Singleton

@Module
abstract class NoteListFragmentModule {

    companion object {
        @JvmStatic
        @Provides
        fun provideNoteDao(noteDatabase: NoteDatabase) = noteDatabase.noteDao()
    }


    @Binds
    @IntoMap
    @ViewModelKey(NotesListViewModel::class)
    abstract fun bindMainViewModel(viewModel: NotesListViewModel): ViewModel

}