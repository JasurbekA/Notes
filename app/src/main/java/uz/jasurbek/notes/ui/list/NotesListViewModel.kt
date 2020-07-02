package uz.jasurbek.notes.ui.list

import androidx.lifecycle.ViewModel
import uz.jasurbek.notes.date.NoteRepo
import javax.inject.Inject

class NotesListViewModel @Inject constructor(private val noteRepo: NoteRepo) : ViewModel()