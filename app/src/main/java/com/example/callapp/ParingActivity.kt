package com.example.callapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
//import kotlinx.android.synthetic.main.activity_call.*
import kotlinx.android.synthetic.main.activity_join.*
import kotlinx.android.synthetic.main.activity_join.JoinBtn
import kotlinx.android.synthetic.main.activity_paring.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query
import java.io.InputStream
import java.security.KeyStore
import java.security.cert.Certificate
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.*
import javax.net.ssl.*
interface CometChatFriendsService3 {
    @Headers(
        "accept: application/json",
        "content-type: application/json"
    )
    //@POST("/{value}?id={value_id}&passwd=user1234!&role=50&name=김하은100&contact1=010&contact2=3333&contact3=4444")
    // @POST("/{value}?id={value_id}&passwd={userpw}&role=50&name={name}&contact1={contact1}&contact2={contact2}&contact3={contact3}")
    @POST("api/certPairingNum?")
    fun addFriend(
        @Header("apikey") apiKey: String,
        @Header("appid") appID: String,
        // @Body params: HashMap<String, List<String>>,
        // @Path("value") value: String,
        @Query("pnum") pnum: String,
        @Query("id") id: String
    )
            : Call<Data4>
}

interface CometChatFriendsService4 {
    @Headers(
        "accept: application/json",
        "content-type: application/json"
    )
    //@POST("/{value}?id={value_id}&passwd=user1234!&role=50&name=김하은100&contact1=010&contact2=3333&contact3=4444")
    // @POST("/{value}?id={value_id}&passwd={userpw}&role=50&name={name}&contact1={contact1}&contact2={contact2}&contact3={contact3}")
    @POST("api/setContact?")
    fun addFriend(
        @Header("apikey") apiKey: String,
        @Header("appid") appID: String,
        // @Body params: HashMap<String, List<String>>,
        // @Path("value") value: String,
        @Query("id") id: String,
        @Query("stb_id") stb_id: String,
        @Query("fidname") fidname: String,
        @Query("fstbname") fstbname: String,
        @Query("fchnum") fchnum: String
    )
            : Call<Data>
}

class ParingActivity : AppCompatActivity() {

    var firebaseRef = Firebase.database.getReference("pairing")
    var username =""
    var pid = ""
    var stbname=""
    var myname=""
    var channel=""
    var Verify=""

    var res=""
    var res1=""
    var stb_id=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paring)

        val VerifyEdit = findViewById<View>(R.id.VerifyEdit) as EditText

        Verify=VerifyEdit.toString()

        username = intent.getStringExtra("username")!!


        Btn.setOnClickListener {
            send()
          //  pid = VerifyEdit.text.toString()
           // println("테스트 버튼 클릭 pid $pid")
       //     pid_check()
        }

        infoBtn.setOnClickListener {
            stbname = othernameEdit.text.toString() // 상대방
            myname=nameEdit.text.toString() // tv로 보여질 내이름
            channel = channelEdit.text.toString()

            send_paring()


        }

        //에디트에 입력된것 받아오기
        //파이어 베이스에서 찾기


    }
    private fun send() {


        //   (SSL_Activity.mContext as SSL_Activity).ssl_raw()
        val cf = CertificateFactory.getInstance("X.509")
        val caInput: InputStream = resources.openRawResource(R.raw.server)
        var ca: Certificate? = null
        try {
            ca = cf.generateCertificate(caInput)
            println("ca=" + (ca as X509Certificate?)!!.subjectDN)
        } catch (e: CertificateException) {
            e.printStackTrace()
        } finally {
            caInput.close()
        }
        val keyStoreType = KeyStore.getDefaultType()
        var keyStore = KeyStore.getInstance(keyStoreType)
        keyStore.load(null, null)
        if (ca == null) {

        }
        keyStore.setCertificateEntry("ca", ca)



        val trustManagerFactory =
            TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(keyStore)


        val trustManagers: Array<TrustManager> = trustManagerFactory.trustManagers
        check(!(trustManagers.size != 1 || trustManagers[0] !is X509TrustManager)) {
            "Unexpected default trust managers:" + Arrays.toString(
                trustManagers
            )

        }
        val hostnameVerifier = HostnameVerifier { _, session ->
            HttpsURLConnection.getDefaultHostnameVerifier().run {
                verify("https://13.125.233.161:6443", session)
            }
        }
        val trustManager: X509TrustManager = trustManagers[0] as X509TrustManager
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, arrayOf<TrustManager>(trustManager), null)
        val sslSocketFactory = sslContext.socketFactory
        val client1: OkHttpClient.Builder = OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustManager)

        client1.hostnameVerifier(HostnameVerifier { hostname, session -> true })


        val retrofit = Retrofit.Builder()
            .baseUrl("https://13.125.233.161:6443")
            .client(client1.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()


        //retrofit 객체를 통해 인터페이스 생성
        val service = retrofit.create(CometChatFriendsService3::class.java)

        val body = HashMap<String, List<String>>()



        //         "/userReg?id=0test&passwd=user1234!&role=50&name=김하은100&contact1=010&contact2=3333&contact3=4444"
        //val value="/userReg?id=$userID&passwd=$userpw&role=50&name=$username&contact1=$PhoneNum1&contact2=$PhoneNum2&contact3=$PhoneNum3"
        val value="/api/appLogin"//"userReg?id=$userID&passwd=$userpw&role=50&name=$username&contact1=$PhoneNum1&contact2=$PhoneNum2&contact3=$PhoneNum3"

        val userpw=userpw
        val id=username

        val VerifyEdit = findViewById<View>(R.id.VerifyEdit) as EditText
       // pid = VerifyEdit.text.toString()
        Verify=VerifyEdit.text.toString()
        println("테스트 버튼 verift $Verify")

        val apiKey = "12"
        val appID = "123"
        service.addFriend(
            apiKey, appID,
            Verify, id
        )?.enqueue(object : Callback<Data4> {
            override fun onFailure(call: Call<Data4>, t: Throwable) {
                Log.d(
                    "CometChatAPI::", "Failed API call with call: " + call +
                            " + exception: " + t
                )
            }

            override fun onResponse(call: Call<Data4>, response: Response<Data4>) {
                Log.d("Response:: ", response.body().toString())
                res=response.body()?.result.toString()
                stb_id=response.body()?.stb_id.toString()
               // println(pw)
                //LOG_data=response.body().toString()
                if(res=="1")
                    Toast_add()
            }
        })


    }
    private fun send_paring() {


        //   (SSL_Activity.mContext as SSL_Activity).ssl_raw()
        val cf = CertificateFactory.getInstance("X.509")
        val caInput: InputStream = resources.openRawResource(R.raw.server)
        var ca: Certificate? = null
        try {
            ca = cf.generateCertificate(caInput)
            println("ca=" + (ca as X509Certificate?)!!.subjectDN)
        } catch (e: CertificateException) {
            e.printStackTrace()
        } finally {
            caInput.close()
        }
        val keyStoreType = KeyStore.getDefaultType()
        var keyStore = KeyStore.getInstance(keyStoreType)
        keyStore.load(null, null)
        if (ca == null) {

        }
        keyStore.setCertificateEntry("ca", ca)



        val trustManagerFactory =
            TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        trustManagerFactory.init(keyStore)


        val trustManagers: Array<TrustManager> = trustManagerFactory.trustManagers
        check(!(trustManagers.size != 1 || trustManagers[0] !is X509TrustManager)) {
            "Unexpected default trust managers:" + Arrays.toString(
                trustManagers
            )

        }
        val hostnameVerifier = HostnameVerifier { _, session ->
            HttpsURLConnection.getDefaultHostnameVerifier().run {
                verify("https://13.125.233.161:6443", session)
            }
        }
        val trustManager: X509TrustManager = trustManagers[0] as X509TrustManager
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, arrayOf<TrustManager>(trustManager), null)
        val sslSocketFactory = sslContext.socketFactory
        val client1: OkHttpClient.Builder = OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustManager)

        client1.hostnameVerifier(HostnameVerifier { hostname, session -> true })


        val retrofit = Retrofit.Builder()
            .baseUrl("https://13.125.233.161:6443")
            .client(client1.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()


        //retrofit 객체를 통해 인터페이스 생성
        val service = retrofit.create(CometChatFriendsService4::class.java)


        val id=username

        val VerifyEdit = findViewById<View>(R.id.VerifyEdit) as EditText
        // pid = VerifyEdit.text.toString()
        Verify=VerifyEdit.text.toString()
        println("테스트 버튼 verift $Verify")

        val apiKey = "12"
        val appID = "123"
        service.addFriend(
            apiKey, appID,
            id, stb_id, myname, stbname, channel
        )?.enqueue(object : Callback<Data> {
            override fun onFailure(call: Call<Data>, t: Throwable) {
                Log.d(
                    "CometChatAPI::", "Failed API call with call: " + call +
                            " + exception: " + t
                )
            }

            override fun onResponse(call: Call<Data>, response: Response<Data>) {
                Log.d("Response:: ", response.body().toString())
                res1=response.body()?.result.toString()
                //stb_id=response.body()?.stb_id.toString()
                // println(pw)
                //LOG_data=response.body().toString()
            }
        })
        if(res1=="1")
            initinfo()

    }

    private fun pid_check() {

/*
        firebaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val children = snapshot.children.iterator()
                var key: String?

                while (children.hasNext()) { // 다음 값이 있으면
                    key = children.next().key // 다음 데이터 반환

                    println("테스트 key $key / pid $pid")
                    if(key==pid)
                    {
                        firebaseRef.child(pid).child("user").setValue(username)
                        Toast_add()
                        //파이어베이스에 친구 추가 해주기
                        //그전에 자기 정보 등록하기 / 채널 선택 / 상대방에게 보여질 이름 /
                    }

                }
                return

            }
            override fun onCancelled(error: DatabaseError) {
                println("Failed to read value.")
            }
        }) */
    }

    private fun Toast_add() {
        Toast.makeText(this, "페어링 성공", Toast.LENGTH_SHORT).show()
        Verifylayout.visibility = View.GONE
        infolayout.visibility = View.VISIBLE
    }
    private fun Toast_info() {
        Toast.makeText(this, "등록 완료", Toast.LENGTH_SHORT).show()

    }

    private fun initinfo() {
      //  success()
        /*firebaseRef.child(pid).child("STB").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                firebaseRef = Firebase.database.getReference("users")
                firebaseRef.child("${snapshot.value}").child("info").child("friends").
                child(username).child("name").setValue(stbname)
                firebaseRef.child("${snapshot.value}").child("info").child("friends").
                child(username).child("channel").setValue(channel)

                firebaseRef.child(username).child("info").child("friends").
                child("${snapshot.value}").child("channel").setValue(channel)
                success()
                //firebaseRef.child(username).child("info").child("friends").child("${snapshot.value}").child("name").setValue(stbname)

            }
            override fun onCancelled(error: DatabaseError) {
                println("Failed to read value.")
            }
        })*/


    }

    private fun success() {
        firebaseRef = Firebase.database.getReference("pairing")
        firebaseRef.child(pid).child("success").setValue(true)
        Toast_info()
        val intent = Intent(this, PhoneBookFragment::class.java)
        intent.putExtra("username", username)
        startActivity(intent)

    }
}