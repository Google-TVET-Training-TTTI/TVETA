package com.ttti.project_basic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class VoterLogin : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.voter_login)

        val user_name = findViewById(R.id.userName) as EditText
        val org_pass = findViewById(R.id.orgPass) as EditText
        val btn_login = findViewById(R.id.btnLogin) as Button
        btn_login.setOnClickListener(View.OnClickListener {
            if (user_name.getText().toString().isEmpty() || org_pass.getText().toString().isEmpty()) {
                Toast.makeText(applicationContext, "Please enter Username and Password", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(getApplicationContext(), "Your Username - " + user_name.getText().toString() + " \n"
                            + "Your Password - " + org_pass.getText().toString(), Toast.LENGTH_LONG).show()
            }
        })
    }
}