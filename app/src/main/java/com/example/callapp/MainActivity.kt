package com.example.callapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import kotlinx.android.synthetic.main.activity_main.*
import com.google.firebase.database.ktx.database

var username = ""
var userpw = ""
var check_username = false
var check_userpw = false
var firebaseRef = Firebase.database.getReference("users")
var val_PW=false

class MainActivity : AppCompatActivity() {

    private val permissions =
        arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)  // 권한 가져오기
    private val requestcode = 1

    val nameTest = "TEST"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        if (!isPermissionGranted()) {  //권한이 부여되었니?
            askPermissions() //true --> askPermisstions으로 ㄱㄱ

        }

        Firebase.initialize(this) // 파이어베이스 initㅎㅏ는 녀석

        Join.setOnClickListener(View.OnClickListener {
            // TextView 클릭될 시 할 코드작성
            val intent = Intent(this, joinActivity::class.java)
            startActivity(intent)

        })
        findid.setOnClickListener(View.OnClickListener {
            // TextView 클릭될 시 할 코드작성

            Toast.makeText(this, "서비스 준비중입니다.", Toast.LENGTH_SHORT).show();

        })
        findpw.setOnClickListener(View.OnClickListener {
            // TextView 클릭될 시 할 코드작성
            Toast.makeText(this, "서비스 준비중입니다.", Toast.LENGTH_SHORT).show();

        })

        loginBtn.setOnClickListener {
            username = userIdEdit.text.toString()
            userpw = userPwEdit.text.toString()
            check_username = false
            check_userpw = false


            if (username == "") {
                Toast.makeText(this, "ID를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
            if (userpw == "") {
                Toast.makeText(this, "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show()
            }

            println("테스트1 check_username : $check_username")



            firebaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val children = snapshot.children.iterator()
                    var key: String?
                    check_username = false
                    // check_userpw = false

                    while (children.hasNext()) { // 다음 값이 있으면
                        key = children.next().key // 다음 데이터 반환
                        if (!key.isNullOrEmpty()) {
                            check_username = username == key

                            if (check_username) {

                                firebaseRef.child(username).child("info").child("pw")
                                    .addValueEventListener(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {

                                            val value = snapshot.value

                                            if (value == userpw && !check_userpw) {
                                                check_userpw=true

                                                intent()

                                            } else {
                                                Toast_wrong()
                                            }
                                        }
                                        override fun onCancelled(error: DatabaseError) {
                                            println("Failed to read value.")
                                        }
                                    })
                                break
                            }

                        }
                    }
                    if(!check_username)
                        Toast_wrong()

                }

                override fun onCancelled(error: DatabaseError) {
                    println("Failed to read value.")
                }
            })

            println("테스트2 check_username : $check_username")

            // intent()

        }


    }


    private fun Toast_wrong(){

        Toast.makeText(this, "잘못된 ID 혹은 비밀번호입니다.", Toast.LENGTH_SHORT).show()
    }


    private fun intent() {
        val intent = Intent(this, Menubar::class.java)
        intent.putExtra("username", username)
        startActivity(intent)
    }

    private fun askPermissions() {
        ActivityCompat.requestPermissions(
            this,
            permissions,
            requestcode
        )  // 외부저장소(카메라, 오디오)에 접근하려면 이걸 해여함!
    }

    private fun isPermissionGranted(): Boolean {

        permissions.forEach {
            if (ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED)
                return false
            // 연결이 안되엉ㅆ다면 실패!
        }

        return true // 연결 성공하면
    }


}