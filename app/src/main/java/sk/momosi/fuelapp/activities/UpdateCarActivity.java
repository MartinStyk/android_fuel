package sk.momosi.fuelapp.activities;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import sk.momosi.fuel.R;
import sk.momosi.fuelapp.dbaccess.CarManager;
import sk.momosi.fuelapp.entities.entitiesImpl.Car;
import sk.momosi.fuelapp.entities.entitiesImpl.Car.CarType;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class UpdateCarActivity extends Activity {

    public static final String EXTRA_CAR = "extra_car_to_add_fillup";
    public static final String TAG = "UpdateCarActivity";

    private static final String PHOTO = "photo";
    public static final int REQUEST_PICTURE = 123456789;
    public static final int REQUEST_TAKE_PHOTOS = 98765;
    public static final int REQUEST_PIC_CROP = 1111;

    private String mCurrentPhotoPath;
    private Bitmap mCurrentPhotoLarge;

    private Car mCar;
    private CarManager mCarManager;

    private EditText mNick;
    private EditText mManufacturer;
    private EditText mMileage;
    private TextView mMileageUnit;
    private Spinner mTypeSpinner;
    private ImageView mImgCarPhoto;

    private CarType mCarType;
    private String mNickToUpdate;
    private String mManufacturerToUpdate;
    private Long mMileageToUpdate;
    private Long mActualMileageToUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_update);

        Intent intent = getIntent();

        if (intent != null) {
            mCar = (Car) intent
                    .getSerializableExtra(EXTRA_CAR);
            if (mCar == null) {
                setResult(RESULT_CANCELED);
                finish();
            }
        }

        mCarManager = new CarManager(this);
        initViews();

        if (savedInstanceState == null) {
            populateFields();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mCurrentPhotoLarge != null) {
            outState.putParcelable(PHOTO, mCurrentPhotoLarge);
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey(PHOTO)) {
            mCurrentPhotoLarge = savedInstanceState.getParcelable(PHOTO);
            ImageView im = (ImageView) findViewById(R.id.img_addcar_car);
            im.setImageBitmap(mCurrentPhotoLarge);
        }
    }

    private void initViews() {
        mNick = (EditText) findViewById(R.id.txt_carupdate_nick);
        mManufacturer = (EditText) findViewById(R.id.txt_carupdate_manufacturer);
        mMileage = (EditText) findViewById(R.id.txt_carupdate_mileage);
        mMileageUnit = (TextView) findViewById(R.id.txt_carupdate_unit);
        mTypeSpinner = (Spinner) findViewById(R.id.spinner_carupdate_types);
        mImgCarPhoto = (ImageView) findViewById(R.id.img_addcar_car);
    }

    private void populateFields() {
        mNick.setText(mCar.getNick());
        mManufacturer.setText(mCar.getTypeName());
        mMileage.setText(mCar.getStartMileage().toString());
        mTypeSpinner.setSelection(mCar.getCarType().ordinal());
        mMileageUnit.setText(mCar.getDistanceUnitString());
        if (mCar.getImage() != null) {
            mImgCarPhoto.setImageBitmap(mCar.getImage());
            mCurrentPhotoLarge = mCar.getImage();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCarManager != null) {
            mCarManager.close();
        }
    }

    @Override
    public Intent getParentActivityIntent() {
        Intent intent = new Intent(this, ListCarsActivity.class);
        intent.putExtra(ListCarsActivity.FORCE_SHOW_LIST_CARS, true);
        return intent;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_PICTURE) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                mCurrentPhotoPath = cursor.getString(columnIndex);
                cursor.close();
                performCrop();
            }
            if (requestCode == REQUEST_TAKE_PHOTOS) {
                performCrop();
            }
            if (requestCode == REQUEST_PIC_CROP) {
                setPictureLarge();
            }
        }
    }

    public void onCarImageBtnClick(View v) {
        final int TAKE_PHOTO = 0;
        final int SELECT_PHOTO = 1;
        final int DELETE_PHOTO = 2;

        final CharSequence[] opsWithDelete = {getResources().getString(R.string.add_car_activity_take_photo), getResources().getString(R.string.add_car_activity_select_photo),getResources().getString(R.string.add_car_activity_delete_photo)};
        final CharSequence[] opsWithOutDelete = {getResources().getString(R.string.add_car_activity_take_photo), getResources().getString(R.string.add_car_activity_select_photo)};

        final CharSequence[] opsChars = mCurrentPhotoLarge == null ? opsWithOutDelete : opsWithDelete;

        AlertDialog.Builder getImageFrom = new AlertDialog.Builder(this);
        getImageFrom.setTitle("Select:");

        getImageFrom.setItems(opsChars, new android.content.DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == TAKE_PHOTO) {
                    takePhoto();
                } else if (which == SELECT_PHOTO) {
                    selectFromGallerty();
                } else if (which == DELETE_PHOTO) {
                    deletePhoto();
                }
                dialog.dismiss();
            }
        });
        getImageFrom.create().show();
    }

    public void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTOS);
            }

        }
    }

    public void selectFromGallerty() {

        Intent i = new Intent(
                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, REQUEST_PICTURE);
    }

    public void deletePhoto() {
        mCurrentPhotoLarge = null;
        mImgCarPhoto.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_camera));
    }

    public void onUpdateBtnClick(View v) {
        Editable nick = mNick.getText();
        Editable manufacturer = mManufacturer.getText();
        Editable mileage = mMileage.getText();

        Log.d(TAG, getString(R.string.updateCarActivity_LOG_updateBtnClicked));
        if (TextUtils.isEmpty(nick)
                || TextUtils.isEmpty(manufacturer)
                || TextUtils.isEmpty(mileage)) {
            Toast.makeText(this, R.string.updateCarActivity_emptyFields, Toast.LENGTH_LONG).show();
        } else {
            String msg = null;
            Long createdMileage = Long.valueOf(0);

            try {
                createdMileage = Long.parseLong(mileage.toString());
            } catch (NumberFormatException ex) {
                Log.d(TAG, getString(R.string.updateCarActivity_LOG_badLongNumberFormat));
                msg = getString(R.string.updateCarActivity_wrong_number_format);
            }
            if (msg != null) {
                Toast.makeText(this, msg + " - " + getString(R.string.car_update_mileage), Toast.LENGTH_LONG).show();
            } else {
//                if (nick.toString().equals(mCar.getNick())
//                        && manufacturer.toString().equals(mCar.getTypeName())
//                        && createdMileage.equals(mCar.getStartMileage())
//                        && Car.CarType.valueOf(mTypeSpinner.getSelectedItem().toString()).equals(mCar.getCarType())
//                        ) {
//                    if ((mCurrentPhoto == null && mCar.getImage() == null) || (mCurrentPhoto != null && mCurrentPhoto.equals(mCar.getImage())))
//                    {
//                        //ziadna zmena = konec
//                        Toast.makeText(this, getString(R.string.updateCarActivity_Toast_carNoUpdate), Toast.LENGTH_LONG).show();
//                        setResult(RESULT_OK);
//                        finish();
//                    }
                if (!createdMileage.equals(mCar.getStartMileage())) {
                    //varovanie pri zmene startMielage
                    mNickToUpdate = nick.toString();
                    mManufacturerToUpdate = manufacturer.toString();
                    mMileageToUpdate = createdMileage;
                    mCarType = Car.CarType.valueOf(mTypeSpinner.getSelectedItem().toString());

                    showUpdateDialogConfirmation(createdMileage);
                } else {
                    //pri zmene nazvov len zmena nazvov
                    mCar.setNick(nick.toString());
                    mCar.setTypeName(manufacturer.toString());
                    mCar.setCarType(Car.CarType.valueOf(mTypeSpinner.getSelectedItem().toString()));
                    mCar.setImage(mCurrentPhotoLarge);

                    mCarManager.updateCar(mCar);
                    Toast.makeText(this, getString(R.string.updateCarActivity_Toast_carUpdatedSuccesfully01) + " \""
                                    + mCar.getNick() + "\" " + getString(R.string.updateCarActivity_Toast_carUpdatedSuccesfully02),
                            Toast.LENGTH_LONG).show();

                    setResult(RESULT_OK);
                    finish();
                }
            }
        }
    }

    public void onDeleteBtnClick(View v) {
        Log.d(TAG, getString(R.string.updateCarActivity_LOG_deleteBtnClicked));
        showDeleteDialogConfirmation();
    }

    private void showUpdateDialogConfirmation(Long mileage) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        mActualMileageToUpdate = mileage - mCar.getStartMileage() + mCar.getActualMileage();
        alertDialogBuilder.setTitle(getString(R.string.updateCarActivity_DialogUpdate_title));
        alertDialogBuilder.setMessage(getString(R.string.updateCarActivity_DialogUpdate_msg01) + " "    //you want change from
                + mCar.getStartMileage().toString() + mCar.getDistanceUnitString() + " "                //VALUE
                + getString(R.string.updateCarActivity_DialogUpdate_msg02) + " " + mileage.toString()    //to VALUE
                + mCar.getDistanceUnitString()
                + getString(R.string.updateCarActivity_DialogUpdate_msg03) + " "                        //this also change from
                + mCar.getActualMileage().toString() + mCar.getDistanceUnitString() + " "                //VALUE
                + getString(R.string.updateCarActivity_DialogUpdate_msg04) + " "                        //to
                + mActualMileageToUpdate.toString() + mCar.getDistanceUnitString()                        //VALUE
                + getString(R.string.updateCarActivity_DialogUpdate_msg05));                            //are you sure?

        // set positive button YES message
        alertDialogBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        Log.d(TAG, getString(R.string.updateCarActivity_LOG_dialogConfirm));
                        Dialog dialog = (Dialog) dialogInterface;
                        Context context = dialog.getContext();

                        mCar.setNick(mNickToUpdate);
                        mCar.setTypeName(mManufacturerToUpdate);
                        mCar.setStartMileage(mMileageToUpdate);
                        mCar.setActualMileage(mActualMileageToUpdate);
                        mCar.setCarType(mCarType);

                        mCarManager.updateCar(mCar);
                        Toast.makeText(context, getString(R.string.updateCarActivity_Toast_carUpdatedSuccesfully01) + " \""
                                        + mCar.getNick() + "\" " + getString(R.string.updateCarActivity_Toast_carUpdatedSuccesfully02),
                                Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                        setResult(RESULT_OK);
                        finish();
                    }
                });

        // set neutral button OK
        alertDialogBuilder.setNeutralButton(android.R.string.no,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Dismiss the dialog
                        Log.d(TAG, getString(R.string.updateCarActivity_LOG_dialogDecline));
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        // show alert
        alertDialog.show();
    }

    private void showDeleteDialogConfirmation() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle(getString(R.string.updateCarActivity_DialogDelete_title));
        alertDialogBuilder.setMessage(getString(R.string.updateCarActivity_DialogDelete_msg01) + " "
                + mCar.getNick().toUpperCase(Locale.getDefault()) + " - " + mCar.getTypeName() + "?");

        // set positive button YES message
        alertDialogBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        // delete the company and refresh the list
                        Log.d(TAG, getString(R.string.updateCarActivity_LOG_dialogConfirm));
                        Dialog dialog = (Dialog) dialogInterface;
                        Context context = dialog.getContext();

                        CarManager mCarManager = new CarManager(getBaseContext());
                        if (mCarManager != null) {
                            mCarManager.deleteCar(mCar);
                        }

                        Toast.makeText(context, getString(R.string.updateCarActivity_Toast_carDeleted01) + " \""
                                + mCar.getNick() + "\" " + getString(R.string.updateCarActivity_Toast_carDeleted02), Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                        setResult(RESULT_OK);
                        finish();
                    }
                });

        // set neutral button OK
        alertDialogBuilder.setNeutralButton(android.R.string.no,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Dismiss the dialog
                        Log.d(TAG, getString(R.string.updateCarActivity_LOG_dialogDecline));
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        // show alert
        alertDialog.show();
    }

    private void setPictureLarge() {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = 1;
        bmOptions.inPurgeable = true;
        mCurrentPhotoLarge = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImgCarPhoto.setImageBitmap(mCurrentPhotoLarge);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        storageDir = new File(storageDir, getResources().getString(R.string.app_name));
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        File image = new File(storageDir, imageFileName + ".jpg");

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void performCrop() {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");

        //indicate image type and Uri
        cropIntent.setDataAndType(Uri.fromFile(new File(mCurrentPhotoPath)), "image/*");

        cropIntent.putExtra("crop", "true");
        //indicate aspect of desired crop
        cropIntent.putExtra("aspectX", 16);
        cropIntent.putExtra("aspectY", 9);
        //indicate output X and Y
        cropIntent.putExtra("scaleUpIfNeeded", true);
        cropIntent.putExtra("outputX", 800);
        cropIntent.putExtra("outputY", 450);
        //retrieve data on return
        cropIntent.putExtra("scale", true);
        cropIntent.putExtra("return-data", false);
        //start the activity - we handle returning in onActivityResult
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File
        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(photoFile));

            startActivityForResult(cropIntent, REQUEST_PIC_CROP);
        }

    }
}
