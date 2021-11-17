package com.sudrives.sudrives.utils.server;

import android.content.Context;
import android.util.Log;

import com.sudrives.sudrives.utils.Config;

import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.OkHttpClient;

/**
 * Created
 */

public class SocketConnection {
    private static final SocketConnection ourInstance = new SocketConnection();
    private static Socket socketInstance;

    private static android.os.Handler handler = new android.os.Handler();
    private static SocketStatusCallBacks socketStatusCallBacks;

    private static Context context;


    public SocketConnection() {
    }

    public static SocketConnection getInstance(Context mContext, SocketStatusCallBacks socketCallBacks) {
        context = mContext;
        socketStatusCallBacks = (SocketStatusCallBacks) mContext;
        if (socketInstance == null) {
            try {

                HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                };
                TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {

                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {

                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[0];
                    }
                }};
                X509TrustManager trustManager = (X509TrustManager) trustAllCerts[0];

                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
                SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .hostnameVerifier(hostnameVerifier)
                        .sslSocketFactory(sslSocketFactory, trustManager)
                        .connectTimeout(60, TimeUnit.SECONDS).writeTimeout(1, TimeUnit.MINUTES).readTimeout(1, TimeUnit.MINUTES)
                        .build();
               IO.Options opts = new IO.Options();

               opts.sslContext = SSLContext.getDefault();
                opts.hostnameVerifier = hostnameVerifier;
                opts.secure=true;
                socketInstance = IO.socket(Config.BASE_URL_SOCKET, opts);
                connect();
            } catch (URISyntaxException e) {
                Log.e("URISyntaxException",e.toString());
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                Log.e("NoSuchAlgorithm",e.toString());
            } catch (KeyManagementException e) {
                e.printStackTrace();
                Log.e("KeyManagementException",e.toString());
            }
        }
        return ourInstance;
    }

    public static void connect() {
        socketInstance.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                Log.e("Connected", "Connected");
                socketStatusCallBacks.onSocketConnected();


            }

        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                Log.e("Disconnected", "Disconnected");
                socketStatusCallBacks.onSocketDisconnected();
            }

        }).on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                socketStatusCallBacks.onSocketConnectionError();
                Log.e("Connection Error", "Error in Connection " + args[0]);
            }
        }).on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                socketStatusCallBacks.onSocketConnectionTimeout();
                Log.e("Connection Timeout", "Connection Time Out");
            }
        }).on(Socket.EVENT_RECONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                socketStatusCallBacks.onSocketReconnected();
                Log.e("Connection Reconnect", "Reconnected");
            }
        });

        socketInstance.connect();
    }

    public static boolean isConnected() {
        return socketInstance.connected();
    }

    public static void disconnect() {
        Log.e("SocketDisconnect", "SocketDisconnect;;;;");
        if (socketInstance != null) {
            socketInstance.disconnect();
        }
        socketInstance = null;
    }

    public static void attachListener(String nodeName, Emitter.Listener listener) {
        Log.e("Connection Status", "" + isConnected());

        if (socketInstance.connected() && !isListenerEnabled(nodeName)) {
            socketInstance.on(nodeName, listener);
        }



    }


    public static void detachListener(String nodeName, Emitter.Listener listener) {
        Log.e("Connection Status", "" + isConnected());
        if (socketInstance.connected() && isListenerEnabled(nodeName)) {
            socketInstance.off(nodeName, listener);
        }

    }

    public static void attachSingleEventListener(String nodeName, Emitter.Listener listener) {
        if (socketInstance.connected()) {
            socketInstance.once(nodeName, listener);
        }

    }

    public static void removeListener(String nodeName, Emitter.Listener listener) {
        if (socketInstance.connected() && isListenerEnabled(nodeName)) {
            socketInstance.off(nodeName, listener);
        }
    }

    public static boolean isListenerEnabled(String nodeName) {
        return socketInstance.hasListeners(nodeName);
    }


    public static void emitToServer(String event, Object object) {
        socketInstance.emit(event, object);
    }

    public static void emitToServer(String event, Object object, Object object2) {
        socketInstance.emit(event, object, object2);
    }

    public static void emitToServer(String event, Object object, Object object2, Object object3) {
        socketInstance.emit(event, object, object2, object3);
    }

    public static void emitToServer(String event, Object object, Object object2, Object object3, Object object4) {
        socketInstance.emit(event, object, object2, object3, object4);
    }


    public static void emitToServer(String event, Object object, Object object2, Object object3, Object object4, Object object5) {
        socketInstance.emit(event, object, object2, object3, object4, object5);
    }


    public static void emitToServer(String event, Object object, Object object2, Object object3, Object object4, Object object5, Object object6) {
        socketInstance.emit(event, object, object2, object3, object4, object5, object6);
    }


    public static void emitToServer(String event, Object object, Object object2, Object object3, Object object4, Object object5, Object object6, Object object7, Object object8) {
        socketInstance.emit(event, object, object2, object3, object4, object5, object6, object7, object8);
    }

    public static void emitToServer(String event, Object object, Object object2, Object object3, Object object4, Object object5, Object object6, Object object7, Object object8, Object object9, Object object10, Object object11, Object object12) {
        socketInstance.emit(event, object, object2, object3, object4, object5, object6, object7, object8, object9, object10, object11, object12);
    }

    public static Socket getSocket() {

        return socketInstance;
    }

    public interface SocketStatusCallBacks {
        public void onSocketConnected();

        public void onSocketDisconnected();

        public void onSocketReconnected();

        public void onSocketConnectionError();

        public void onSocketConnectionTimeout();
    }
}
