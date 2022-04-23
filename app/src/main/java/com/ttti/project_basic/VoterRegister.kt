package com.ttti.project_basic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class VoterRegister : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.voter_register)

        val first_name = findViewById(R.id.firstName) as EditText
        val full_name  = findViewById(R.id.fullName) as EditText
        val mobile = findViewById(R.id.mobNo) as EditText
        val email = findViewById(R.id.email) as EditText
        val org_pass = findViewById(R.id.orgPass) as EditText
        val conf_pass = findViewById(R.id.confPass) as EditText
        val btn_login = findViewById(R.id.btnLogin) as Button

        btn_login.setOnClickListener(View.OnClickListener {
            if (first_name.getText().toString().isEmpty() || full_name.getText().toString().isEmpty() ||
                mobile.getText().toString().isEmpty() || email.getText().toString().isEmpty() ||
                org_pass.getText().toString().isEmpty() || conf_pass.getText().toString().isEmpty()) {
                Toast.makeText(applicationContext, "Please enter all the details", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(getApplicationContext(), "Your First name - " + first_name.getText().toString() +
                        " \n" + "Your Full Name - " + full_name.getText().toString() + " \n" + "Your Mobile - " +
                            mobile.getText().toString() + " \n" + "Your Email - " + email.getText().toString() + " \n"
                        + "Your Password - " + org_pass.getText().toString() + " \n" + "Your Password Repeat - " +
                            conf_pass.getText().toString(),Toast.LENGTH_LONG).show()

            }
        })
    }
}