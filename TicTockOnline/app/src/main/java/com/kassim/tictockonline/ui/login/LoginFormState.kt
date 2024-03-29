package com.kassim.tictockonline.ui.login

/**
 * Data validation state of the login form.
 */
data class LoginFormState(
    val usernameError: Int? = null,
    val passwordError: Int? = null,
    val firstNameError: Int? = null,
    val lastNameError: Int? = null,
    val isDataValid: Boolean = false
)