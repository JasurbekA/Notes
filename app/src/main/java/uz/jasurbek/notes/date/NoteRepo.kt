package uz.jasurbek.notes.date

import uz.jasurbek.notes.date.local.NoteDao
import javax.inject.Inject

class NoteRepo @Inject constructor(private val noteDao : NoteDao) {

}