package com.example.callapp

import android.annotation.SuppressLint
import android.content.Intent
import android.net.http.SslError
import android.os.Bundle
import android.view.View
import android.webkit.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_call.*
import java.util.*

class CallActivity : AppCompatActivity() {


    var username = ""
    var friendsUsername = ""
    var uniqueId = ""

    var isPeerConnected = false

    var firebaseRef = Firebase.database.getReference("users")

    // 오디오, 비디오 컨트롤하는 녀석
    var isAudio = true
    var isVideo = true



    var conn=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)



        username = intent.getStringExtra("username")!!
        println("테스트 CallActivity username: $username")
        friendsUsername = intent.getStringExtra("friendsUsername")!!
        println("테스트 friend name: $friendsUsername")


        onPeerConnected()

        sendCallRequest()




        toggleAudioBtn.setOnClickListener {
            isAudio = !isAudio
            callJavascriptFunction("javascript:toggleAudio(\"${isAudio}\")")
            toggleAudioBtn.setImageResource(if (isAudio) R.drawable.ic_baseline_mic_24 else R.drawable.ic_baseline_mic_off_24)
        }

        toggleVideoBtn.setOnClickListener {
            isVideo = !isVideo
            callJavascriptFunction("javascript:toggleVideo(\"${isVideo}\")")
            toggleVideoBtn.setImageResource(if (isVideo) R.drawable.ic_baseline_videocam_24 else R.drawable.ic_baseline_videocam_off_24)
        }

        rejectBtn1.setOnClickListener {
            // 전화 종료되는 것을 ~ 써여함

            finish()
            //finish_conn()


        }

        firebaseRef.child(username).child("info").child("connection").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connection=snapshot.value

                if(conn && connection==false)
                    finish()//finish_conn()//

            }
            override fun onCancelled(error: DatabaseError) {
                println("Failed to read value.")
            }
        })

        setupWebView()
    }




    private fun sendCallRequest() {
        if (!isPeerConnected) {
            Toast.makeText(this, "You're not connected. Check your internet", LENGTH_LONG).show()
            return
        }

        firebaseRef.child(username).child("info").child("outgoing").setValue(friendsUsername) // 발신
        firebaseRef.child(friendsUsername).child("info").child("receive").setValue(username) // 수신 == incoming

        firebaseRef.child(friendsUsername).child("info").child("isAvailable").addValueEventListener(object :
            ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.value.toString() == "true" && snapshot.value.toString() != "none" ) {
                    listenForConnId()
                }

            }

        })

    }

    //connid 수정
    private fun listenForConnId() {
        switchToControls()
        callJavascriptFunction("javascript:call(\"${friendsUsername}\")")

        /*firebaseRef.child(friendsUsername).child("UUID").addValueEventListener(object :
            ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value == null)
                    return
                switchToControls()
                callJavascriptFunction("javascript:startCall(\"${snapshot.value}\")")
            }

        })*/
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        // 웹뷰한테 ask한다 allow 할것인지(웹페이지)
        webView.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest?) { // for this permission!
                request?.grant(request.resources)
            }
        }

        webView.settings.javaScriptEnabled = true  // 자바스크립스 액션이 가능하게 한다
        webView.settings.mediaPlaybackRequiresUserGesture = false
        webView.addJavascriptInterface(JavascriptInterface(this), "Android") //자바스크립트 인터페이스 추가

        loadVideoCall()
    }

    private fun loadVideoCall() {
        // val filePath = "file:android_asset/call.html"  //html 불러오기!
        //webView.loadUrl(filePath) // url(html) load한다
        webView.loadUrl("https://13.125.233.161:8443/call.html")

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                println("웹뷰: webViewClient ")
                initializePeer()
            }
            override fun onReceivedSslError(
                view: WebView?,
                handler: SslErrorHandler,
                error: SslError?
            ) {
                handler.proceed() // Ignore SSL certificate errors
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                view?.loadUrl(request?.getUrl().toString());
                return true;
            }
        }
    }


    private fun initializePeer() {
        // uniqueId = getUniqueID()


        callJavascriptFunction("javascript:register(\"${username}\")")

        /*  firebaseRef.child(username).child("UUID").addValueEventListener(object : ValueEventListener {
              override fun onDataChange(snapshot: DataSnapshot) {
                  val value = snapshot.value
                  uniqueId= value as String

                 // callJavascriptFunction("javascript:init(\"${uniqueId}\")")
              }

              override fun onCancelled(error: DatabaseError) {
                  println("Failed to read value.")
              }
          })*/


        //유니크 아이디가 firebase안에 있으면 ""로 나옴 결국 피어 인잇 불가능 ㅠㅠ
        println("유니크아이디 : $uniqueId")

        //  callJavascriptFunction("javascript:init(\"${uniqueId}\")")
        firebaseRef.child(username).child("info").child("receive").addValueEventListener(object :
            ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.value!="none")
                    onCallRequest(snapshot.value as? String)
            }

        })

    }

    private fun onCallRequest(caller: String?) {
        if (caller == null || caller == "none") return

        friendsUsername=caller
        callLayout.visibility = View.VISIBLE
        incomingCallTxt.text = "$caller is calling..."

        // 파이어 베이스로 보내는 녀석들~
        acceptBtn.setOnClickListener {
            firebaseRef.child(username).child("info").child("isAvailable").setValue(true)

            callLayout.visibility = View.GONE
            switchToControls()
        }

        // reject 했을 때 incoming value를 없애야함
        rejectBtn.setOnClickListener {
            firebaseRef.child(username).child("info").child("receive").setValue("none")
            firebaseRef.child(friendsUsername).child("info").child("outgoing").setValue("none")
            callLayout.visibility = View.GONE
        }

    }

    private fun switchToControls() {
        inputLayout.visibility = View.GONE
        webView.visibility = View.VISIBLE
        callControlLayout.visibility = View.VISIBLE

        firebaseRef.child("$username").child("info").child("connection").setValue(true) // 가능한지
        conn=true
    }


    private fun getUniqueID(): String {

        return UUID.randomUUID().toString()  //유니크 아이디를 랜덤으로 만들어서 return 한다
    }


    private fun callJavascriptFunction(functionString: String) {
        webView.post { webView.evaluateJavascript(functionString, null) }
    }


    fun onPeerConnected() {
        isPeerConnected = true  // 자바스크립트 인터페이스에서 받아오는 녀석
    }

    override fun onBackPressed() {

        finish()
    }

    override fun onDestroy() {

        //firebaseRef.child(username).child("info").child("outgoing").setValue("none") // 발신
        //  firebaseRef.child(friendsUsername).child("info").child("receive").setValue("none") // 수신

        //firebaseRef.child(username).child("info").child("isAvailable").setValue("none")
        // firebaseRef.child(username).child("info").child("outgoing").setValue("none")
        // firebaseRef.child(friendsUsername).child("info").child("receive").setValue("none")

        firebaseRef.child(username).child("info").child("outgoing").setValue("none") // 발신
        firebaseRef.child(username).child("info").child("receive").setValue("none") // 발신
        firebaseRef.child(friendsUsername).child("info").child("outgoing").setValue("none")
        firebaseRef.child(friendsUsername).child("info").child("receive").setValue("none") // 수신 == incoming
        firebaseRef.child(username).child("info").child("isAvailable").setValue("none")

        firebaseRef.child(username).child("info").child("connection").setValue(false)
        firebaseRef.child(friendsUsername).child("info").child("connection").setValue(false)


        webView.loadUrl("about:blank")
        super.onDestroy()
    }

    private fun finish_conn() {

        firebaseRef.child(username).child("info").child("outgoing").setValue("none") // 발신
        firebaseRef.child(username).child("info").child("receive").setValue("none") // 발신
        firebaseRef.child(friendsUsername).child("info").child("outgoing").setValue("none")
        firebaseRef.child(friendsUsername).child("info").child("receive").setValue("none") // 수신 == incoming
        firebaseRef.child(username).child("info").child("isAvailable").setValue("none")

        firebaseRef.child(username).child("info").child("connection").setValue(false)
        firebaseRef.child(friendsUsername).child("info").child("connection").setValue(false)

        callControlLayout.visibility = View.GONE
        inputLayout.visibility = View.VISIBLE
        webView.loadUrl("about:blank")
        ///   sendCallRequest()
    }
}