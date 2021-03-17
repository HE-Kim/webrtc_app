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


@Suppress("UNCHECKED_CAST")
class PhoneBookFragment2 : Fragment() {

    val LIST_MENU: MutableList<String> = mutableListOf<String>("")
    var username = ""
    var friendsUsername = ""
    var firebaseRef = Firebase.database.getReference("users")

    var arraylist: MutableList<String> = mutableListOf<String>("")


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

                            // 버튼1 (직접 작성)
                            val intent = Intent(activity, CallActivity::class.java)
                            intent.putExtra("friendUsername", friendsUsername)
                            intent.putExtra("username", username)
                            startActivity(intent)


                        }
                        .setNegativeButton(
                            "취소"
                        ) { dialog, which ->

                            // 버튼2 (직접 작성)
                            dialog.dismiss()

                        }
                        .show()
                }



            }


        }

    }

}






