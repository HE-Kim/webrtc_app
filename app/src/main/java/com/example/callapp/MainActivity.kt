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
    //@POST("/{value}?id={value_id}&passwd=user1234!&role=50&name=κΉνμ100&contact1=010&contact2=3333&contact3=4444")
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
        arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)  // κΆν κ°μ Έμ€κΈ°
    private val requestcode = 1

    val nameTest = "TEST"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        if (!isPermissionGranted()) {  //κΆνμ΄ λΆμ¬λμλ?
            askPermissions() //true --> askPermisstionsμΌλ‘ γ±γ±

        }

        Firebase.initialize(this) // νμ΄μ΄λ² μ΄μ€ initγγλ λμ

        Join.setOnClickListener(View.OnClickListener {
            // TextView ν΄λ¦­λ  μ ν  μ½λμμ±
            val intent = Intent(this, joinActivity::class.java)
            startActivity(intent)

        })
        findid.setOnClickListener(View.OnClickListener {
            // TextView ν΄λ¦­λ  μ ν  μ½λμμ±

            Toast.makeText(this, "μλΉμ€ μ€λΉμ€μλλ€.", Toast.LENGTH_SHORT).show();

        })
        findpw.setOnClickListener(View.OnClickListener {
            // TextView ν΄λ¦­λ  μ ν  μ½λμμ±
            Toast.makeText(this, "μλΉμ€ μ€λΉμ€μλλ€.", Toast.LENGTH_SHORT).show();

        })

        loginBtn.setOnClickListener {
            username = userIdEdit.text.toString()
            userpw = userPwEdit.text.toString()
            check_username = false
            check_userpw = false


            if (username == "") {
                Toast.makeText(this, "IDλ₯Ό μλ ₯νμΈμ.", Toast.LENGTH_SHORT).show()
            }
            if (userpw == "") {
                Toast.makeText(this, "λΉλ°λ²νΈλ₯Ό μλ ₯νμΈμ.", Toast.LENGTH_SHORT).show()
            }

          //  println("νμ€νΈ1 check_username : $check_username")

            send()


           /* firebaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val children = snapshot.children.iterator()
                    var key: String?
                    check_username = false
                    // check_userpw = false

                    while (children.hasNext()) { // λ€μ κ°μ΄ μμΌλ©΄
                        key = children.next().key // λ€μ λ°μ΄ν° λ°ν
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
            println("νμ€νΈ2 check_username : $check_username")

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


        //retrofit κ°μ²΄λ₯Ό ν΅ν΄ μΈν°νμ΄μ€ μμ±
        val service = retrofit.create(CometChatFriendsService2::class.java)

        val body = HashMap<String, List<String>>()



        //         "/userReg?id=0test&passwd=user1234!&role=50&name=κΉνμ100&contact1=010&contact2=3333&contact3=4444"
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

        Toast.makeText(this, "μλͺ»λ ID νΉμ λΉλ°λ²νΈμλλ€.", Toast.LENGTH_SHORT).show()
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
        )  // μΈλΆμ μ₯μ(μΉ΄λ©λΌ, μ€λμ€)μ μ κ·Όνλ €λ©΄ μ΄κ±Έ ν΄μ¬ν¨!
    }

    private fun isPermissionGranted(): Boolean {

        permissions.forEach {
            if (ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED)
                return false
            // μ°κ²°μ΄ μλμγλ€λ©΄ μ€ν¨!
        }

        return true // μ°κ²° μ±κ³΅νλ©΄
    }


}