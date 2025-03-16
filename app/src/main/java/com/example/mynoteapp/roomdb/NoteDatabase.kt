package com.example.mynoteapp.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mynoteapp.model.Note

@Database(entities = [Note::class], version = 1)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun notesDao(): NotesDao
}