package com.example.mynoteapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.mynoteapp.R
import com.example.mynoteapp.databinding.RecyclerRowBinding
import com.example.mynoteapp.model.Note
import com.example.mynoteapp.roomdb.NoteDatabase
import com.example.mynoteapp.roomdb.NotesDao
import com.example.mynoteapp.view.ListeFragmentDirections
import io.reactivex.rxjava3.disposables.CompositeDisposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NotesAdapter(val notesListesi : MutableList<Note>) : RecyclerView.Adapter<NotesAdapter.NotesHolder>() {

    private lateinit var db : NoteDatabase
    private lateinit var  noteDao : NotesDao


    // CoroutineScope, class level'da tanımlandı
    private val coroutineScope = CoroutineScope(Dispatchers.Main)


    class NotesHolder(val binding: RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesHolder {
        var recyclerRowBinding =
            RecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        db = Room.databaseBuilder(parent.context, NoteDatabase::class.java, "Notes").build()
        noteDao = db.notesDao()

        return NotesHolder(recyclerRowBinding)

    }

    override fun getItemCount(): Int {
        return notesListesi.size

    }

    override fun onBindViewHolder(holder: NotesHolder, position: Int) {
        holder.binding.recyclerViewTitle.text = notesListesi[position].title
        holder.binding.recyclerViewSubtitle.text = notesListesi[position].note

        val timestamp = notesListesi[position].date // Long türünde tarih verisi
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(Date(timestamp))
        holder.binding.recyclerViewDate.text = formattedDate
        holder.itemView.setOnClickListener {
            val action = ListeFragmentDirections.actionListeFragmentToNoteFragment(
                bilgi = "eski",
                id = notesListesi[position].id
            )
            Navigation.findNavController(it).navigate(action)
        }

        holder.itemView.setOnLongClickListener {
            showDeleteDialog(it.context, position)

            // true döndürerek işlemin tamamlandığını belirt
            true
        }

    }

    fun showDeleteDialog(context: Context, position: Int) {
        val builder = AlertDialog.Builder(context, R.style.CustomAlertDialog)
        builder.setMessage("Silmek ister misiniz?")
            .setCancelable(true)  // Dialog dışına tıklanarak kapanabilmesi için true yapıyoruz
            .setPositiveButton("Evet") { dialog, id ->
                // Silme işlemini burada gerçekleştir
                deleteNote(position)  // Burada silme işlemi gerçekleştirilir
            }
            .setNegativeButton("Hayır") { dialog, id ->
                dialog.dismiss()  // "Hayır" dediyse, dialog'u kapat
            }

        // Dialog'u göster
        builder.create().show()
    }

    fun deleteNote(position: Int) {
        val silinecekNot = notesListesi[position]

        CoroutineScope(Dispatchers.IO).launch {
            try {
                noteDao.delete(silinecekNot)
                    .subscribe({

                        CoroutineScope(Dispatchers.Main).launch {
                            (notesListesi as MutableList).removeAt(position)
                            notifyItemRemoved(position)
                            notifyItemRangeChanged(position, notesListesi.size)
                        }
                    }, { error ->
                      println(error)

                    })
            } catch (e: Exception) {
                println(e)

            }
        }
    }



}