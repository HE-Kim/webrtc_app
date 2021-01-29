package com.example.callapp

import android.annotation.SuppressLint
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
    var addUsername=""


    var isPeerConnected = false

    var firebaseRef = Firebase.database.getReference("users")

    // 오디오, 비디오 컨트롤하는 녀석
    var isAudio = true
    var isVideo = true



  val LIST_MENU: MutableList<String> = mutableListOf<String>("")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)

        username = intent.getStringExtra("username")!!
        if(username!="") {
            firebaseRef = Firebase.database.getReference("$username")
        }
        // firebaseRef = Firebase.database.getReference("$username")

        //파이어 베이스에서 불러와야함
        initID()
       initDatabase()

        callBtn.setOnClickListener {
            addUsername = friendNameEdit.text.toString()
            firebaseRef.child("$addUsername").child("test").setValue("success")
         //   sendCallRequest()
        }

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

        setupWebView()
    }

    private fun initID() {

        //임의로 넣은 값들, test 이후 변경 예정
        if(username!="A")
            firebaseRef.child("A").child("test").setValue("success")

        if(username!="B")
        firebaseRef.child("B").child("test").setValue("success")

        if(username!="C")
        firebaseRef.child("C").child("test").setValue("success")

        firebaseRef.child("TEST").setValue(null)

    }

    //리스트뷰 업데이트
    private fun initDatabase() {
        firebaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                LIST_MENU.clear()

                val children = snapshot.children.iterator()
                var key:String?
                while (children.hasNext()) { // 다음 값이 있으면
                    key = children.next().key // 다음 데이터 반환
                    if (!key.isNullOrEmpty() && username != key && LIST_MENU.indexOf(key) == -1) {
                       // userList.add(key)
                        LIST_MENU.add(key)
                    }
                }

                initList()
            }

            override fun onCancelled(error: DatabaseError) {
                println("Failed to read value.")

            }

        })




    }

    private fun initList() {

        val listview = findViewById(R.id.IdListview) as ListView

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, LIST_MENU)
        listview.adapter = adapter

        listview.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>, v: View, position: Int, id: Long) {

                // get TextView's Text.
                val strText = parent.getItemAtPosition(position) as String

                friendsUsername=strText;
                sendCallRequest()
            }
        }

    }

    private fun sendCallRequest() {
        if (!isPeerConnected) {
            Toast.makeText(this, "You're not connected. Check your internet", LENGTH_LONG).show()
            return
        }
        firebaseRef.child(friendsUsername).child("incoming").setValue(username)
        firebaseRef.child(friendsUsername).child("isAvailable").addValueEventListener(object :
            ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.value.toString() == "true") {
                    listenForConnId()
                }

            }

        })

    }

    private fun listenForConnId() {
        firebaseRef.child(friendsUsername).child("connId").addValueEventListener(object :
            ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value == null)
                    return
                switchToControls()
                callJavascriptFunction("javascript:startCall(\"${snapshot.value}\")")
            }

        })
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        // 웹뷰한테 ask한다 allow 할것인지(웹페이지)
        webView.webChromeClient = object: WebChromeClient() {
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
        val filePath = "file:android_asset/call.html"  //html 불러오기!
        webView.loadUrl(filePath) // url(html) load한다

        webView.webViewClient = object: WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                initializePeer()
            }
        }
    }

    var uniqueId = ""

    private fun initializePeer() {
        uniqueId = getUniqueID()
        println("유니크아이디 : $uniqueId")

        callJavascriptFunction("javascript:init(\"${uniqueId}\")")
        firebaseRef.child(username).child("incoming").addValueEventListener(object :
            ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                onCallRequest(snapshot.value as? String)
            }

        })

    }

    private fun onCallRequest(caller: String?) {
        if (caller == null) return

        callLayout.visibility = View.VISIBLE
        incomingCallTxt.text = "$caller is calling..."

        // 파이어 베이스로 보내는 녀석들~
        acceptBtn.setOnClickListener {
            firebaseRef.child(username).child("connId").setValue(uniqueId)
            firebaseRef.child(username).child("isAvailable").setValue(true)



            callLayout.visibility = View.GONE
            switchToControls()
        }

        // reject 했을 때 incoming value를 없애야함
        rejectBtn.setOnClickListener {
            firebaseRef.child(username).child("incoming").setValue(null)
            callLayout.visibility = View.GONE
        }

    }

    private fun switchToControls() {
        inputLayout.visibility = View.GONE
        listviewlayout.visibility = View.GONE
        callControlLayout.visibility = View.VISIBLE
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
      //  firebaseRef.child(username).setValue(null)
        firebaseRef.child(username).child("incoming").setValue(null)
        firebaseRef.child(username).child("connId").setValue(null)
        firebaseRef.child(username).child(" isAvailable").setValue(null)
        webView.loadUrl("about:blank")
        super.onDestroy()
    }

}