package com.example.todolistapp.model

class ProjectModel(
    private var project: String,
    private var startDate: String,
    private var endDate: String,
) {
    fun setProjectName(projectName: String) {
        this.project = projectName
    }
    fun setStartDate(startDate: String) {
        this.startDate = startDate
    }
    fun setEndDate(endDate: String) {
        this.endDate = endDate
    }
    fun getProjectName(): String {
        return project
    }
    fun getStartDate(): String {
        return startDate
    }
    fun getEndDate(): String {
        return endDate
    }

}