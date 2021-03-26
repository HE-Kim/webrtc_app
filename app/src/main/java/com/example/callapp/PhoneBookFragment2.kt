package com.example.callapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
//import kotlinx.android.synthetic.main.activity_call.*
//import kotlinx.android.synthetic.main.activity_call.incomingCallTxt
import kotlinx.android.synthetic.main.fragment_phone_book.*
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
import kotlin.collections.HashMap

interface CometChatFriendsService_pb2 {
    @Headers(
        "accept: application/json",
        "content-type: application/json"
    )
    //@POST("/{value}?id={value_id}&passwd=user1234!&role=50&name=김하은100&contact1=010&contact2=3333&contact3=4444")
    // @POST("/{value}?id={value_id}&passwd={userpw}&role=50&name={name}&contact1={contact1}&contact2={contact2}&contact3={contact3}")
    @POST("/api/getContacts?")
    fun addFriend(
        @Header("apikey") apiKey: String,
        @Header("appid") appID: String,
      //  @Body params: HashMap<String, List<String>>,
        //@Path("value") value: String,
        @Query("id") id: String
    )
            : Call<Data5>
}
@Suppress("UNCHECKED_CAST")
class PhoneBookFragment2 : Fragment() {

    val LIST_MENU: MutableList<String> = mutableListOf<String>("")
    var username = ""
    var friendsUsername = ""
    var firebaseRef = Firebase.database.getReference("users")
    var incoming = "false"
    var res_check1 = "false"
    var res_contacts = ""

    var variable = false

    companion object {
        const val TAG: String = "로그"

        fun newInstance(): PhoneBookFragment2 {
            return PhoneBookFragment2()
        }
    }

    //메모리에 올라갔을 때
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "phoneBookFragment2-onCreate() called")


    }


    // 프레그먼트를 안고 있는 엑티비에 붙었을 때
    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "phoneBookFragment2-onAttach() called")

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "phoneBookFragment2-onCreateView() called")

        val view = inflater.inflate(R.layout.fragment_phone_book2, container, false)

        val listview = view.findViewById<ListView>(R.id.IdListview)

        val editSearch = view.findViewById<View>(R.id.userIdEdit) as EditText




        val adapter = ArrayAdapter<String>(
            view.context,
            android.R.layout.simple_list_item_1,
            LIST_MENU
        )

        listview.adapter = adapter

        listview.isTextFilterEnabled = true
        editSearch.addTextChangedListener()


        arguments?.let {
            username = it.getString("username").toString()
        }
        send(listview, adapter)
        initDatabase(listview, adapter)


        editSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (editSearch.text.isEmpty()) {
                    listview.clearTextFilter();
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                (listview.adapter as ArrayAdapter<String?>).filter.filter(editSearch.text.toString())
            }


        })

        return view
    }


    private fun initDatabase(listview: ListView, adapter: ArrayAdapter<String>) {
        arguments?.let {
            username = it.getString("username").toString()
        }

        firebaseRef.child(username).child("info").child("friends").addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                LIST_MENU.clear()
                val children = snapshot.children.iterator()

                var key: String?
                while (children.hasNext()) { // 다음 값이 있으면
                    key = children.next().key // 다음 데이터 반환
                    if (!key.isNullOrEmpty() && username != key && LIST_MENU.indexOf(key) == -1) {
                        LIST_MENU.add(key)

                    }
                }




                initList(listview, adapter)
            }

            override fun onCancelled(error: DatabaseError) {
                println("Failed to read value.")
            }
        })


    }
    private fun send(listview: ListView, adapter: ArrayAdapter<String>) {



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
        val service = retrofit.create(CometChatFriendsService_pb2::class.java)

        val body = HashMap<String, List<String>>()




        //         "/userReg?id=0test&passwd=user1234!&role=50&name=김하은100&contact1=010&contact2=3333&contact3=4444"
        //val value="/userReg?id=$userID&passwd=$userpw&role=50&name=$username&contact1=$PhoneNum1&contact2=$PhoneNum2&contact3=$PhoneNum3"
        val value="userReg"//"userReg?id=$userID&passwd=$userpw&role=50&name=$username&contact1=$PhoneNum1&contact2=$PhoneNum2&contact3=$PhoneNum3"
        val id=username

        val apiKey = "12"
        val appID = "123"
        service.addFriend(
            apiKey, appID,
            id
        )?.enqueue(object : Callback<Data5> {
            override fun onFailure(call: Call<Data5>, t: Throwable) {
                Log.d(
                    "CometChatAPI::", "Failed API call with call: " + call +
                            " + exception: " + t
                )
            }

            override fun onResponse(call: Call<Data5>, response: Response<Data5>) {
                Log.d("Response5:: ", response.body().toString())
                res_contacts= response.body()?.contacts.toString()
              //  res_list=response.body()!!.contacts.
                val friends = response.body()!!.contacts
                Log.d("Response5:: ", friends.toString())
             //   Log.d("Response5:: ", friends[0].fidname)
               // LIST_MENU.clear()
                for(i in 0 until friends.size step 1)
                {
                    if(friends[i].fidname!="null")
                    {
                        firebaseRef.child(username).child("info").child("friends").child(friends[i].fidname).setValue(friends[i].fidname)

                    }
                 //   firebaseRef.child(friends[i].fidname).child("info").child("friends").child(username).setValue(username)
                  //  firebaseRef.child(username).child("info").child("friends").child("stb1").child("UUID").setValue("11e40088-e0bb-4c05-866e-4021d56ada3e")
                 //   firebaseRef.child(username).child("info").child("friends").child("stb2").child("UUID").setValue("1cdc9285-a392-4b12-a2f4-d3531a15dfba")
                  //  LIST_MENU.add(friends[i].fidname)
                }

                initList(listview, adapter)
              //  Log.d("Response5:: ", res_contacts[1].toString())
            }
        })
    }


    private fun initList(listview: ListView, adapter: ArrayAdapter<String>) {
        arguments?.let {
            username = it.getString("username").toString()
        }

        adapter.notifyDataSetChanged()

        listview.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>, v: View, position: Int, id: Long) {
                // get TextView's Text.
                val strText = parent.getItemAtPosition(position) as String
                friendsUsername = strText

                activity?.let {
                    AlertDialog.Builder(it,android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth) // TestActivity 부분에는 현재 Activity의 이름 입력.
                        .setMessage(friendsUsername+"님께 통화를 연결하시겠습니까?") // 제목 부분 (직접 작성)
                        .setPositiveButton("통화") { dialog, which ->
                            incoming = "true"
                           // res_check1="true"
                            res_check1="false"
                            variable = false

                            val intent = Intent(activity, Menubar::class.java)
                           intent.putExtra("friendsUsername", friendsUsername)
                            intent.putExtra("username", username)
                        //    intent.putExtra("incoming", incoming)
                            intent.putExtra("check_res", variable)

                            println("테스트 Menubar username: $username")
                            println("테스트 Menubar incoming: $incoming")

                            println("테스트 friend name: $friendsUsername")

                            (activity as Menubar).call(friendsUsername, username, variable)
                        //    startActivity(intent)


                        }
                        .setNegativeButton(
                            "취소"
                        ) { dialog, which ->

                            dialog.dismiss()

                        }
                        .show()
                }



            }


        }

    }

}





