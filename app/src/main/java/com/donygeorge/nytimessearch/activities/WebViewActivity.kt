package com.donygeorge.nytimessearch.activities

import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.donygeorge.nytimessearch.R
import kotlinx.android.synthetic.main.activity_web_view.*



class WebViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        val url = intent.getStringExtra("url")
        myWebView.getSettings().setLoadsImagesAutomatically(true);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        myWebView.webViewClient = MyBrowser()
        myWebView.loadUrl(url)
    }

    private class MyBrowser : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
            view?.loadUrl(url)
            return true
        }

        @TargetApi(Build.VERSION_CODES.N)
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            if (request == null) return true
            view!!.loadUrl(request.url.toString());
            return true;
        }
    }
}
