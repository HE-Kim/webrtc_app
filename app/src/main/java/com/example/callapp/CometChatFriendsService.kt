package com.example.callapp

import retrofit2.Call
import retrofit2.http.*
import java.util.HashMap

interface CometChatFriendsService1 {
    @Headers(
        "accept: application/json",
        "content-type: application/json"
    )
    //@POST("/{value}?id={value_id}&passwd=user1234!&role=50&name=김하은100&contact1=010&contact2=3333&contact3=4444")
   // @POST("/{value}?id={value_id}&passwd={userpw}&role=50&name={name}&contact1={contact1}&contact2={contact2}&contact3={contact3}")
    @POST("/{value}?")
    fun addFriend(
        @Header("apikey") apiKey: String,
        @Header("appid") appID: String,
        @Body params: HashMap<String, List<String>>,
        @Path("value") value: String,
        @Query("id") id: String,
        @Query("passwd") passwd: String,
        @Query("role") role: String,
        @Query("name") name: String,
        @Query("contact1") contact1: String,
        @Query("contact2") contact2: String,
        @Query("contact3") contact3: String
    )
            : Call<Data>
}