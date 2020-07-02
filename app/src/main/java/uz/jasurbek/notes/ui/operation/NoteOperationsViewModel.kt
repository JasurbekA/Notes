package uz.jasurbek.notes.ui.operation

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import uz.jasurbek.notes.data.model.Note
import uz.jasurbek.notes.data.repos.NoteOperationRepo
import uz.jasurbek.notes.ui.list.LoadingNoteStatus
import javax.inject.Inject

class NoteOperationsViewModel @Inject constructor(
    private val repo: NoteOperationRepo
) : ViewModel() {

    private var _noteResponse = MutableLiveData<LoadingNoteStatus>()
    val noteResponse: LiveData<LoadingNoteStatus>
        get() = _noteResponse


    private val errorHandling = CoroutineExceptionHandler { _, _ ->
        _noteResponse.value = LoadingNoteStatus.OnError("Error while loading notes")
    }

    fun createImageFile(activity: FragmentActivity?) = repo.createTemporaryImageFile(activity)

    fun updateNote(note: Note) = viewModelScope.launch { repo.updateNote(note) }
    fun insertNote(note: Note) = viewModelScope.launch { repo.insertNote(note) }
    fun deleteNote(note: Note) = viewModelScope.launch { repo.deleteNote(note) }
    fun getNote(noteID: String) = viewModelScope.launch(errorHandling) {
        _noteResponse.value = LoadingNoteStatus.OnLoading
        val note = repo.getNote(noteID)
        _noteResponse.value = LoadingNoteStatus.OnSuccess(arrayListOf(note))
    }


}


