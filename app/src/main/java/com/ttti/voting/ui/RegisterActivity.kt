package com.ttti.voting.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.coreict.models.CreateUserMutation
import com.ttti.voting.R
import okhttp3.OkHttpClient
import org.json.JSONException
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {

    private var BASE_URL = ""
    private lateinit var client: ApolloClient
    var sharedpreferences: SharedPreferences? = null

    private fun setPref(con: Context, Key :String, Value :String ){
        sharedpreferences = con.getSharedPreferences("Voting", MODE_PRIVATE)
        var edit = sharedpreferences?.edit()
        edit?.putString(Key, Value)
        edit?.commit()
    }

    private fun setUpApolloClient(): ApolloClient {
        val okHttp = OkHttpClient
            .Builder()
        return ApolloClient.builder()
            .serverUrl(BASE_URL)
            .okHttpClient(okHttp.build())
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        BASE_URL = getString(R.string.graphql_server)
        client = setUpApolloClient()

        val  txtFirstName: EditText = findViewById(R.id.txtFirstName)
        val  txtLastName: EditText = findViewById(R.id.txtLastName)
        val  txtPhonenumber: EditText = findViewById(R.id.txtPhonenumber)
        val  txtEmail: EditText = findViewById(R.id.txtEmail)
        val  txtPassword: EditText = findViewById(R.id.txtPassword)


        val  btnRegister: Button = findViewById(R.id.btnRegister)
        btnRegister.setOnClickListener {

            client.mutate(
                CreateUserMutation
                    .builder()
                    .phoneNumber(txtPhonenumber.text.toString())
                    .firstName(txtFirstName.text.toString())
                    .lastName(txtLastName.text.toString())
                    .password(txtPassword.text.toString())
                    .email(txtEmail.text.toString())
                    .build()
            )
                .enqueue(object : ApolloCall.Callback<CreateUserMutation.Data>() {
                    override fun onFailure(e: ApolloException) {
                        Log.e("DEBUG",e.message.toString())
                    }
                    override fun onResponse(response: Response<CreateUserMutation.Data>) {
                        runOnUiThread {
                            try {
                                val err : String? = response.data()?.data()?.__typename()
                                if(err == "CreateError"){
                                    val myToast = Toast.makeText(applicationContext, response.data()?.data()?.toString() , Toast.LENGTH_SHORT)
                                    myToast.show()
                                }else{

                                   //var obj = response.data()?.data()?.__typename()?.elementAt(1)

                                    //Log.d("Voting App",obj.toString())

                                    /*setPref(this@RegisterActivity, "Pref_User_ID", response.data().)
                                    setPref(this@RegisterActivity, "Pref_Username",response.data()?.data()?.nodes()?.get(0)!!.firstName() + " "+ response.data()?.data()?.nodes()?.get(0)!!.lastName())
                                    setPref(this@RegisterActivity, "Pref_Phone", response.data()?.data()?.nodes()?.get(0)!!.phoneNumber())
                                    response.data()?.data()?.nodes()?.get(0)!!.email()
                                        ?.let { it1 -> setPref(this@RegisterActivity, "Pref_Phone", it1) }*/


                                    //val myToast = Toast.makeText(applicationContext,"INSERTED", Toast.LENGTH_SHORT)
                                    //myToast.show()

                                    //add to preference then perform login
                                    val intent = Intent(applicationContext,LoginActivity::class.java).apply {
                                    }
                                    startActivity(intent)
                                }

                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                    }
                })
        }

        val  btnLogin: TextView = findViewById(R.id.btnLoginVoter)
        btnLogin.setOnClickListener {
            val intent = Intent(applicationContext,LoginActivity::class.java).apply {
            }
            startActivity(intent)
        }
    }
}