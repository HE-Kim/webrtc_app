package com.example.callapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class PhoneBookFragment3 : Fragment(){

    companion object{
        const val  TAG : String = "로그"

        fun newInstance() : PhoneBookFragment3 {
            return PhoneBookFragment3()
        }
    }
    //메모리에 올라갔을 때
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG,"phoneBookFragment3-onCreate() called")
    }
    // 프레그먼트를 안고 있는 엑티비에 붙었을 때
    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG,"phoneBookFragment3-onAttach() called")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG,"phoneBookFragment3-onCreateView() called")
        val view = inflater.inflate(R.layout.fragment_phone_book3,container,false)
        return view
    }
}