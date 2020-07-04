package uz.jasurbek.notes.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uz.jasurbek.notes.data.model.Note
import uz.jasurbek.notes.data.repos.NoteListRepo
import javax.inject.Inject

class NotesListViewModel @Inject constructor(private val noteRepo: NoteListRepo) : ViewModel() {


    fun observeNotes(status: Int) = noteRepo.getNotes(status)

    fun mapFilterOptionsToStatus(filter : String) = noteRepo.mapFilterOptionsToStatus(filter)

}


sealed class LoadingNoteStatus() {
    object OnLoading : LoadingNoteStatus()
    data class OnSuccess(val notes: List<Note>) : LoadingNoteStatus()
    data class OnError(val errorMessage: String) : LoadingNoteStatus()
}