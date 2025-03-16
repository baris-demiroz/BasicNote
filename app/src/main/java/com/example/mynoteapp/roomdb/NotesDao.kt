package com.example.mynoteapp.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.mynoteapp.model.Note
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface NotesDao {

    @Query("SELECT * FROM Note")
    fun getAll() : Flowable<List<Note>>

    @Query("SELECT * FROM Note WHERE id = :id")
    fun findById(id: Int) : Flowable<Note>

    @Insert
    fun insert(note: Note) : Completable

    @Update
    fun update(note: Note): Completable

    @Delete
    fun delete(note: Note) : Completable

}