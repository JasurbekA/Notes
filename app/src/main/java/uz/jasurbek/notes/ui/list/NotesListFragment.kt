package uz.jasurbek.notes.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_notes_list.*
import uz.jasurbek.notes.R
import uz.jasurbek.notes.extentions.navigate
import javax.inject.Inject

class NotesListFragment : DaggerFragment() {

    private lateinit var viewModel: NotesListViewModel

    @Inject
    lateinit var providerFactory: ViewModelProvider.Factory


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
        initVM()
        setupRV()
        setClickListeners()
    }

    private fun initVM() {
        viewModel = ViewModelProvider(
            this,
            providerFactory
        )[NotesListViewModel::class.java]
    }


    private fun setupRV() {
        val noteListAdapter = NoteListAdapter()
        notesListRV.adapter = noteListAdapter
    }


    private fun setClickListeners() {
        addNoteFab.setOnClickListener { navigate(R.id.notesAddAndEditFragment) }
    }

}