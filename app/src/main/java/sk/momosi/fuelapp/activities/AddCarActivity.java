package sk.momosi.fuelapp.activities;

import sk.momosi.fuel.R;
import sk.momosi.fuelapp.dbaccess.CarManager;
import sk.momosi.fuelapp.entities.entitiesImpl.Car;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
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
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class AddCarActivity extends Activity implements OnClickListener {

    public static final String TAG = "AddCarActivity";
    private static final String PHOTO = "photo";
    public static final int REQUEST_PICTURE = 123456789;
    public static final int REQUEST_TAKE_PHOTOS = 98765;

    private String mCurrentPhotoPath;
    private Bitmap mCurrentPhoto;

    private EditText mTxtNick;
    private EditText mTxtTypeName;
    private EditText mTxtActualMileage;
    private Spinner mTypeSpinner;
    private Spinner mCurrencySpinner;
    private Spinner mDistanceSpinner;
    private Button mBtnTakePhoto;
    private Button mBtnSelectPhotoFromGallery;
    private Button mBtnAdd;
    private ImageView mImgCarPhoto;

    private CarManager mCarManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car);

        initViews();

        mCarManager = new CarManager(this);
    }
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putParcelable(PHOTO, mCurrentPhoto);
    }
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        if (savedInstanceState.containsKey(PHOTO)) {
            mCurrentPhoto = savedInstanceState.getParcelable(PHOTO);
            ImageView im = (ImageView)findViewById(R.id.img_addcar_car);
            im.setImageBitmap(mCurrentPhoto);
        }
    }

    private void initViews() {
        this.mTxtNick = (EditText) findViewById(R.id.txt_addcar_nick);
        this.mTxtTypeName = (EditText) findViewById(R.id.txt_type_name);
        this.mTxtActualMileage = (EditText) findViewById(R.id.txt_start_mileage);
        this.mBtnSelectPhotoFromGallery = (Button) findViewById(R.id.btn_selectphoto);
        this.mBtnTakePhoto = (Button) findViewById(R.id.btn_takephoto);
        this.mBtnAdd = (Button) findViewById(R.id.btn_add);
        this.mTypeSpinner = (Spinner) findViewById(R.id.spinner_types);
        this.mCurrencySpinner = (Spinner) findViewById(R.id.spinner_currency);
        this.mDistanceSpinner = (Spinner) findViewById(R.id.spinner_distance_units);
        this.mImgCarPhoto = (ImageView) findViewById(R.id.img_addcar_car);

        mTxtActualMileage.setRawInputType(Configuration.KEYBOARD_QWERTY);

        mBtnTakePhoto.setOnClickListener(this);
        mBtnSelectPhotoFromGallery.setOnClickListener(this);

        this.mBtnAdd.setOnClickListener(this);
        mTypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

                                                   public void onItemSelected(AdapterView<?> parent, View view,
                                                                              int pos, long id) {
                                                   }

                                                   @Override
                                                   public void onNothingSelected(AdapterView<?> arg0) {
                                                   }
                                               }

        );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add:
                Editable nick = mTxtNick.getText();
                Editable typeName = mTxtTypeName.getText();
                Editable actualMileage = mTxtActualMileage.getText();

                if (!TextUtils.isEmpty(nick)
                        && !TextUtils.isEmpty(typeName)
                        && !TextUtils.isEmpty(actualMileage)) {
                    // add the car to database
                    Car createdCar = new Car();
                    Long createdMileage = Long.valueOf(0);
                    String msg = null;

                    try {
                        createdMileage = Long.parseLong(actualMileage.toString());
                    } catch (NumberFormatException ex) {
                        Log.d(TAG, getString(R.string.addCarActivity_LOG_badLongNumberFormat));
                        msg = getString(R.string.addCarActivity_wrong_number_format);
                    }
                    if (msg == null) {
                        createdCar.setNick(nick.toString());
                        createdCar.setTypeName(typeName.toString());
                        createdCar.setStartMileage(createdMileage);
                        createdCar.setActualMileage(createdMileage);
                        createdCar.setAvgFuelConsumption(0.0);
                        createdCar.setCarType(Car.CarType.valueOf(mTypeSpinner.getSelectedItem().toString()));
                        createdCar.setCarCurrency(Car.CarCurrency.valueOf(mCurrencySpinner.getSelectedItem().toString()));
                        createdCar.setDistanceUnit(Car.CarDistanceUnit.valueOf(mDistanceSpinner.getSelectedItem().toString()));
                        createdCar.setImage(mCurrentPhoto);

                        Log.d(TAG, getString(R.string.addCarActivity_LOG_wantToAdd) + " "
                                + createdCar.getNick() + "-"
                                + createdCar.getTypeName() + ":" + createdCar.getCarType().toString() + ":"
                                + createdCar.getCarCurrency().toString() + ":" + createdCar.getDistanceUnitString().toString());
                        createdCar = mCarManager.createCar(createdCar);

                        Log.d(TAG, getString(R.string.addCarActivity_LOG_added) + " "
                                + createdCar.getNick() + "-"
                                + createdCar.getTypeName());

                        Intent intent = new Intent();
                        intent.putExtra(ListCarsActivity.EXTRA_ADDED_CAR,
                                createdCar);
                        setResult(RESULT_OK, intent);
                        Toast.makeText(this, getString(R.string.addCarActivity_Toast_added01) + " \""
                                        + createdCar.getNick() + "\" " + getString(R.string.addCarActivity_Toast_added02),
                                Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(this, msg + "- actualMileage", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, getString(R.string.addCarActivity_Toast_emptyFields), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btn_selectphoto:
                selectFromGallerty();
                break;
            case R.id.btn_takephoto:
                takePhoto();
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_PICTURE) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                mCurrentPhotoPath = cursor.getString(columnIndex);
                cursor.close();
                setPic();
            }
            if (requestCode == REQUEST_TAKE_PHOTOS) {
                setPic();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCarManager.close();
    }

    @Override
    public Intent getParentActivityIntent() {
        Intent intent = new Intent(this, ListCarsActivity.class);
        intent.putExtra(ListCarsActivity.FORCE_SHOW_LIST_CARS, true);
        return intent;
    }

    private void selectFromGallerty() {
        Intent i = new Intent(
                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, REQUEST_PICTURE);
    }

    private void takePhoto() {
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

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir, imageFileName + ".jpg");

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = mImgCarPhoto.getWidth();
        int targetH = mImgCarPhoto.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        mCurrentPhoto = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        mImgCarPhoto.setImageBitmap(mCurrentPhoto);
    }
}
