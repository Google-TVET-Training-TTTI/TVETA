package com.ttti.project_basic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {


    fun add(x: Int, y: Int) : Int {


        return x + y
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val  btn: Button = findViewById(R.id.mybutton)
        btn.setOnClickListener {
            val myToast = Toast.makeText(this, "Executing", Toast.LENGTH_SHORT)
            myToast.show()

            val  txtAnswer: TextView = findViewById(R.id.answer)
            txtAnswer.text = this.add(10,50).toString()

        }

    }
}