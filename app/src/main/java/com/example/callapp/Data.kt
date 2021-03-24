package com.example.callapp

import java.util.HashMap

data class Data(val result: String)//Accepted)
data class Data1(val result: String, val login: login)
//data class login(val login: HashMap<String, Friend>)
data class login(
    val uid: Int,
    val id: String,
    val maskedId: String,
    val name: String,
    val passwd: String,
    val salt: String,
    val org: String,
    val role: String,
    val contact1: String,
    val contact2: String,
    val contact3: String,
    val email: String,
    val regDate: String,
    val loginIp: String,
    val loginDate: String
)
data class Data4(val result: String, val stb_id: Int)//Accepted)