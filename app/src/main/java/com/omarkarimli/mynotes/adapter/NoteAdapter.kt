package com.omarkarimli.mynotes.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.omarkarimli.mynotes.DateTimeUtils
import com.omarkarimli.mynotes.data.entity.Note
import com.omarkarimli.mynotes.databinding.ItemNotesBinding

class NoteAdapter(private val listener: OnNoteClickListener) : RecyclerView.Adapter<NoteAdapter.ViewHolder>() {

    private var notes: List<Note> = emptyList()
    private var filteredNotes: List<Note> = emptyList()

    init {
        filteredNotes = notes
    }

    interface OnNoteClickListener {
        fun onNoteClick(note: Note)
    }

    inner class ViewHolder(private val binding: ItemNotesBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val note = notes[position]
                    listener.onNoteClick(note)
                }
            }
        }

        fun bind(note: Note) {
            binding.apply {
                textviewTitleNote.text = note.title
                textviewDateNote.text = DateTimeUtils.convertLongToTimeString(note.date)
                textviewContentNote.text = if (note.content!!.isNotEmpty()) {
                    note.content
                } else {
                    "No additional text"
                }

                // Set the visibility of the pin icon based on the isPinned flag
                imageViewPin.visibility = if (note.isPinned) {
                    android.view.View.VISIBLE
                } else {
                    android.view.View.GONE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNotesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = filteredNotes.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(filteredNotes[position])
    }

    // Update the list data
    fun submitList(newNotes: List<Note>) {
        notes = newNotes
        filteredNotes = notes
        notifyDataSetChanged() // You can optimize this using DiffUtil for large lists
    }

    // Added method to get note at a specific position
    fun getNoteAtPosition(position: Int): Note {
        return notes[position]
    }

    // Filter notes based on search query
    fun filter(query: String) {
        filteredNotes = if (query.isEmpty()) {
            notes
        } else {
            notes.filter { it.title!!.contains(query, ignoreCase = true) || it.content!!.contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged()  // Update the list based on the filtered results
    }
}
