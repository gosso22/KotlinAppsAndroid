package com.kassim.tictockonline.ui.login

/**
 * User details post authentication that is exposed to the UI
 */
data class LoggedInUserView(
    val displayName: String,
    val id: String
    //... other data fields that may be accessible to the UI
)