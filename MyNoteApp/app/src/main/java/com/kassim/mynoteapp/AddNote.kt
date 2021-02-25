package com.kassim.mynoteapp

import android.content.ContentValues
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.kassim.mynoteapp.NotesDB.DbManager
import com.kassim.mynoteapp.databinding.ActivityAddNoteBinding
import java.lang.Exception

class AddNote : AppCompatActivity() {

    lateinit var binding: ActivityAddNoteBinding

    var noteID: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        val rootView = binding.root
        setContentView(rootView)

        var bundle: Bundle? = intent.extras


        if (bundle != null) {

            try {

                noteID = bundle.getInt("ID")

                if (noteID != 0) {

                    binding.etTitle.setText(bundle.getString("title"))
                    binding.metContent.setText(bundle.getString("content"))

                }

            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }


    }

    fun buAdd(view: View) {

        val notesDbManager = DbManager(this)
        var contentValues = ContentValues()

        contentValues.put("title", binding.etTitle.text.toString())
        contentValues.put("Content", binding.metContent.text.toString())

        if (noteID == 0) {

            val ID = notesDbManager.Insert(contentValues)

            if (ID > 0) {

                Toast.makeText(this, " note was added!", Toast.LENGTH_LONG).show()
            } else {

                Toast.makeText(this, " can not add note!", Toast.LENGTH_LONG).show()
            }
        } else {

            var selectionArgs = arrayOf(noteID.toString())
            val ID = notesDbManager.Update(contentValues, " ID=? ", selectionArgs)

            if (ID > 0) {

                Toast.makeText(this, " note was added!", Toast.LENGTH_LONG).show()
            } else {

                Toast.makeText(this, " can not add note!", Toast.LENGTH_LONG).show()
            }

        }

        finish()

    }
}