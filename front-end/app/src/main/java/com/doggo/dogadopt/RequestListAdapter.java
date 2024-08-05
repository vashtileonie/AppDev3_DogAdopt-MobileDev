package com.doggo.dogadopt;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.doggo.dogadopt.activity.resolvereqActivity;
import com.doggo.dogadopt.activity.startupActivity;
import com.doggo.dogadopt.activity.updateActivity;
import com.doggo.dogadopt.activity.viewActivity;
import com.doggo.dogadopt.model.Account;
import com.doggo.dogadopt.model.Dog;
import com.doggo.dogadopt.model.Request;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RequestListAdapter extends BaseAdapter {

    Context context;
    private Request[] requests;
    private Dog[] dogs;
    private int LAUNCH_SECOND_ACTIVITY = 1;
    private Account account;

    public RequestListAdapter(Context context, Request[] requests, Dog[] dogs, Account account) {
        this.context = context;
        this.requests = requests;
        this.dogs = dogs;
        this.account = account;
    }

    @Override
    public int getCount() {
        return requests.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NotNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        final View result;

        if (convertView == null){

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.list_request_layout, parent, false);
            viewHolder.dNameTxt = (TextView) convertView.findViewById(R.id.dNameTxt);
            viewHolder.rdogIDTxt = (TextView) convertView.findViewById(R.id.rdogIDTxt);
            viewHolder.reqStatusTxt = (TextView) convertView.findViewById(R.id.reqStatusTxt);
            viewHolder.resolveRequest = (Button) convertView.findViewById(R.id.resolveRequest);

            result = convertView;

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        if(account.getRole().equals("USER")){
            viewHolder.resolveRequest.setVisibility(View.GONE);
        }


        for (Dog dog : dogs){

            String name ="";
            if (dog.getId().equals(requests[position].getDogId())){
                name = "For "+dog.getName().replace("\"", "");
                viewHolder.dNameTxt.setText(name);
            }

        };

        viewHolder.rdogIDTxt.setText("Dog ID: " +Integer.toString(Math.toIntExact(requests[position].getDogId())));
        if (requests[position].getReqStatus().equals("FOR REVIEW")){
            viewHolder.reqStatusTxt.setTextColor(Color.parseColor("#6D737A"));
        }else {
            viewHolder.reqStatusTxt.setTextColor(Color.parseColor("#539D7A"));
        }
        viewHolder.reqStatusTxt.setText(requests[position].getReqStatus().replace("\"", ""));

        viewHolder.resolveRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context.getApplicationContext(), resolvereqActivity.class);
                i.putExtra("reqId",requests[position].getReqId());
                ((Activity) context).startActivityForResult(i,LAUNCH_SECOND_ACTIVITY);
                //context.startActivity(i);
            }
        });

        return convertView;
    }

    private class ViewHolder {

        TextView dNameTxt;
        TextView rdogIDTxt;
        TextView reqStatusTxt;
        Button resolveRequest;

    }


}
