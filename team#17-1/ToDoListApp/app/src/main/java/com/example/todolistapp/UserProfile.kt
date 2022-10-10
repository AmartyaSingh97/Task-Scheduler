package com.example.todolistapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserProfile : Fragment() {

    private var mSignOutBtn: Button? = null
    private var mUserName: TextView? = null
    private var fAuth: FirebaseAuth? = null
    private var fStore: FirebaseFirestore? = null
    private var userID: String? = null
    private var imageView : ImageView? = null
    private var mProjects : TextView? = null

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
        imageView = view?.findViewById(R.id.imageView)
        mProjects = view?.findViewById(R.id.Projects)

        //Get User ID
        userID = fAuth?.currentUser?.uid
        //Get User Name
        val docRef = fStore?.collection("users")?.document(userID.toString())
        docRef?.get()?.addOnSuccessListener { document ->
            if (document != null) {
                mUserName?.text = document.getString("fName")
            }
        }

        //Set the image fetched from URL to the image view
        imageView?.loadUrl("https://avatars.dicebear.com/api/avataaars/lol.svg")

        //Set the on click listener for the sign out button
        mSignOutBtn?.setOnClickListener {
            fAuth?.signOut()
            navController.navigate(R.id.action_userProfile_to_signUp)
        }

        //Set the on click listener for the projects button
        mProjects?.setOnClickListener {
            navController.navigate(R.id.action_userProfile_to_projects)
        }
        return view
    }

    //Function to load image from URL in svg format
    private fun ImageView.loadUrl(url: String) {

        val imageLoader = ImageLoader.Builder(this.context)
            .componentRegistry { add(SvgDecoder(this@loadUrl.context)) }
            .build()

        val request = ImageRequest.Builder(this.context)
            .crossfade(true)
            .crossfade(500)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.error)
            .data(url)
            .target(this)
            .build()

        imageLoader.enqueue(request)
    }
    fun onBackPressed() {
        requireActivity().finish()
    }

}