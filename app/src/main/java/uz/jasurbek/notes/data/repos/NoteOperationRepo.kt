package uz.jasurbek.notes.data.repos

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.FragmentActivity
import uz.jasurbek.notes.alarm.AlarmReceiver
import uz.jasurbek.notes.data.Constants
import uz.jasurbek.notes.data.local.NoteDao
import uz.jasurbek.notes.data.model.Note
import uz.jasurbek.notes.util.Util
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class NoteOperationRepo @Inject constructor(
    private val noteDao: NoteDao
) {
    /*Note operations*/
    suspend fun deleteNote(note: Note) = noteDao.deleteNote(note)
    suspend fun updateNote(note: Note) = noteDao.updateNote(note)
    suspend fun insertNote(note: Note) = noteDao.insertNote(note)
    suspend fun getNote(noteID: String) = noteDao.getNote(noteID)


    /*Additional helper functions to make UI code cleaner*/

    fun calculateReminderDifference(dueDate: String, reminderDate: String) =
        Util.calculateReminderDifference(dueDate, reminderDate)

    fun mapStatusNameToStatus(name: String) = Util.mapStatusNameToStatus(name)

    fun mapCalendarToStringDate(calendar: Calendar?) = Util.mapCalendarToStringDate(calendar)

    fun isReminderAllowed(dueDate: String, hourOffset: Int) =
        Util.isReminderAllowed(dueDate, hourOffset)

    fun getReminderDate(dueDate: String, hourOffset: Int) =
        Util.getReminderDate(dueDate, hourOffset)

    fun mapStatusToString(status: Int) = Util.mapStatusToString(status)
    /*End of helper staff*/


    /*Start Alarm staff*/

    private fun startAlarm(context: Context, calendar: Calendar, alarmRequestID: Int) {
        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, alarmRequestID, intent, 0)

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }

    private fun cancelAlarm(context: Context, alarmRequestID: Int) {
        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, alarmRequestID, intent, 0)
        alarmManager.cancel(pendingIntent)
    }

    /*End Alarm staff*/


    /*Saving & creating Image staff*/
    fun deleteImage(imagePath: String?) = imagePath?.let {
        val file = File(it)
        if (file.exists())
            println("delete file ${file.delete()}")
    }

    fun saveImage(context: Context, imageUri: Uri, callback: (imagePath: String?) -> Unit) {
        val contentResolver = context.contentResolver
        val imageFile = if (imageUri.path != null) File(imageUri.path!!) else null
        getBitmap(contentResolver, imageUri)?.let {
            saveImageIntoStorage(
                contentResolver,
                it,
                imageFile?.name ?: "${Date().time}.jpg",
                callback
            )
        }
    }

    private fun getBitmap(contentResolver: ContentResolver, imageUri: Uri): Bitmap? =
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                @Suppress("deprecation")
                MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
            } else {
                val source = ImageDecoder.createSource(contentResolver, imageUri)
                ImageDecoder.decodeBitmap(source)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }


    private fun saveImageIntoStorage(
        contentResolver: ContentResolver,
        bitmap: Bitmap,
        name: String,
        callback: (imagePath: String?) -> Unit
    ) {
        val fos = try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                val contentValues = ContentValues()
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                contentValues.put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    "DCIM/${Constants.IMAGE_FOLDER_NAME}"
                )
                contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )?.let {
                    callback(getRealPathFromUri(contentResolver, it))
                    contentResolver.openOutputStream(it)
                }
            } else {
                @Suppress("deprecation")
                val imageDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM
                ).toString() + File.separator + Constants.IMAGE_FOLDER_NAME

                val file = File(imageDir)
                if (!file.exists()) file.mkdir()
                val image = File(imageDir, name)
                callback(image.absolutePath)
                FileOutputStream(image)
            }
        } catch (ex: IOException) {
            null
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, fos)
        fos?.flush()
        fos?.close()
    }

    private fun getRealPathFromUri(contentResolver: ContentResolver, contentUri: Uri): String? {
        var cursor: Cursor? = null
        return try {
            val projection = arrayOf(MediaStore.Images.Media._ID)
            cursor = contentResolver.query(contentUri, projection, null, null, null)
            val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            cursor?.moveToFirst()
            cursor?.getString(columnIndex!!)
        } finally {
            cursor?.close()
        }
    }


    fun createTemporaryImageFile(activity: FragmentActivity?): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val imageFileName = "JPEG" + timeStamp + "_"
        val publicDir = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (publicDir?.exists() == false) publicDir.mkdir()
        val storageDir = File(publicDir, "Notes Image")
        if (!storageDir.exists()) storageDir.mkdir()
        return createTempFile(imageFileName, ".jpg", storageDir)
    }

}