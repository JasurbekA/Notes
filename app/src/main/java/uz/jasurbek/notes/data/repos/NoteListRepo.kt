package uz.jasurbek.notes.data.repos

import androidx.lifecycle.LiveData
import uz.jasurbek.notes.data.local.NoteDao
import uz.jasurbek.notes.data.model.Note
import uz.jasurbek.notes.data.model.NoteStatus
import uz.jasurbek.notes.util.Util
import javax.inject.Inject

class NoteListRepo @Inject constructor(private val noteDao: NoteDao) {

    private fun getAllNotes() = noteDao.allNotes()
    private fun filterNotes(status: Int)= noteDao.filterNotes(status)

    fun getNotes(status: Int) : LiveData<List<Note>> =
        if (status == NoteStatus.NOTES_STATUS_DEFAULT) getAllNotes()
        else filterNotes(status)

    fun mapStatusToText(status: Int) : String =
        when (status) {
            NoteStatus.NOTES_STATUS_DEFAULT -> "All notes"
            NoteStatus.NOTES_STATUS_COMPLETED -> "Completed notes"
            NoteStatus.NOTES_STATUS_EXPIRED -> "Expired notes"
            else -> "Active notes"
        }


    fun mapFilterOptionsToStatus(filter : String) = Util.mapFilterOptionsToStatus(filter)
}