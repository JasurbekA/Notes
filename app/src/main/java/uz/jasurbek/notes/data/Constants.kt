package uz.jasurbek.notes.data

import uz.jasurbek.notes.data.model.Note

object Constants {
    const val DB_VERSION = 1
    const val DB_NAME = "notes_db"
    const val DB_NOTE_ID_DATE_FORMAT = "MMM d, yyyy h:mm:ss a"

    const val IMAGE_FOLDER_NAME = "NotesImage"

    const val AUTHORITY = "uz.jasurbek.notes.provider"

    const val REQUEST_CODE_CHOOSE_GALLERY_IMAGE = 1
    const val REQUEST_CODE_TAKE_PHOTO_IMAGE = 2

    const val BUNDLE_KEY_NOTE_OPERATION = "note_operation"

    //Template constants
    val noteEditStatusOptions = arrayOf("Active", "Completed", "Expired", "Cancel")
    val noteAddStatusOptions = arrayOf("Active", "Cancel")
    val moreOptions = arrayOf("Due date", "Reminder", "Cancel")
    val choosingPhotoOptions = arrayOf("Take Photo", "Choose from Gallery", "Cancel")

    val reminderOptions =
        arrayOf("1 hour before", "2 hour before", "3 hour before", "4 hour before", "5 hour before")

    val noteFilterOptions = arrayOf("All", "Active", "Completed", "Expired")
}