package com.example.callapp

import android.webkit.JavascriptInterface


class JavascriptInterface(private val callActivity: CallActivity) {

    @JavascriptInterface
    public fun onPeerConnected() {
        callActivity.onPeerConnected()
    }

}