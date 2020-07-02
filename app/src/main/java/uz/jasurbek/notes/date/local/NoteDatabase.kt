package uz.jasurbek.notes.date.local

import androidx.room.Database
import androidx.room.RoomDatabase
import uz.jasurbek.notes.date.Constants.DB_VERSION
import uz.jasurbek.notes.date.model.Note

@Database(entities = [Note::class], version = DB_VERSION, exportSchema = false)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}
