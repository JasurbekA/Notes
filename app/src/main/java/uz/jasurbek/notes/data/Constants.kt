package uz.jasurbek.notes.data

import uz.jasurbek.notes.data.model.Note

object Constants {
    const val DB_VERSION = 1
    const val DB_NAME = "notes_db"

    const val AUTHORITY = "uz.jasurbek.notes.provider"
    
    const val REQUEST_CODE_CHOOSE_GALLERY_IMAGE = 1
    const val REQUEST_CODE_TAKE_PHOTO_IMAGE = 2

    const val BUNDLE_KEY_NOTE_OPERATION = "note_operation"

    //Template constants
    val rawNote = Note(name = "", description = "")
    val noteStatusOptions = arrayOf("Active", "Completed", "Expired", "Cancel")
    val moreOptions = arrayOf("Due date", "Reminder", "Cancel")
    val choosingPhotoOptions = arrayOf("Take Photo", "Choose from Gallery", "Cancel")
}