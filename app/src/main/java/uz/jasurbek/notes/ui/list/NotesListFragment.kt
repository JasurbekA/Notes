package uz.jasurbek.notes.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_notes_list.*
import uz.jasurbek.notes.R
import uz.jasurbek.notes.data.model.Note
import uz.jasurbek.notes.data.model.NoteStatus
import uz.jasurbek.notes.extentions.navigate
import uz.jasurbek.notes.extentions.toast
import javax.inject.Inject

class NotesListFragment : DaggerFragment() {

    @Inject
    lateinit var providerFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<NotesListViewModel> { providerFactory }

    private lateinit var noteListAdapter: NoteListAdapter
    private var loadNotesWithStatus: Int = NoteStatus.NOTES_STATUS_DEFAULT


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_notes_list, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }


    private fun setupUI() {

        setupRV()
        observeNotes()
        setClickListeners()
    }


    private fun setupRV() {
        noteListAdapter = NoteListAdapter()
        notesListRV.adapter = noteListAdapter
    }

    private fun observeNotes() {
        viewModel.getNotes(loadNotesWithStatus)
        viewModel.noteResponse.observe(viewLifecycleOwner, Observer {
            when (it) {
                is LoadingNoteStatus.OnLoading -> println("observeNotes Loading")
                is LoadingNoteStatus.OnSuccess -> loadingNotesSuccess(it.notes)
                is LoadingNoteStatus.OnError -> toast(it.errorMessage)
            }
        })
    }


    private fun loadingNotesSuccess(notes: List<Note>) {
        changeViewVisibility(notes.isEmpty())
        noteListAdapter.submitList(notes)
    }

    private fun changeViewVisibility(noNotes: Boolean) {
        if (noNotes) {
            notesListRV.visibility = View.GONE
            noNotesLayout.visibility = View.VISIBLE
        } else {
            notesListRV.visibility = View.VISIBLE
            noNotesLayout.visibility = View.GONE
        }
    }


    private fun setClickListeners() {
        addNoteFab.setOnClickListener { navigate(R.id.notesAddAndEditFragment) }
    }

}