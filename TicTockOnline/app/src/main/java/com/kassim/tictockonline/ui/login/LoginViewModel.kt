package com.kassim.tictockonline.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Patterns
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.kassim.tictockonline.data.LoginRepository

import com.kassim.tictockonline.R
import java.lang.Exception

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private val _loggedUserDetails = MutableLiveData<String>()
    val loggedUserDetails: LiveData<String> = _loggedUserDetails

    private val _loggedInUserEmail = MutableLiveData<String>()
    val loggedInUserEmail: LiveData<String> = _loggedInUserEmail

    private val _playRequests = MutableLiveData<String>()
    val playRequests: LiveData<String> = _playRequests

    private val _currentPlay = MutableLiveData<HashMap<String, String>>()
    val currentPlay: LiveData<HashMap<String, String>> = _currentPlay

    fun login(username: String, password: String) {
        // can be launched in a separate asynchronous job
        val result = loginRepository.login(username, password)

        result.addOnCompleteListener { task ->

            if (task.isComplete) {

                if (task.isSuccessful) {

                    if (task.result?.user != null) {
                        val uid = task.result!!.user!!.uid
                        _loginResult.value =
                            LoginResult(
                                success = LoggedInUserView(
                                    displayName = task.result!!.user!!.email,
                                    uid
                                )
                            )
                        loginRepository.saveLoggedInUser(
                            "Player X", //Fix me
                            "lastName",
                            uid,
                            task.result!!.user!!.email!!.toString())
                    }
                } else {
                    _loginResult.value = LoginResult(error = task.exception)
                }

            }

        }


    }

    fun signUp(username: String, password: String, firstName: String, lastName: String) {
        // can be launched in a separate asynchronous job
        val result = loginRepository.signUpWithEmail(username, password)

        result.addOnCompleteListener { task ->

            if (task.isComplete) {

                if (task.isSuccessful) {

                    if (task.result?.user != null) {
                        val uid = task.result!!.user!!.uid
                        _loginResult.value =
                            LoginResult(
                                success = LoggedInUserView(
                                    displayName = firstName,
                                    uid
                                )
                            )
                        loginRepository.saveLoggedInUser(
                            firstName,
                            lastName,
                            uid,
                            task.result!!.user!!.email!!.toString()
                        )
                    }
                } else {
                    _loginResult.value = LoginResult(error = task.exception)
                }

            }

        }


    }

    fun signInWithGoogle(tokenId: String) {

        loginRepository.signInWithGoogle(tokenId)
            ?.addOnCompleteListener { task ->

                if (task.isComplete) {

                    if (task.isSuccessful) {

                        if (task.result?.user != null) {
                            _loginResult.value =
                                LoginResult(
                                    success = LoggedInUserView(
                                        displayName = task.result!!.user!!.displayName!!,
                                        task.result!!.user!!.uid
                                    )
                                )
                            val nameSplitted = task.result!!.user!!.displayName?.split(" ")
                            loginRepository.saveLoggedInUser(
                                nameSplitted?.get(0),
                                nameSplitted?.get(1),
                                task.result!!.user.uid,
                                task.result!!.user!!.email!!.toString()
                            )
                        }
                    }
                }

            }


    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    fun signUpDataChanged(username: String, password: String, firstName: String, lastName: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else if (!isNameValid(firstName)) {
            _loginForm.value = LoginFormState(firstNameError = R.string.invalid_firstname)
        } else if (!isNameValid(lastName)) {
            _loginForm.value = LoginFormState(lastNameError = R.string.invalid_lastname)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains("@")) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else if (!username.contains("@")) {
            false
        } else {
            username.isNotBlank() && !username.contains("@")
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    private fun isNameValid(firstName: String): Boolean {

        val patterns: String = "^[a-zA-Z]{4,}(?: [a-zA-Z]+){0,2}\$"
        val regex = Regex(patterns)

        return firstName.contains(regex)
    }

    fun isUserLoggedIn(): Boolean {

        return loginRepository.dataSource.mAuth.currentUser != null

    }

    fun getUserName() {

        val result = loginRepository.dataSource.getUserName()

        result.addOnSuccessListener { task ->
            _loggedUserDetails.value = task.value.toString()
        }

    }

    fun getUserEmail() {
        val result = loginRepository.dataSource.getUserEmail()

        result.addOnSuccessListener { task ->
            _loggedInUserEmail.value = task.value.toString()
        }

    }

    fun requestToPlay(refUserId: String) {

        loginRepository.dataSource.requestToPlayGame(refUserId)
    }

    fun openPlayRequests() {

        loginRepository.dataSource.incomingRequestListener()
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    try {

                        val td = snapshot.value as HashMap<String, Any>
                        if (td != null) {

                            for (key in td.keys) {

                                _playRequests.value = td[key] as String
                                break

                            }
                        }

                    } catch (ex: Exception) {
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

    }

    fun addButtonClicked(sessionId: String, cellID: String, userEmail: String) {
        loginRepository.dataSource.storeButtonCliked(sessionId, cellID, userEmail)
    }

    fun onlineGamePlay(sessionId: String) {

        loginRepository.dataSource.getPlayOnlineRef(sessionId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.value != null) {
                        val td = snapshot!!.value as HashMap<String, Any>
                        if (td != null) {

                            _currentPlay.value = td as HashMap<String, String>
                        }
                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

    }

    fun resetGame() {

        loginRepository.dataSource.resetGame()

    }

}