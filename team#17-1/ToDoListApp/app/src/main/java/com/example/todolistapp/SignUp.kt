package com.example.todolistapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


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
    private var fStore : FirebaseFirestore? = null
    private var userID : String? = null

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
        fStore = FirebaseFirestore.getInstance()

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
            val name = mName?.text.toString()
            val phone = mPhone?.text.toString()

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

            //Make the progress bar visible
            mProgressBar?.visibility = View.VISIBLE

            //  Register the user in firebase
            fAuth?.createUserWithEmailAndPassword(email, password)?.addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    // Sign in success, go to profile fragment
                    Toast.makeText(activity, "User Created.", Toast.LENGTH_SHORT).show()

                    //Add the user to the database
                    userID = fAuth?.currentUser?.uid
                    val documentReference = fStore?.collection("users")?.document(userID.toString())
                    val user: MutableMap<String, Any> = HashMap()
                    user["fName"] = name
                    user["email"] = email
                    user["phone"] = phone
                    documentReference?.set(user)?.addOnSuccessListener {
                         Log.d("TAG", "onSuccess: user Profile is created for $userID")
                    }?.addOnFailureListener { e ->
                        Toast.makeText(activity, "Error adding document", Toast.LENGTH_SHORT).show()
                    }
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