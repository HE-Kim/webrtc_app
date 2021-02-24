package com.example.callapp

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_menubar.*

class Menubar : AppCompatActivity(),BottomNavigationView.OnNavigationItemSelectedListener{

    private lateinit var homeFragment: HomeFragment
    private lateinit var phoneBookFragment: PhoneBookFragment
    private lateinit var recentCallFragment: RecentCallFragment
    private lateinit var settingFragment: SettingFragment
    var username = ""

    companion object{
        const val TAG:String="로그"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menubar)
        Log.d(TAG, "Menubar - oncreate() called")

        bottom_nav.setOnNavigationItemSelectedListener(this)
        homeFragment = HomeFragment.newInstance()
        supportFragmentManager.beginTransaction().add(R.id.fragment_frame, homeFragment).commit()

    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        Log.d(TAG, "Menubar - onNavigationItemSelected() called")
        when(item.itemId){
            R.id.ic_home -> {
                Log.d(TAG, "Menubar - 홈 클릭")
                homeFragment = HomeFragment.newInstance()
                supportFragmentManager.beginTransaction().replace(R.id.fragment_frame, homeFragment)
                    .commit()
            }
            R.id.ic_phoneBook -> {
                Log.d(TAG, "Menubar - 연락처 클릭")
                phoneBookFragment = PhoneBookFragment.newInstance()
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_frame,
                    phoneBookFragment
                ).commit()
            }
            R.id.ic_recentCall -> {
                Log.d(TAG, "Menubar - 최근기록 클릭")
                recentCallFragment = RecentCallFragment.newInstance()
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_frame,
                    recentCallFragment
                ).commit()
            }
            R.id.ic_setting -> {
                Log.d(TAG, "Menubar - 설정 클릭")
                settingFragment = SettingFragment.newInstance()
                supportFragmentManager.beginTransaction().replace(
                    R.id.fragment_frame,
                    settingFragment
                ).commit()
            }
        }

        return true
    }
}