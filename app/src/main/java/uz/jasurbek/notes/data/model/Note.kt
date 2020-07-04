package uz.jasurbek.notes.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import uz.jasurbek.notes.data.Constants.DB_NOTE_ID_DATE_FORMAT
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey (autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: String = SimpleDateFormat(DB_NOTE_ID_DATE_FORMAT, Locale.ROOT).format(Date()),

    var status : Int = NoteStatus.NOTES_STATUS_ACTIVE,

    @ColumnInfo(name = "image_path")
    var imagePath: String? = null,

    var name: String,
    var description: String,
    var dueDate: String? = null,
    var alarmDate: String? = null
) {
    infix fun isSameWith(other : Note): Boolean {
        return status == other.status && imagePath == other.imagePath
                && description == other.description && name == other.name
                && alarmDate == other.alarmDate && dueDate == other.dueDate
    }
}


object NoteStatus {
    const val NOTES_STATUS_DEFAULT = 0 // used for filtering to show all notes
    const val NOTES_STATUS_ACTIVE = 1
    const val NOTES_STATUS_EXPIRED = 2
    const val NOTES_STATUS_COMPLETED = 3
}
