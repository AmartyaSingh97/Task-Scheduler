package com.example.todolistapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class Project : Fragment() {
    var projectName : String? = null
    var projectId : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_project, container, false)
        projectName = arguments?.getString("projectName").toString()
        projectId = arguments?.getString("projectId").toString()
        val mProjectName : TextView = view.findViewById(R.id.ProjectName)
        mProjectName.text = projectName
        // Inflate the layout for this fragment
        return  view
    }

}