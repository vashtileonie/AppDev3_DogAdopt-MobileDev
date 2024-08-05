package com.doggo.dogadopt.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
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
import com.doggo.dogadopt.model.Request;
import com.doggo.dogadopt.retrofit.CallBack;
import com.doggo.dogadopt.retrofit.QueryProcessor;
import com.doggo.dogadopt.R;
import com.google.android.material.textfield.TextInputEditText;

public class resolvereqActivity extends AppCompatActivity {
    private Long reqId;
    private EditText reqIDtxt;
    private EditText dogIDtxt;
    private EditText reqNametxt;
    private EditText reqNotxt;
    private EditText reqMestxt;
    private EditText reqStatustxt;
    private Button resolveBtn;
    private Request req;

    QueryProcessor processor = new QueryProcessor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resolve_request);

        Intent intent = getIntent();
        reqId = intent.getLongExtra("reqId",0);

        reqIDtxt = findViewById(R.id.reqID);
        dogIDtxt = findViewById(R.id.dogID);
        reqNametxt = findViewById(R.id.reqBy);
        reqNotxt = findViewById(R.id.reqCon);
        reqMestxt = findViewById(R.id.messageReq);
        reqStatustxt = findViewById(R.id.reqStatusTxt);
        resolveBtn = (Button) findViewById(R.id.resolveBtn);

        resolveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reqStatustxt.setText("RESOLVED");

                processor.RequestUpdate(req.getReqId(),req.getDogId(),req.getUserId(),req.getReqContact(),req.getReqMessage(),req.getReqName(),reqStatustxt.getText().toString());

                processor.setCbs(new CallBack() {
                    @Override
                    public void returnResult(Object obj) {
                        LoadingDialog progress = new LoadingDialog(resolvereqActivity.this);
                        progress.startLoadingAnimation();
                        boolean isSuccessful = (boolean) obj;
                        progress.dismissAnimation();
                        if (isSuccessful){
                            Toast.makeText(resolvereqActivity.this,"Update successful.", Toast.LENGTH_SHORT).show();
                            Intent returnIntent = new Intent();
                            setResult(Activity.RESULT_OK, returnIntent);
                            finish();
                        } else{
                            Toast.makeText(resolvereqActivity.this,"Update not successful. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        initializeFields(reqId);
    }

    private void initializeFields(Long id){
        processor.RequestRead(Math.toIntExact(reqId));

        processor.setCbs(new CallBack() {
            @Override
            public void returnResult(Object obj) {
                LoadingDialog progress = new LoadingDialog(resolvereqActivity.this);
                progress.startLoadingAnimation();
                req = (Request) obj;

                if (req != null){
                    reqIDtxt.setText(Integer.toString(Math.toIntExact(req.getReqId())));
                    dogIDtxt.setText(Integer.toString(Math.toIntExact(req.getDogId())));
                    reqNametxt.setText(req.getReqName());
                    reqNotxt.setText(req.getReqContact());
                    reqMestxt.setText(req.getReqMessage());
                    reqStatustxt.setText(req.getReqStatus());
                }
                if (reqStatustxt.getText().toString().equals("RESOLVED")) {
                    resolveBtn.setEnabled(false);
                }
                progress.dismissAnimation();
            }
        });

    }

}
