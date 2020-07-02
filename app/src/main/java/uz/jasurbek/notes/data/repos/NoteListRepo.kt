package uz.jasurbek.notes.data.repos

import uz.jasurbek.notes.data.local.NoteDao
import uz.jasurbek.notes.data.model.Note
import uz.jasurbek.notes.data.model.NoteStatus
import javax.inject.Inject

class NoteListRepo @Inject constructor(private val noteDao: NoteDao) {

    private suspend fun getAllNotes(): List<Note> = noteDao.allNotes()
    private suspend fun filterNotes(status: Int): List<Note> = noteDao.filterNotes(status)

    suspend fun getNotes(status: Int) : List<Note> =
        if (status == NoteStatus.NOTES_STATUS_DEFAULT) getAllNotes()
        else filterNotes(status)

}