package uz.jasurbek.notes.data

import uz.jasurbek.notes.data.model.Note

object Constants {

    //DB
    const val DB_VERSION = 1
    const val DB_NAME = "notes_db"
    const val DB_NOTE_ID_DATE_FORMAT = "MMM d, yyyy h:mm:ss a"

    //Notification
    const val NOTIFICATION_ID = 1
    const val NOTIFICATION_BUNDLE_KEY_TITLE = "n_title"
    const val NOTIFICATION_BUNDLE_KEY_BODY = "n_body"


    //Prefs
    const val SHARED_PREF_NAME = "uz.jas.notes.prefs"
    const val SHARED_PREF_NOTIFICATION_TITLE_PREFIX = "n_title"
    const val SHARED_PREF_NOTIFICATION_BODY_PREFIX = "n_body"

    //Saving Image
    const val IMAGE_FOLDER_NAME = "NotesImage"
    const val AUTHORITY = "uz.jasurbek.notes.provider"

    //Request
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

    val noteFilterOptions = arrayOf("All", "Active", "Expired", "Completed")
}
