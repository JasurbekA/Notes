package uz.jasurbek.notes.ui.operation

import android.Manifest
import android.app.Activity.RESULT_CANCELED
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import coil.api.load
import dagger.android.support.DaggerAppCompatActivity
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_add_edit_note.*
import kotlinx.coroutines.Job
import uz.jasurbek.notes.R
import uz.jasurbek.notes.data.Constants
import uz.jasurbek.notes.data.Constants.choosingPhotoOptions
import uz.jasurbek.notes.data.Constants.moreOptions
import uz.jasurbek.notes.data.Constants.noteAddStatusOptions
import uz.jasurbek.notes.data.Constants.noteEditStatusOptions
import uz.jasurbek.notes.data.Constants.reminderOptions
import uz.jasurbek.notes.data.model.Note
import uz.jasurbek.notes.extentions.*
import uz.jasurbek.notes.ui.list.LoadingNoteStatus
import uz.jasurbek.notes.util.EventObserver
import java.io.File
import java.io.IOException
import java.util.*
import javax.inject.Inject

class NoteOperationsFragment : DaggerFragment() {

    @Inject
    lateinit var providerFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<NoteOperationsViewModel> { providerFactory }

    private lateinit var currentNote: Note
    private var attachedImageUri: Uri? = null

    /*Fragment callbacks */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_add_edit_note, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initVariables()
        setupPageTitle()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (isNewNote()) inflater.inflate(R.menu.save_note_menu, menu)
        else inflater.inflate(R.menu.edit_note_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

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
            Constants.REQUEST_CODE_CHOOSE_GALLERY_IMAGE -> choosePhotoFromGalleryAction()
            Constants.REQUEST_CODE_TAKE_PHOTO_IMAGE -> takePhotoFromCameraAction()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_CANCELED) return
        when (requestCode) {
            Constants.REQUEST_CODE_CHOOSE_GALLERY_IMAGE -> loadImage(data?.data)
            Constants.REQUEST_CODE_TAKE_PHOTO_IMAGE -> loadImage(attachedImageUri)
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.saveEditNote -> saveNote(viewModel::saveEditedNote)
            R.id.deleteNote -> deleteCurrentNoteClicked()
            R.id.saveNote -> saveNote(viewModel::addNote)
        }
        return super.onOptionsItemSelected(item)
    }


    /*Observe*/
    private fun observeNoteObject() =
        viewModel.noteResponse.observe(viewLifecycleOwner, Observer {
            when (it) {
                is LoadingNoteStatus.OnLoading -> println("Loading")
                is LoadingNoteStatus.OnSuccess -> updateUI(it.notes[0])
                is LoadingNoteStatus.OnError -> toast(it.errorMessage)
            }
        })

    private fun observeSaveState() {
        viewModel.noteSaveState.observe(viewLifecycleOwner, EventObserver {
            when (it) {
                is SaveNoteState.OnSuccess -> activity?.onBackPressed()
                is SaveNoteState.OnError -> noteImageHolder?.showSnackBar(it.message)
            }
        })

    }

    /*Operations*/
    private fun deleteCurrentNoteClicked() {
        viewModel.deleteCurrentNote(requireContext(), currentNote)
        activity?.onBackPressed()
    }


    private fun saveNote(saveFunction: (context: Context, note: Note, uri: Uri?) -> Job) {
        if (requiredFieldsNotFilled()) {
            operationNoteTitle.showSnackBar("Title and description are required")
            return
        }
        operationNoteImage?.hideSoftKeyboard()
        setNoteRequiredFields()
        saveFunction(requireContext(), currentNote, attachedImageUri)
        observeSaveState()
    }


    /*Helpers*/
    private fun setNoteRequiredFields() {
        currentNote.name = operationNoteTitle.text.toString()
        currentNote.description = operationNoteDesc.text.toString()
    }

    private fun requiredFieldsNotFilled() =
        operationNoteTitle.text.isNullOrEmpty() || operationNoteDesc.text.isNullOrEmpty()

    private fun isNewNote() =
        arguments?.getString(Constants.BUNDLE_KEY_NOTE_OPERATION).isNullOrBlank()

    private fun initVariables() =
        if (isNewNote()) {
            currentNote = Note(name = "", description = "")
            setupUI()
        } else {
            viewModel.getNote(arguments?.getString(Constants.BUNDLE_KEY_NOTE_OPERATION)!!)
            observeNoteObject()
        }


    private fun setClickListeners() {
        operationNoteImage.setOnClickListener { selectImageClicked() }
        operationNoteStatus.setOnClickListener { selectStatusClicked() }
        operationMoreOptionView.setOnClickListener { selectMoreOptions() }
        operationNoteDueDate.setOnClickListener { dueDateViewClicked() }
        operationNoteReminder.setOnClickListener { reminderViewClicked() }
    }


    /*UI update*/
    private fun updateUI(note: Note) {
        currentNote = note
        setupUI()
    }


    private fun setupPageTitle() {
        val parentActivity = activity as? DaggerAppCompatActivity
        if (isNewNote()) parentActivity?.supportActionBar?.title = "Add note"
        else parentActivity?.supportActionBar?.title = "Edit note"
    }

    private fun loadImage(imagePath: String?) = imagePath?.let {
        val imageFile = File(imagePath)
        if (imageFile.exists()) {
            operationNoteImage.setPadding(0, 0, 0, 0)
            operationNoteImage.load(imageFile)
        }
    }


    private fun loadImage(imageUri: Uri?) = imageUri?.let {
        attachedImageUri = it
        operationNoteImage.setPadding(0, 0, 0, 0)
        operationNoteImage.load(attachedImageUri)
    }

    private fun loadStatus(status: String?) {
        val result = "Status : ${status ?: noteEditStatusOptions.first()}"
        operationNoteStatus.text = result
    }

    private fun loadTitle(title: String?) = title?.let {
        operationNoteTitle.setText(it)
    }

    private fun loadDescription(desc: String?) = desc?.let {
        operationNoteDesc.setText(it)
    }

    private fun loadDueDate(dueDate: String?) = dueDate?.let {
        operationNoteDueDate.text = it
    }

    private fun loadReminder(reminderDate: String?) = reminderDate?.let {
        val difference =
            viewModel.calculateReminderDifference(operationNoteDueDate.text.toString(), it)
        val result = "(-$difference hours)"
        operationNoteReminder.text = result
    }


    private fun resetNoteAlarm() {
        currentNote.alarmDate = null
        operationNoteReminder.text = getString(R.string.no_reminder)
    }

    private fun configDueTimeAndUI(calendar: Calendar?) {
        currentNote.dueDate = viewModel.mapCalendarToStringDate(calendar)
        loadDueDate(currentNote.dueDate)
        resetNoteAlarm()
    }


    /*Views click events*/

    private fun setupUI() {
        loadImage(currentNote.imagePath)
        loadStatus(viewModel.mapStatusToString(currentNote.status))
        loadTitle(currentNote.name)
        loadDescription(currentNote.description)
        loadDueDate(currentNote.dueDate)
        loadReminder(currentNote.alarmDate)
        setClickListeners()
    }

    private fun selectMoreOptions() =
        showOptionsAlertDialog(moreOptions, "More options") {
            when (it) {
                "Due date" -> alertDueDateClicked()
                "Reminder" -> alertReminderClicked()
            }
        }


    private fun dueDateViewClicked() {
        if (operationNoteDueDate.text == getString(R.string.no_due))
            operationNoteDueDate.showSnackBar("Select due date using more option at bottom right corner")
        else
            showConfirmationDialog("Do you want to delete due date") {
                currentNote.dueDate = null
                operationNoteDueDate.text = getString(R.string.no_due)
                resetNoteAlarm()
            }
    }

    private fun reminderViewClicked() {
        if (operationNoteReminder.text == getString(R.string.no_reminder))
            operationNoteReminder.showSnackBar("Select reminder using more option at bottom right corner")
        else
            showConfirmationDialog("Do you want to delete reminder") {
                resetNoteAlarm()
            }
    }


    private fun selectStatusClicked() {
        val options = if (isNewNote()) noteAddStatusOptions else noteEditStatusOptions
        showOptionsAlertDialog(options, "Note status") {
            if (it == noteEditStatusOptions.last()) return@showOptionsAlertDialog
            currentNote.status = viewModel.mapStatusNameToStatus(it)
            loadStatus(it)
        }
    }


    private fun selectImageClicked() =
        showOptionsAlertDialog(choosingPhotoOptions, "Choose your note image") {
            when (it) {
                "Take Photo" -> takeImageFromCameraClick()
                "Choose from Gallery" -> chooseFromGalleryClick()
            }
        }


    private fun alertDueDateClicked() =
        showDatePickerDialog { date ->
            showTimePickerDialog(date) {
                if (viewModel.isDueTimeAllowed(viewModel.mapCalendarToStringDate(it)))
                    configDueTimeAndUI(it)
                else operationNoteImage?.showSnackBar("Please select valid due time")
            }
        }


    private fun alertReminderClicked() {
        if (operationNoteDueDate.text == getString(R.string.no_due)) {
            toast("You cannot set reminder unless you select due date")
            return
        }
        showSingleChoiceDialog(reminderOptions, "Remind me") {
            val isAllowed = viewModel.isReminderAllowed(operationNoteDueDate.text.toString(), it)
            if (isAllowed) {
                currentNote.alarmDate =
                    viewModel.getReminderDate(operationNoteDueDate.text.toString(), it)
                loadReminder(currentNote.alarmDate)
            } else operationNoteDueDate.showSnackBar("Please try different offset")
        }
    }


    /*Image choosing*/
    private fun takeImageFromCameraClick() {
        if (hasPermissionGranted(Manifest.permission.CAMERA)) takePhotoFromCameraAction()
        else requestPermission(
            arrayOf(Manifest.permission.CAMERA),
            Constants.REQUEST_CODE_TAKE_PHOTO_IMAGE
        )
    }

    private fun chooseFromGalleryClick() {
        if (hasPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE) &&
            hasPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        ) choosePhotoFromGalleryAction()
        else requestPermission(
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            Constants.REQUEST_CODE_CHOOSE_GALLERY_IMAGE
        )
    }


    private fun requestPermission(
        array: Array<String>,
        requestCode: Int
    ) = ActivityCompat.requestPermissions(
        requireActivity(),
        array,
        requestCode
    )


    private fun hasPermissionGranted(
        permission: String
    ) = ContextCompat.checkSelfPermission(
        requireContext(),
        permission
    ) == PackageManager.PERMISSION_GRANTED


    private fun choosePhotoFromGalleryAction() {
        val getIntent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" }

        val pickIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        ).apply { type = "image/*" }

        val chooserIntent = Intent.createChooser(getIntent, "Pick an image")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))
        startActivityForResult(chooserIntent, Constants.REQUEST_CODE_CHOOSE_GALLERY_IMAGE)
    }


    private fun takePhotoFromCameraAction(): Unit = with(Intent(MediaStore.ACTION_IMAGE_CAPTURE)) {
        if (resolveActivity(requireActivity().packageManager) != null) {
            try {
                val imageFile = viewModel.createImageFile(activity)
                attachedImageUri = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP)
                    FileProvider.getUriForFile(requireContext(), Constants.AUTHORITY, imageFile)
                else Uri.fromFile(imageFile)

                putExtra(MediaStore.EXTRA_OUTPUT, attachedImageUri)
                startActivityForResult(
                    this,
                    Constants.REQUEST_CODE_TAKE_PHOTO_IMAGE
                )
            } catch (e: IOException) {
                toast("Error while try to take photo")
            }
        }
    }

}

