package uz.jasurbek.notes.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import uz.jasurbek.notes.data.model.Note

@Dao
interface NoteDao {

    @Insert
    suspend fun insertNote(note: Note)

    @Query("Select * from notes where status = :notesStatus")
    fun filterNotes(notesStatus: Int): LiveData<List<Note>>

    @Query("Select * from notes")
    fun allNotes(): LiveData<List<Note>>

    @Query("Select * from notes where id = :noteID")
    suspend fun getNote(noteID: String): Note

    @Delete
    suspend fun deleteNote(note: Note)

    @Update
    suspend fun updateNote(note: Note)

}