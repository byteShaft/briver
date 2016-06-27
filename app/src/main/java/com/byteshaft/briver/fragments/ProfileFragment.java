package com.byteshaft.briver.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.byteshaft.briver.MainActivity;
import com.byteshaft.briver.R;
import com.byteshaft.briver.utils.AppGlobals;
import com.byteshaft.briver.utils.EndPoints;
import com.byteshaft.briver.utils.Helpers;
import com.byteshaft.briver.utils.MultipartDataUtility;
import com.byteshaft.briver.utils.WebServiceHelpers;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by fi8er1 on 01/05/2016.
 */
public class ProfileFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

    public static int responseCode;
    public static boolean isProfileFragmentOpen;
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
    View baseViewProfileFragment;
    LinearLayout llProfileDriverElements;
    EditText etProfileEmail;
    EditText etProfileFullName;
    EditText etProfileContactNumber;
    EditText etProfileDrivingExperience;
    EditText etProfileBio;
    EditText etProfileAttachments;
    boolean documentsRetrieved;
    boolean imageChanged;
    boolean imagePendingUpload;
    boolean isImagesRetrievingTaskRunning;
    String profileFullName;
    String profileContactNumber;
    String profileDrivingExperience;
    String profileBio;

    HttpURLConnection connection;
    EditProfileTask taskEditProfile;
    boolean isEditProfileTaskRunning;
    HashMap<Integer, String> hashMap;
    HashMap<Integer, String> hashMapTemp;
    ImageButton ibPhotoOne;
    ImageButton ibPhotoTwo;
    ImageButton ibPhotoThree;
    int ibPosition;
    RatingBar ratingBarHome;
    TextView tvRatingBarHome;
    RetrieveImages taskRetrieveImages;
    private ArrayList<HashMap<Integer, String>> imagePathsArray;

    public static String getProfileEditStringForCustomer(
            String full_name, String phone_number) {
        return "{" +
                String.format("\"full_name\": \"%s\", ", full_name) +
                String.format("\"phone_number\": \"%s\"", phone_number) +
                "}";
    }

    public static String getProfileEditStringForDriver(
            String full_name, String phone_number, String driving_experience, String bio) {
        return "{" +
                String.format("\"full_name\": \"%s\", ", full_name) +
                String.format("\"phone_number\": \"%s\", ", phone_number) +
                String.format("\"driving_experience\": \"%s\", ", driving_experience) +
                String.format("\"bio\": \"%s\"", bio) +
                "}";
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        baseViewProfileFragment = inflater.inflate(R.layout.fragment_profile, container, false);

        etProfileEmail = (EditText) baseViewProfileFragment.findViewById(R.id.et_profile_email);
        etProfileFullName = (EditText) baseViewProfileFragment.findViewById(R.id.et_profile_full_name);
        etProfileContactNumber = (EditText) baseViewProfileFragment.findViewById(R.id.et_profile_phone_number);
        etProfileDrivingExperience = (EditText) baseViewProfileFragment.findViewById(R.id.et_profile_driving_experience);
        etProfileBio = (EditText) baseViewProfileFragment.findViewById(R.id.et_profile_bio);
        etProfileAttachments = (EditText) baseViewProfileFragment.findViewById(R.id.et_profile_attachments);

        llProfileDriverElements = (LinearLayout) baseViewProfileFragment.findViewById(R.id.layout_profile_elements_driver);

        ratingBarHome = (RatingBar) baseViewProfileFragment.findViewById(R.id.rBar_profile);
        tvRatingBarHome = (TextView) baseViewProfileFragment.findViewById(R.id.tv_rBar_profile);

        etProfileAttachments.setOnClickListener(this);

        float starsValue = AppGlobals.getStarsValue();

        if (starsValue > 0.0) {
            ratingBarHome.setRating(starsValue);
        } else {
            ratingBarHome.setRating((float) 0.0);
        }
        tvRatingBarHome.setText("(" + AppGlobals.getRatingCount() + ")");

        if (AppGlobals.getUserType() == 1) {
            llProfileDriverElements.setVisibility(View.VISIBLE);
            etProfileDrivingExperience.setText(AppGlobals.getDrivingExperience());
            etProfileBio.setText(AppGlobals.getDriverBio());
        }
        etProfileFullName.setText(AppGlobals.getPeronName());
        etProfileEmail.setText(AppGlobals.getUsername());
        etProfileContactNumber.setText(AppGlobals.getPhoneNumber());

        hashMap = new HashMap<>();
        hashMapTemp = new HashMap<>();
        imagePathsArray = new ArrayList<>();

        return baseViewProfileFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_done, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                profileFullName = etProfileFullName.getText().toString();
                profileContactNumber = etProfileContactNumber.getText().toString();
                if (AppGlobals.getUserType() == 1) {
                    profileDrivingExperience = etProfileDrivingExperience.getText().toString();
                    profileBio = etProfileBio.getText().toString();
                }
                if (validateProfileChangeInfo()) {
                    taskEditProfile = (EditProfileTask) new EditProfileTask().execute();
                }
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean validateProfileChangeInfo() {
        boolean valid = true;

        if (profileFullName.trim().isEmpty()) {
            etProfileFullName.setError("Empty");
            valid = false;
        } else if (profileFullName.trim().length() < 3) {
            etProfileFullName.setError("at least 3 characters");
            valid = false;
        }

        if (profileContactNumber.trim().isEmpty()) {
            etProfileContactNumber.setError("Empty");
            valid = false;
        } else if (!profileContactNumber.isEmpty() && !PhoneNumberUtils.isGlobalPhoneNumber(profileContactNumber)) {
            etProfileContactNumber.setError("Number is invalid");
            valid = false;
        } else {
            etProfileContactNumber.setError(null);
        }

        if (AppGlobals.getUserType() == 1) {
            if (profileDrivingExperience.trim().isEmpty()) {
                etProfileDrivingExperience.setError("Empty");
                valid = false;
            } else {
                etProfileDrivingExperience.setError(null);
            }
            if (profileFullName.equals(AppGlobals.getPeronName()) &&
                    profileContactNumber.equals(AppGlobals.getPhoneNumber()) &&
                    profileDrivingExperience.equals(AppGlobals.getDrivingExperience())
                    && profileBio.equals(AppGlobals.getDriverBio()) && !imagePendingUpload) {
                Helpers.showSnackBar(getView(), "No changes to submit", Snackbar.LENGTH_LONG, "#ffffff");
                valid = false;
            }
        }
        return valid;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_profile_attachments:
                if (documentsRetrieved) {
                    showDocumentsAttachmentCustomDialog();
                } else {
                    taskRetrieveImages = (RetrieveImages) new RetrieveImages().execute();
                }
                break;
        }
    }

    private void onEditSuccess(String message) {
        AppGlobals.putPersonName(profileFullName);
        AppGlobals.putPhoneNumber(profileContactNumber);
        if (AppGlobals.getUserType() == 1) {
            AppGlobals.putDrivingExperience(profileDrivingExperience);
            if (!profileBio.trim().isEmpty()) {
                AppGlobals.putDriverBio(profileBio);
            }
        }
        Helpers.showSnackBar(getView(), message, Snackbar.LENGTH_LONG, "#A4C639");
        MainActivity.tvPersonName.setText(AppGlobals.getPeronName());
        Helpers.closeSoftKeyboard(getActivity());
        getActivity().onBackPressed();
        imagePendingUpload = false;
    }

    private void onEditFailed(String message) {
        Helpers.showSnackBar(getView(), message, Snackbar.LENGTH_LONG, "#f44336");
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isEditProfileTaskRunning) {
            taskEditProfile.cancel(true);
        }

        if (isImagesRetrievingTaskRunning) {
            taskRetrieveImages.cancel(true);
        }
        isProfileFragmentOpen = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        isProfileFragmentOpen = true;
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
                if (imageChanged) {
                    customAttachmentsDialog.dismiss();
                    etProfileAttachments.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_edit_text_attachment, 0, R.mipmap.ic_edit_text_add_image, 0);
                    File destination = new File(Environment.getExternalStorageDirectory() + File.separator
                            + "Android/data" + File.separator + AppGlobals.getContext().getPackageName());
                    for (int i = 0; i < 3; i++) {
                        if (hashMapTemp.containsKey(i)) {
                            File file = new File(destination, i + ".jpg");
                            file.delete();
                            File from = new File(destination, i + "temp" + ".jpg");
                            File to = new File(destination, i + ".jpg");
                            from.renameTo(to);
                        }
                    }

                    Log.i("images array", String.valueOf(imagePathsArray));
                    imageChanged = false;
                } else {
                    Toast.makeText(getActivity(), "No Change to submit", Toast.LENGTH_SHORT).show();
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

        ibPhotoOne.setBackgroundDrawable(null);
        ibPhotoOne.setImageBitmap(Helpers.getCroppedBitmap(Helpers.getResizedBitmapToDisplay(BitmapFactory.decodeFile(hashMap.get(0)), 120)));
        ibPhotoTwo.setBackgroundDrawable(null);
        ibPhotoTwo.setImageBitmap(Helpers.getCroppedBitmap(Helpers.getResizedBitmapToDisplay(BitmapFactory.decodeFile(hashMap.get(1)), 120)));
        ibPhotoThree.setBackgroundDrawable(null);
        ibPhotoThree.setImageBitmap(Helpers.getCroppedBitmap(Helpers.getResizedBitmapToDisplay(BitmapFactory.decodeFile(hashMap.get(2)), 120)));
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
        hashMapTemp.put(ibPosition, writeImageToExternalStorage(bytes, String.valueOf(ibPosition + "temp")));
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
        imageChanged = true;
        imagePendingUpload = true;
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

    private class EditProfileTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isEditProfileTaskRunning = true;
            Helpers.showProgressDialog(getActivity(), "Saving Changes");
        }

        @Override
        protected Void doInBackground(Void... params) {
            String urlString;
            DataOutputStream out = null;
            try {
                urlString = EndPoints.BASE_ACCOUNTS_ME;
                if (!imagePendingUpload) {
                    connection = WebServiceHelpers.openConnectionForUrl(urlString, "PUT", true);
                    out = new DataOutputStream(connection.getOutputStream());
                }
                String editProfileString = null;
                if (AppGlobals.getUserType() == 0) {
                    editProfileString = getProfileEditStringForCustomer(profileFullName, profileContactNumber);
                } else if (AppGlobals.getUserType() == 1) {
                    if (imagePendingUpload) {
                        MultipartDataUtility http;
                        try {
                            URL url = new URL(urlString);
                            URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(),
                                    url.getPort(), url.getPath(), url.getQuery(), url.getRef());
                            url = uri.toURL();
                            http = new MultipartDataUtility(url, "PUT", true);
                            http.addFormField("full_name", profileFullName);
                            http.addFormField("phone_number", profileContactNumber);
                            http.addFormField("driving_experience", profileDrivingExperience);
                            if (profileBio != null || !profileBio.equals("")) {
                                http.addFormField("bio", profileBio);
                            }
                            Log.i("TAG", String.valueOf(imagePathsArray));
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
                        } catch (IOException | URISyntaxException e) {
                            e.printStackTrace();
                        }
                    } else {
                        editProfileString = getProfileEditStringForDriver(profileFullName, profileContactNumber,
                                profileDrivingExperience, profileBio);
                    }
                }
                if (!imagePendingUpload) {
                    assert out != null;
                    out.writeBytes(editProfileString);
                    out.flush();
                    out.close();
                    responseCode = connection.getResponseCode();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            isEditProfileTaskRunning = false;
            Helpers.dismissProgressDialog();
            if (responseCode == 200) {
                onEditSuccess("Details change successful");
            } else {
                onEditFailed("Details change failed!");
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            isEditProfileTaskRunning = false;
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
                        bm.compress(Bitmap.CompressFormat.JPEG, 60, bytes);
                    }
                    hashMapTemp.put(ibPosition, writeImageToExternalStorage(bytes, String.valueOf(ibPosition + "temp")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            return bm;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            Helpers.dismissProgressDialog();
            imageChanged = true;
            imagePendingUpload = true;
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

    class RetrieveImages extends AsyncTask<Intent, String, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Helpers.showProgressDialog(getActivity(), "Retrieving Documents");
            isImagesRetrievingTaskRunning = true;
        }

        @Override
        protected Void doInBackground(Intent... params) {
            for (int i = 0; i < 3; i++) {
                Bitmap bm = null;
                if (i == 0) {
                    bm = Helpers.getBitmapFromURL(AppGlobals.getDocOne());
                } else if (i == 1) {
                    bm = Helpers.getBitmapFromURL(AppGlobals.getDocTwo());
                } else if (i == 2) {
                    bm = Helpers.getBitmapFromURL(AppGlobals.getDocThree());
                }
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                assert bm != null;
                bm.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                hashMap.put(i, writeImageToExternalStorage(bytes, String.valueOf(i)));
                imagePathsArray.add(hashMap);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            isImagesRetrievingTaskRunning = false;
            Helpers.dismissProgressDialog();
            documentsRetrieved = true;
            showDocumentsAttachmentCustomDialog();
        }

        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
            isImagesRetrievingTaskRunning = false;
        }
    }
}