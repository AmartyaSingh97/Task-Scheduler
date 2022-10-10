package com.example.todolistapp.data

import com.example.todolistapp.model.ProjectModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

public class ProjectsDatasource {
    private var fAuth: FirebaseAuth? = null
    private var fStore: FirebaseFirestore? = null
    private var userID: String? = null
    private var list : ArrayList<ProjectModel>? = null

    public fun loadProjects() : ArrayList<ProjectModel> {
        fAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        userID = fAuth?.currentUser?.uid
        list = ArrayList<ProjectModel>()
        fStore?.collection("projects")?.whereEqualTo("userID", userID)?.addSnapshotListener { value, error ->
            if (error != null) {
                return@addSnapshotListener
            }
            for (doc in value!!) {
                val project = doc.getString("project").toString()
                val startDate = doc.getString("startDate").toString()
                val endDate = doc.getString("endDate").toString()
                val projectModel = ProjectModel(project, startDate, endDate)
                list?.add(projectModel)
            }
        }
        return list as ArrayList<ProjectModel>
    }
}