package com.example.callapp

import android.webkit.JavascriptInterface


class JavascriptInterface(private val callActivity: Menubar) {

    @JavascriptInterface
    public fun onPeerConnected() {
        callActivity.onPeerConnected()
    }

}