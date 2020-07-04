package uz.jasurbek.notes.ui.list


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import kotlinx.android.synthetic.main.item_notes.view.*
import uz.jasurbek.notes.R
import uz.jasurbek.notes.data.model.Note
import java.io.File


class NoteListAdapter(private val itemClickEvent: ItemClickEvent? = null) :
    ListAdapter<Note, NoteListAdapter.NoteViewHolder>(NoteDiffCallback()) {

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) =
        holder.bindItem(getItem(position))


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_notes, parent, false)
        return NoteViewHolder(view)
    }


    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItem(note: Note): Unit = with(itemView) {

            setOnClickListener { itemClickEvent?.onItemClicked(note.id) }

            noteName.text = note.name
            noteDesc.text = note.description

            val dueDateText = note.dueDate ?: "Not due date"
            noteDueDate.text = dueDateText

            loadImage(noteImage, note.imagePath)
        }

        private fun loadImage(imageView: AppCompatImageView, imagePath: String?) = imagePath?.let {
            val imageFile = File(it)
            if (imageFile.exists()) {
                imageView.setPadding(0,0,0,0)
                imageView.load(imageFile)
            }
        }

    }

}


interface ItemClickEvent {
    fun onItemClicked(noteID: String)
}

/**
 * Callback for calculating the diff between two non-null items in a list.
 *
 * Used by ListAdapter to calculate the minimum number of changes between and old list and a new
 * list that's been passed to `submitList`.
 */
class NoteDiffCallback : DiffUtil.ItemCallback<Note>() {
    override fun areItemsTheSame(oldItem: Note, newItem: Note) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Note, newItem: Note) = oldItem.isItemsTheSame(newItem)
}