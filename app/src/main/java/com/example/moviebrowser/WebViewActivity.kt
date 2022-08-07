package com.example.moviebrowser

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible

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
            webViewClient = object: WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)

                    progressBar.isVisible = true
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)

                    progressBar.isVisible = false
                }
            }
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