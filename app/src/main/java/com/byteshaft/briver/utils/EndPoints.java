package com.byteshaft.briver.utils;

/**
 * Created by fi8er1 on 06/05/2016.
 */
public class EndPoints {
    public static final String SERVER_URL = "http://139.59.228.194:8000/";
    public static final String BASE_URL = "http://139.59.228.194:8000/api/";
    public static final String BASE_URL_HIRE = "http://139.59.228.194:8000/api/hire/";
    public static final String BASE_FILTER_DRIVERS = BASE_URL_HIRE + "filter-drivers?";
    public static final String HIRE_REQUEST = BASE_URL_HIRE + "create";
    public static final String HIRING_REQUESTS = BASE_URL_HIRE + "list";
    public static final String HIRE_RESPONSE = BASE_URL + "hire-response";
    public static final String BASE_URL_USER = BASE_URL + "user/";
    public static final String PUSH_NOTIFICATION_ACTIVATION = BASE_URL_USER + "push-id/add";
    public static final String BASE_ACCOUNTS_ME =  BASE_URL_USER + "me";
    public static final String REGISTER_DRIVER = BASE_URL_USER + "driver-registration";
    public static final String REGISTER_CUSTOMER = BASE_URL_USER + "customer-registration";
    public static final String ACTIVATE_ACCOUNT = BASE_URL_USER + "activation";
    public static final String LOGIN = BASE_URL_USER + "login";
    public static final String RESET_PASSWORD = BASE_URL_USER + "forgotten-password";
    public static final String CHANGE_PASSWORD = BASE_URL_USER + "change=password";
    public static final String USER_STATUS = BASE_URL_USER + "status";
}
