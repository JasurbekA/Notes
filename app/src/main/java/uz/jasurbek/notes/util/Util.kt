package uz.jasurbek.notes.util

import android.text.format.DateUtils
import uz.jasurbek.notes.data.Constants.noteFilterOptions
import uz.jasurbek.notes.data.model.NoteStatus
import java.text.SimpleDateFormat
import java.util.*

object Util {

    private const val DATE_FORMAT_PARSER = "yyyy/MM/dd HH:mm"

    fun mapStatusNameToStatus(string: String): Int =
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

    fun mapCalendarToStringDate(calendar: Calendar?): String = if (calendar == null) ""
    else SimpleDateFormat(DATE_FORMAT_PARSER, Locale.US).format(Date(calendar.timeInMillis))

    fun mapStringToCalendar(stringDate: String, dateFormat: String = DATE_FORMAT_PARSER): Calendar {
        val calendar = Calendar.getInstance()
        val dateFormatter = SimpleDateFormat(dateFormat, Locale.ROOT)
        calendar.time = dateFormatter.parse(stringDate) ?: Date()
        return calendar
    }


    fun getReminderDate(dueDate: String, hourOffset: Int) : String {
        val dueDateCalendar = mapStringToCalendar(dueDate)
        val negativeOffset = hourOffset * -1
        dueDateCalendar.add(Calendar.HOUR, negativeOffset)
        val dateFormatter = SimpleDateFormat(DATE_FORMAT_PARSER, Locale.ROOT)
        return dateFormatter.format(Date(dueDateCalendar.timeInMillis))
    }

    fun isReminderAllowed(dueDate: String, hourOffset: Int): Boolean {
        val calendarNow = Calendar.getInstance()
        val dueDateCalendar = mapStringToCalendar(dueDate)
        calendarNow.add(Calendar.HOUR, hourOffset)
        return dueDateCalendar.timeInMillis >= calendarNow.timeInMillis
    }

    fun isDueTimeAllowed(dueDate: String) : Boolean {
        val calendarNow = Calendar.getInstance()
        val dueDateCalendar = mapStringToCalendar(dueDate)
        return dueDateCalendar.timeInMillis >= calendarNow.timeInMillis
    }

    fun calculateReminderDifference(dueDate: String, reminderDate: String) : Long {
        val due = mapStringToCalendar(dueDate)
        val reminder = mapStringToCalendar(reminderDate)
        val difference = due.timeInMillis - reminder.timeInMillis
        return difference / DateUtils.HOUR_IN_MILLIS
    }


    fun mapFilterOptionsToStatus(filter: String) = noteFilterOptions.indexOf(filter)


}