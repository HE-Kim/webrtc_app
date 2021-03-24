package com.example.callapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_join.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.io.InputStream
import java.security.KeyStore
import java.security.cert.Certificate
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.*
import javax.net.ssl.*


//data class Accepted(val accepted: HashMap<String, Friend>)
//data class Friend(val success: Boolean, val message: String)
interface CometChatFriendsService {
    @Headers(
        "accept: application/json",
        "content-type: application/json"
    )
    //@POST("/{value}?id={value_id}&passwd=user1234!&role=50&name=김하은100&contact1=010&contact2=3333&contact3=4444")
    // @POST("/{value}?id={value_id}&passwd={userpw}&role=50&name={name}&contact1={contact1}&contact2={contact2}&contact3={contact3}")
    @POST("/{value}?")
    fun addFriend(
        @Header("apikey") apiKey: String,
        @Header("appid") appID: String,
        @Body params: HashMap<String, List<String>>,
        @Path("value") value: String,
        @Query("id") id: String,
        @Query("passwd") passwd: String,
        @Query("role") role: String,
        @Query("name") name: String,
        @Query("contact1") contact1: String,
        @Query("contact2") contact2: String,
        @Query("contact3") contact3: String
    )
            : Call<Data>
}
class joinActivity : AppCompatActivity() {

    var firebaseRef = Firebase.database.getReference("users")

    var uniqueId = ""


    var userID = ""
    var username = ""
    var userpw = ""
    var userpw2 = ""

    var check_id = false
    var check_name = false
    var check_pw = false

    var PhoneNum1=""
    var PhoneNum2=""
    var PhoneNum3=""

    val arrList: MutableList<String> = mutableListOf<String>("")


    @SuppressLint("result")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        val userIdEdit = findViewById<View>(R.id.userIdEdit) as EditText
        val usernameEdit = findViewById<View>(R.id.usernameEdit) as EditText
        val userPwEdit = findViewById<View>(R.id.userPwEdit) as EditText
        val userPwEdit2 = findViewById<View>(R.id.userPWEdit2) as EditText
        val userPhonenumber = findViewById<View>(R.id.userPhonenumber) as EditText









        JoinBtn.setOnClickListener {
            //edittext 값이 다 들어갔는지 확인
            userID = userIdEdit.text.toString()
            username = usernameEdit.text.toString()
            userpw = userPwEdit.text.toString()
            userpw2 = userPwEdit2.text.toString()

            PhoneNum1=userPhonenumber.text.toString().substring(0,3)
            PhoneNum2=userPhonenumber.text.toString().substring(3,7)
            PhoneNum3=userPhonenumber.text.toString().substring(7,11)
            check_id = false
            check_name = false
            check_pw = false

          //  send()
            userID_check()

            //pw 일치 확인

        }

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
        val service = retrofit.create(CometChatFriendsService::class.java)

        val body = HashMap<String, List<String>>()



       //         "/userReg?id=0test&passwd=user1234!&role=50&name=김하은100&contact1=010&contact2=3333&contact3=4444"
     //val value="/userReg?id=$userID&passwd=$userpw&role=50&name=$username&contact1=$PhoneNum1&contact2=$PhoneNum2&contact3=$PhoneNum3"
        val value="userReg"//"userReg?id=$userID&passwd=$userpw&role=50&name=$username&contact1=$PhoneNum1&contact2=$PhoneNum2&contact3=$PhoneNum3"
        val id=userID//"test012"//"$userID"
        val passwd=userpw
        val name=username
        val contact1=PhoneNum1
        val contact2=PhoneNum2
        val contact3=PhoneNum3
        val role="50"

        val apiKey = "12"
        val appID = "123"
        service.addFriend(
            apiKey, appID,
            body, value, id, passwd, role, name, contact1, contact2, contact3
        )?.enqueue(object : Callback<Data> {
            override fun onFailure(call: Call<Data>, t: Throwable) {
                Log.d(
                    "CometChatAPI::", "Failed API call with call: " + call +
                            " + exception: " + t
                )
            }

            override fun onResponse(call: Call<Data>, response: Response<Data>) {
                Log.d("Response:: ", response.body().toString())

            }
        })
    }


    private fun userID_check() {


        if (userID == "")
            Toast.makeText(this, "ID를 입력하세요.", Toast.LENGTH_SHORT).show()
        else {

            firebaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val children = snapshot.children.iterator()
                    var key: String?
                    arrList.clear()

                    while (children.hasNext()) { // 다음 값이 있으면
                        key = children.next().key // 다음 데이터 반환

                        if (!key.isNullOrEmpty()) {
                            arrList.add(key)
                        }
                    }

                    check_overlap(arrList)
                    return

                }

                override fun onCancelled(error: DatabaseError) {
                    println("Failed to read value.")
                }
            })
        }
    }


    private fun check_overlap(arrList: MutableList<String>) {

        if (arrList.contains(userID)) {
            Toast.makeText(this, "중복된 ID 입니다.", Toast.LENGTH_SHORT).show()

        } else {
            check_id = true
            username_check()
        }
    }

    private fun info() {
        var UUID = UUID.randomUUID().toString()
        firebaseRef.child("$userID").child("UUID").setValue("$UUID")
        firebaseRef.child("$userID").child("info").child("outgoing").setValue("none") // 발신
        firebaseRef.child("$userID").child("info").child("receive").setValue("none") // 수신
        firebaseRef.child("$userID").child("info").child("friends").setValue("none") // 친구
        firebaseRef.child("$userID").child("info").child("isAvailable").setValue("none") // 가능한지
        firebaseRef.child("$userID").child("info").child("connection").setValue("false") // 가능한지
        firebaseRef.child("$userID").child("info").child("type").setValue("APP")




        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("username", username)
        startActivity(intent)
    }

    private fun userPw_check1() {
        if(userpw!=userpw2)
        {
            Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()

        }
        else {
            send()
            info()
        }
    }
    private fun userPw_check() {
        if (userpw == "")
            Toast.makeText(this, "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show()
        else if (check_id == true) {
            firebaseRef.child("$userID").child("info").child("pw").setValue("$userpw")
            check_pw = true
           // userPw_check1()
            send()
            info()
        }
    }

    private fun username_check() {
        if (username == "")
            Toast.makeText(this, "이름을 입력하세요", Toast.LENGTH_SHORT).show()
        else if (check_id == true) {
            firebaseRef.child("$userID").child("info").child("username").setValue("$username")
            check_name = true
            userPw_check()
        }
    }

}