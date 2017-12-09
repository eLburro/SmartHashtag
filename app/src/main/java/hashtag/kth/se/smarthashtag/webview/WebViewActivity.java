package hashtag.kth.se.smarthashtag.webview;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import hashtag.kth.se.smarthashtag.R;
import hashtag.kth.se.smarthashtag.api.InstagramAPI;
import hashtag.kth.se.smarthashtag.webview.AuthWebViewClient;

public class WebViewActivity extends Activity {

    private WebView webView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);

        webView = findViewById(R.id.webViewAuth);
        webView.setWebViewClient(new AuthWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(InstagramAPI.getRequestAccessTokenUrl());
    }
}
