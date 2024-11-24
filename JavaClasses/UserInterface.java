package com.example.airsimapp;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class UserInterface extends AppCompatActivity {
    private static final String TAG = "WebSocket";
    private Button start;
    private TextView output;
    private OkHttpClient client;
    private WebSocket webSocket;
    private final class EchoWebSocketListener extends WebSocketListener {
        private static final int NORMAL_CLOSURE_STATUS = 1000;
        @Override
        public void onOpen(WebSocket webSocket, okhttp3.Response response){
//            webSocket.send("Hello, it's Jayden !");
//            webSocket.send("Wassssssup!!");
//            webSocket.send("deadbeat?");

            runOnUiThread(() -> output.setText("Connected to server!"));
            Log.d(TAG, "WebSocket opened");
//            webSocket.close(NORMAL_CLOSURE_STATUS, "Goodbye !!");
        }

        @Override
        public void onMessage(WebSocket WebSocket, String text) {
//            output("recieving: " + text);
            runOnUiThread(() -> output.setText("Received: " + text));
            Log.d(TAG, "Message received: " + text);
        }
        @Override
        public void onMessage(WebSocket WebSocket, ByteString bytes) {
//            output("recieving bytes: " +bytes.hex());
            runOnUiThread(() -> output.setText("Received bytes: " + bytes.hex()));
            Log.d(TAG, "Byte message received: " + bytes.hex());
        }

        @Override
        public void onClosing(WebSocket WebSocket, int code,  String reason){
//            WebSocket.close(NORMAL_CLOSURE_STATUS, null);
//            output("Closing: "+ code + " / " + reason);
            runOnUiThread(() -> output.setText("Connection closed: " + reason));
            Log.d(TAG, "WebSocket closed: " + reason);
        }

        @Override
        public void onFailure(WebSocket WebSocket, Throwable t, okhttp3.Response response){
            output("Error: " + t.getMessage());
//            runOnUiThread(() -> output.setText("Connection failed: " + t.getMessage()));
//            Log.e(TAG, "WebSocket error", t);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        start = (Button) findViewById(R.id.start);
        output = (TextView) findViewById(R.id.output);
        client = new OkHttpClient();

        start.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                start();
            }
        });

//        Request request = new Request.Builder().url("ws://127.0.0.1:8765").build(); // Replace with your server's IP
//        webSocket = client.newWebSocket(request, new EchoWebSocketListener());

    }

    private void start(){

        if (client == null) {
            client = new OkHttpClient();
        }
//        Request request = new Request.Builder().url("wss://echo.websocket.org").build();
//        EchoWebSocketListener Listener = new EchoWebSocketListener();
//        WebSocket ws = client.newWebSocket(request, Listener);
//        client.dispatcher().executorService().shutdown();

        Request request = new Request.Builder().url("ws://10.0.2.2:8765").build(); // Replace with your server's IP
        webSocket = client.newWebSocket(request, new EchoWebSocketListener());
    }

    private void output(final String txt){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                output.setText(output.getText().toString() + "\n\n" + txt);
            }
        });
    }
}