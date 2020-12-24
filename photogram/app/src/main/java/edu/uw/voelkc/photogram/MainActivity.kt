package edu.uw.voelkc.photogram

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.fragment_gallery.*


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private val RC_SIGN_IN = 729
    private lateinit var viewModel: ViewModel
    private var isLoggedIn = false
    var currentUserUID: String? = null
    var colorMode: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.log_menu -> {
                if (!observeAuthenticationState()) {
                    createSignInIntent()
                    val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
                } else {
                   Toast.makeText(this,"Logged Out", Toast.LENGTH_SHORT).show()
                    signOut()
                }
                return true
            }

            R.id.settings_fragment -> {
                findNavController(R.id.nav_host_fragment).navigate(R.id.settings_fragment)
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun observeAuthenticationState() : Boolean{
        var state = false;
        (viewModel as LoginViewModel).authenticationState.observe(this, Observer { authenticationState ->
            when (authenticationState) {
                LoginViewModel.AuthenticationState.AUTHENTICATED -> {
                    Log.v(TAG, "Set to true")
                    state = true
                }
                else -> {
                    Log.v(TAG, "Set to false")
                    state = false
                }
            }
        })
        return state
    }


    private fun createSignInIntent() {
        // Choose authentication providers
        val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build()
        )

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN
        )
        // [END auth_fui_create_intent]
    }

    private fun signOut() {
        // [START auth_fui_signout]
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                invalidateOptionsMenu()
            }

        findNavController(R.id.nav_host_fragment).navigate(R.id.gallery_fragment)

        // [END auth_fui_signout]
    }

    fun getUID(): String? {
        return currentUserUID
    }

    // [START auth_fui_result]
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                //val user = FirebaseAuth.getInstance().currentUser
               // val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
                //it would be cool if this worked.
                //Toast.makeText(this, "User UID: ${user?.uid}", Toast.LENGTH_LONG).show()
                invalidateOptionsMenu()
                //Re-render

                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                Toast.makeText(this, "Log In Failed", Toast.LENGTH_LONG).show()
            }
        }
    }
    // [END auth_fui_result]


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        val inflater = menuInflater
        inflater.inflate(R.menu.activity_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if(FirebaseAuth.getInstance().currentUser != null){
            if (menu != null) {
                menu.findItem(R.id.log_menu).setTitle("Log Out")
            }
        } else {
            if (menu != null) {
                menu.findItem(R.id.log_menu).setTitle("Log In")
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }


    fun changeRed() {
        colorMode = 1;
    }

    fun changeCyan() {
        colorMode = 2;
    }
    fun changePurple() {
        colorMode = 0;
    }


}