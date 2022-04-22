package com.ttti.project_basic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class PageAdd : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pageadd)
        val btnAdd1 = findViewById(R.id.btnAdd) as Button
        val firstNo1 = findViewById(R.id.firstNo) as EditText
        val secNo1 = findViewById(R.id.secNo) as EditText
        val tvAnswer1 = findViewById(R.id.tvAnswer) as TextView
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
    }
}