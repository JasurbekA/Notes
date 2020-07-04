package uz.jasurbek.notes.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import uz.jasurbek.notes.data.model.Note
import uz.jasurbek.notes.data.repos.NoteListRepo
import javax.inject.Inject

class NotesListViewModel @Inject constructor(private val noteRepo: NoteListRepo) : ViewModel() {

    private val _filterText = MutableLiveData<String>()
    val filterText: LiveData<String>
        get() = _filterText


    fun observeNotes(status: Int) : LiveData<List<Note>>{
        _filterText.value = noteRepo.mapStatusToText(status)
        return noteRepo.getNotes(status)
    }

    fun mapFilterOptionsToStatus(filter: String) = noteRepo.mapFilterOptionsToStatus(filter)

}


sealed class LoadingNoteStatus() {
    object OnLoading : LoadingNoteStatus()
    data class OnSuccess(val notes: List<Note>) : LoadingNoteStatus()
    data class OnError(val errorMessage: String) : LoadingNoteStatus()
}