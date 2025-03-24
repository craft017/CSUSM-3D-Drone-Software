package com.example.airsimapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import android.os.Handler;
import android.os.Looper;

public class WebSocketClientTesting {

        private static final String TAG = "WebSocketClient";
        private WebSocket webSocket;
        private final OkHttpClient client;

        private WebSocketListener listener;

        public interface WebSocketMessageListener {
            void onMessageReceived(String message);

          //  void onByteReceived(Bitmap bitmap);
            // void onByteReceived(Bitmap bitmap);
        }

        public void setWebSocketMessageListener(WebSocketMessageListener listener) {
            this.messageListener = listener;
        }

        private WebSocketMessageListener messageListener;
        public WebSocketClientTesting() {
            this.client = new OkHttpClient(  );
        }


        public void connect(String url) {
            Request request = new Request.Builder().url(url).build();
            webSocket = client.newWebSocket(request, new EchoWebSocketListener(messageListener));
        }

        public void sendMessage(String message) {
            if (webSocket != null) {
                webSocket.send(message);
            }
        }

        public void sendByte(byte[] bytes) {
            if (webSocket != null) {
                webSocket.send(ByteString.of(bytes));
            }
        }

        public void closeConnection(int code, String reason) {
            if (webSocket != null) {
                webSocket.close(code, reason);
            }
        }

        public static class EchoWebSocketListener extends WebSocketListener {

            private final WebSocketMessageListener listener;

            public EchoWebSocketListener(WebSocketMessageListener listener) {
                this.listener = listener;
            }

            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.d(TAG, "WebSocket opened");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Log.d(TAG, "Message received: " + text);
                if (listener != null) {
                    postToMainThread(() -> listener.onMessageReceived(text));
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, final ByteString bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes.toByteArray(), 0, bytes.size());
                //imageView.post(() -> imageView.setImageBitmap(bitmap)); // Update UI
                if (listener != null) {
                  //  postToMainThread(() -> listener.onByteReceived(bitmap));
                }
            }

//            @Override
//            public void onMessage(WebSocket webSocket, ByteString bytes) {
//                Log.d(TAG, "Byte message received: " + bytes.hex());
//                if (listener != null) {
//                    postToMainThread(() -> listener.onMessageReceived(bytes));
//                }
//            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                webSocket.close(1000, null);
                Log.d(TAG, "WebSocket closing: " + reason);
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                Log.e(TAG, "WebSocket error", t);

            }

            private void postToMainThread(Runnable runnable) {
                new Handler(Looper.getMainLooper()).post(runnable);
            }

        }
}