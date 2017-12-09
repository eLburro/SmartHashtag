package hashtag.kth.se.smarthashtag;

import android.content.Intent;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class AuthWebViewClient extends WebViewClient {

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        String url = request.getUrl().toString();

        if (url.startsWith(InstagramAPI.REDIRECT_URI)) {
            String parts[] = url.split("=");
            InstagramAPI.accessToken = parts[1];
            Intent intent = new Intent(view.getContext(), SelectPictureActivity.class);
            view.getContext().startActivity(intent);
            return true;
        }

        return false;
    }
}