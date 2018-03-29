package com.example.travorzhu.smstest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import static android.provider.Telephony.Sms.Intents.getMessagesFromIntent;

/**
 * Created by TravorZhu on 2018/3/27.
 */

public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SmsMessage smsMessages[] = getMessagesFromIntent(intent);
        for (SmsMessage message:
                smsMessages) {
            String s = message.getMessageBody();
            System.out.println("get a new sms:"+s);
        }
        Intent intent1=new Intent("BroadcastSMS");
        Bundle bundle=intent.getExtras();
        intent1.putExtras(bundle);
        context.sendBroadcast(intent1);
    }
}
