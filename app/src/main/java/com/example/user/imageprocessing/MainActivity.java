package com.example.user.imageprocessing;

import android.content.Intent;
import android.graphics.Bitmap;

import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;



import java.io.File;
import java.io.IOException;





public class MainActivity extends AppCompatActivity {

    /*
      The User has two choices,either to click a picture or upload a picture from the gallery
      The Function:  void ProcessImage(Bitmap bitmapImage) receives the image as a Bitmap object. Put the Image Processing Codes in here

      Activity 1: Capture Image from camera
      Activity 2: Upload Image from Phone Memory
     */

    /**
     * photoURI --> Stores the URI of the image the user wants to process
     */
    Uri photoURI = null;


    static final int REQUEST_TAKE_PHOTO = 1; //Flag for first Activity 1


    static final int REQUEST_IMAGE_GET = 2; // Flag for Second Activity 2
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    /**
     *
     * @param view On Clicking on a initiate button ,the Image Processing starts
     */
    public void processImage(View view){

        /**
         * TextRead: Store the Text that is recognized by OCR in TextRead
         */
     String TextRead = "Hello World!";
     Bitmap bm = getBitmapImageFrom_photoURI();




     /*
         TODO: Put Image Processing Code here. bm --> stores the BitMap image of the file to process.
         TODO: Store OCR Generated Answer in TextRead
         TODO: Diplay The Processed Image.In this implementation A BitMap image is passed to Display(BitMap localizedImage).

        */





     /*
     Display the Image you want to show the User after processing
     Replace bm with the appropiate BitMap Image
      */
     Display(bm);
     DisplayAnswer(TextRead); //Display the Answer generated after Reading Text Off Image
    }

    /**
     * @param localizedImage The Image you want to display the User after Processing is done
     */
    private void Display(Bitmap localizedImage){

        ImageView localizedPreview = (ImageView)findViewById(R.id.image_thumbnail);
        localizedPreview.setImageBitmap(localizedImage);
    }

    /**
     *
     * @param answer The the text that is recognized by the OCR from the Image
     */
    private void DisplayAnswer(String answer){

        TextView textViewAnswer = findViewById(R.id.text_view_answer);
        textViewAnswer.setText(answer);
    }

    /**
     * @param view The Button View that invokes this function
     */
     public void directTranslate(View view) {  // Called When User Chooses to click pictures from Camera
         dispatchTakePictureIntent();
    }

    /**
     * @param view The Button View that invokes this function
     */
    public void uploadAndTranslate(View view) {  //Called when the User Chooses to upload pictures
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        if (intent.resolveActivity(getPackageManager()) != null) { // Initiate Activity 2: Upload picture from phone
            startActivityForResult(intent, REQUEST_IMAGE_GET);
        }
    }


    /**
     *
     * Initiates Intent to capture Image from Camera.
     * Creates jpeg file named photoFile,stored in the apps cache memory.The camera captured image is stored in this file
     * URI of the file is stored in the global variable photoURI
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile(); // Create a jpeg image file
            } catch (IOException ex) {
                // Error occurred while creating the File
                TextView error_text = findViewById(R.id.error_text);  // If Error in creation,Error is Displayed
                error_text.setText("File could not be created");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                 photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI); //Initiate Activity 1: Taking Image from camera
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }

        }

    }

    /**
     *
     * @return A JPEG File that is stored in cache.
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
       // String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + "TestImage" ;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: mCurrentPhotoPath stores the path to the image captured from camera.Not used till now
        String mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    /**
     *
     * @param requestCode Indicator. 1: Image has been captured by camera(Activity 1 called) . 2: Image has been uploaded from phone(Activity 2 called)
     * @param resultCode Indicates if the Activity was successful or not
     * @param data Data returned from the activity
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) { //If Activity 1: is invoked
              Display();
        }
        if (requestCode == REQUEST_IMAGE_GET && resultCode == RESULT_OK) { //If Activity 2 is invoked
            photoURI = data.getData(); // Get URI of the Image Selected
            Display();   // Display the Image from the URI

        }
    }

    /**
     * Displays a Preview Image before processing to the User
     */
    public void Display() {
        setContentView(R.layout.activity_processing);
        if (photoURI != null) {

            try {
                ImageView imagePreview = findViewById(R.id.image_thumbnail);
                imagePreview.setImageURI(photoURI); //Display the Image that has to be processed
            } catch(Exception e){
                displayErrorMessage("Could not Display File");
            }

        }
    }

    /**
     *
     * @param message The Error message to be displayed
     */
    private void displayErrorMessage(String message){

        TextView errorText = findViewById(R.id.error_text);
        errorText.setText(message);
    }

    /**
     * @return Bitmap bm: The Bitmap of the image pointed by photoURI
     */
    private Bitmap getBitmapImageFrom_photoURI(){

        Bitmap bm = null;
            try {
                bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoURI);
          } catch (IOException e) {
                displayErrorMessage("The File Selected is not a valid image file");
            }
        return bm;
    }



    }





