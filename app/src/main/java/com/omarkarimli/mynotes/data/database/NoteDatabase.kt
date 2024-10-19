package com.omarkarimli.mynotes.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.omarkarimli.mynotes.data.dao.NoteDao
import com.omarkarimli.mynotes.data.entity.Note

@Database(entities = arrayOf(Note::class), version = 1)
abstract class NoteDatabase: RoomDatabase() {
    abstract fun noteDao(): NoteDao
}