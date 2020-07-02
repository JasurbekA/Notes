package uz.jasurbek.notes.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.CoroutineExceptionHandler
import uz.jasurbek.notes.data.NoteRepo
import uz.jasurbek.notes.data.model.Note
import javax.inject.Inject

class NotesListViewModel @Inject constructor(private val noteRepo: NoteRepo) : ViewModel() {


    private var _noteResponse = MutableLiveData<LoadingNoteStatus>()
    val noteResponse: LiveData<LoadingNoteStatus>
        get() = _noteResponse


    private val errorHandling = CoroutineExceptionHandler { _, _ ->
        _noteResponse.value = LoadingNoteStatus.OnError("Error while loading notes")
    }

    fun getNotes(status: Int) {
        /*Convert suspend result to liveData*/
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