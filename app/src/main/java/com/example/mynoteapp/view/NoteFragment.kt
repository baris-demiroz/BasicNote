package com.example.mynoteapp.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.room.Room
import com.example.mynoteapp.R
import com.example.mynoteapp.databinding.FragmentListeBinding
import com.example.mynoteapp.databinding.FragmentNoteBinding
import com.example.mynoteapp.model.Note
import com.example.mynoteapp.roomdb.NoteDatabase
import com.example.mynoteapp.roomdb.NotesDao
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class NoteFragment : Fragment() {
    private var _binding: FragmentNoteBinding? = null
    private val binding get() = _binding!!
    private val mDisposable = CompositeDisposable()
    private var secilenNote : Note? = null

    private lateinit var db : NoteDatabase
    private lateinit var  noteDao : NotesDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = Room.databaseBuilder(requireContext(),NoteDatabase::class.java,"Notes").build()
        noteDao = db.notesDao()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNoteBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


            binding.btnKaydet.setOnClickListener{
                val title = binding.txtTitle.text.toString().trim()
                val note = binding.txtNote.text.toString().trim()

                if (title.isNotEmpty() && note.isNotEmpty()) {
                    kaydet(it)
                } else {
                    Toast.makeText(requireContext(), "Başlık Ve Not Boş Olamaz!!!!", Toast.LENGTH_SHORT).show()
                }
            }



        binding.btnSil.setOnClickListener{
            sil(it)
        }

        arguments?.let {
            var bilgi =NoteFragmentArgs.fromBundle(it).bilgi

            if (bilgi == "yeni") {
                secilenNote = null
                binding.btnSil.isEnabled = false
                binding.btnKaydet.isEnabled = true
                binding.btnSil.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.gray))
                binding.txtNote.setText("")
                binding.txtTitle.setText("")
            }
            else{
                binding.btnKaydet.isEnabled = true
                binding.btnSil.isEnabled = true
                binding.btnKaydet.setText("Güncelle")
                binding.btnKaydet.setOnClickListener{
                    val title = binding.txtTitle.text.toString().trim()
                    val note = binding.txtNote.text.toString().trim()

                    if (title.isNotEmpty() && note.isNotEmpty()) {
                        guncelle(it)
                    } else {
                        Toast.makeText(requireContext(), "Başlık Ve Not Boş Olamaz!!!!", Toast.LENGTH_SHORT).show()
                    }

                }
                val id = NoteFragmentArgs.fromBundle(it).id

                mDisposable.add(
                    noteDao.findById(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::handleResponseForSelect)
                )

            }

        }

    }

    private fun handleResponseForSelect(note: Note){


        binding.txtTitle.setText(note.title)
        binding.txtNote.setText(note.note)
        secilenNote = note

    }


    fun kaydet(view: View){
        val title = binding.txtTitle.text.toString()
        val note = binding.txtNote.text.toString()


        if (note != null){
            val currentTime = System.currentTimeMillis()


            val notes =Note(title,note,currentTime)

            //Rxjava
            mDisposable.add(
                noteDao.insert(notes)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleResponseForInsert))




        }
    }

    fun sil(view: View){
        if (secilenNote != null){
            mDisposable.add(
                noteDao.delete( secilenNote!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleResponseForInsert) //GeriyeGideceğim için handleResponseForInsertü kullandım
            )
        }

    }

    fun guncelle(view: View) {
        val title = binding.txtTitle.text.toString().trim()
        val note = binding.txtNote.text.toString().trim()
        val currentTime = System.currentTimeMillis()

        if (secilenNote != null) {
            // Seçilen notun id'sini alıyoruz ve güncellenmiş notu oluşturuyoruz
            val updatedNote = Note(
                title = title,
                note = note,
                date = currentTime
            ).apply {
                id = secilenNote!!.id  // Seçilen notun id'sini güncelleme nesnesine atıyoruz
            }

            mDisposable.add(
                noteDao.update(updatedNote)  // Burada id dahil güncel notu gönderiyoruz
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        handleResponseForInsert() // Güncelleme başarılı olursa
                    }, { error ->

                    })
            )
        }
    }



    private fun handleResponseForInsert() {
        //Bir Önceki Fragmenta Don -mesela yani kaydettikten sonra yapılıcak birşey

        val action = NoteFragmentDirections.actionNoteFragmentToListeFragment()
        Navigation.findNavController(requireView()).navigate(action)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mDisposable.clear()
    }

}