package com.example.callapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_menubar.*
import kotlinx.android.synthetic.main.activity_paring.view.*
import kotlinx.android.synthetic.main.fragment_phone_book.*
import kotlinx.android.synthetic.main.fragment_phone_book.view.*

class PhoneBookFragment : Fragment(),BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var phoneBookFragment1: PhoneBookFragment1
    private lateinit var phoneBookFragment2: PhoneBookFragment2
    private lateinit var phoneBookFragment3: PhoneBookFragment3
    var listCount = ""



    companion object {
        const val TAG: String = "로그"

        fun newInstance(): PhoneBookFragment {
            return PhoneBookFragment()

        }
    }

    //메모리에 올라갔을 때
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "phoneBookFragment-onCreate() called")

        arguments?.let {
            username = it.getString("username").toString()
            listCount = it.getString("listCount").toString()
        }

        Log.d(TAG, "username: ${username}")

    }

    // 프레그먼트를 안고 있는 엑티비에 붙었을 때
    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "phoneBookFragment-onAttach() called")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "PhoneBookFragment-onCreateView() called")
        val view = inflater.inflate(R.layout.fragment_phone_book, container, false)
        view.top_nav.setOnNavigationItemSelectedListener(this)
        phoneBookFragment2 = PhoneBookFragment2.newInstance()
        childFragmentManager.beginTransaction().add(R.id.fragment_frame2, phoneBookFragment2).commit()

        val bundle = Bundle()
        bundle.putString("username", username)
        phoneBookFragment2.setArguments(bundle)


        return view
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, "phoneBookFragment - onNavigationItemSelected() called")
        when(item.itemId){
            R.id.ic_friend -> {
                Log.d(Menubar.TAG, "phoneBookFragment - 모든연락처 클릭")
                phoneBookFragment2 = PhoneBookFragment2.newInstance()
                childFragmentManager.beginTransaction().replace(
                    R.id.fragment_frame2,
                    phoneBookFragment2
                ).commit();
                val bundle = Bundle()
                bundle.putString("username", username)
                phoneBookFragment2.setArguments(bundle)
            }

            R.id.ic_friendplus -> {
                Log.d(Menubar.TAG, "phoneBookFragment - 추가등록 클릭")

                phoneBookFragment3 = PhoneBookFragment3.newInstance()
                childFragmentManager.beginTransaction().replace(
                    R.id.fragment_frame2,
                    phoneBookFragment3
                ).commit()

                val bundle = Bundle()
                bundle.putString("username", username)
                phoneBookFragment3.setArguments(bundle)
            }

            R.id.ic_favorite -> {
                Log.d(TAG, "phoneBookFragment - 즐겨찾기")
                phoneBookFragment1 = PhoneBookFragment1.newInstance()
                childFragmentManager.beginTransaction().replace(
                    R.id.fragment_frame2,
                    phoneBookFragment1
                ).commit()
            }



        }

        return true
    }



}