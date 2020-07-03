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

    private val _noteResponse = MutableLiveData<LoadingNoteStatus>()
    val noteResponse: LiveData<LoadingNoteStatus>
        get() = _noteResponse


    private val errorHandling = CoroutineExceptionHandler { _, _ ->
        _noteResponse.value = LoadingNoteStatus.OnError("Error while loading notes")
    }

    fun observeNotes(status: Int) = noteRepo.getNotes(status)

    fun getNotes(status: Int) = viewModelScope.launch(errorHandling) {

        _noteResponse.value = LoadingNoteStatus.OnLoading

       _noteResponse.value = LoadingNoteStatus.OnLoading

    }

}


sealed class LoadingNoteStatus() {
    object OnLoading : LoadingNoteStatus()
    data class OnSuccess(val notes: List<Note>) : LoadingNoteStatus()
    data class OnError(val errorMessage: String) : LoadingNoteStatus()
}