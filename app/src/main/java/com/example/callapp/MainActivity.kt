package com.example.callapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import kotlinx.android.synthetic.main.activity_main.*
import com.google.firebase.database.ktx.database

var username=""

class MainActivity : AppCompatActivity() {

    private val permissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)  // 권한 가져오기
    private val requestcode = 1

    val nameTest = "TEST"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        if (!isPermissionGranted()) {  //권한이 부여되었니?
            askPermissions() //true --> askPermisstions으로 ㄱㄱ

        }

        Firebase.initialize(this) // 파이어베이스 initㅎㅏ는 녀석

        loginBtn.setOnClickListener {
            username = usernameEdit.text.toString()
            if(username!="")
            {
                setID()
            }

            val intent = Intent(this, CallActivity::class.java)
            intent.putExtra("username", username)
            startActivity(intent)
        }




    }

    private fun askPermissions() {
        ActivityCompat.requestPermissions(this, permissions, requestcode)  // 외부저장소(카메라, 오디오)에 접근하려면 이걸 해여함!
    }

    private fun isPermissionGranted(): Boolean {

        permissions.forEach {
            if (ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED)
                return false
            // 연결이 안되엉ㅆ다면 실패!
        }

        return true // 연결 성공하면
    }

   // var firebaseRef = Firebase.database.getReference("users")
    var firebaseRef = Firebase.database.getReference("$username")

    private fun setID(){
       // firebaseRef.child(nameTest).child("test").setValue("success")
        firebaseRef = Firebase.database.getReference("$username")
        firebaseRef.child("TEST").setValue("success")

        /*
        firebaseRef.child("A").child("test").setValue("success")
        firebaseRef.child("B").child("test").setValue("success")
        firebaseRef.child("C").child("test").setValue("success")
        */

    }
}