package edu.uw.voelkc.photogram

import android.content.Context
import android.content.res.ColorStateList
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.fragment_gallery.*
import kotlin.random.Random


class LoginViewModel: ViewModel() {

    private val TAG = "ViewModel"

    enum class AuthenticationState {
        AUTHENTICATED, UNAUTHENTICATED, INVALID_AUTHENTICATION
    }

    //val userUID = FirebaseUserLiveData.userUID

    val authenticationState = FirebaseUserLiveData().map { user ->
        if (user != null) {
            AuthenticationState.AUTHENTICATED
        } else {
            AuthenticationState.UNAUTHENTICATED
        }
    }




}