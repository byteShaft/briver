package com.beza.briver.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.beza.briver.R;

/**
 * Created by fi8er1 on 02/11/2016.
 */

public class PaymentDetailsFragment extends Fragment {

    View baseViewPaymentDetailsFragment;
    static String urlPaymentPDF = "https://docs.google.com/gview?embedded=true&url=http://139.59.228.194:8000/media/pricing.pdf";
    ProgressBar pbPricingPDFFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        baseViewPaymentDetailsFragment = inflater.inflate(R.layout.fragment_payment_details, container, false);

        pbPricingPDFFragment = (ProgressBar) baseViewPaymentDetailsFragment.findViewById(R.id.pb_payment_details_fragment);

        final WebView wvPricingFragment = (WebView) baseViewPaymentDetailsFragment.findViewById(R.id.wv_payment_details_fragment);
        WebSettings webSettings = wvPricingFragment.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        wvPricingFragment.loadUrl(urlPaymentPDF);
        wvPricingFragment.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pbPricingPDFFragment.setVisibility(View.GONE);
                wvPricingFragment.setVisibility(View.VISIBLE);
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        return baseViewPaymentDetailsFragment;
    }
}
