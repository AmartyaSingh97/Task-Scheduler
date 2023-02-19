package com.example.todolistapp.model

class ProjectModel(
    private var project: String? = "",
    private var startDate: String?= "",
    private var endDate: String?= "",
    private var id : String?= ""
) {
    fun getId() : String? {
        return id
    }
    fun setId(id : String) {
        this.id = id
    }
    fun getProjectName(): String? {
        return project
    }
    fun getStartDate(): String? {
        return startDate
    }
    fun getEndDate(): String? {
        return endDate
    }
    fun setProjectName(project: String) {
        this.project = project
    }
    fun setStartDate(startDate: String) {
        this.startDate = startDate
    }
    fun setEndDate(endDate: String) {
        this.endDate = endDate
    }

}