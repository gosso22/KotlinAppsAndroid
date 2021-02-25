package com.kassim.mynoteapp

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import com.kassim.mynoteapp.NotesDB.DbManager
import com.kassim.mynoteapp.databinding.ActivityMainBinding
import com.kassim.mynoteapp.databinding.NotesListViewBinding.*
import com.kassim.mynoteapp.model.Note

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    var listOfNotes = arrayListOf<Note>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        val rootView = binding.root
        setContentView(rootView)

/*        listOfNotes.add(Note(1, "Meet professor", "This can be done usually " +
                "via email or phone at the professor's discretion. A good rule of thumb is to visit " +
                "your professors once a week. Before major projects or exams is also good so that you " +
                "can get the proper help if needed. Don't: Wait until you get a bad grade to meet with " +
                "your professor."))

        listOfNotes.add(Note(2, "Study Calculus", "CliffsNotes study guides " +
                "are written by real teachers and professors, so no matter what you're studying, " +
                "CliffsNotes can ease your homework headaches and help you score high on exams"))

        listOfNotes.add(Note(3,"Take Dog to Vet", "What to expect in a " +
                "curbside veterinary service During a curbside appointment, you call the front desk " +
                "when you arrive, then wait inside your vehicle for the hospital staff to come out " +
                "and retrieve your pet."))*/

        // Load from DB
        LoadQuery("%")


    }

    fun LoadQuery(title: String) {
        listOfNotes.clear()
        val notedbManager = DbManager(this)

        val projectionIn = arrayOf("ID", "title", "Content")
        val selection: String? = null
        val selectionArgs = arrayOf(title)
        val sortOrder = "ID"

        val cursor = notedbManager.QueryDB(null, "title like ?", selectionArgs, sortOrder)

        if (cursor.moveToFirst()) {

            do {

                val ID = cursor.getInt(cursor.getColumnIndex("ID"))
                val title = cursor.getString(cursor.getColumnIndex("title"))
                val content = cursor.getString(cursor.getColumnIndex("Content"))

                listOfNotes.add(Note(ID, title, content))

            } while (cursor.moveToNext())

        }

        val adapter = MyNotesAdapter(listOfNotes, this)
        binding.notesList.adapter = adapter

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.main_menu, menu)
        val sv: SearchView = menu!!.findItem(R.id.app_bar_search).actionView as SearchView

        val sm = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        sv.setSearchableInfo(sm.getSearchableInfo(componentName))
        sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Toast.makeText(applicationContext, query, Toast.LENGTH_LONG).show()
                LoadQuery("%$query%")
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.addNote -> {
                val intent = Intent(this, AddNote::class.java)
                startActivity(intent)
            }

            R.id.app_bar_search -> {

            }
        }

        return super.onOptionsItemSelected(item)
    }

    inner class MyNotesAdapter(listOfNotes: ArrayList<Note>, context: Context) : BaseAdapter() {

        var listOfNotes: ArrayList<Note>? = listOfNotes
        var context = context

        override fun getCount(): Int {
            return listOfNotes!!.size
        }

        override fun getItem(position: Int): Any {
            return listOfNotes!![position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

            val note: Note = listOfNotes!![position]
            val noteBinding = inflate(layoutInflater)

            val noteTitle = note.getNoteTitle()
            val noteContent = note.getNoteContent()
            noteBinding.tvTitle.text = noteTitle
            noteBinding.tvNotes.text = noteContent

            noteBinding.buDelete.setOnClickListener {
                val notedbManager = DbManager(context)

                notedbManager.Delete(" ID = ?", arrayOf("${note.getNoteID()}"))
                LoadQuery("%")
            }

            noteBinding.buEdit.setOnClickListener {

                GoToEditNote(note)

            }

            return noteBinding.root

        }

    }

    fun GoToEditNote(note: Note) {

        val intent = Intent(this, AddNote::class.java)
        intent.putExtra("ID", note.getNoteID())
        intent.putExtra("title", note.getNoteTitle())
        intent.putExtra("content", note.getNoteContent())
        startActivity(intent)


    }

    override fun onResume() {
        super.onResume()
        LoadQuery("%")

    }

}