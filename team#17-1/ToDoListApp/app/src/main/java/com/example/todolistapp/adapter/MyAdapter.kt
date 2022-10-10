package com.example.todolistapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistapp.R
import com.example.todolistapp.model.ProjectModel

class MyAdapter(val context: Context, private val list :ArrayList<ProjectModel> ): RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.retrieved_layout, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
         val projectModel: ProjectModel = list[position]
         holder.name.text = projectModel.getProjectName()
         holder.startDate.text = projectModel.getStartDate()
         holder.endDate.text = projectModel.getEndDate()
    }

    override fun getItemCount(): Int {

        return list.size
    }

     class MyViewHolder (private val view: View) : RecyclerView.ViewHolder(view) {
           val name: TextView = view.findViewById(R.id.project_name)
            val startDate : TextView = view.findViewById(R.id.start_date)
            val endDate : TextView = view.findViewById(R.id.end_date)
        }
}