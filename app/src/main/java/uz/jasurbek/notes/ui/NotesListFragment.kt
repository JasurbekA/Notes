package uz.jasurbek.notes.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_notes_list.*
import uz.jasurbek.notes.R
import uz.jasurbek.notes.extentions.navigate

class NotesListFragment : Fragment() {


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
        setClickListeners()
    }


    private fun setClickListeners() {
        addNoteFab.setOnClickListener { navigate(R.id.notesAddAndEditFragment) }
    }

}