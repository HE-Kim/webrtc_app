package com.example.callapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
//import kotlinx.android.synthetic.main.activity_call.*
import kotlinx.android.synthetic.main.activity_join_2.*
import kotlin.math.log


class JoinActivity_2 : AppCompatActivity() {

    var username = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_2)


        username = intent.getStringExtra("username")!!

        text2_name.text = username+"님!"

        print(text2_name)

       loginBtn3.setOnClickListener{

           val intent = Intent(this, MainActivity::class.java)
           intent.putExtra("username", username)
           startActivity(intent)

        }
    }
}