package com.example.callapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_setting.*

class SettingFragment : Fragment(){
    val LIST_MENU: MutableList<String> = mutableListOf<String>("")
    var username = ""
    var friendsUsername = ""
    var firebaseRef = Firebase.database.getReference("users")
    var listCount = ""

    companion object{
        const val  TAG : String = "로그"

        fun newInstance() : SettingFragment {
            return SettingFragment()
        }
    }
    //메모리에 올라갔을 때
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG,"settingFragment-onCreate() called")
    }
    // 프레그먼트를 안고 있는 엑티비에 붙었을 때
    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG,"settingFragment-onAttach() called")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG,"settingFragment-onCreateView() called")
        val view = inflater.inflate(R.layout.fragment_setting,container,false)

        PlusFriend.setOnClickListener{

        }

        return view
    }
}