package com.beza.briver.utils;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.paytm.pgsdk.PaytmMerchant;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Paytm {

    public static void onStartTransaction(final Context context, String transactionAmount, final Runnable onSuccessTask) {
        PaytmPGService Service = PaytmPGService.getStagingService();
        Map<String, String> paramMap = new HashMap<String, String>();

        paramMap.put("ORDER_ID", getOrderIdForPayment());
        paramMap.put("MID", "Bezaon21015855849279");
        paramMap.put("CUST_ID", "CUST" + String.valueOf(AppGlobals.getUserID()));
        paramMap.put("CHANNEL_ID", "WAP");
        paramMap.put("INDUSTRY_TYPE_ID", "Retail");
        paramMap.put("WEBSITE", "Bezawap");
        paramMap.put("TXN_AMOUNT", transactionAmount);
        paramMap.put("THEME", "merchant");
        paramMap.put("EMAIL", AppGlobals.getUsername());
        paramMap.put("MOBILE_NO", AppGlobals.getPhoneNumber());
        PaytmOrder Order = new PaytmOrder(paramMap);
        PaytmMerchant Merchant = new PaytmMerchant(
                "http://139.59.228.194:8000/api/generatechecksum.cgi",
                "http://139.59.228.194:8000/api/verifychecksum.cgi");
        Service.initialize(Order, Merchant, null);
        Service.startPaymentTransaction(context, true, true,
                new PaytmPaymentTransactionCallback() {
                    @Override
                    public void someUIErrorOccurred(String inErrorMessage) {
                        // Some UI Error Occurred in Payment Gateway Activity.
                        // // This may be due to initialization of views in
                        // Payment Gateway Activity or may be due to //
                        // initialization of webview. // Error Message details
                        // the error occurred.
                        Log.d("LOG", "Some UI error occurred " + inErrorMessage);
                        Toast.makeText(context, "Some UI error occurred", Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onTransactionSuccess(Bundle inResponse) {
                        // After successful transaction this method gets called.
                        // // Response bundle contains the merchant response
                        // parameters.
                        Log.d("LOG", "Payment Transaction is successful " + inResponse);
                        Toast.makeText(context, "Payment Transaction is successful", Toast.LENGTH_LONG).show();
                        onSuccessTask.run();
                    }

                    @Override
                    public void onTransactionFailure(String inErrorMessage,
                                                     Bundle inResponse) {
                        // This method gets called if transaction failed. //
                        // Here in this case transaction is completed, but with
                        // a failure. // Error Message describes the reason for
                        // failure. // Response bundle contains the merchant
                        // response parameters.
                        Log.d("LOG", "Payment Transaction Failed " + inErrorMessage);
                        Toast.makeText(context, "Payment Transaction Failed ", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void networkNotAvailable() { // If network is not
                        // available, then this
                        // method gets called.
                        Log.d("LOG", "Network not available");
                        Toast.makeText(context, "Network not available", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void clientAuthenticationFailed(String inErrorMessage) {
                        // This method gets called if client authentication
                        // failed. // Failure may be due to following reasons //
                        // 1. Server error or downtime. // 2. Server unable to
                        // generate checksum or checksum response is not in
                        // proper format. // 3. Server failed to authenticate
                        // that client. That is value of payt_STATUS is 2. //
                        // Error Message describes the reason for failure.
                        Log.d("LOG", "Client authentication failed " + inErrorMessage);
                        Toast.makeText(context, "Client authentication failed", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onErrorLoadingWebPage(int iniErrorCode,
                                                      String inErrorMessage, String inFailingUrl) {
                        Log.d("LOG", "Error loading Paytm WebPage " + inErrorMessage);
                        Toast.makeText(context, "Error loading Paytm WebPage", Toast.LENGTH_LONG).show();
                    }

                    // had to be added: NOTE
                    @Override
                    public void onBackPressedCancelTransaction() {
                        Log.d("LOG", "onBackPressed. Transaction cancelled");
                        Toast.makeText(context, "onBackPressed. Transaction cancelled", Toast.LENGTH_LONG).show();
                    }

                });
    }

    private static String getOrderIdForPayment() {
        Random r = new Random(System.currentTimeMillis());
        return "ORDER" + (1 + r.nextInt(2)) * 10000
                + r.nextInt(10000);
    }

}
