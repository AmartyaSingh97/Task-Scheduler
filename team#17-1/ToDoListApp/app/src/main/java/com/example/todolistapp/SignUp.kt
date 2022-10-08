package com.example.todolistapp

import android.os.Bundle
import android.provider.ContactsContract
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.FirebaseAuth


class SignUp : Fragment() {

    //Declare variables
    private var mEmail: EditText? = null
    private var mPassword: EditText? = null
    private var mName: EditText? = null
    private var mPhone:EditText? = null
    private var mProgressBar: ProgressBar? = null
    private var mSignUp: Button? = null
    private var fAuth : FirebaseAuth? = null
    private var mNavHostFragment: NavHostFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Get Instances
        mEmail = view?.findViewById(R.id.EmailAddress)
        mName = view?.findViewById(R.id.PersonName)
        mPassword = view?.findViewById(R.id.Password)
        mPhone = view?.findViewById(R.id.Phone)
        mProgressBar = view?.findViewById(R.id.progressBar)
        mSignUp = view?.findViewById(R.id.SignUpButton)

        //Get Firebase Instance
        fAuth = FirebaseAuth.getInstance()

        //Get NavHostFragment Instance
        mNavHostFragment = activity?.supportFragmentManager?.findFragmentById(R.id.signUp) as NavHostFragment?

        //If user is already logged in, then go to the main activity
        if (fAuth?.currentUser != null) {
            mNavHostFragment?.navController?.navigate(R.id.userProfile)
        }

        //Set On Click Listener
        mSignUp?.setOnClickListener {

            val email = mEmail?.text.toString().trim()
            val password = mPassword?.text.toString().trim()
            val name = mName?.text.toString().trim()
            val phone = mPhone?.text.toString().trim()

            if (email.isEmpty()) {
                mEmail?.error = "Email is Required."
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                mPassword?.error = "Password is Required."
                return@setOnClickListener
            }

            if (password.length < 6) {
                mPassword?.error = "Password Must be >= 6 Characters"
                return@setOnClickListener
            }

            //Make the progress bar visible
            mProgressBar?.visibility = View.VISIBLE

            //  Register the user in firebase
            fAuth?.createUserWithEmailAndPassword(email, password)?.addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    // Sign in success, go to profile fragment
                    Toast.makeText(activity, "User Created.", Toast.LENGTH_SHORT).show()
                    mNavHostFragment?.navController?.navigate(R.id.userProfile)
                }
                else {

                    //If sign in fails, display a message to the user.
                    Toast.makeText(activity, "Error ! " + task.exception?.message, Toast.LENGTH_SHORT).show()
                    mProgressBar?.visibility = View.GONE
                }
            }
        }

    }
}