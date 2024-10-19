package com.omarkarimli.mynotes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.omarkarimli.mynotes.data.dao.NoteDao
import com.omarkarimli.mynotes.data.entity.Note
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(private val noteDao: NoteDao) : ViewModel() {

    val notes = noteDao.getAllNotes().asLiveData()


    private val notesChannel = Channel<NotesEvent>()
    val notesEvent = notesChannel.receiveAsFlow()

    fun insertNote(note: Note) = viewModelScope.launch {
        noteDao.insertNote(note)
        notesChannel.send(NotesEvent.NavigateToNotesFragment)
    }

    fun updateNote(note: Note) = viewModelScope.launch {
        noteDao.updateNote(note)
        notesChannel.send(NotesEvent.NavigateToNotesFragment)
    }

    fun deleteNote(note: Note) = viewModelScope.launch {
        noteDao.deleteNote(note)
        notesChannel.send(NotesEvent.ShowUndoDeleteNoteMessage("Note deleted", note))
    }

    fun pinNote(note: Note) = viewModelScope.launch {
        note.isPinned = true // Assuming you have a field in Note to track pinned status
        noteDao.updateNote(note) // Update note in the database to reflect pinning
        notesChannel.send(NotesEvent.ShowPinnedNoteMessage("Note pinned", note))
    }

    // NotesEvent
    sealed class NotesEvent {
        data class ShowUndoDeleteNoteMessage(val msg: String, val note: Note) : NotesEvent()
        data class ShowPinnedNoteMessage(val msg: String, val note: Note) : NotesEvent()
        object NavigateToNotesFragment : NotesEvent()
        // Add more events as needed
    }
}
