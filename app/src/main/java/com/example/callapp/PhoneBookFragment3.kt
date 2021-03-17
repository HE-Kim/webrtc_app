package com.example.callapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class PhoneBookFragment3 : Fragment(){

    var username = ""

    companion object{
        const val  TAG : String = "로그"

        fun newInstance() : PhoneBookFragment3 {
            return PhoneBookFragment3()
        }
    }
    //메모리에 올라갔을 때
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG,"phoneBookFragment1-onCreate() called")
    }
    // 프레그먼트를 안고 있는 엑티비에 붙었을 때
    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG,"phoneBookFragment1-onAttach() called")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG,"phoneBookFragment1-onCreateView() called")
        val view = inflater.inflate(R.layout.fragment_phone_book3,container,false)
        val registerBtn = view.findViewById<Button>(R.id.registerBtn)
        arguments?.let {
            username = it.getString("username").toString()
        }
        registerBtn.setOnClickListener {

            val intent = Intent(activity, ParingActivity::class.java)
            intent.putExtra("username", username)
            startActivity(intent)
        }


        return view
    }
}