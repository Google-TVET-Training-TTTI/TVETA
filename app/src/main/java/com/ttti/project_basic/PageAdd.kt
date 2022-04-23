package com.ttti.project_basic

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class PageAdd : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pageadd)
        val btnAdd1 = findViewById(R.id.btnAdd) as Button
        val firstNo1 = findViewById(R.id.firstNo) as EditText
        val secNo1 = findViewById(R.id.secNo) as EditText
        val tvAnswer1 = findViewById(R.id.tvAnswer) as TextView
        val btnLogin1 = findViewById(R.id.btnLogin) as Button
        val btnReg1 = findViewById(R.id.btnReg) as Button
        btnAdd1.setOnClickListener(View.OnClickListener {
            if (firstNo1.getText().toString().isEmpty() || secNo1.getText().toString().isEmpty()) {
                Toast.makeText(applicationContext, "Please enter both values", Toast.LENGTH_SHORT).show()
            } else if (firstNo1.getText().toString().length == 0) {
                firstNo1.setText("0")
            } else if (secNo1.getText().toString().length == 0) {
                secNo1.setText("0")
            } else {
                val num1 = firstNo1.getText().toString().toInt()
                val num2 = secNo1.getText().toString().toInt()
                val sum = num1 + num2
                tvAnswer1.setText(sum.toString())
            }
        })

        btnLogin1.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@PageAdd, VoterLogin::class.java)
            startActivity(intent)
        })

        btnReg1.setOnClickListener(View.OnClickListener {
            val i = Intent(this@PageAdd, VoterRegister::class.java)
            startActivity(i)
        })
    }
}