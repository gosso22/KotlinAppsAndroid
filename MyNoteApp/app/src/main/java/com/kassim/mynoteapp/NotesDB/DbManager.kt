package com.kassim.mynoteapp.NotesDB

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import android.widget.Toast

class DbManager {

    val dbName = "MyNotes"
    val dbTable = "Notes"
    val colID = "ID"
    val colTitle = "title"
    val colContent = "Content"
    val dbVersion = 1

    // Create a table for the application
    val sqlCreateTable = "CREATE TABLE IF NOT EXISTS $dbTable (" +
            "$colID INTEGER PRIMARY KEY," +
            "$colTitle TEXT," +
            "$colContent TEXT);"

    var sqlDB : SQLiteDatabase? = null

    constructor(context: Context) {

        var dbHelper = DatabaseHelperNotes(context)
        this.sqlDB = dbHelper.writableDatabase

    }

    inner class DatabaseHelperNotes: SQLiteOpenHelper {
        var context: Context? = null

        constructor(context: Context): super(context, dbName, null, dbVersion) {
            this.context = context
        }

        override fun onCreate(db: SQLiteDatabase?) {
            db!!.execSQL(sqlCreateTable)
            Toast.makeText(context, " database is created!", Toast.LENGTH_LONG).show()
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db!!.execSQL("DROP TABLE IF EXISTS $dbTable")
        }
    }

    fun Insert(value: ContentValues): Long {
        val ID = sqlDB!!.insert(dbTable, null, value)

        return ID
    }

    fun QueryDB(projection: Array<String>?, selection: String, selectionArgs: Array<String>, sortOrder: String): Cursor {

        val qb = SQLiteQueryBuilder()
        qb.tables = dbTable

        val cursor = qb.query(sqlDB, projection, selection, selectionArgs, null, null, sortOrder)


        return cursor

    }

    fun Delete(selection: String, selectionArgs: Array<String>): Int {

        val count = sqlDB!!.delete(dbTable, selection, selectionArgs)

        return count

    }

    fun Update(value: ContentValues, selection: String, selectionArgs: Array<String>) : Int {

        val count = sqlDB!!.update(dbTable, value, selection, selectionArgs)

        return count
    }
}