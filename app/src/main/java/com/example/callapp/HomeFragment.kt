package com.example.callapp

import android.content.Context
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_join_2.*
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(){

    var username = ""

    companion object{
        const val  TAG : String = "로그"

        fun newInstance() : HomeFragment {
            return HomeFragment()
        }
    }
    //메모리에 올라갔을 때
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG,"HomeFragment-onCreate() called")
        arguments?.let {
            username = it.getString("username").toString()
        }

        Log.d(PhoneBookFragment.TAG, "username: ${username}")


    }
    // 프레그먼트를 안고 있는 엑티비에 붙었을 때
    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG,"HomeFragment-onAttach() called")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG,"HomeFragment-onCreateView() called")
        val view = inflater.inflate(R.layout.fragment_home,container,false)
        return view
    }
}