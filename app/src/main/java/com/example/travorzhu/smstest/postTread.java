package com.example.travorzhu.smstest;

import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by TravorZhu on 2018/3/29.
 */

public class postTread extends Thread{
    String title;
    String text;
    String urls;

    public postTread(String title, String text, String urls) {
        this.title = title;
        this.text = text;
        this.urls=urls;
    }

    public void run() {
        System.out.println("开始转发");
        try {
            String parm="text=\""+title+"\"&desp=\" "+text+" \"";
            URL url=new URL(urls);
            HttpURLConnection connection= (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            PrintWriter writer=new PrintWriter(connection.getOutputStream());
            writer.print(parm);
            writer.flush();
            if(connection.getResponseCode()==200){
                System.out.println("短信转发成功");
            }
            else {
                System.out.println("短信转发失败");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
