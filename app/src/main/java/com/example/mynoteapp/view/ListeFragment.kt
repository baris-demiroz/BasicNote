package com.example.mynoteapp.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.mynoteapp.R
import com.example.mynoteapp.adapter.NotesAdapter
import com.example.mynoteapp.databinding.FragmentListeBinding
import com.example.mynoteapp.databinding.FragmentNoteBinding
import com.example.mynoteapp.model.Note
import com.example.mynoteapp.roomdb.NoteDatabase
import com.example.mynoteapp.roomdb.NotesDao
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class ListeFragment : Fragment() {
    private var _binding: FragmentListeBinding? = null
    private val binding get() = _binding!!

    private val mDisposable = CompositeDisposable()
    private var secilenNote : Note? = null

    private lateinit var db : NoteDatabase
    private lateinit var  noteDao : NotesDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = Room.databaseBuilder(requireContext(), NoteDatabase::class.java,"Notes").build()
        noteDao = db.notesDao()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.floatingActionButton.setOnClickListener{
            ekle(it)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        verileriAl()

    }

    private fun verileriAl(){
        mDisposable.add(
            noteDao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handeResponse)
        )
    }

    private fun handeResponse(notes : List<Note>){
        val adapter = NotesAdapter(notes.toMutableList())
        binding.recyclerView.adapter = adapter

    }


    private fun ekle(view: View){
        val action = ListeFragmentDirections.actionListeFragmentToNoteFragment("yeni",-1)
        Navigation.findNavController(view).navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mDisposable.clear()
    }

}