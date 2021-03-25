package com.example.callapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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

var username = ""
var userpw = ""
var check_username = false
var check_userpw = false
var firebaseRef = Firebase.database.getReference("users")
var val_PW=false
var LOG_data: MutableList<String> = mutableListOf<String>("")
var res=""

interface CometChatFriendsService2 {
    @Headers(
        "accept: application/json",
        "content-type: application/json"
    )
    //@POST("/{value}?id={value_id}&passwd=user1234!&role=50&name=김하은100&contact1=010&contact2=3333&contact3=4444")
    // @POST("/{value}?id={value_id}&passwd={userpw}&role=50&name={name}&contact1={contact1}&contact2={contact2}&contact3={contact3}")
    @POST("api/appLogin?")
    fun addFriend(
        @Header("apikey") apiKey: String,
        @Header("appid") appID: String,
       // @Body params: HashMap<String, List<String>>,
       // @Path("value") value: String,
        @Query("userid") userid: String,
        @Query("userpw") userpw: String
    )
            : Call<Data1>
}
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

          //  println("테스트1 check_username : $check_username")

            send()


           /* firebaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
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
*/
            println("테스트2 check_username : $check_username")

            // intent()

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
        val service = retrofit.create(CometChatFriendsService2::class.java)

        val body = HashMap<String, List<String>>()



        //         "/userReg?id=0test&passwd=user1234!&role=50&name=김하은100&contact1=010&contact2=3333&contact3=4444"
        //val value="/userReg?id=$userID&passwd=$userpw&role=50&name=$username&contact1=$PhoneNum1&contact2=$PhoneNum2&contact3=$PhoneNum3"
        val value="/api/appLogin"//"userReg?id=$userID&passwd=$userpw&role=50&name=$username&contact1=$PhoneNum1&contact2=$PhoneNum2&contact3=$PhoneNum3"

        val userpw=userpw
        val userid=username



        val apiKey = "12"
        val appID = "123"
        service.addFriend(
            apiKey, appID,
            userid, userpw
        )?.enqueue(object : Callback<Data1> {
            override fun onFailure(call: Call<Data1>, t: Throwable) {
                Log.d(
                    "CometChatAPI::", "Failed API call with call: " + call +
                            " + exception: " + t
                )
            }

            override fun onResponse(call: Call<Data1>, response: Response<Data1>) {
                Log.d("Response:: ", response.body().toString())
                res=response.body()?.result.toString()
                println(res)
                //LOG_data=response.body().toString()
             if(res=="1")
            intent()
            else
            Toast_wrong()
            }
    })



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