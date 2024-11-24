package com.example.airsimapp;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
    private Button start, sendMessage;
    private TextView output;
    private OkHttpClient client;
    private WebSocket webSocket;
    private EditText inputField;
    private final class EchoWebSocketListener extends WebSocketListener {
        @Override
        public void onOpen(WebSocket webSocket, okhttp3.Response response){

            runOnUiThread(() -> output("Connected to server!"));
            Log.d(TAG, "WebSocket opened");

        }

        @Override
        public void onMessage(WebSocket WebSocket, String text) {
            runOnUiThread(() -> output("Received: " + text));
            Log.d(TAG, "Message received: " + text);
        }
        @Override
        public void onMessage(WebSocket WebSocket, ByteString bytes) {
            runOnUiThread(() -> output("Received bytes: " + bytes.hex()));
            Log.d(TAG, "Byte message received: " + bytes.hex());
        }

        @Override
        public void onClosing(WebSocket WebSocket, int code,  String reason){
            runOnUiThread(() -> output("Connection closed: " + reason));
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
        sendMessage = (Button) findViewById(R.id.send_message); // New button for sending a message
        inputField = (EditText) findViewById(R.id.input_field); // Input field for the message
        client = new OkHttpClient();

        start.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                start();
            }
        });

        sendMessage.setOnClickListener(view -> sendMessage());
    }

    private void start(){

        if (client == null) {
            client = new OkHttpClient();
        }
        Request request = new Request.Builder().url("ws://10.0.2.2:8765").build(); // 10.0.2.2 is the host machines IP
        webSocket = client.newWebSocket(request, new EchoWebSocketListener());
    }

    private void sendMessage() {
        String message = inputField.getText().toString();
        if (webSocket != null && !message.isEmpty()) {
            webSocket.send(message);
            output("Sent: " + message);
        } else {
            output("WebSocket is not connected or message is empty.");
        }
    }
    private void output(final String txt){
        runOnUiThread(( ) -> {
            output.setText(output.getText().toString() + "\n\n" + txt);
        });
    }
}