package uz.jasurbek.notes.util

import uz.jasurbek.notes.data.model.NoteStatus
import java.util.*

object Util {
    fun mapStringToStatus(string: String): Int =
        when (string.toLowerCase(Locale.ROOT)) {
            "active" -> NoteStatus.NOTES_STATUS_ACTIVE
            "completed" -> NoteStatus.NOTES_STATUS_COMPLETED
            "expired" -> NoteStatus.NOTES_STATUS_EXPIRED
            else -> NoteStatus.NOTES_STATUS_DEFAULT
        }

    fun mapStatusToString(status: Int): String =
        when (status) {
            NoteStatus.NOTES_STATUS_COMPLETED -> "Completed"
            NoteStatus.NOTES_STATUS_EXPIRED -> "Expired"
            else -> "Active"
        }

}