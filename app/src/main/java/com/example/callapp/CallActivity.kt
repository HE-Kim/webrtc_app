package com.example.callapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
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

    var isPeerConnected = false

    var firebaseRef = Firebase.database.getReference("users")

    var isAudio = true
    var isVideo = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)
        onPeerConnected()
        username = intent.getStringExtra("username")!!

        sendCallRequest()


        toggleAudioBtn.setOnClickListener {
            isAudio = !isAudio
            callJavascriptFunction("javascript:toggleAudio(\"${isAudio}\")")
            toggleAudioBtn.setImageResource(if (isAudio) R.drawable.ic_baseline_mic_24 else R.drawable.ic_baseline_mic_off_24 )
        }

        toggleVideoBtn.setOnClickListener {
            isVideo = !isVideo
            callJavascriptFunction("javascript:toggleVideo(\"${isVideo}\")")
            toggleVideoBtn.setImageResource(if (isVideo) R.drawable.ic_baseline_videocam_24 else R.drawable.ic_baseline_videocam_off_24 )
        }

        setupWebView()
    }

    private fun sendCallRequest() {

        if (!isPeerConnected) {
            Toast.makeText(this, "You're not connected. Check your internet", Toast.LENGTH_LONG).show()
            return
        }

        firebaseRef.child(username).child("info").child("outgoing").setValue(friendsUsername) // 발신
        firebaseRef.child(friendsUsername).child("info").child("receive").setValue(username) // 수신 == incoming
        firebaseRef.child(friendsUsername).child("isAvailable").addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.value.toString() == "true") {
                    listenForConnId()
                }

            }

        })

    }

    private fun listenForConnId() {
        firebaseRef.child(friendsUsername).child("UUID").addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value == null)
                    return
                switchToControls()
                callJavascriptFunction("javascript:startCall(\"${snapshot.value}\")")
            }

        })
    }

    private fun setupWebView() {

        webView.webChromeClient = object: WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest?) {
                request?.grant(request.resources)
            }
        }

        webView.settings.javaScriptEnabled = true
        webView.settings.mediaPlaybackRequiresUserGesture = false
        webView.addJavascriptInterface(JavascriptInterface(this), "Android")

        loadVideoCall()
    }

    private fun loadVideoCall() {
        val filePath = "file:android_asset/call.html"
        webView.loadUrl(filePath)

        webView.webViewClient = object: WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                initializePeer()
            }
        }
    }

    var uniqueId = ""

    private fun initializePeer() {

        firebaseRef.child(username).child("UUID").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.value
                uniqueId= value as String

                callJavascriptFunction("javascript:init(\"${uniqueId}\")")
            }

            override fun onCancelled(error: DatabaseError) {
                println("Failed to read value.")
            }
        })


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
        if (caller == null) return

        callLayout.visibility = View.VISIBLE
        incomingCallTxt.text = "$caller is calling..."

        acceptBtn.setOnClickListener {
            firebaseRef.child(username).child("info").child("isAvailable").setValue(true)

            callLayout.visibility = View.GONE
            switchToControls()
        }

        rejectBtn.setOnClickListener {
            firebaseRef.child(username).child("incoming").setValue(null)
            callLayout.visibility = View.GONE
        }

    }

    private fun switchToControls() {
        inputLayout.visibility = View.GONE
        callControlLayout.visibility = View.VISIBLE
    }



    private fun callJavascriptFunction(functionString: String) {
        webView.post { webView.evaluateJavascript(functionString, null) }
    }


    fun onPeerConnected() {
        isPeerConnected = true
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onDestroy() {
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

}