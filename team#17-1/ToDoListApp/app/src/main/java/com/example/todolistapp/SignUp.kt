package com.example.todolistapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class SignUp : Fragment() {

    //Declare variables
    private var mEmail: EditText? = null
    private var mPassword: EditText? = null
    private var mName: EditText? = null
    private var mPhone:EditText? = null
    private var mLogin : TextView? = null
    private var mProgressBar: ProgressBar? = null
    private var mSignUp: Button? = null
    private var fAuth : FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_sign_up, container, false)

        //Get Instances
        mEmail = view?.findViewById(R.id.EmailAddress)
        mName = view?.findViewById(R.id.PersonName)
        mPassword = view?.findViewById(R.id.Password)
        mPhone = view?.findViewById(R.id.Phone)
        mProgressBar = view?.findViewById(R.id.progressBar)
        mSignUp = view?.findViewById(R.id.SignUpButton)
        mLogin = view?.findViewById(R.id.textLogin)

        //Get Firebase Instance
        fAuth = FirebaseAuth.getInstance()

        //Get FireStore Instance
        val db = Firebase.firestore

        //Accessing the nav controller
        val navController = Navigation.findNavController(requireActivity(), this.id)


        //If user is already logged in, then go to the user profile
        if (fAuth?.currentUser != null) {
             navController.navigate(R.id.action_signUp_to_userProfile)
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
            if(phone.length < 10){
                mPhone?.error = "Phone Number Must be >= 10 Characters"
                return@setOnClickListener
            }
            //Get Collection Reference
            val users = db.collection("users")

            //Create a new user
            val user = hashMapOf(
                "name" to name,
                "email" to email,
                "phone" to phone
            )

            //Make the progress bar visible
            mProgressBar?.visibility = View.VISIBLE

            //  Register the user in firebase
            fAuth?.createUserWithEmailAndPassword(email, password)?.addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    //Add the user to the database
                     users.document("${fAuth?.currentUser?.uid}").set(user)

                    // Sign in success, go to profile fragment
                    Toast.makeText(activity, "User Created.", Toast.LENGTH_SHORT).show()
                    navController.navigate(R.id.action_signUp_to_userProfile)
                }
                else {

                    //If sign in fails, display a message to the user.
                    Toast.makeText(activity, "Error ! " + task.exception?.message, Toast.LENGTH_SHORT).show()
                    mProgressBar?.visibility = View.GONE
                }
            }
        }
        mLogin?.setOnClickListener{
            navController.navigate(R.id.action_signUp_to_login)
        }

        return view
    }

}