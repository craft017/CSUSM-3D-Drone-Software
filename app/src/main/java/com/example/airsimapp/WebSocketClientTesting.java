package com.example.airsimapp;

import android.util.Log;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import android.os.Handler;
import android.os.Looper;

    public class WebSocketClientTesting {
        private static final String TAG = "WebSocketClient";
        private WebSocket webSocket;
        private final OkHttpClient client;
        private final WebSocketListener listener;

        public WebSocketClientTesting(WebSocketListener listener) {
            this.client = new OkHttpClient();
            this.listener = listener;
        }

        public void connect(String url) {
            Request request = new Request.Builder().url(url).build();
            webSocket = client.newWebSocket(request, listener);
        }

        public void sendMessage(String message) {
            if (webSocket != null) {
                webSocket.send(message);
            }
        }

        public void closeConnection(int code, String reason) {
            if (webSocket != null) {
                webSocket.close(code, reason);
            }
        }

        public static class DefaultWebSocketListener extends WebSocketListener {
            private final Callback callback;

            public DefaultWebSocketListener(Callback callback) {
                this.callback = callback;
            }

            @Override
            public void onOpen(WebSocket webSocket, okhttp3.Response response) {
                Log.d(TAG, "WebSocket opened");
                postToMainThread(() -> callback.onOpen());
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Log.d(TAG, "Message received: " + text);
                postToMainThread(() -> callback.onMessage(text));
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                Log.d(TAG, "Byte message received: " + bytes.hex());
                postToMainThread(() -> callback.onMessage(bytes.hex()));
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                webSocket.close(1000, null);
                Log.d(TAG, "WebSocket closing: " + reason);
                postToMainThread(() -> callback.onClosing(reason));
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
                Log.e(TAG, "WebSocket error", t);
                postToMainThread(() -> callback.onFailure(t.getMessage()));
            }

            private void postToMainThread(Runnable runnable) {
                new Handler(Looper.getMainLooper()).post(runnable);
            }

            public interface Callback {
                void onOpen();

                void onMessage(String message);

                void onClosing(String reason);

                void onFailure(String error);
            }
        }
}