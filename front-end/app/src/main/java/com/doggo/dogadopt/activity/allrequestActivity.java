package com.doggo.dogadopt.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.doggo.dogadopt.LoadingDialog;
import com.doggo.dogadopt.R;
import com.doggo.dogadopt.RequestListAdapter;
import com.doggo.dogadopt.model.Account;
import com.doggo.dogadopt.model.Dog;
import com.doggo.dogadopt.model.Request;
import com.doggo.dogadopt.retrofit.CallBack;
import com.doggo.dogadopt.retrofit.QueryProcessor;

import java.util.ArrayList;
import java.util.List;

public class allrequestActivity extends AppCompatActivity{

    ListView lView;
    RequestListAdapter rListAdapter;
    List<Request> requestList;
    List<Dog> dogList;
    Account account;
    QueryProcessor processor = new QueryProcessor();
    LoadingDialog progress = new LoadingDialog(allrequestActivity.this);
    int LAUNCH_SECOND_ACTIVITY = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_SECOND_ACTIVITY){
            if(resultCode == Activity.RESULT_OK){
                progress.startLoadingAnimation("Reloading data...");
                generateList();
            }
            if (resultCode == Activity.RESULT_CANCELED){
                //reloadList("Reloading data...", false);
            }
        }


    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_request);
        Intent intent = getIntent();
        account = (Account) intent.getSerializableExtra("account");
        progress.startLoadingAnimation("Initializing data...");
        generateList();
    }

    private void generateList(){
        processor.RequestReadAll();
        processor.setCbs(new CallBack() {
            @Override
            public void returnResult(Object obj) {
                requestList = (List<Request>) obj;
            }
        });
        processor = new QueryProcessor();
        processor.DogReadAll();
        processor.setCbs(new CallBack() {
            @Override
            public void returnResult(Object obj) {
                dogList = (List<Dog>) obj;
                lView = findViewById(R.id.requestList);

                List<Request> filteredRequests = new ArrayList<>();

                if(account.getRole().equals("USER")){
                    for (Request utos: requestList){
                        if (utos.getUserId().equals(account.getMyId())){
                            filteredRequests.add(utos);
                        }
                    }
                }
                else {
                    filteredRequests = requestList;
                }

                rListAdapter = new RequestListAdapter(allrequestActivity.this, filteredRequests.toArray(new Request[0]), dogList.toArray(new Dog[0]), account);
                lView.setAdapter(rListAdapter);
                progress.dismissAnimation();

                if (filteredRequests.isEmpty()){
                    new AlertDialog.Builder(allrequestActivity.this)
                            .setTitle("Empty Request List")
                            .setMessage("There are no requests made. Please create one first.")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .show();
                }

            }
        });
    }

}
