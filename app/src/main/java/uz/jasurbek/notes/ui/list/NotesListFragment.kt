package uz.jasurbek.notes.ui.list

import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.DaggerAppCompatActivity
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_notes_list.*
import uz.jasurbek.notes.R
import uz.jasurbek.notes.data.Constants
import uz.jasurbek.notes.data.Constants.noteFilterOptions
import uz.jasurbek.notes.data.model.Note
import uz.jasurbek.notes.data.model.NoteStatus
import uz.jasurbek.notes.extentions.navigate
import uz.jasurbek.notes.extentions.showOptionsAlertDialog
import javax.inject.Inject

class NotesListFragment : DaggerFragment() {

    @Inject
    lateinit var providerFactory: ViewModelProvider.Factory
    private val viewModel by viewModels<NotesListViewModel> { providerFactory }

    private lateinit var noteListAdapter: NoteListAdapter
    private var loadNotesWithStatus: Int = NoteStatus.NOTES_STATUS_DEFAULT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_notes_list, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.note_list_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.filterNotes -> filterNotesClicked()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun filterNotesClicked() {
        showOptionsAlertDialog(noteFilterOptions, "Filter notes"){
            loadNotesWithStatus = viewModel.mapFilterOptionsToStatus(it)
            observeNotes(loadNotesWithStatus)
        }
    }

    private fun setupUI() {
        setupPageTitle()
        setupRV()
        observeNotes(loadNotesWithStatus)
        observeNotesStatusTitle()
        setClickListeners()
    }


    private fun setupPageTitle() {
        val parentActivity = activity as? DaggerAppCompatActivity
        parentActivity?.supportActionBar?.title = "Note list"
    }

    private val itemClickEvent = object : ItemClickEvent {
        override fun onItemClicked(noteID: String) {
            val bundle = bundleOf(Constants.BUNDLE_KEY_NOTE_OPERATION to noteID)
            navigate(R.id.notesAddAndEditFragment, bundle)
        }
    }

    private fun setupRV() {
        noteListAdapter = NoteListAdapter(itemClickEvent)
        notesListRV.adapter = noteListAdapter
    }

    private fun observeNotes(status: Int) {
        viewModel.observeNotes(status).observe(viewLifecycleOwner, Observer {
            loadingNotesSuccess(it)
        })
    }

    private fun observeNotesStatusTitle() {
        viewModel.filterText.observe(viewLifecycleOwner, Observer {
            notesStatus.text = it
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