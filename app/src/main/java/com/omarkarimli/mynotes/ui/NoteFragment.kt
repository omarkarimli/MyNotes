package com.omarkarimli.mynotes.ui

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.omarkarimli.mynotes.R
import com.omarkarimli.mynotes.adapter.NoteAdapter
import com.omarkarimli.mynotes.data.entity.Note
import com.omarkarimli.mynotes.databinding.FragmentNotesBinding
import com.omarkarimli.mynotes.viewmodel.NoteViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NoteFragment : Fragment(R.layout.fragment_notes), NoteAdapter.OnNoteClickListener {

    private val viewModel by viewModels<NoteViewModel>()
    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!

    private lateinit var noteAdapter: NoteAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentNotesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        setupRecyclerView()

        binding.addFab.setOnClickListener {
            val action = NoteFragmentDirections.actionNoteFragmentToAddEditNoteFragment(null)
            findNavController().navigate(action)
        }

        // Observe the notes LiveData
        viewModel.notes.observe(viewLifecycleOwner) { notes ->
            noteAdapter.submitList(notes)
        }

        // Handle events (e.g., undo delete)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.notesEvent.collect { event ->
                when (event) {
                    is NoteViewModel.NotesEvent.ShowUndoDeleteNoteMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG).setAction("UNDO") {
                            viewModel.insertNote(event.note)
                        }.show()
                    }
                    is NoteViewModel.NotesEvent.ShowPinnedNoteMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG).show()
                    }
                    else -> {}
                }
            }
        }
    }

    // Initialize RecyclerView and Adapter
    private fun setupRecyclerView() {
        noteAdapter = NoteAdapter(this)
        binding.recyclerViewNotes.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = noteAdapter
            setupSwipeGesture(this)
        }
    }

    private fun setupSwipeGesture(recyclerView: RecyclerView) {
        val swipeHandler = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false // We don't want to move items
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val note = noteAdapter.getNoteAtPosition(position)

                if (direction == ItemTouchHelper.LEFT) {
                    // Handle delete action
                    viewModel.deleteNote(note)
                } else {
                    // Handle pin action
                    note.isPinned = !note.isPinned // Toggle pin state
                    viewModel.updateNote(note)
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val drawableDelete = R.drawable.baseline_delete_24
                val drawablePinOrUnpin = when(noteAdapter.getNoteAtPosition(viewHolder.adapterPosition).isPinned) {
                    true -> R.drawable.unpin_24
                    false -> R.drawable.baseline_push_pin_24
                }

                val itemView = viewHolder.itemView
                val iconDelete = ContextCompat.getDrawable(requireContext(), drawableDelete)
                val iconPin = ContextCompat.getDrawable(requireContext(), drawablePinOrUnpin)

                // Corner Radius of the background
                val backgroundCornerRadius = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    12f, // 12dp
                    resources.displayMetrics
                )

                Paint().apply {
                    color = Color.WHITE
                    alpha = 255
                }

                if (dX < 0) {
                    // Swipe left (Delete)
                    val deleteBackgroundColor = ContextCompat.getColor(requireContext(), R.color.error)

                    // Create a rounded rectangle for the delete background
                    val deleteRect = RectF(
                        itemView.right + dX,
                        itemView.top.toFloat(),
                        itemView.right.toFloat(),
                        itemView.bottom.toFloat()
                    )

                    // Draw the rounded rectangle
                    c.drawRoundRect(deleteRect, backgroundCornerRadius, backgroundCornerRadius, Paint().apply {
                        color = deleteBackgroundColor
                    })

                    // Draw delete icon in white
                    iconDelete!!.setTint(Color.WHITE) // Set tint to white
                    val deleteIconMargin = (itemView.height - iconDelete.intrinsicHeight) / 2
                    val deleteIconTop = itemView.top + deleteIconMargin
                    val deleteIconBottom = deleteIconTop + iconDelete.intrinsicHeight
                    iconDelete.setBounds(
                        itemView.right - deleteIconMargin - iconDelete.intrinsicWidth,
                        deleteIconTop,
                        itemView.right - deleteIconMargin,
                        deleteIconBottom
                    )
                    iconDelete.draw(c)

                } else {

                    val bgColor = when(noteAdapter.getNoteAtPosition(viewHolder.adapterPosition).isPinned) {
                        true -> R.color.lilac
                        false -> R.color.purple_200
                    }

                    // Swipe right (Pin)
                    val pinBackgroundColor = ContextCompat.getColor(requireContext(), bgColor)

                    // Create a rounded rectangle for the pin background
                    val pinRect = RectF(
                        itemView.left.toFloat(),
                        itemView.top.toFloat(),
                        itemView.left + dX,
                        itemView.bottom.toFloat()
                    )

                    // Draw the rounded rectangle
                    c.drawRoundRect(pinRect, backgroundCornerRadius, backgroundCornerRadius, Paint().apply {
                        color = pinBackgroundColor
                    })

                    // Draw pin icon in white
                    iconPin!!.setTint(Color.WHITE) // Set tint to white
                    val pinIconMargin = (itemView.height - iconPin.intrinsicHeight) / 2
                    val pinIconTop = itemView.top + pinIconMargin
                    val pinIconBottom = pinIconTop + iconPin.intrinsicHeight
                    iconPin.setBounds(
                        itemView.left + pinIconMargin,
                        pinIconTop,
                        itemView.left + pinIconMargin + iconPin.intrinsicWidth,
                        pinIconBottom
                    )
                    iconPin.draw(c)
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }

        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun onNoteClick(note: Note) {
        val action = NoteFragmentDirections.actionNoteFragmentToAddEditNoteFragment(note)
        findNavController().navigate(action)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.queryHint = "Search Notes"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    filterNotes(it)  // Filter the notes when search is submitted
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    filterNotes(it)  // Filter notes as text changes
                }
                return true
            }
        })

        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun filterNotes(query: String) {
        noteAdapter.filter(query)  // Call the filter method in the adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Prevent memory leaks by nullifying the binding
    }
}
