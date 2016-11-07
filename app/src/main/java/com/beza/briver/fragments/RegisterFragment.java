package com.beza.briver.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.beza.briver.R;
import com.beza.briver.utils.AppGlobals;
import com.beza.briver.utils.EndPoints;
import com.beza.briver.utils.Helpers;
import com.beza.briver.utils.LocationService;
import com.beza.briver.utils.MultipartDataUtility;
import com.beza.briver.utils.SpinnerPlus;
import com.beza.briver.utils.StaticVehicleData;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by fi8er1 on 28/04/2016.
 */
public class RegisterFragment extends Fragment implements View.OnClickListener {

    public static LatLng latLngDriverLocationForRegistration;
    public static boolean isRegistrationFragmentOpen;
    public static int responseCode;
    public static View baseViewRegisterFragment;
    public static boolean locationAcquired;
    public static HttpURLConnection connection;
    static String urlTOS = "http://139.59.228.194:8000/media/terms.html";
    static String userRegisterEmail;
    static int registerUserType = -1;
    static String driverLocationToString;
    final Runnable closeRegistration = new Runnable() {
        public void run() {
            getActivity().onBackPressed();
            locationAcquired = false;
        }
    };
    final Runnable openLocationServiceSettings = new Runnable() {
        public void run() {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }
    };
    final Runnable cancelRegistration = new Runnable() {
        public void run() {
            getActivity().onBackPressed();
        }
    };
    final private int CAPTURE_IMAGE = 1;
    final Runnable openCameraIntent = new Runnable() {
        public void run() {
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            File file = new File(Environment.getExternalStorageDirectory() + File.separator +
                    "image.jpg");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
            startActivityForResult(intent, CAPTURE_IMAGE);
        }
    };
    final private int PICK_IMAGE = 2;
    final Runnable openGalleryIntent = new Runnable() {
        public void run() {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);//
            startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_IMAGE);
        }
    };
    HashMap<Integer, String> hashMap;
    ImageButton ibPhotoOne;
    ImageButton ibPhotoTwo;
    ImageButton ibPhotoThree;
    int ibPosition;
    EditText etRegisterUserFullName;
    EditText etRegisterUserEmail;
    EditText etRegisterUserEmailRepeat;
    EditText etRegisterUserPassword;
    EditText etRegisterUserConfirmPassword;
    EditText etRegisterUserContactNumber;
    EditText etRegisterUserContactNumberRepeat;
    EditText etRegisterUserDrivingExperience;
    EditText etRegisterUserBasicBio;
    SpinnerPlus spinnerRegisterUserVehicleMake;
    SpinnerPlus spinnerRegisterUserVehicleModel;
    EditText etRegisterUserVehicleModelYear;
    EditText etRegisterAttachments;
    LinearLayout llRegisterElements;
    LinearLayout llRegisterElementsDriver;
    LinearLayout llRegisterElementsCustomer;
    LinearLayout llSpinnerVehicleModel;
    RadioGroup rgRegisterSelectUserType;
    RadioGroup rgRegisterCustomerSelectTransmissionType;
    RadioGroup rgRegisterDriverSelectTransmissionType;
    RadioGroup rgRegisterDriverGenderType;
    RadioButton rbRegisterCustomer;
    RadioButton rbRegisterDriver;
    RadioButton rbRegisterDriverMale;
    RadioButton rbRegisterDriverFemale;
    CheckBox cbTermsOfServiceCheck;
    String userRegisterFullName;
    String userRegisterEmailRepeat;
    String userRegisterPassword;
    String userRegisterConfirmPassword;
    String userRegisterContactNumber;
    String userRegisterConfirmContactNumber;
    String userRegisterDrivingExperience;
    String userRegisterBasicBio;
    String userRegisterVehicleMake;
    String userRegisterVehicleModel;
    String userRegisterVehicleModelYear;
    TextView spinnerTarget;
    TextView spinnerTargetTwo;
    int userRegisterVehicleType = -1;
    int transmissionType = -1;
    int genderType = -1;
    boolean dummySelectionVehicleMake = true;
    boolean dummySelectionVehicleModel = true;
    LocationService mLocationService;
    final Runnable recheckLocationServiceStatus = new Runnable() {
        public void run() {
            if (Helpers.isAnyLocationServiceAvailable()) {
                mLocationService.startLocationServices();
            } else {
                Helpers.AlertDialogWithPositiveNegativeNeutralFunctions(getActivity(), "Location Service disabled",
                        "Enable device GPS to continue driver registration", "Settings", "Exit", "Re-Check",
                        openLocationServiceSettings, closeRegistration, recheckLocationServiceStatus);
            }
        }
    };
    Button btnCreateUser;
    RegisterUserTask taskRegisterUser;
    boolean isUserRegistrationTaskRunning;
    private ArrayList<HashMap<Integer, String>> imagePathsArray;

    public static String getRegistrationStringForCustomer(
            String fullName, String email, String password, String phone, int transmissionType, int vehicleType,
            String vehicleMake, String vehicleModel, String vehicleModelYear) {

        JSONObject json = new JSONObject();
        try {
            json.put("full_name", fullName);
            json.put("email", email);
            json.put("password", password);
            json.put("phone_number", phone);
            json.put("transmission_type", transmissionType);
            json.put("vehicle_type", vehicleType);
            json.put("vehicle_make", vehicleMake);
            json.put("vehicle_model", vehicleModel);
            json.put("vehicle_model_year", vehicleModelYear);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        baseViewRegisterFragment = inflater.inflate(R.layout.fragment_register, container, false);
        mLocationService = new LocationService(getActivity());

        etRegisterUserFullName = (EditText) baseViewRegisterFragment.findViewById(R.id.et_register_full_name);
        etRegisterUserEmail = (EditText) baseViewRegisterFragment.findViewById(R.id.et_register_email);
        etRegisterUserEmailRepeat = (EditText) baseViewRegisterFragment.findViewById(R.id.et_register_email_repeat);
        etRegisterUserPassword = (EditText) baseViewRegisterFragment.findViewById(R.id.et_register_password);
        etRegisterUserConfirmPassword = (EditText) baseViewRegisterFragment.findViewById(R.id.et_register_confirm_password);
        etRegisterUserContactNumber = (EditText) baseViewRegisterFragment.findViewById(R.id.et_register_phone_number);
        etRegisterUserContactNumberRepeat = (EditText) baseViewRegisterFragment.findViewById(R.id.et_register_phone_number_repeat);
        etRegisterUserDrivingExperience = (EditText) baseViewRegisterFragment.findViewById(R.id.et_register_driving_experience);
        etRegisterUserBasicBio = (EditText) baseViewRegisterFragment.findViewById(R.id.et_register_bio);
        spinnerRegisterUserVehicleMake = (SpinnerPlus) baseViewRegisterFragment.findViewById(R.id.spinner_register_vehicle_make);
        spinnerRegisterUserVehicleModel = (SpinnerPlus) baseViewRegisterFragment.findViewById(R.id.spinner_register_vehicle_model);
        etRegisterUserVehicleModelYear = (EditText) baseViewRegisterFragment.findViewById(R.id.et_register_vehicle_model_year);
        etRegisterAttachments = (EditText) baseViewRegisterFragment.findViewById(R.id.et_register_attachments);

        cbTermsOfServiceCheck = (CheckBox) baseViewRegisterFragment.findViewById(R.id.cb_terms_of_service_check);

        baseViewRegisterFragment.findViewById(R.id.ll_register).requestFocus();
        rgRegisterSelectUserType = (RadioGroup) baseViewRegisterFragment.findViewById(R.id.rg_register_select_user_type);
        rgRegisterCustomerSelectTransmissionType = (RadioGroup) baseViewRegisterFragment.findViewById(R.id.rg_register_customer_select_transmission_type);
        rgRegisterDriverSelectTransmissionType = (RadioGroup) baseViewRegisterFragment.findViewById(R.id.rg_register_driver_select_transmission_type);
        rgRegisterDriverGenderType = (RadioGroup) baseViewRegisterFragment.findViewById(R.id.rg_register_driver_gender_type);
        rbRegisterCustomer = (RadioButton) baseViewRegisterFragment.findViewById(R.id.rb_register_customer);
        rbRegisterDriver = (RadioButton) baseViewRegisterFragment.findViewById(R.id.rb_register_driver);
        rbRegisterDriverMale = (RadioButton) baseViewRegisterFragment.findViewById(R.id.rb_register_driver_male);
        rbRegisterDriverFemale = (RadioButton) baseViewRegisterFragment.findViewById(R.id.rb_register_driver_female);

        etRegisterAttachments.setOnClickListener(this);

        btnCreateUser = (Button) baseViewRegisterFragment.findViewById(R.id.btn_register_create_account);
        btnCreateUser.setOnClickListener(this);
        hashMap = new HashMap<>();
        imagePathsArray = new ArrayList<>();

        llRegisterElements = (LinearLayout) baseViewRegisterFragment.findViewById(R.id.layout_elements_register);
        llRegisterElementsCustomer = (LinearLayout) baseViewRegisterFragment.findViewById(R.id.layout_elements_register_customer);
        llRegisterElementsDriver = (LinearLayout) baseViewRegisterFragment.findViewById(R.id.layout_elements_register_driver);
        llSpinnerVehicleModel = (LinearLayout) baseViewRegisterFragment.findViewById(R.id.ll_spinner_vehicle_model);

        Collection<String> valuesMainHashMap = StaticVehicleData.hmMain.keySet();
        String[] array = valuesMainHashMap.toArray(new String[valuesMainHashMap.size()]);
        ArrayAdapter<CharSequence> dataAdapter = new ArrayAdapter<CharSequence>(getActivity(), R.layout.spinner_text, array);
        dataAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        spinnerRegisterUserVehicleMake.setAdapter(dataAdapter);

        etRegisterUserContactNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(etRegisterUserContactNumber.getText().toString().length() < 4) {
                    etRegisterUserContactNumber.setText("+91-");
                    etRegisterUserContactNumber.setSelection(etRegisterUserContactNumber.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        etRegisterUserContactNumberRepeat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(etRegisterUserContactNumberRepeat.getText().toString().length() < 4) {
                    etRegisterUserContactNumberRepeat.setText("+91-");
                    etRegisterUserContactNumberRepeat.setSelection(etRegisterUserContactNumberRepeat.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        etRegisterUserContactNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    etRegisterUserContactNumber.setText("+91-");
                    etRegisterUserContactNumber.setSelection(etRegisterUserContactNumber.getText().length());
                }
            }
        });

        etRegisterUserContactNumberRepeat.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    etRegisterUserContactNumberRepeat.setText("+91-");
                    etRegisterUserContactNumberRepeat.setSelection(etRegisterUserContactNumberRepeat.getText().length());
                }
            }
        });

        spinnerRegisterUserVehicleMake.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinnerTarget = (TextView) view.findViewById(android.R.id.text1);
                if (dummySelectionVehicleMake) {
                    spinnerTarget.setText("Select Manufacturer");
                    spinnerTarget.setTextColor(Color.GRAY);
                    dummySelectionVehicleMake = false;
                } else {
                    Collection<String> valuesSecondaryHashMap = StaticVehicleData.hmMain.get(
                            spinnerRegisterUserVehicleMake.getSelectedItem()).keySet();
                    spinnerTarget.setText(spinnerRegisterUserVehicleMake.getSelectedItem().toString());
                    spinnerTarget.setTextColor(Color.WHITE);
                    String[] array = valuesSecondaryHashMap.toArray(new String[valuesSecondaryHashMap.size()]);
                    ArrayAdapter<CharSequence> dataAdapterSecondary = new ArrayAdapter<CharSequence>(
                            getActivity(), R.layout.spinner_text_two, array);
                    dataAdapterSecondary.setDropDownViewResource(R.layout.simple_spinner_dropdown_two);
                    if (array.length > 1) {
                        dummySelectionVehicleModel = true;
                    } else {
                        dummySelectionVehicleModel = false;
                    }
                    spinnerRegisterUserVehicleModel.setAdapter(dataAdapterSecondary);
                    llSpinnerVehicleModel.setVisibility(View.VISIBLE);
                    userRegisterVehicleMake = spinnerRegisterUserVehicleMake.getSelectedItem().toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        spinnerRegisterUserVehicleModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinnerTargetTwo = (TextView) view.findViewById(android.R.id.text2);
                if (dummySelectionVehicleModel) {
                    spinnerTargetTwo.setText("Select Model");
                    spinnerTargetTwo.setTextColor(Color.GRAY);
                    userRegisterVehicleType = -1;
                    dummySelectionVehicleModel = false;
                } else {
                    spinnerTargetTwo.setText(spinnerRegisterUserVehicleModel.getSelectedItem().toString());
                    spinnerTargetTwo.setTextColor(Color.WHITE);
                    userRegisterVehicleModel = spinnerRegisterUserVehicleModel.getSelectedItem().toString();
                    userRegisterVehicleType = Integer.parseInt(StaticVehicleData.hmMain.get(
                            spinnerRegisterUserVehicleMake.getSelectedItem()).get(
                            spinnerRegisterUserVehicleModel.getSelectedItem()).toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        rgRegisterSelectUserType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_register_customer) {
                    rbRegisterDriver.setVisibility(View.INVISIBLE);
                    llRegisterElements.setVisibility(View.VISIBLE);
                    llRegisterElementsCustomer.setVisibility(View.VISIBLE);
                    registerUserType = 0;
                    rgRegisterCustomerSelectTransmissionType.check(R.id.rb_register_customer_transmission_type_manual);
                } else if (checkedId == R.id.rb_register_driver) {
                    rbRegisterCustomer.setVisibility(View.INVISIBLE);
                    llRegisterElements.setVisibility(View.VISIBLE);
                    llRegisterElementsDriver.setVisibility(View.VISIBLE);
                    registerUserType = 1;
                    rgRegisterDriverGenderType.check(R.id.rb_register_driver_male);
                    rgRegisterDriverSelectTransmissionType.check(R.id.rb_register_driver_transmission_type_both);
                    if (Helpers.checkPlayServicesAvailability()) {
                        if (Helpers.isAnyLocationServiceAvailable()) {
                            mLocationService.startLocationServices();
                            Helpers.showSnackBar(baseViewRegisterFragment, "Acquiring location for registration", Snackbar.LENGTH_SHORT, "#ffffff");
                        } else {
                            Helpers.AlertDialogWithPositiveNegativeNeutralFunctions(getActivity(), "Location Service disabled",
                                    "Enable device GPS to continue driver registration", "Settings", "Exit", "Re-Check",
                                    openLocationServiceSettings, closeRegistration, recheckLocationServiceStatus);
                        }

                    }
                }
            }
        });

        rgRegisterCustomerSelectTransmissionType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_register_customer_transmission_type_manual) {
                    transmissionType = 0;
                }
                if (checkedId == R.id.rb_register_customer_transmission_type_auto) {
                    transmissionType = 1;
                }
            }
        });

        rgRegisterDriverSelectTransmissionType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_register_driver_transmission_type_manual) {
                    transmissionType = 0;
                } else if (checkedId == R.id.rb_register_driver_transmission_type_auto) {
                    transmissionType = 1;
                } else if (checkedId == R.id.rb_register_driver_transmission_type_both) {
                    transmissionType = 2;
                }
            }
        });

        rgRegisterDriverGenderType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.rb_register_driver_male) {
                    genderType = 0;
                } else if (i == R.id.rb_register_driver_female) {
                    genderType = 1;
                }
            }
        });

        SpannableStringBuilder text = new SpannableStringBuilder();
        text.append(getString(R.string.TermsOfServiceInitialText)).append(" ");

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Helpers.WebViewAlertDialog(getActivity(), urlTOS);
            }
        };
        TextPaint ds = new TextPaint();
        clickableSpan.updateDrawState(ds);
        ds.setUnderlineText(false);
        text.append(getString(R.string.TermsOfServiceLateralText));

        text.setSpan(clickableSpan, getString(R.string.TermsOfServiceInitialText).length() + 1,
                getString(R.string.TermsOfServiceInitialText).length() + 1 + getString(R.string.TermsOfServiceLateralText).length(), 0);
        cbTermsOfServiceCheck.setMovementMethod(LinkMovementMethod.getInstance());
        cbTermsOfServiceCheck.setText(text, TextView.BufferType.SPANNABLE);
        return baseViewRegisterFragment;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register_create_account:

                userRegisterFullName = etRegisterUserFullName.getText().toString();
                userRegisterEmail = etRegisterUserEmail.getText().toString();
                userRegisterEmailRepeat = etRegisterUserEmailRepeat.getText().toString();
                userRegisterPassword = etRegisterUserPassword.getText().toString();
                userRegisterConfirmPassword = etRegisterUserConfirmPassword.getText().toString();
                userRegisterContactNumber = etRegisterUserContactNumber.getText().toString();
                userRegisterConfirmContactNumber = etRegisterUserContactNumberRepeat.getText().toString();
                userRegisterDrivingExperience = etRegisterUserDrivingExperience.getText().toString();
                userRegisterBasicBio = etRegisterUserBasicBio.getText().toString();
                userRegisterVehicleModelYear = etRegisterUserVehicleModelYear.getText().toString();

                if (validateRegisterInfo()) {
                    if (registerUserType == 1) {
                        if (latLngDriverLocationForRegistration != null) {
                            driverLocationToString = latLngDriverLocationForRegistration.latitude + "," + latLngDriverLocationForRegistration.longitude;
                            taskRegisterUser = (RegisterUserTask) new RegisterUserTask().execute();
                        } else {
                            driverLocationToString = null;
                            Helpers.AlertDialogWithPositiveFunctionNegativeButton(getActivity(), "Location Unavailable",
                                    "Your location is being acquired. You'll have to wait for the location to be acquired before you proceed",
                                    "Cancel Registration", "Wait", cancelRegistration);
                        }
                    } else {
                        taskRegisterUser = (RegisterUserTask) new RegisterUserTask().execute();
                    }
                } else {
                    Log.e("validation", "failed");
                }
                break;
            case R.id.et_register_attachments:
                showDocumentsAttachmentCustomDialog();
                etRegisterAttachments.setError(null);
                break;
        }
    }

    public void loadFragment(Fragment fragment) {
        android.app.FragmentTransaction tx = getFragmentManager().beginTransaction();
        tx.setCustomAnimations(R.animator.anim_transition_fragment_slide_right_enter, R.animator.anim_transition_fragment_slide_left_exit,
                R.animator.anim_transition_fragment_slide_left_enter, R.animator.anim_transition_fragment_slide_right_exit);
        tx.replace(R.id.container, fragment).addToBackStack("Confirmation");
        tx.commit();
    }

    public boolean validateRegisterInfo() {
        boolean valid = true;

        if (userRegisterFullName.trim().isEmpty()) {
            etRegisterUserFullName.setError("Empty");
            valid = false;
        } else {
            etRegisterUserFullName.setError(null);
        }

        if (userRegisterEmail.trim().isEmpty()) {
            etRegisterUserEmail.setError("Empty");
            valid = false;
        } else if (!userRegisterEmail.trim().isEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(userRegisterEmail).matches()) {
            etRegisterUserEmail.setError("Invalid E-Mail");
            valid = false;
        } else if (!userRegisterEmail.equals(userRegisterEmailRepeat)) {
            etRegisterUserEmailRepeat.setError("E-Mail doesn't match");
            valid = false;
        } else {
            etRegisterUserEmail.setError(null);
        }

        if (userRegisterPassword.trim().isEmpty() || userRegisterPassword.length() < 6) {
            etRegisterUserPassword.setError("Minimum 6 Characters");
            valid = false;
        } else if (!userRegisterPassword.equals(userRegisterConfirmPassword)) {
            etRegisterUserConfirmPassword.setError("Password doesn't match");
            valid = false;
        } else {
            etRegisterUserPassword.setError(null);
        }

        if (userRegisterContactNumber.trim().isEmpty()) {
            etRegisterUserContactNumber.setError("Empty");
            valid = false;
        } else if (!userRegisterContactNumber.equals(userRegisterConfirmContactNumber)) {
            etRegisterUserContactNumberRepeat.setError("Contact doesn't match");
            valid = false;
        } else if (!userRegisterContactNumber.isEmpty() && !PhoneNumberUtils.isGlobalPhoneNumber(userRegisterContactNumber)) {
            etRegisterUserContactNumber.setError("Number is invalid");
            valid = false;
        } else {
            etRegisterUserContactNumber.setError(null);
        }

        if (registerUserType == 1) {
            if (userRegisterDrivingExperience.trim().isEmpty()) {
                etRegisterUserDrivingExperience.setError("Empty");
                valid = false;
            } else {
                etRegisterUserDrivingExperience.setError(null);
            }

            if (!etRegisterAttachments.getText().toString().equals("Documents 3/3")) {
                etRegisterAttachments.setError("Attach documents");
                valid = false;
            } else {
                etRegisterAttachments.setError(null);
            }
        } else if (registerUserType == 0) {

            if (userRegisterVehicleModelYear.isEmpty()) {
                etRegisterUserVehicleModelYear.setError("Empty");
                valid = false;
            } else if (userRegisterVehicleModelYear.length() < 4) {
                etRegisterUserVehicleModelYear.setError("Minimum 4 Characters");
                valid = false;
            }

            if (valid && userRegisterVehicleType == -1) {
                Helpers.showSnackBar(getView(), "Select Vehicle", Snackbar.LENGTH_LONG, "#f44336");
                valid = false;
            }
        }

        if (valid && !cbTermsOfServiceCheck.isChecked()) {
            Helpers.showSnackBar(getView(), "Check terms of service to continue", Snackbar.LENGTH_LONG, "#f44336");
            valid = false;
        }

        return valid;
    }

    @Override
    public void onPause() {
        super.onPause();
        isRegistrationFragmentOpen = false;
        if (mLocationService.mGoogleApiClient != null && mLocationService.mGoogleApiClient.isConnected()) {
            mLocationService.stopLocationService();
        }
        if (isUserRegistrationTaskRunning) {
            taskRegisterUser.cancel(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isRegistrationFragmentOpen = true;
        if (!locationAcquired && rgRegisterSelectUserType.getCheckedRadioButtonId() == R.id.rb_register_driver) {
            if (Helpers.isAnyLocationServiceAvailable()) {
                mLocationService.startLocationServices();
                Helpers.showSnackBar(baseViewRegisterFragment, "Acquiring location for registration", Snackbar.LENGTH_SHORT, "#ffffff");
            } else {
                Helpers.AlertDialogWithPositiveNegativeNeutralFunctions(getActivity(), "Location Service disabled",
                        "Enable device GPS to continue driver registration", "Settings", "Exit", "Re-Check",
                        openLocationServiceSettings, closeRegistration, recheckLocationServiceStatus);
            }
        }
    }

    public void onRegistrationSuccess() {
        Toast.makeText(getActivity(), "Registration successful", Toast.LENGTH_SHORT).show();
        Helpers.closeSoftKeyboard(getActivity());
        loadFragment(new CodeConfirmationFragment());
        CodeConfirmationFragment.isFragmentOpenedFromLogin = false;
    }

    public void onRegistrationFailed(String message) {
        Helpers.showSnackBar(baseViewRegisterFragment, message, Snackbar.LENGTH_SHORT, "#f44336");
    }

    private void showDocumentsAttachmentCustomDialog() {
        final Dialog customAttachmentsDialog = new Dialog(getActivity());
        customAttachmentsDialog.setContentView(R.layout.layout_custom_attachment_dialog);

        customAttachmentsDialog.setCancelable(false);
        customAttachmentsDialog.setTitle("Attach Documents");

        Button buttonNo = (Button) customAttachmentsDialog.findViewById(R.id.btn_driver_hire_dialog_cancel);
        buttonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customAttachmentsDialog.dismiss();
            }
        });

        Button buttonYes = (Button) customAttachmentsDialog.findViewById(R.id.btn_driver_hire_dialog_hire);
        buttonYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hashMap.size() > 2) {
                    customAttachmentsDialog.dismiss();
                    etRegisterAttachments.setText("Documents 3/3");
                    etRegisterAttachments.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_edit_text_attachment, 0, R.mipmap.ic_edit_text_ok, 0);
                } else {
                    Toast.makeText(getActivity(), "Add all photos to continue", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ibPhotoOne = (ImageButton) customAttachmentsDialog.findViewById(R.id.ib_photo_one);
        ibPhotoTwo = (ImageButton) customAttachmentsDialog.findViewById(R.id.ib_photo_two);
        ibPhotoThree = (ImageButton) customAttachmentsDialog.findViewById(R.id.ib_photo_three);

        ibPhotoOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ibPosition = 0;
                Helpers.AlertDialogWithPositiveNegativeFunctionsNeutralButton(getActivity(), "License Front",
                        "Select an option to add photo", "Camera", "Gallery", "Cancel", openCameraIntent, openGalleryIntent);
            }
        });

        ibPhotoTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ibPosition = 1;
                Helpers.AlertDialogWithPositiveNegativeFunctionsNeutralButton(getActivity(), "License Back",
                        "Select an option to add photo", "Camera", "Gallery", "Cancel", openCameraIntent, openGalleryIntent);
            }
        });

        ibPhotoThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ibPosition = 2;
                Helpers.AlertDialogWithPositiveNegativeFunctionsNeutralButton(getActivity(), "Police Verification",
                        "Select an option to add photo", "Camera", "Gallery", "Cancel", openCameraIntent, openGalleryIntent);
            }
        });

        if (etRegisterAttachments.getText().toString().equals("Documents 3/3")) {
            ibPhotoOne.setBackgroundDrawable(null);
            ibPhotoOne.setImageBitmap(Helpers.getResizedBitmapToDisplay(Helpers.getCroppedBitmap(BitmapFactory.decodeFile(hashMap.get(0))), 120));
            ibPhotoTwo.setBackgroundDrawable(null);
            ibPhotoTwo.setImageBitmap(Helpers.getResizedBitmapToDisplay(Helpers.getCroppedBitmap(BitmapFactory.decodeFile(hashMap.get(1))), 120));
            ibPhotoThree.setBackgroundDrawable(null);
            ibPhotoThree.setImageBitmap(Helpers.getResizedBitmapToDisplay(Helpers.getCroppedBitmap(BitmapFactory.decodeFile(hashMap.get(2))), 120));
        }

        customAttachmentsDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_IMAGE)
                new ConvertImage().execute(data);
            else if (requestCode == CAPTURE_IMAGE)
                onCaptureImageResult();
        }
    }

    private void onCaptureImageResult() {
        File file = new File(Environment.getExternalStorageDirectory() + File.separator +
                "image.jpg");
        Bitmap bm = Helpers.decodeBitmapFromFile(file.getAbsolutePath());
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        if (bm.getHeight() > 3200 || bm.getWidth() > 3200) {
            Bitmap.createScaledBitmap(bm, bm.getWidth() / 4, bm.getHeight() / 4, false).compress(Bitmap.CompressFormat.JPEG, 40, bytes);
        } else if (bm.getHeight() > 2560 || bm.getWidth() > 2560) {
            Bitmap.createScaledBitmap(bm, bm.getWidth() / 3, bm.getHeight() / 3, false).compress(Bitmap.CompressFormat.JPEG, 40, bytes);
        } else if (bm.getHeight() > 1600 || bm.getWidth() > 1600) {
            Bitmap.createScaledBitmap(bm, bm.getWidth() / 2, bm.getHeight() / 2, false).compress(Bitmap.CompressFormat.JPEG, 40, bytes);
        } else {
            bm.compress(Bitmap.CompressFormat.JPEG, 60, bytes);
        }
        hashMap.put(ibPosition, writeImageToExternalStorage(bytes, String.valueOf(ibPosition)));
        imagePathsArray.add(hashMap);
        if (ibPosition == 0) {
            ibPhotoOne.setBackgroundDrawable(null);
            ibPhotoOne.setImageBitmap(Helpers.getCroppedBitmap(Helpers.getResizedBitmapToDisplay(bm, 120)));
        } else if (ibPosition == 1) {
            ibPhotoTwo.setBackgroundDrawable(null);
            ibPhotoTwo.setImageBitmap(Helpers.getCroppedBitmap(Helpers.getResizedBitmapToDisplay(bm, 120)));
        } else if (ibPosition == 2) {
            ibPhotoThree.setBackgroundDrawable(null);
            ibPhotoThree.setImageBitmap(Helpers.getCroppedBitmap(Helpers.getResizedBitmapToDisplay(bm, 120)));
        }
    }

    private String writeImageToExternalStorage(ByteArrayOutputStream bytes, String name) {
        File destination = new File(Environment.getExternalStorageDirectory() + File.separator
                + "Android/data" + File.separator + AppGlobals.getContext().getPackageName());
        if (!destination.exists()) {
            destination.mkdirs();
        }
        File file = new File(destination, name + ".jpg");
        FileOutputStream fo;
        try {
            file.createNewFile();
            fo = new FileOutputStream(file);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

    private class RegisterUserTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Helpers.showProgressDialog(getActivity(), "Registering");
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL url;
                if (registerUserType == 0) {
                    url = new URL(EndPoints.REGISTER_CUSTOMER);
                } else {
                    url = new URL(EndPoints.REGISTER_DRIVER);
                }
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setInstanceFollowRedirects(false);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("charset", "utf-8");
                DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                if (registerUserType == 0) {
                    String dataForRegistration = getRegistrationStringForCustomer(userRegisterFullName,
                            userRegisterEmail, userRegisterPassword, userRegisterContactNumber, transmissionType, userRegisterVehicleType,
                            userRegisterVehicleMake, userRegisterVehicleModel, userRegisterVehicleModelYear);
                    out.writeBytes(dataForRegistration);
                    out.flush();
                    out.close();
                } else {
                    MultipartDataUtility http;
                    try {
                        http = new MultipartDataUtility(url, "POST", false);
                        http.addFormField("full_name", userRegisterFullName);
                        http.addFormField("email", userRegisterEmail);
                        http.addFormField("password", userRegisterPassword);
                        http.addFormField("phone_number", userRegisterContactNumber);
                        http.addFormField("transmission_type", String.valueOf(transmissionType));
                        http.addFormField("gender", String.valueOf(genderType));
                        http.addFormField("driving_experience", userRegisterDrivingExperience);
                        if (userRegisterBasicBio != null || !userRegisterBasicBio.equals("")) {
                            http.addFormField("bio", userRegisterBasicBio);
                        }
                        if (driverLocationToString != null) {
                            http.addFormField("location", driverLocationToString);
                        }
                        int doc = 1;
                        for (HashMap<Integer, String> item : imagePathsArray) {
                            System.out.println(item);
                            File file = new File(item.get(doc - 1));
                            http.addFilePart(("doc" + doc), file);
                            doc++;
                        }
                        final byte[] bytes = http.finish();
                        int counter = 0;
                        for (HashMap<Integer, String> item : imagePathsArray) {
                            try {
                                OutputStream os = new FileOutputStream(item.get(counter));
                                os.write(bytes);
                                counter++;
                            } catch (IOException ignored) {
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                responseCode = connection.getResponseCode();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Helpers.dismissProgressDialog();

            if (responseCode == 201 || responseCode == 200) {
                onRegistrationSuccess();
            } else if (responseCode == 400) {
                onRegistrationFailed("Registration failed. Email already in use");
            } else {
                onRegistrationFailed("Registration failed. Check internet and retry");
            }
        }
    }

    class ConvertImage extends AsyncTask<Intent, String, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Helpers.showProgressDialog(getActivity(), "Loading Image");
        }

        @Override
        protected Bitmap doInBackground(Intent... params) {
            Bitmap bm = null;
            if (params[0] != null) {
                try {
                    bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), params[0].getData());
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    if (bm.getHeight() > 3200 || bm.getWidth() > 3200) {
                        Bitmap.createScaledBitmap(bm, bm.getWidth() / 4, bm.getHeight() / 4, false).compress(Bitmap.CompressFormat.JPEG, 40, bytes);
                    } else if (bm.getHeight() > 2560 || bm.getWidth() > 2560) {
                        Bitmap.createScaledBitmap(bm, bm.getWidth() / 3, bm.getHeight() / 3, false).compress(Bitmap.CompressFormat.JPEG, 40, bytes);
                    } else if (bm.getHeight() > 1600 || bm.getWidth() > 1600) {
                        Bitmap.createScaledBitmap(bm, bm.getWidth() / 2, bm.getHeight() / 2, false).compress(Bitmap.CompressFormat.JPEG, 40, bytes);
                    } else {
                        bm.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
                    }
                    hashMap.put(ibPosition, writeImageToExternalStorage(bytes, String.valueOf(ibPosition)));
                    imagePathsArray.add(hashMap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return bm;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            Helpers.dismissProgressDialog();
            if (ibPosition == 0) {
                ibPhotoOne.setBackgroundDrawable(null);
                ibPhotoOne.setImageBitmap(Helpers.getCroppedBitmap(Helpers.getResizedBitmapToDisplay(bitmap, 120)));
            } else if (ibPosition == 1) {
                ibPhotoTwo.setBackgroundDrawable(null);
                ibPhotoTwo.setImageBitmap(Helpers.getCroppedBitmap(Helpers.getResizedBitmapToDisplay(bitmap, 120)));
            } else if (ibPosition == 2) {
                ibPhotoThree.setBackgroundDrawable(null);
                ibPhotoThree.setImageBitmap(Helpers.getCroppedBitmap(Helpers.getResizedBitmapToDisplay(bitmap, 120)));
            }
        }
    }
}