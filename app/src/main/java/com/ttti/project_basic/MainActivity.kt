package com.ttti.project_basic

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
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
        val btnPage1 = findViewById(R.id.btnPage) as Button
        btn.setOnClickListener {
            val myToast = Toast.makeText(this, "Executing", Toast.LENGTH_SHORT)
            myToast.show()

            val  txtAnswer: TextView = findViewById(R.id.answer)
            txtAnswer.text = this.add(10,50).toString()

        }



        val  btnedit: Button = findViewById(R.id.mybuttonedit)
        btnedit.setOnClickListener {

            val  txtEdited: EditText = findViewById(R.id.edittext)
            val myToastedt = Toast.makeText(this, txtEdited.text.toString(), Toast.LENGTH_SHORT)
            myToastedt.show()

        }

        btnPage1.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@MainActivity, PageAdd::class.java)
            startActivity(intent)
        })
    }
}