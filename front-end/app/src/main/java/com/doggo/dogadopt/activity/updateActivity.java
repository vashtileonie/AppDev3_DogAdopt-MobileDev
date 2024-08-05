package com.doggo.dogadopt.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.doggo.dogadopt.LoadingDialog;
import com.doggo.dogadopt.model.Dog;
import com.doggo.dogadopt.retrofit.CallBack;
import com.doggo.dogadopt.retrofit.QueryProcessor;
import com.doggo.dogadopt.R;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class updateActivity extends AppCompatActivity {

    private ImageView dogPicPreview;
    private Button dogChoosePhoto;
    private EditText DName;
    private EditText DBreed;
    private EditText DAge;
    private Spinner DStatus;
    private Spinner DGender;
    private EditText dogDOAEditText;
    private TextInputEditText DogPersonal;
    private Button updateDog_button;
    private Button deleteDog_button;
    private Calendar calendar;
    private Long DogID;
    int SELECT_PICTURE = 200;
    Dog aso = new Dog();
    QueryProcessor processor = new QueryProcessor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        LoadingDialog progress = new LoadingDialog(updateActivity.this);
        progress.startLoadingAnimation("Populating fields...");
        Intent intent = getIntent();
        DogID = intent.getLongExtra("dogID",0);

        dogDOAEditText = findViewById(R.id.asoDOA);
        DName = findViewById(R.id.asoName);
        DGender = findViewById(R.id.asoGender);
        dogChoosePhoto = (Button) findViewById(R.id.asoChoosePhoto);
        dogPicPreview = findViewById(R.id.asoPhoto);
        DAge = findViewById(R.id.asoAge);
        DBreed = findViewById(R.id.asoBreed);
        DogPersonal = findViewById(R.id.asoPersonality);
        updateDog_button = (Button) findViewById(R.id.updateAso_button);
        deleteDog_button = (Button) findViewById(R.id.deleteAso_button);
        DStatus = findViewById(R.id.asoStatus);
        calendar = Calendar.getInstance();

        dogChoosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("HELLO", "Register Button Clicked");
                imageChooser();
            }
        });
        updateDog_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Bitmap bitmap = ((BitmapDrawable) dogPicPreview.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageInByte = baos.toByteArray();

                processor.DogUpdate(Math.toIntExact(DogID),imageInByte,DName.getText().toString(),DBreed.getText().toString(),DAge.getText().toString(),dogDOAEditText.getText().toString(),DogPersonal.getText().toString(),DStatus.getSelectedItem().toString(),DGender.getSelectedItem().toString());
                processor.setCbs(new CallBack() {
                    @Override
                    public void returnResult(Object obj) {
                        boolean isSuccessful = (boolean) obj;

                        if (isSuccessful){
                            Toast.makeText(updateActivity.this,"Update successful.", Toast.LENGTH_SHORT).show();
                            Intent returnIntent = new Intent();
                            setResult(Activity.RESULT_OK, returnIntent);
                            finish();
                        } else{
                            Toast.makeText(updateActivity.this,"Update not successful. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        deleteDog_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the deleteDog method to delete the dog

                new AlertDialog.Builder(updateActivity.this)
                        .setTitle("Delete Dog")
                        .setMessage("Do you want to delete this entry?")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteDog(DogID);
                            }
                        }
                        ).setNegativeButton("Cancel", null)
                        .show();

            }
        });

        initializeParameters(DogID);
        // Optionally, set an initial date in the EditText
        updateDateInView();
        progress.dismissAnimation();
    }

    private void deleteDog(Long dogID) {
        processor.DogRead(Math.toIntExact(dogID));
        processor.setCbs(new CallBack() {
            @Override
            public void returnResult(Object obj) {
                if (obj instanceof Dog) {
                    Dog dog = (Dog) obj;
                    if (dog != null) {
                        // Dog with the given ID exists, proceed with deletion
                        processor.deleteDog(dog.getId()); // Assuming you need to pass the ID of the dog to delete
                        Toast.makeText(updateActivity.this, "Dog deleted successfully.", Toast.LENGTH_SHORT).show();
                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish(); // Optionally, close the activity after successful deletion
                    } else {
                        // Dog with the given ID doesn't exist
                        Toast.makeText(updateActivity.this, "Dog does not exist.", Toast.LENGTH_SHORT).show();
                    }
                } else if (obj instanceof Boolean) {
                    boolean isSuccess = (Boolean) obj;
                    if (!isSuccess) {
                        // Deletion failed
                        Toast.makeText(updateActivity.this, "Failed to delete dog.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        });
    }



    public void showDatePickerDialog(View view) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDay) {
                        calendar.set(Calendar.YEAR, selectedYear);
                        calendar.set(Calendar.MONTH, selectedMonth);
                        calendar.set(Calendar.DAY_OF_MONTH, selectedDay);

                        updateDateInView();
                    }
                },
                year, month, dayOfMonth);

        datePickerDialog.show();
    }

    private void updateDateInView() {
        String myFormat = "yyyy-MM-dd"; // Choose the format you desire
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dogDOAEditText.setText(sdf.format(calendar.getTime()));
    }

    private void imageChooser(){

        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            if (requestCode == SELECT_PICTURE){
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri){
                    dogPicPreview.setImageURI(selectedImageUri);
                }
            }
        }

    }
    private void initializeParameters(Long id) {

        processor.DogRead(Math.toIntExact(id));

        processor.setCbs(new CallBack() {
            @Override
            public void returnResult(Object obj) {

                Dog aso = (Dog) obj;
                if (aso != null) {
                    Log.i("Information", "umabot siya dito");

                    DName.setText(aso.getName().replace("\"", ""));
                    DBreed.setText(aso.getBreed().replace("\"", ""));
                    DAge.setText(((String.valueOf(aso.getAge()))));
                    dogDOAEditText.setText(aso.getDoa().toString());
                    DogPersonal.setText(aso.getPersonality().replace("\"", ""));
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(updateActivity.this, R.array.dogGenderSpin, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    DGender.setAdapter(adapter);
                    if (aso.getGender() != null) {
                        Log.i("Gender", "The gender is " + aso.getGender());
                        int spinnerPosition = adapter.getPosition(aso.getGender().replace("\"", ""));
                        DGender.setSelection(spinnerPosition);
                    }
                    ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(updateActivity.this, R.array.dogStatusSpin, android.R.layout.simple_spinner_item);
                    adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    DStatus.setAdapter(adapter2);
                    if (aso.getStatus() != null) {
                        Log.i("Status", "The status is " + aso.getStatus());
                        int spinnerPosition = adapter2.getPosition(aso.getStatus().replace("\"", ""));
                        DStatus.setSelection(spinnerPosition);
                    }
                    dogPicPreview.setImageBitmap(
                            Bitmap.createScaledBitmap(

                                    BitmapFactory.decodeByteArray(aso.getPhoto(), 0, aso.getPhoto().length),
                                    dogPicPreview.getWidth(),
                                    dogPicPreview.getHeight(),
                                    false
                            )
                    );

                }

            }

        });

    }
}



