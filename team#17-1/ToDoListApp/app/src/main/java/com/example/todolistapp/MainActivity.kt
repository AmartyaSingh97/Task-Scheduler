package com.example.todolistapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Launch the Sign Up Fragment
        val myFragment : Fragment = SignUp()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.fragmentContainerView, myFragment).commit()
    }}