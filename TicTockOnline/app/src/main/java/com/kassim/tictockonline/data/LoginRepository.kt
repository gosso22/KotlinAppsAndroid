package com.kassim.tictockonline.data

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(val dataSource: LoginDataSource) {

    // in-memory cache of the loggedInUser object
    var user: FirebaseUser? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user = null
    }

    fun logout() {
        user = null
        dataSource.logout()
    }

    fun login(username: String, password: String): Task<AuthResult> {
        // handle login
        val result = dataSource.login(username, password)

        result.addOnCompleteListener { task ->

            if (task.isComplete) {

                if (task.isSuccessful) {
                    setLoggedInUser(result.result!!.user)

                }
            }

        }

        return result
    }

    fun signUpWithEmail(username: String, password: String): Task<AuthResult> {

        val result = dataSource.signUpWithEmail(username, password)

        result.addOnCompleteListener { task ->

            if (task.isComplete) {

                if (task.isSuccessful) {

                    setLoggedInUser(result.result!!.user)

                }

            }

        }

        return result
    }

    fun signInWithGoogle(tokenId: String): Task<AuthResult>? {

        val result = dataSource.signInWithGoogle(tokenId)?.addOnCompleteListener { task ->

            if (task.isComplete) {

                if (task.isSuccessful) {

                    setLoggedInUser(task.result!!.user)

                }

            }
        }

        return result

    }

    private fun setLoggedInUser(loggedInUser: FirebaseUser?) {
        this.user = loggedInUser
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    fun saveLoggedInUser(firstName: String?, lastName: String?, uid: String, emailAddress: String) {

        dataSource.saveUserDetails(firstName ?: "Player", lastName ?: "X", uid, emailAddress)

    }
}