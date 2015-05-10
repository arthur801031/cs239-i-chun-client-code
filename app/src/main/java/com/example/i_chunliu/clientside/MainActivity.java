package com.example.i_chunliu.clientside;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;


public class MainActivity extends Activity {

    private EditText serverIp;
    private EditText clientMessage;
    private TextView serverMessage;
    private Button sendButton;
    private Button connectPhones;

    private String serverIpAddress = "";
    String temp = "";
    private boolean connected = false;

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serverIp = (EditText) findViewById(R.id.server_ip);
        connectPhones = (Button) findViewById(R.id.connect_phones);
        connectPhones.setOnClickListener(connectListener);
        clientMessage = (EditText) findViewById(R.id.clientMessage);
        sendButton = (Button) findViewById(R.id.sendButton);
        serverMessage = (TextView) findViewById(R.id.serverMessage);
    }



    //Client codes
    private View.OnClickListener connectListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (!connected) {
                serverIpAddress = serverIp.getText().toString();
                if (!serverIpAddress.equals("")) {
                    Thread cThread = new Thread(new ClientThread());
                    cThread.start();
                }
            }
        }
    };

    public class ClientThread implements Runnable {

        public void run() {
            try {
                InetAddress serverAddr = InetAddress.getByName(serverIpAddress);
                Log.d("ClientActivity", "C: Connecting...");
                Socket socket = new Socket(serverAddr, 8080);
                connected = true;
                while (connected) {
                    try {
                        Log.d("ClientActivity", "C: Sending command.");
                        final PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket
                                .getOutputStream())), true);
                        // WHERE YOU ISSUE THE COMMANDS
                        sendButton.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                out.println(clientMessage.getText().toString());
                                Log.d("ClientActivity", "C: Sent.");

                            }
                        });


                        //Reading input from the server - !!!!!!!!!!server codes!!!!!!!!!!!!!!!!!!!
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String line = null;
                        while ((line = in.readLine()) != null) {
                            Log.d("ServerActivity", line);
                            final String finalLine = line;
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    serverMessage.getText().toString();
                                    temp += finalLine + '\n';
                                    serverMessage.setText(temp);
                                    // DO WHATEVER YOU WANT TO THE FRONT END
                                    // THIS IS WHERE YOU CAN BE CREATIVE
                                }
                            });
                        }
                        //!!!!!!!!!!!!!!!!!!server codes!!!!!!!!!!!!!!!!!!!!!!!
                    } catch (Exception e) {
                        Log.e("ClientActivity", "S: Error", e);
                    }
                }
                socket.close();
                Log.d("ClientActivity", "C: Closed.");
            } catch (Exception e) {
                Log.e("ClientActivity", "C: Error", e);
                connected = false;
            }
        }
    }
}