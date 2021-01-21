package com.example.videocallapp

import android.os.Bundle
import android.view.View
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
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
import kotlin.collections.ArrayList

class CallActivity : AppCompatActivity() {

    
    var username = ""
    var friendsUsername = ""

    var nameA="TEST"

    var isPeerConnected = false

    var firebaseRef = Firebase.database.getReference("users")

    // 오디오, 비디오 컨트롤하는 녀석
    var isAudio = true
    var isVideo = true

    //리스트뷰
   // val LIST_MENU = arrayOfNulls<String>(5)//arrayOf("LIST1", "LIST2", "LIST3")
    val LIST_MENU=Array<String>(5,{""})
    //val LIST_MENU = arrayOf("LIST1", "LIST2", "LIST3")
    //val listview = findViewById(R.id.IdListview) as ListView
   // val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, LIST_MENU)
    //private val adapter: ArrayAdapter<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)

        initID()

        username = intent.getStringExtra("username")!!

        //파이어 베이스에서 불러와야함

       initDatabase();



        callBtn.setOnClickListener {
            friendsUsername = friendNameEdit.text.toString()
            sendCallRequest()
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
        firebaseRef.child("A").child("test").setValue("success")
        firebaseRef.child("B").child("test").setValue("success")
        firebaseRef.child("C").child("test").setValue("success")
    }

    //리스트뷰 업데이트
    private fun initDatabase() {
 //       firebaseRef = firebaseRef.getReference("child 이름") // 변경값을 확인할 child 이름




        firebaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //값이 변경된게 있으면 database의 값이 갱신되면 자동 호출된다.

                val value = snapshot.value as Map<*, *>
                val val2=value.keys
               // val mutableList: MutableList<String> = mutableListOf<String>("kildong","Dooly","Chelsu")
                val mutableList: MutableList<String> = mutableListOf<String>(value.keys.toString())
                val firlist = mutableList[0][1] // 1=A/ 2=B/3=C 이런 식 일 듯  //[==0

               /* LIST_MENU.set(0,"${mutableList[0][1]}") //A +3씩
                LIST_MENU.set(1,"${mutableList[0][4]}") //B
                LIST_MENU.set(2,"${mutableList[0][7]}") //C*/
                val countVal=value.size
                var k=1
                var j=0
                for(i in 1..countVal)
                {
                    if(username=="${mutableList[0][k]}")
                    {
                        k += 3
                        continue
                    }
                    LIST_MENU.set(j,"${mutableList[0][k]}")
                    k += 3
                    j++
                }
/*
                println("성공성공 value  $value")
                println("성공성공 val(key)  $val2")
                println("성공성공 mutableList  $mutableList")
                println("성공성공 firlist  $firlist")
                println("성공성공 list  ${LIST_MENU[0]}")
*/
           //     println("성공성공 username  $username/${mutableList[0][1]}/${mutableList[0][2]}/${mutableList[0][3]}/${mutableList[0][4]}  ")

                //println("성공성공 countval  $countVal")
                initList()
            }

            override fun onCancelled(error: DatabaseError) {
                println("Failed to read value.")

            }

        })

        //println("성공성공 list2  ${LIST_MENU[0]}")
      //  val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, LIST_MENU)
      //  listview.adapter = adapter

    }

    private fun initList() {
        val listview = findViewById(R.id.IdListview) as ListView
     //   println("성공성공 list2  ${LIST_MENU[0]}")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, LIST_MENU)
        listview.adapter = adapter

        listview.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>, v: View, position: Int, id: Long) {

                // get TextView's Text.
                val strText = parent.getItemAtPosition(position) as String
                println("성공성공 선택한 자식  $strText")
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
      //  firebaseRef.child(nameA)//.child("incoming").setValue(null)
        firebaseRef.child(friendsUsername).child("incoming").setValue(username)
       // firebaseRef.child(nameA).child("incoming").setValue("")
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
        firebaseRef.child(username).setValue(null)
        webView.loadUrl("about:blank")
        super.onDestroy()
    }

}