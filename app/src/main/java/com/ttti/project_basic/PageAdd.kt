package com.ttti.project_basic

import androidx.appcompat.app.AppCompatActivity
import android.app.Activity
import android.os.Bundle
import android.app.*
import android.os.*
import android.view.View
import android.widget.*

class PageAdd : Activity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pageadd)
    }


    fun btnAdd(view: View) {
        ///to do - addition function
        val myToast = Toast.makeText(this, "To create additional (add) function", Toast.LENGTH_SHORT)
        myToast.show()
    }
}