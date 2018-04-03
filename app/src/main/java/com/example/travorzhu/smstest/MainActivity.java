package com.example.travorzhu.smstest;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsMessage;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

import static android.provider.Telephony.Sms.Intents.getMessagesFromIntent;

public class MainActivity extends AppCompatActivity {

    private SMSRecivers recivers;
    private String urls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recivers=new SMSRecivers();
        IntentFilter filter=new IntentFilter();
        filter.addAction("BroadcastSMS");
        registerReceiver(recivers,filter);

        final EditText editText=findViewById(R.id.editText);
        TextView textView=findViewById(R.id.text);

        Button button=findViewById(R.id.button);
        Button button1=findViewById(R.id.button1);

        SharedPreferences preferences=getSharedPreferences("SMS",MODE_PRIVATE);
        String urls=preferences.getString("Urls",null);
        if(urls!=null)
            editText.setText(urls);
        View.OnClickListener clickListener= new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final SharedPreferences.Editor editor=getSharedPreferences("SMS",MODE_PRIVATE).edit();
                System.out.println("保存地址:"+editText.getText().toString());
                editor.putString("Urls",editText.getText().toString());
                editor.apply();
            }
        };
        button.setOnClickListener(clickListener);

        View.OnClickListener clickListener_load=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences=getSharedPreferences("SMS",MODE_PRIVATE);

                String urls=preferences.getString("Urls",null);

                System.out.println("读取地址:"+urls);

                editText.setText(urls);
            }
        };

        button1.setOnClickListener(clickListener_load);

        boolean flag1 = true, flag2 = true, flag3 = true;

        while (flag1 || flag2 || flag3) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECEIVE_SMS}, 1);
                System.out.println("短信接收权限错误");
            } else {
                System.out.println("短信接收权限正常");
                flag1 = false;
            }

            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET}, 1);
                System.out.println("网络权限错误");
            } else {
                System.out.println("网络权限正常");
                flag2 = false;
            }

            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.INTERNET}, 1);
                System.out.println("联系人权限错误");
            } else {
                System.out.println("联系人权限正常");
                flag3 = false;
            }
        }


    }

    String getContactName(Context context, String phoneNumber) {
        ContentResolver contentResolver = context.getContentResolver();
        String contactName = "未知联系人";

        Cursor pCur = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",
                new String[]{phoneNumber}, null);
        if (pCur != null && pCur.moveToFirst()) {
            contactName = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            pCur.close();
        }
        return contactName;
    }

    class SMSRecivers extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            SharedPreferences preferences=getSharedPreferences("SMS",MODE_PRIVATE);
            String urls = preferences.getString("Urls",null);
            System.out.println("读取到的地址:"+urls);
            if (urls == null || Objects.equals(urls, "send地址")) {
                Toast.makeText(context,"请先设置地址",Toast.LENGTH_LONG).show();
            }

            SmsMessage smsMessages[]=getMessagesFromIntent(intent);
            for (SmsMessage sms:smsMessages
                 ) {
                String phonenumber=sms.getDisplayOriginatingAddress();
                String text=sms.getDisplayMessageBody();
                String contactName = getContactName(context, phonenumber);
                String body = text + "<br/> \n    From:" + contactName + "(" + phonenumber + ")";
                String Title="你收到一条新短信";

                postTread postTread=new postTread(Title,body,urls);
                postTread.start();
            }
//            GetTread getTread=new GetTread();
//            getTread.setMessage(smsMessages[0].getMessageBody());
//            getTread.start();
        }
    }

    class GetTread extends Thread{

        String message;

        public void setMessage(String message) {
            this.message = message;
        }

        public void run(){
            String urlS=urls+message;
            URL url= null;
            try {
                url = new URL(urlS);
                HttpURLConnection connection= (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                if(connection.getResponseCode()==200){
                    InputStream is = connection.getInputStream(); //获取输入流
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader bufferReader = new BufferedReader(isr);
                    String inputLine  = "";
                    StringBuilder resultData = new StringBuilder();
                    while((inputLine = bufferReader.readLine()) != null){
                        resultData.append(inputLine).append("\n");
                    }
                    System.out.println("get方法取回内容："+resultData);

                    System.out.print("Success");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



}
