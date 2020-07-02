package uz.jasurbek.notes.data.repos

import android.graphics.Bitmap
import android.os.Environment
import androidx.fragment.app.FragmentActivity
import uz.jasurbek.notes.data.local.NoteDao
import uz.jasurbek.notes.data.model.Note
import java.io.*
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
    fun saveImage(activity: FragmentActivity?, bitmap: Bitmap): String? {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val output: OutputStream
        val file: File = createTemporaryImageFile(activity)
        return try {
            output = FileOutputStream(file)
            output.write(bytes.toByteArray())
            output.close()
            file.absolutePath // save successfully
        } catch (e: IOException) {
            "" //fail to save because of IOException
        }
    }

    fun createTemporaryImageFile(activity: FragmentActivity?): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val imageFileName = "JPEG" + timeStamp + "_"

        val publicDir = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (publicDir?.exists() == false) publicDir.mkdir()

        val storageDir = File(publicDir, "Notes Image");
        if (!storageDir.exists()) storageDir.mkdir()

        return createTempFile(imageFileName, ".jpg", storageDir)
    }

}