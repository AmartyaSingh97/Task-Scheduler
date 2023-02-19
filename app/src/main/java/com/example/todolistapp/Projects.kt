package com.example.todolistapp

import android.app.AlertDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistapp.adapter.SwipeToDeleteCallback
import com.example.todolistapp.model.ProjectModel
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class Projects : Fragment() {

    private var mImageBtn: ImageButton? = null
    private var mFloatBtn: FloatingActionButton? = null
    private var recyclerView: RecyclerView? = null
    private var fAuth: FirebaseAuth? = null
    private var fStore: FirebaseFirestore? = null
    private var userID: String? = null
    private var myView : View? = null
    private var mBase : DatabaseReference? = null
    private var loader : ProgressDialog? = null
    private var navController : androidx.navigation.NavController? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myView = inflater.inflate(R.layout.fragment_projects, container, false)
        //Get Nav Controller
        navController = Navigation.findNavController(requireActivity(), this.id)
        //Get Instances
        mImageBtn = myView?.findViewById(R.id.userProfile)
        mFloatBtn = myView?.findViewById(R.id.addProjectButton)
        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        userID = fAuth?.currentUser?.uid
        mBase = FirebaseDatabase.getInstance().reference.child("Projects").child(userID.toString())
        recyclerView = myView?.findViewById(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(context)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        recyclerView?.layoutManager = layoutManager

        loader = ProgressDialog(context)
        loader?.setMessage("Loading Projects...")
        loader?.setCancelable(false)

        //Set On Click Listeners
        mFloatBtn?.setOnClickListener {
            addProject()
        }
        //Go to User Profile
        mImageBtn?.setOnClickListener {
            navController!!.navigate(R.id.action_projects_to_userProfile)
        }


        return myView
    }


    //Add Project
    private fun addProject() {
        val myDialog: AlertDialog.Builder = AlertDialog.Builder(this.context)
        val inflater: LayoutInflater = LayoutInflater.from(this.context)
        val myView: View = inflater.inflate(R.layout.input_project, null)

        myDialog.setView(myView)

        val dialog: AlertDialog = myDialog.create()
        dialog.setCancelable(false)
        dialog.show()

        val mProject: EditText? = myView.findViewById(R.id.project_name)
        val mStartDate: EditText? = myView.findViewById(R.id.start_date)
        val mEndDate: EditText? = myView.findViewById(R.id.end_date)
        val saveBtn: Button? = myView.findViewById(R.id.save_btn)
        val cancelBtn: Button? = myView.findViewById(R.id.cancel_btn)

        //Cancel Button
        cancelBtn?.setOnClickListener {
            dialog.dismiss()
        }

        //Save Button
        saveBtn?.setOnClickListener {
            val project = mProject?.text.toString().trim()
            val startDate = mStartDate?.text.toString().trim()
            val endDate = mEndDate?.text.toString().trim()
            val id : String = mBase?.push()?.key.toString()

            if (project.isEmpty()) {
                mProject?.error = "Required"
                return@setOnClickListener
            }
            if (startDate.isEmpty()) {
                mStartDate?.error = "Required"
                return@setOnClickListener
            }
            if (endDate.isEmpty()) {
                mEndDate?.error = "Required"
                return@setOnClickListener
            }
            if(startDate.length != 10){
                mStartDate?.error = "Invalid Date"
                return@setOnClickListener
            }
            if(endDate.length != 10){
                mEndDate?.error = "Invalid Date"
                return@setOnClickListener
            }
            if(startDate[2] != '/' || startDate[5] != '/'){
                mStartDate?.error = "Invalid Date"
                return@setOnClickListener
            }
            if(endDate[2] != '/' || endDate[5] != '/'){
                mEndDate?.error = "Invalid Date"
                return@setOnClickListener
            }
            else{
                loader?.setMessage("Adding Project")
                loader?.setCanceledOnTouchOutside(false)
                loader?.show()
                val data = ProjectModel(project, startDate, endDate, id)
                mBase?.child(id)?.setValue(data)?.addOnCompleteListener {
                    if (it.isSuccessful) {
                        loader?.dismiss()
                        Toast.makeText(context, "Project Added", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    } else {
                        loader?.dismiss()
                        Toast.makeText(context, "Error: " + it.exception?.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }



    //Load Projects
    override fun onStart() {
        super.onStart()
        loader?.show()
        val options : FirebaseRecyclerOptions<ProjectModel> = FirebaseRecyclerOptions.Builder<ProjectModel>()
            .setQuery(mBase!!, ProjectModel::class.java)
            .build()

        val adapter : FirebaseRecyclerAdapter<ProjectModel,MyViewHolder> = object : FirebaseRecyclerAdapter<ProjectModel, MyViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
                val view : View = LayoutInflater.from(parent.context).inflate(R.layout.retrieved_layout, parent, false)
                return MyViewHolder(view)
            }

            override fun onBindViewHolder(holder: MyViewHolder, position: Int, model: ProjectModel) {
                model.getProjectName()?.let { holder.setProject(it) }
                model.getStartDate()?.let { holder.setStartDate(it) }
                model.getEndDate()?.let { holder.setEndDate(it) }

                //Going to details of project
                holder.itemView.setOnClickListener {
                    val args = Bundle()
                    args.putString("projectName", model.getProjectName())
                    args.putString("projectId",model.getId())
                    navController!!.navigate(R.id.action_projects_to_project,args)
                }
            }

        }
        recyclerView?.adapter = adapter
        adapter.startListening()
        loader?.dismiss()
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

    }

    private val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
       fun CallBack(dragDirs: Int, swipeDirs: Int) : Boolean {
            return true
        }

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
            val adapter = recyclerView?.adapter as FirebaseRecyclerAdapter<*, *>
            adapter.getRef(position).removeValue()
            deleteProject()
        }
    }

    //Delete Project
    fun deleteProject(){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Delete Project")
        builder.setMessage("Are you sure you want to delete this project?")
        builder.setPositiveButton("Yes"){dialog, which ->
            val id = mBase?.push()?.key.toString()
            mBase?.child(id)?.removeValue()?.addOnSuccessListener{
                Toast.makeText(context, "Project Deleted", Toast.LENGTH_SHORT).show() }
            dialog.dismiss()
        }
        builder.setNegativeButton("No"){dialog, which ->
            dialog.dismiss()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    //View Holder
       class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var mProject: TextView? = null
        private var mStartDate: TextView? = null
        private var mEndDate: TextView? = null
          init {
              mProject = itemView.findViewById(R.id.projectName)
              mStartDate = itemView.findViewById(R.id.startDate)
              mEndDate = itemView.findViewById(R.id.endDate)
          }
            fun setProject(project: String) {
                mProject?.text = project
            }
            fun setStartDate(startDate: String) {
                mStartDate?.text = startDate
            }
            fun setEndDate(endDate: String) {
                mEndDate?.text = endDate
            }

      }
}
