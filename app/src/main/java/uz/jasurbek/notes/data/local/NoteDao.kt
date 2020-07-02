package uz.jasurbek.notes.data.local

import androidx.room.*
import uz.jasurbek.notes.data.model.Note

@Dao
interface NoteDao {

    @Insert
    suspend fun insertNote(note : Note) : Long

    @Query("Select * from notes where status = :notesStatus")
    suspend fun filterNotes(notesStatus : Int) : List<Note>

    @Query("Select * from notes")
    suspend fun allNotes() : List<Note>

    @Delete
    suspend fun deleteNote(note: Note)

    @Update
    suspend fun updateNote(note: Note)

}