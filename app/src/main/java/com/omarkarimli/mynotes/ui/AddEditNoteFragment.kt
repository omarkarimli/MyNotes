package com.omarkarimli.mynotes.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.omarkarimli.mynotes.DateTimeUtils
import com.omarkarimli.mynotes.R
import com.omarkarimli.mynotes.data.entity.Note
import com.omarkarimli.mynotes.databinding.FragmentAddeditnotesBinding
import com.omarkarimli.mynotes.viewmodel.NoteViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddEditNoteFragment: Fragment(R.layout.fragment_addeditnotes) {

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel by viewModels<NoteViewModel>()
        val binding = FragmentAddeditnotesBinding.bind(requireView())
        val args: AddEditNoteFragmentArgs by navArgs()
        val note = args.note

        if (note != null) {
            binding.apply {
                binding.editTextTitle.setText(note.title)
                binding.editTextContent.setText(note.content)

                binding.textviewEditedOn.text = getString(R.string.edited_on) + " " + DateTimeUtils.convertLongToTimeString(note.date)

                binding.btnSaveNote.setOnClickListener {
                    val title = binding.editTextTitle.text.toString()
                    val content = binding.editTextContent.text.toString()
                    val updatedNote = note.copy(title = title, content = content, date = System.currentTimeMillis())

                    viewModel.updateNote(updatedNote)
                }
            }
        } else {
            binding.apply {

                // layoutEditedOn should be GONE
                binding.layoutEditedOn.visibility = View.GONE

                binding.btnSaveNote.setOnClickListener {
                    val title = binding.editTextTitle.text.toString()

                    if (title.isNotEmpty()) {
                        val content = binding.editTextContent.text.toString()
                        val newNote = Note(title = title, content = content, date = System.currentTimeMillis())

                        viewModel.insertNote(newNote)
                    } else {
                        // Snack bar for empty title
                        Snackbar.make(requireView(), "Title cannot be empty", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.notesEvent.collect { event ->
                if (event is NoteViewModel.NotesEvent.NavigateToNotesFragment) {
                    val action = AddEditNoteFragmentDirections.actionAddEditNoteFragmentToNoteFragment()
                    findNavController().navigate(action)
                }
            }
        }

        // Set up the backBtn click listener to navigate back
        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}