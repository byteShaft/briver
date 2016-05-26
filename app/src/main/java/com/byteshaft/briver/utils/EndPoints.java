package com.byteshaft.briver.utils;

/**
 * Created by fi8er1 on 06/05/2016.
 */
public class EndPoints {

    public static final String BASE_URL = "http://139.59.228.194:8000/api/";
    public static final String BASE_FILTER_DRIVERS = BASE_URL + "filter_drivers?";
    public static final String BASE_HIRE_REQUEST = BASE_URL + "hire";
    public static final String BASE_HIRE_RESPONSE = BASE_URL + "hire_response";
    public static final String BASE_ACCOUNTS = BASE_URL + "accounts/";
    public static final String REGISTER_DRIVER = BASE_URL + "register_driver";
    public static final String REGISTER_CUSTOMER = BASE_URL + "register_customer";
    public static final String ACTIVATE_ACCOUNT = BASE_ACCOUNTS + "activate";
    public static final String LOGIN = BASE_ACCOUNTS + "login";
    public static final String RESET_PASSWORD = BASE_ACCOUNTS + "reset_password";
    public static final String CHANGE_PASSWORD = BASE_ACCOUNTS + "change_password";
    public static final String USER_STATUS = BASE_ACCOUNTS + "status";

    public static final String SHOW_CUSTOMERS = BASE_URL + "customers/";
    public static final String SHOW_DRIVERS = BASE_URL + "drivers/";

//    public static final String DRIVERS_IN_RADIUS =  BASE_URL + "drivers_around";
}
