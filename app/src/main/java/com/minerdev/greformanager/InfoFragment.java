package com.minerdev.greformanager;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

public class InfoFragment extends Fragment {
    private Handler handler = new Handler();
    private TextView textView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_info, container, false);

        textView = rootView.findViewById(R.id.house_modify_textView_address);

        MaterialButton button = rootView.findViewById(R.id.house_modify_search);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebView webView = new WebView(getContext());

                webView.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
                webView.getSettings().setSupportMultipleWindows(true);

                webView.setWebChromeClient(new PopupWebChromeClient());

                webView.addJavascriptInterface(new AndroidBridge(), "GREApp");
                webView.loadUrl("http://192.168.35.91:80/get_daum_address.php");
            }
        });

        Spinner spinner_payment = rootView.findViewById(R.id.house_modify_spinner_paymentType);
        ArrayAdapter<String> arrayAdapter4 = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item);
        arrayAdapter4.addAll(getResources().getStringArray(R.array.paymentType));
        spinner_payment.setAdapter(arrayAdapter4);

        return rootView;
    }

    private class PopupWebChromeClient extends WebChromeClient {
        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            WebView newWebView = new WebView(getContext());
            WebSettings webSettings = newWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);

            final Dialog dialog = new Dialog(getContext());
            dialog.setContentView(newWebView);

            ViewGroup.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
            dialog.show();
            newWebView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onCloseWindow(WebView window) {
                    dialog.dismiss();
                }
            });

            newWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    return false;
                }
            });

            ((WebView.WebViewTransport) resultMsg.obj).setWebView(newWebView);
            resultMsg.sendToTarget();
            return true;
        }
    }

    private class AndroidBridge {
        @JavascriptInterface
        public void setAddress(final String arg1, final String arg2, final String arg3) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    textView.setText(String.format("(%s) %s %s", arg1, arg2, arg3));
                }
            });
        }
    }
}