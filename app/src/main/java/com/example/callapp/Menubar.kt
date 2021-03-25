package com.example.callapp

import android.annotation.SuppressLint
import android.content.Intent
import android.net.http.SslError
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
//import kotlinx.android.synthetic.main.activity_call.*
import kotlinx.android.synthetic.main.activity_menubar.*
import java.util.*

class Menubar : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var homeFragment: HomeFragment
    private lateinit var phoneBookFragment: PhoneBookFragment
    private lateinit var phoneBookFragment2: PhoneBookFragment2
    private lateinit var recentCallFragment: RecentCallFragment
    private lateinit var settingFragment: SettingFragment
    var username = ""
    var firebaseRef = Firebase.database.getReference("users")



 //   var username = ""
    var friendsUsername = ""
    var uniqueId = ""
    var friendname = ""
    var check_res = true
    var incoming = ""

    var isPeerConnected = false

   // var firebaseRef = Firebase.database.getReference("users")

    // 오디오, 비디오 컨트롤하는 녀석
    var isAudio = true
    var isVideo = true



    var conn=false


    companion object {
        const val TAG: String = "로그"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_menubar)
        Log.d(TAG, "Menubar - oncreate() called")

        bottom_nav.setOnNavigationItemSelectedListener(this)
        homeFragment = HomeFragment.newInstance()
        supportFragmentManager.beginTransaction().add(R.id.fragment_frame, homeFragment).commit()

        username = intent.getStringExtra("username")!!
      //  friendsUsername = intent.getStringExtra("friendsUsername")!!
        friendsUsername = "stb1"

        println("username:"+username)


        check_res = intent.getBooleanExtra("check_res", true)


        firebaseRef.child(username).child("info").child("receive").addValueEventListener(object :
            ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.value!="none"){
                  //  fun_intent(snapshot.value as? String)
                    // onCallRequest(snapshot.value as? String)
                }

            }

        })

      //  username = intent.getStringExtra("username")!!

//        incoming = intent.getStringExtra("incoming")!!


        onPeerConnected()

        if(incoming=="true"){

           // sendCallRequest()
        }

//        friendname = intent.getStringExtra("friendname")!!


        if(friendname!="")
        {
            // callJavascriptFunction("javascript:register(\"${username}\")")
            //onCallRequest(friendname)
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

        if(!check_res)
        {
            sendCallRequest()
            //check_res=false
//            val intent = Intent(this, Menubar::class.java)
//            intent.putExtra("username", username)
//            startActivity(intent)
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

        if(!check_res)
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

     //   if(check_res)
        loadVideoCall()
       // else
     //   initializePeer()
    }

    private fun loadVideoCall() {
        // val filePath = "file:android_asset/call.html"  //html 불러오기!
        //webView.loadUrl(filePath) // url(html) load한다
        webView.loadUrl("https://13.125.233.161:8443/call.html")

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                println("웹뷰: webViewClient ")
                if(check_res)
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

        if(check_res)
        {
            callJavascriptFunction("javascript:register(\"${username}\")")
            check_res=false
        }
        else
        {
            callJavascriptFunction("javascript:ws.close();")
          //  callJavascriptFunction("javascript:ws.stop(1);")
         //   callJavascriptFunction("javascript:webRtcPeer.dispose();")

            callJavascriptFunction("javascript:register(\"${username}\")")
        }




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
        fragment_frame.visibility = View.GONE
        bottom_nav.visibility = View.GONE
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
//        fragment_frame.visibility = View.GONE
//        bottom_nav.visibility = View.GONE
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








    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, "Menubar - onNavigationItemSelected() called")
        when (item.itemId) {
            R.id.ic_home -> {
                Log.d(TAG, "Menubar - 홈 클릭")
                homeFragment = HomeFragment.newInstance()
                supportFragmentManager.beginTransaction().replace(R.id.fragment_frame, homeFragment)
                    .commit()
                val bundle = Bundle()
                bundle.putString("username",username)
                homeFragment.setArguments(bundle)
            }
            R.id.ic_phoneBook -> {
                Log.d(TAG, "Menubar - 연락처 클릭")
                phoneBookFragment = PhoneBookFragment.newInstance()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_frame, phoneBookFragment).commit()
                val bundle = Bundle()
                bundle.putString("username",username)
                phoneBookFragment.setArguments(bundle)
            }
            R.id.ic_recentCall -> {
                Log.d(TAG, "Menubar - 최근기록 클릭")
                recentCallFragment = RecentCallFragment.newInstance()
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_frame,
                    recentCallFragment
                ).commit()
            }
            R.id.ic_setting -> {
                Log.d(TAG, "Menubar - 설정 클릭")
                settingFragment = SettingFragment.newInstance()
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_frame,
                    settingFragment
                ).commit()
            }
        }

        return true
    }
    fun call(friendsUsername: String, username: String, variable : Boolean) {
        this.friendsUsername = friendsUsername
        this.username = username
        this.check_res = variable

        if (!check_res) {
            sendCallRequest()
        }
    }
}