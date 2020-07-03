package uz.jasurbek.notes.ui.operation

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import uz.jasurbek.notes.data.model.Note
import uz.jasurbek.notes.data.repos.NoteOperationRepo
import uz.jasurbek.notes.ui.list.LoadingNoteStatus
import java.io.File
import java.net.URI
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
    private fun insertNote(note: Note) = viewModelScope.launch { repo.insertNote(note) }
    fun deleteNote(note: Note) = viewModelScope.launch { repo.deleteNote(note) }

    fun saveNote(context: Context, note: Note, imageUri: Uri?) =
        CoroutineScope(Dispatchers.IO).launch {
            if (imageUri == null) {
                repo.insertNote(note)
            } else repo.saveImage(context, imageUri) {
                note.imagePath = it
                println("It is ")
                CoroutineScope(Dispatchers.IO).launch {
                    repo.insertNote(note)
                }
            }

        }


    fun getNote(noteID: String) = viewModelScope.launch(errorHandling) {
        _noteResponse.value = LoadingNoteStatus.OnLoading
        val note = repo.getNote(noteID)
        _noteResponse.value = LoadingNoteStatus.OnSuccess(arrayListOf(note))
    }


}


