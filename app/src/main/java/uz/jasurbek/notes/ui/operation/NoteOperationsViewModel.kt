package uz.jasurbek.notes.ui.operation

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import uz.jasurbek.notes.data.repos.NoteOperationRepo
import javax.inject.Inject

class NoteOperationsViewModel @Inject constructor(
    private val repo: NoteOperationRepo
) : ViewModel() {


    fun createImageFile(activity: FragmentActivity?) = repo.createTemporaryImageFile(activity)


}