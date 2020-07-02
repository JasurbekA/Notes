package uz.jasurbek.notes.ui.operation

import android.Manifest
import android.app.Activity.RESULT_CANCELED
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import coil.api.load
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_add_edit_note.*
import uz.jasurbek.notes.R
import uz.jasurbek.notes.data.Constants
import uz.jasurbek.notes.extentions.toast
import java.io.IOException
import javax.inject.Inject


class NoteOperationsFragment : DaggerFragment() {

    @Inject
    lateinit var providerFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<NoteOperationsViewModel> { providerFactory }

    private var cameraImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_add_edit_note, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        setClickListeners()
    }

    private fun setClickListeners() {
        operationNoteImage.setOnClickListener { selectImage() }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_CANCELED) return

        when (requestCode) {
            Constants.REQUEST_CODE_CHOOSE_GALLERY_IMAGE -> {
                operationNoteImage.load(data?.data)

            }
            Constants.REQUEST_CODE_TAKE_PHOTO_IMAGE -> {
                operationNoteImage.load(cameraImageUri)
            }
        }
    }


    private fun selectImage() {
        val options = arrayOf(
            "Take Photo",
            "Choose from Gallery",
            "Cancel"
        )
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle("Choose your note image")
        builder.setItems(options) { dialog, item ->
            when {
                options[item] == "Take Photo" -> openCamera()
                options[item] == "Choose from Gallery" -> chooseFromGallery()
                options[item] == "Cancel" -> dialog.dismiss()
            }
        }
        builder.show()
    }


    private fun openCamera() {
        if (hasPermissionGranted(Manifest.permission.CAMERA)) takePhotoFromCamera()
        else requestPermission(
            arrayOf(Manifest.permission.CAMERA),
            Constants.REQUEST_CODE_TAKE_PHOTO_IMAGE
        )
    }

    private fun chooseFromGallery() {
        if (hasPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE) &&
            hasPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        ) choosePhotoFromGallery()
        else requestPermission(
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            Constants.REQUEST_CODE_CHOOSE_GALLERY_IMAGE
        )
    }


    private fun requestPermission(array: Array<String>, requestCode: Int) =
        ActivityCompat.requestPermissions(
            requireActivity(),
            array,
            requestCode
        )


    private fun hasPermissionGranted(permission: String) =
        ContextCompat.checkSelfPermission(
            requireContext(),
            permission
        ) == PackageManager.PERMISSION_GRANTED


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if ((grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED)) {
            toast("Permission denied")
            return
        }
        when (requestCode) {
            Constants.REQUEST_CODE_CHOOSE_GALLERY_IMAGE -> choosePhotoFromGallery()
            Constants.REQUEST_CODE_TAKE_PHOTO_IMAGE -> takePhotoFromCamera()
        }
    }


    private fun choosePhotoFromGallery() = with(Intent()) {
        type = "image/*"
        action = Intent.ACTION_GET_CONTENT
        startActivityForResult(this, Constants.REQUEST_CODE_CHOOSE_GALLERY_IMAGE)
    }


    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            try {
                val imageFile = viewModel.createImageFile(activity)
                cameraImageUri = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)
                    FileProvider.getUriForFile(requireContext(), Constants.AUTHORITY, imageFile)
                else Uri.fromFile(imageFile)

                intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
                startActivityForResult(
                    intent,
                    Constants.REQUEST_CODE_TAKE_PHOTO_IMAGE
                )
            } catch (e: IOException) {
                toast("Error while try to take photo")
            }
        }
    }


}