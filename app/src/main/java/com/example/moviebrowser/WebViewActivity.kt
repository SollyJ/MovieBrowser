package com.example.moviebrowser

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity

class WebViewActivity: AppCompatActivity() {
    private val webView: WebView by lazy {
        findViewById(R.id.webView)
    }
    private val progressBar: ProgressBar by lazy {
        findViewById(R.id.progressBar)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        val url = intent.getStringExtra("url").toString()

        webView.apply {
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()
            settings.javaScriptEnabled = true
        }
        webView.loadUrl(url)
    }

    override fun onBackPressed() {
        if(webView.canGoBack())   webView.goBack()
        else   finish()
    }
}