package uz.jasurbek.notes.ui.operation

import android.content.Context
import android.net.Uri
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
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
    /*
    * viewModelScope not working well here, since we close the fragment viewModel also destroyed
    * But we have to be sure saving note should be completed
    * Using different IO coroutine will solve the issue that may arise
    * */


    private fun updateNote(note: Note) =
        CoroutineScope(Dispatchers.IO).launch { repo.updateNote(note) }

    private fun insertNote(note: Note) =
        CoroutineScope(Dispatchers.IO).launch { repo.insertNote(note) }

    private fun deleteNote(note: Note) =
        CoroutineScope(Dispatchers.IO).launch { repo.deleteNote(note) }

    fun deleteCurrentNote(note: Note) =
        CoroutineScope(Dispatchers.IO).launch {
            repo.deleteImage(note.imagePath)
            deleteNote(note)
        }


    fun saveEditedNote(context: Context, note: Note, imageUri: Uri?) =
        CoroutineScope(Dispatchers.IO).launch {
            //Image uri not null means image has been updated
            imageUri?.let { repo.deleteImage(note.imagePath) } // delete previous image
            saveNote(context, note, imageUri, ::updateNote)
        }

    fun addNote(context: Context, note: Note, imageUri: Uri?) =
        CoroutineScope(Dispatchers.IO).launch {
            saveNote(context, note, imageUri, ::insertNote)
        }

    private fun saveNote(
        context: Context,
        note: Note,
        imageUri: Uri?,
        saveFunction: (note: Note) -> Job
    ) {
        if (imageUri == null) saveFunction(note) //note without attached image
        else repo.saveImage(context, imageUri) { attachedImageSavedPath ->
            note.imagePath = attachedImageSavedPath
            saveFunction(note)
        }
    }


    fun getNote(noteID: String) = viewModelScope.launch(errorHandling) {
        _noteResponse.value = LoadingNoteStatus.OnLoading
        val note = repo.getNote(noteID)
        _noteResponse.value = LoadingNoteStatus.OnSuccess(arrayListOf(note))
    }


}


