package com.example.smsretriever;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class topFragment extends Fragment {
    EditText etNumber;
    TextView tvMessages;
    Button btnRetrieve,btnSendEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_top, container, false);

        tvMessages = view.findViewById(R.id.tvMessages1);
        btnRetrieve = view.findViewById(R.id.btnRetrieve1);
        etNumber = view.findViewById(R.id.etSMSNum);
        btnSendEmail = view.findViewById(R.id.buttonSendEmail);

        btnRetrieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int permissionCheck = PermissionChecker.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS);

                if(permissionCheck != PermissionChecker.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(),new String []{Manifest.permission.READ_SMS},0);
                    return;
                }
                Uri uri = Uri.parse("content://sms");

                String [] reqCols = new String[]{"date","address","body","type"};

                ContentResolver cr =  getActivity().getContentResolver();
                String filter = "address  LIKE ?";
                String[] filterArgs = {"%"+etNumber.getText().toString()+"%"};


                Cursor cursor = cr.query(uri,reqCols,filter,filterArgs,null);
                String smsBody = "";

                if(cursor.moveToFirst()){
                    do {
                        long dateInMillis = cursor.getLong(0);
                        String date = (String) DateFormat.format("dd MMM yyyy h:mm:ss aa", dateInMillis);
                        String address = cursor.getString(1);
                        String body = cursor.getString(2);
                        String type = cursor.getString(3);
                        address = address.replaceAll("\\D+", "");
                        if (type.equalsIgnoreCase("1")) {
                            type = "Inbox:";
                        } else {
                            type = "sent";
                        }
                        smsBody += type+" "+address+"\n at "+date +"\n\"" +body +"\"\n\n";
                    }while(cursor.moveToNext()) ;

                }
                tvMessages.setText(smsBody);
            }
        });

        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int permissionCheck = PermissionChecker.checkSelfPermission(getActivity(), Manifest.permission.READ_SMS);

                if(permissionCheck != PermissionChecker.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(),new String []{Manifest.permission.READ_SMS},0);
                    return;
                }
                Uri uri = Uri.parse("content://sms");

                String [] reqCols = new String[]{"date","address","body","type"};

                ContentResolver cr =  getActivity().getContentResolver();



                Cursor cursor = cr.query(uri,reqCols,null,null,null);
                String smsBody = "";

                if(cursor.moveToFirst()){
                    do {
                        long dateInMillis = cursor.getLong(0);
                        String date = (String) DateFormat.format("dd MMM yyyy h:mm:ss aa", dateInMillis);
                        String address = cursor.getString(1);
                        String body = cursor.getString(2);
                        String type = cursor.getString(3);
                        address = address.replaceAll("\\D+", "");
                        if (type.equalsIgnoreCase("1")) {
                            type = "Inbox:";
                        } else {
                            type = "sent";
                        }
                        smsBody += type+" "+address+"\n at "+date +"\n\"" +body +"\"\n\n";
                    }while(cursor.moveToNext()) ;

                }
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_EMAIL,
                        new String[]{"cavenlim3@gmail.com"});
                intent.setType("message/rfc822");

                intent.putExtra(Intent.EXTRA_SUBJECT, "Email from sms receiver");
                intent.putExtra(Intent.EXTRA_TEXT, smsBody);
                startActivity(Intent.createChooser(intent, "Choose an Email client :"));
            }
        });


        return view;
    } @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the read SMS
                    //  as if the btnRetrieve is clicked
                    btnRetrieve.performClick();

                } else {
                    // permission denied... notify user
                    Toast.makeText(getActivity(), "Permission not granted",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}