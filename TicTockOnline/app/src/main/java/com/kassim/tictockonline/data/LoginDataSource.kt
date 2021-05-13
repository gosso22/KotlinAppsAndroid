package com.kassim.tictockonline.data

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
import java.lang.Exception
import java.util.*
import kotlin.collections.HashMap

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firebaseDB = FirebaseDatabase.getInstance()
    private val dbReference = firebaseDB.reference

    fun login(username: String, password: String): Task<AuthResult> {

        var result: Task<AuthResult>? = null

        try {
            result = mAuth.signInWithEmailAndPassword(username, password)
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        return result!!
    }

    fun signUpWithEmail(username: String, password: String): Task<AuthResult> {

        var result: Task<AuthResult>? = null

        try {

            result = mAuth.createUserWithEmailAndPassword(username, password)

        } catch (e: Throwable) {
            e.printStackTrace()
        }

        return result!!

    }

    fun signInWithGoogle(tokenId: String): Task<AuthResult>? {

        var result: Task<AuthResult>? = null
        val credential = GoogleAuthProvider.getCredential(tokenId, null)

        try {

            result = mAuth.signInWithCredential(credential)

        } catch (e: Exception) {

            e.printStackTrace()
        }

        return result

    }

    fun saveUserDetails(firstName: String, lastName: String, uid: String, emailAddress: String) {

        val userRefId = splitString(emailAddress)
        dbReference.child("Users").child(userRefId).child("first_name").setValue(firstName)
        dbReference.child("Users").child(userRefId).child("last_name").setValue(lastName)
        dbReference.child("Users").child(userRefId).child("email").setValue(emailAddress)
        dbReference.child("Users").child(userRefId).child("Request").setValue(uid)

    }

    fun getUserName():  Task<DataSnapshot> {

        val userRefId = splitString(mAuth.currentUser!!.email!!.toString())

        return dbReference.child("Users").child(userRefId)
            .child("first_name").get()

    }

    fun getUserEmail(): Task<DataSnapshot> {

        val userRefId = splitString(mAuth.currentUser!!.email!!.toString())

        return dbReference.child("Users").child(userRefId).child("email").get()

    }

    fun requestToPlayGame(userRefId: String) {

        dbReference.child("Users").child(userRefId).child("Request").push().setValue(mAuth.currentUser!!.email)

    }

    fun incomingRequestListener(): DatabaseReference {
        val userRefId = splitString(mAuth.currentUser!!.email!!.toString())
        return dbReference.child("Users").child(userRefId).child("Request").ref
    }

    fun getPlayOnlineRef(sessionId: String): DatabaseReference {
        return dbReference.child("PlayOnline").child(sessionId).ref
    }

    fun storeButtonCliked(sessionId:String, cellID:String, userEmail:String) {

        dbReference.child("PlayOnline").child(sessionId).child("_$cellID").setValue(userEmail)

    }

    fun logout() {
        // TODO: revoke authentication
    }

    fun splitString(str: String): String {

        val splittedString = str.split("@")

        return splittedString[0]

    }

    fun resetGame() {

        dbReference.child("PlayOnline").removeValue()
    }
}