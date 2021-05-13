package com.kassim.tictockonline.ui.login

import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

import com.kassim.tictockonline.R

class LoginFragment : Fragment() {

    private lateinit var loginViewModel: LoginViewModel
    private var isSignIn = false
    lateinit var loadingProgressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        val usernameEditText = view.findViewById<EditText>(R.id.username)
        val passwordEditText = view.findViewById<EditText>(R.id.password)
        val loginButton = view.findViewById<Button>(R.id.login)
        loadingProgressBar = view.findViewById<ProgressBar>(R.id.loading)
        val singInOrUpOption = view.findViewById<TextView>(R.id.tvOption)
        val signInOrUpTextView = view.findViewById<TextView>(R.id.sinUpOrIn)
        val firstNameEditText = view.findViewById<EditText>(R.id.firstName)
        val lastNameEditText = view.findViewById<EditText>(R.id.lastName)
        val signUpButton = view.findViewById<Button>(R.id.signUP)

        // Sign In with google
        val signWithGoogleButton = view.findViewById<SignInButton>(R.id.google_sign_in_button)
        val googleSignOpt = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(requireActivity(), googleSignOpt)


        loginViewModel.loginFormState.observe(viewLifecycleOwner,
            Observer { loginFormState ->
                if (loginFormState == null) {
                    return@Observer
                }
                loginButton.isEnabled = loginFormState.isDataValid
                signUpButton.isEnabled = loginFormState.isDataValid
                loginFormState.usernameError?.let {
                    usernameEditText.error = getString(it)
                }
                loginFormState.passwordError?.let {
                    passwordEditText.error = getString(it)
                }
                loginFormState.firstNameError?.let {
                    firstNameEditText.error = getString(it)
                }
                loginFormState.lastNameError?.let {
                    lastNameEditText.error = getString(it)
                }
            })

        loginViewModel.loginResult.observe(viewLifecycleOwner,
            Observer { loginResult ->
                loginResult ?: return@Observer
                loadingProgressBar.visibility = View.GONE
                loginResult.error?.let { ex ->

                    if (ex is FirebaseAuthInvalidUserException) {

                        Toast.makeText(activity, "The user is not registered", Toast.LENGTH_SHORT).show()
                        showLoginFailed(R.string.login_failed)
                    }

                }
                loginResult.success?.let {
                    val bundle: Bundle = Bundle()
                    bundle.putString("username", it.displayName)
                    view.findNavController().navigate(R.id.action_loginFragment_to_ticTacGame, bundle)
                }
            })

        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // ignore
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {
                if (isSignIn) {
                    loginViewModel.loginDataChanged(
                        usernameEditText.text.toString(),
                        passwordEditText.text.toString()
                    )
                } else {
                    loginViewModel.signUpDataChanged(
                        usernameEditText.text.toString(),
                        passwordEditText.text.toString(),
                        firstNameEditText.text.toString(),
                        lastNameEditText.text.toString()
                    )
                }

            }
        }
        usernameEditText.addTextChangedListener(afterTextChangedListener)
        firstNameEditText.addTextChangedListener(afterTextChangedListener)
        lastNameEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (isSignIn) {
                    loginViewModel.login(
                        usernameEditText.text.toString(),
                        passwordEditText.text.toString()
                    )
                } else {
                    loginViewModel.signUp(
                        usernameEditText.text.toString(),
                        passwordEditText.text.toString(),
                        firstName = firstNameEditText.text.toString(),
                        lastName = lastNameEditText.text.toString()
                    )
                }

            }
            false
        }

        loginButton.setOnClickListener {
            loadingProgressBar.visibility = View.VISIBLE
            loginViewModel.login(
                usernameEditText.text.toString(),
                passwordEditText.text.toString()
            )
        }

        signUpButton.setOnClickListener {
            loadingProgressBar.visibility = View.VISIBLE
            loginViewModel.signUp(
                username = usernameEditText.text.toString(),
                password = passwordEditText.text.toString(),
                firstName = firstNameEditText.text.toString(),
                lastName = lastNameEditText.text.toString()
            )

        }

        signWithGoogleButton.setOnClickListener {
            signInWithGoogle(googleSignInClient)
        }

        singInOrUpOption.setOnClickListener {
            if (!isSignIn) {
                signInOrUpTextView.text = getString(R.string.sign_in_option)
                singInOrUpOption.text = getString(R.string.create_an_account_option)
                firstNameEditText.visibility = View.INVISIBLE
                lastNameEditText.visibility = View.INVISIBLE
                loginButton.visibility = View.VISIBLE
                signUpButton.visibility = View.INVISIBLE
                isSignIn = true
            } else {
                signInOrUpTextView.text = getString(R.string.action_sign_up)
                singInOrUpOption.text = getString(R.string.or_sign_in_to_your_account)
                firstNameEditText.visibility = View.VISIBLE
                lastNameEditText.visibility = View.VISIBLE
                loginButton.visibility = View.INVISIBLE
                signUpButton.visibility = View.VISIBLE
                isSignIn = false
            }
        }
    }

    private fun signInWithGoogle(googleSignInClient: GoogleSignInClient) {

        val signIntent: Intent = googleSignInClient.signInIntent
        startActivityForResult(signIntent, RC_SIGN_IN)

    }

    private fun updateUiWithUser(model: LoggedInUserView) {
        val welcome = getString(R.string.welcome) + model.displayName
        // TODO : initiate successful logged in experience
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, welcome, Toast.LENGTH_LONG).show()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {

                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle here here" + account.id)
                loadingProgressBar.visibility = View.VISIBLE
                loginViewModel.signInWithGoogle(account.idToken!!)

            } catch (e: ApiException) {

                Log.w(TAG, "Google sign in failed", e)

            }

        }
    }

    override fun onStart() {
        super.onStart()
        if (loginViewModel.isUserLoggedIn()) {

/*            loginViewModel.getUserName()
            loginViewModel.loggedUserDetails.observe(viewLifecycleOwner,
                Observer { loggedInUser ->
                    val bundle = Bundle()
                    bundle.putString("username", loggedInUser)
                    Toast.makeText(activity, "User has logged in $loggedInUser", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_loginFragment_to_ticTacGame, bundle)
                })*/

            findNavController().navigate(R.id.action_loginFragment_to_ticTacGame)


        }
    }

    override fun onResume() {
        super.onResume()
        if (loginViewModel.isUserLoggedIn()){
            Toast.makeText(activity, "User has logged in", Toast.LENGTH_SHORT).show()
        }

    }

    companion object {
        const val TAG = "GoogleActivity"
        const val RC_SIGN_IN = 9001
    }
}