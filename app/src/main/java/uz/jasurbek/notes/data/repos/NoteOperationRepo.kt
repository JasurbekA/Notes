package uz.jasurbek.notes.data.repos

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.FragmentActivity
import uz.jasurbek.notes.data.local.NoteDao
import uz.jasurbek.notes.data.model.Note
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
    fun saveImage(context: Context, imageUri: Uri, callback: (imagePath: String?) -> Unit) {
        val contentResolver = context.contentResolver
        val imageFile = if (imageUri.path != null) File(imageUri.path!!) else null
        getBitmap(contentResolver, imageUri)?.let {
            saveImageIntoStorage(contentResolver, it, imageFile?.name ?: "test", callback)
        }
    }

    private fun getBitmap(contentResolver: ContentResolver, imageUri: Uri): Bitmap? =
        try {
            if (Build.VERSION.SDK_INT < 29) {
                @Suppress("deprecation")
                MediaStore.Images.Media.getBitmap(
                    contentResolver,
                    imageUri
                )
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
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/NoteImages")
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
                ).toString() + File.separator + "NoteImages"

                val file = File(imageDir)
                if (!file.exists()) file.mkdir()
                val image = File(imageDir, "$name.jpg")
                callback(image.absolutePath)
                FileOutputStream(image)
            }
        } catch (ex: IOException) {
            null
        }
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos?.flush()
        fos?.close()
    }

    private fun getRealPathFromUri(
        contentResolver: ContentResolver,
        contentUri: Uri
    ): String? {
        var cursor: Cursor? = null
        return try {
            val projection =
                arrayOf(MediaStore.Images.Media._ID)
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