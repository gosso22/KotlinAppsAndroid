package com.kassim.firebasedemo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.kassim.firebasedemo.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    lateinit var binding: ActivityMainBinding
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        mAuth = FirebaseAuth.getInstance()
        setContentView(binding.root)
    }

    fun loginEvent(view: View) {

        val email = binding.edEmail.text.toString()
        val password = binding.edPassword.text.toString()
        loginToFirebase(email, password)

    }

    fun loginToFirebase(email:String, password: String) {

        mAuth!!.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) {task ->

                    if (task.isSuccessful) {

                        Toast.makeText(this, "Successful login!", Toast.LENGTH_LONG).show()
                        val currentUser = mAuth!!.currentUser
                        Log.d("Login: ", currentUser!!.uid)

                    } else {

                        Toast.makeText(this, "Failed login!", Toast.LENGTH_LONG).show()
                    }

                }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = mAuth!!.currentUser

        if (currentUser != null) {

            val intent = Intent(this, Control::class.java)
            startActivity(intent)
        }

    }

}