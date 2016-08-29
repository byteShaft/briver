package com.beza.briver.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.beza.briver.R;

/**
 * Created by fi8er1 on 27/08/2016.
 */

public class TermsFragment extends android.support.v4.app.Fragment {

    View baseViewTermsFragment;
    static String urlTOS = "http://139.59.228.194:8000/media/terms.html";
    ProgressBar pbTOSFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        baseViewTermsFragment = inflater.inflate(R.layout.fragment_terms, container, false);

        pbTOSFragment = (ProgressBar) baseViewTermsFragment.findViewById(R.id.pb_tos_fragment);

        final WebView wvTOS = (WebView) baseViewTermsFragment.findViewById(R.id.wv_tos_fragment);
        WebSettings webSettings = wvTOS.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        wvTOS.setInitialScale(100);
        wvTOS.loadUrl(urlTOS);
        wvTOS.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pbTOSFragment.setVisibility(View.GONE);
                wvTOS.setVisibility(View.VISIBLE);
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        return baseViewTermsFragment;
    }

}
