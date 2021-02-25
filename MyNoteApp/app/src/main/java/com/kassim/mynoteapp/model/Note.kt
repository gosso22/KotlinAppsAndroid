package com.kassim.mynoteapp.model

open class Note {

    private var noteID: Int? = null
    private var noteTitle: String? = null
    private var noteContent: String? = null

    constructor(noteID: Int, noteTitle: String?, noteContent: String?) {
        this.noteID = noteID
        this.noteTitle = noteTitle
        this.noteContent = noteContent
    }

    fun getNoteID(): Int? {

        return this.noteID
    }

    fun getNoteTitle(): String? {

        return this.noteTitle
    }

    fun getNoteContent(): String? {

        return this.noteContent
    }

}