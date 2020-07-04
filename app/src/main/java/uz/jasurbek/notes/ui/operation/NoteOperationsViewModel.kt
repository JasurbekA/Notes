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
import uz.jasurbek.notes.util.Event
import uz.jasurbek.notes.util.Util
import java.util.*
import javax.inject.Inject

class NoteOperationsViewModel @Inject constructor(
    private val repo: NoteOperationRepo
) : ViewModel() {

    private var _noteResponse = MutableLiveData<LoadingNoteStatus>()
    val noteResponse: LiveData<LoadingNoteStatus>
        get() = _noteResponse

    private var _noteSaveState = MutableLiveData<Event<SaveNoteState>>()
    val noteSaveState: LiveData<Event<SaveNoteState>>
        get() = _noteSaveState


    private val savingNoteErrorHandler = CoroutineExceptionHandler { _, _ ->
        _noteSaveState.value = Event(SaveNoteState.OnError("Note has not been saved. Error"))
    }


    private val errorHandling = CoroutineExceptionHandler { _, _ ->
        _noteResponse.value = LoadingNoteStatus.OnError("Error while loading notes")
    }

    fun createImageFile(activity: FragmentActivity?) = repo.createTemporaryImageFile(activity)
    /*
    * viewModelScope not working well here, since we close the fragment viewModel also destroyed
    * But we have to be sure saving note should be completed
    * Using different IO coroutine will solve the issue that may arise
    * */

    /*
    * Launch execute sequentially, that is why is ok to put success after function call
    * Eny exception occurred is handled by parent coroutine
    * */
    private fun updateNote(note: Note) =
        CoroutineScope(Dispatchers.IO).launch {
            repo.updateNote(note)
            savingNoteSuccess()
        }

    private fun insertNote(note: Note) =
        CoroutineScope(Dispatchers.IO).launch {
            repo.insertNote(note)
            savingNoteSuccess()
        }


    private fun deleteNote(note: Note) =
        CoroutineScope(Dispatchers.IO).launch { repo.deleteNote(note) }

    fun deleteCurrentNote(context: Context, note: Note) =
        CoroutineScope(Dispatchers.IO).launch {
            repo.deleteImage(note.imagePath)
            repo.cancelAlarm(context, note)
            deleteNote(note)
        }


    fun saveEditedNote(context: Context, note: Note, imageUri: Uri?) =
        CoroutineScope(Dispatchers.IO).launch(savingNoteErrorHandler) {
            //Alarm setting: Warning need to be optimized
            repo.cancelAlarm(context, note)
            repo.startAlarm(context, note)
            //Image uri not null means image has been updated
            imageUri?.let { repo.deleteImage(note.imagePath) } // delete previous image
            saveNote(context, note, imageUri, ::updateNote)
        }

    fun addNote(context: Context, note: Note, imageUri: Uri?) =
        CoroutineScope(Dispatchers.IO).launch(savingNoteErrorHandler) {
            repo.startAlarm(context, note) // will set alarm if only reminder is set
            saveNote(context, note, imageUri, ::insertNote)
        }

    private fun saveNote(
        context: Context, note: Note,
        imageUri: Uri?, saveFunction: (note: Note) -> Job
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

    private suspend fun savingNoteSuccess() = withContext(Dispatchers.Main) {
        _noteSaveState.value = Event(SaveNoteState.OnSuccess)
    }

    fun mapStatusNameToStatus(name: String) = repo.mapStatusNameToStatus(name)
    fun mapCalendarToStringDate(calendar: Calendar?) = repo.mapCalendarToStringDate(calendar)
    fun mapStatusToString(status: Int) = repo.mapStatusToString(status)
    fun isDueTimeAllowed(dueDate: String) = repo.isDueTimeAllowed(dueDate)
    fun getReminderDate(dueDate: String, hourOffset: Int) =
        repo.getReminderDate(dueDate, hourOffset)

    fun isReminderAllowed(dueDate: String, hourOffset: Int) =
        repo.isReminderAllowed(dueDate, hourOffset)

    fun calculateReminderDifference(dueDate: String, reminderDate: String) =
        repo.calculateReminderDifference(dueDate, reminderDate)

}


