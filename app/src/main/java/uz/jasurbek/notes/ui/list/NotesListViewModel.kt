package uz.jasurbek.notes.ui.list

import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import uz.jasurbek.notes.data.repos.NoteListRepo
import uz.jasurbek.notes.data.model.Note
import javax.inject.Inject

class NotesListViewModel @Inject constructor(private val noteRepo: NoteListRepo) : ViewModel() {

    private var _noteResponse = MutableLiveData<LoadingNoteStatus>()
    val noteResponse: LiveData<LoadingNoteStatus>
        get() = _noteResponse


    private val errorHandling = CoroutineExceptionHandler { _, _ ->
        _noteResponse.value = LoadingNoteStatus.OnError("Error while loading notes")
    }

    fun getNotes(status: Int) = viewModelScope.launch {
        _noteResponse = liveData(errorHandling) {
            emit(LoadingNoteStatus.OnLoading)
            emit(LoadingNoteStatus.OnSuccess(noteRepo.getNotes(status)))
        } as MutableLiveData<LoadingNoteStatus>
    }

}


sealed class LoadingNoteStatus() {
    object OnLoading : LoadingNoteStatus()
    data class OnSuccess(val notes: List<Note>) : LoadingNoteStatus()
    data class OnError(val errorMessage: String) : LoadingNoteStatus()
}