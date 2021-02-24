package com.example.callapp

import android.webkit.JavascriptInterface

//take to CallActivity!
class JavascriptInterface(val phoneBookFragment2: PhoneBookFragment2) {

    @JavascriptInterface
    //this func call from the javascript
    public fun onPeerConnected() {
        phoneBookFragment2.onPeerConnected()
    }

}