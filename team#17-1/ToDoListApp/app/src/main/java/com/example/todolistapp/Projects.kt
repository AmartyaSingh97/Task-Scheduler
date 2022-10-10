package com.example.todolistapp

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistapp.adapter.MyAdapter
import com.example.todolistapp.data.ProjectsDatasource
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Projects : Fragment() {

    private var mImageBtn : ImageButton? = null
    private var mFloatBtn : FloatingActionButton? = null
    private var recyclerView : RecyclerView? = null
    private var fAuth: FirebaseAuth? = null
    private var fStore: FirebaseFirestore? = null
    private var userID: String? = null
    private var myAdapter: RecyclerView.Adapter<MyAdapter.MyViewHolder>? = null
    private var layoutManager: RecyclerView.LayoutManager? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_projects, container, false)

        //Get Nav Controller
        val navController = Navigation.findNavController(requireActivity(), this.id)
        //Get Instances
        mImageBtn = view?.findViewById(R.id.userProfile)
        mFloatBtn = view?.findViewById(R.id.addProjectButton)
        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        userID = fAuth?.currentUser?.uid

        recyclerView = view?.findViewById(R.id.recyclerView)

        //Set On Click Listeners
        mFloatBtn?.setOnClickListener {
            addProject()
        }


        //Go to User Profile
        mImageBtn?.setOnClickListener{
            navController.navigate(R.id.action_projects_to_userProfile)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            val dataSource = ProjectsDatasource()
            val myDataset =  dataSource.loadProjects()
            myAdapter =  MyAdapter(this!!.context, myDataset)
            recyclerView?.adapter = myAdapter
            recyclerView?.setHasFixedSize(true)
        }
    }
    //Add Project
    private fun addProject(){
        val myDialog : AlertDialog.Builder = AlertDialog.Builder(this.context)
        val inflater : LayoutInflater = LayoutInflater.from(this.context)
        val myView : View = inflater.inflate(R.layout.input_project, null)

        myDialog.setView(myView)

        val dialog : AlertDialog = myDialog.create()
        dialog.setCancelable(false)
        dialog.show()

        val mProject : EditText? = myView.findViewById(R.id.project_name)
        val mStartDate : EditText? = myView.findViewById(R.id.start_date)
        val mEndDate : EditText? = myView.findViewById(R.id.end_date)
        val saveBtn : Button? = myView.findViewById(R.id.save_btn)
        val cancelBtn : Button? = myView.findViewById(R.id.cancel_btn)

        //Cancel Button
        cancelBtn?.setOnClickListener{
            dialog.dismiss()
        }

        //Save Button
        saveBtn?.setOnClickListener{
            val project = mProject?.text.toString()
            val startDate = mStartDate?.text.toString()
            val endDate = mEndDate?.text.toString()

            if(project.isEmpty()){
                mProject?.error = "Required"
                return@setOnClickListener
            }
            if(startDate.isEmpty()){
                mStartDate?.error = "Required"
                return@setOnClickListener
            }
            if(endDate.isEmpty()){
                mEndDate?.error = "Required"
                return@setOnClickListener
            }

            val projectMap : MutableMap<String, Any> = HashMap()
            projectMap["project"] = project
            projectMap["startDate"] = startDate
            projectMap["endDate"] = endDate

            //In collection USERS find the current user and add another collection called PROJECTS
            // and add the projectMap to document projectName
            fStore?.collection("users")?.document(userID.toString())?.
               collection("Projects")?.document(project)
                ?.set(projectMap)?.addOnSuccessListener {
                dialog.dismiss()
                Toast.makeText(this.context, "New Project Added !!", Toast.LENGTH_SHORT).show()
            }?.addOnFailureListener{
                dialog.dismiss()
                Toast.makeText(this.context, "Error Adding Project !!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()

    }
    }

