package com.example.todolistapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserProfile : Fragment() {

    private var mSignOutBtn: Button? = null
    private var mUserName: TextView? = null
    private var fAuth: FirebaseAuth? = null
    private var fStore: FirebaseFirestore? = null
    private var userID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_profile, container, false)

        //Get Nav Controller
        val navController = Navigation.findNavController(requireActivity(), this.id)
        //Get Firebase Instance
        fAuth = FirebaseAuth.getInstance()
        //Get FireStore Instance
        fStore = FirebaseFirestore.getInstance()
        //Get Instances
        mSignOutBtn = view?.findViewById(R.id.SignOut)
        mUserName = view?.findViewById(R.id.UserName)

        //Get User ID
        userID = fAuth?.currentUser?.uid

        //Get User Name
        val docRef = fStore?.collection("users")?.document(userID.toString())
        docRef?.addSnapshotListener(requireActivity()) { snapshot, e ->
            if (e != null) {
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                if(mUserName != null){
                    mUserName?.text = snapshot.getString("fName")
                }
            }
        }

        //Set the on click listener for the sign out button
        mSignOutBtn?.setOnClickListener {
            fAuth?.signOut()
            navController.navigate(R.id.action_userProfile_to_signUp)
        }
        return view
    }

}