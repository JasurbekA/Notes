package uz.jasurbek.notes.ui.operation

sealed class SaveNoteState {
    object OnSuccess : SaveNoteState()
    data class OnError(val message: String) : SaveNoteState()
}