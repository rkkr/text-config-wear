package rkr.wear.stringblockwatch;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HttpProxy extends WearableListenerService {
    private static final String TAG = "HttpProxy";
    public static final String HTTP_PROXY_PATH = "/watch_face_proxy";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        Log.d(TAG, "onMessageReceived: " + messageEvent);

        switch (messageEvent.getPath()) {
            case HTTP_PROXY_PATH:
                String url = DataMap.fromByteArray(messageEvent.getData()).getString("url");
                try {
                    DataMap response = getHttp(new URL(url));
                    if (response != null)
                        httpCallback(response);

                } catch (MalformedURLException e) {
                    Log.e(TAG, "Invalid url");
                }

                return;
        }
    }

    private void httpCallback(DataMap response) {
        byte[] data = response.toByteArray();
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();

        googleApiClient.blockingConnect(30, TimeUnit.SECONDS);
        if (!googleApiClient.isConnected()) {
            Log.e(TAG, "google api client connect failed");
            return;
        }

        NodeApi.GetConnectedNodesResult result = Wearable.NodeApi.getConnectedNodes(googleApiClient).await();
        List<Node> nodes = result.getNodes();
        if (nodes.size() == 0) {
            Log.e(TAG, "Phone not connected");
            return;
        }

        Wearable.MessageApi.sendMessage(googleApiClient, nodes.get(0).getId(), HTTP_PROXY_PATH, data).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
            @Override
            public void onResult(@NonNull MessageApi.SendMessageResult sendMessageResult) {
                Log.d("Settings", "Send message: " + sendMessageResult.getStatus().getStatusMessage());
                if (sendMessageResult.getStatus().isSuccess()) {
                    Intent intent = new Intent("string.block.watch.SYNCED");
                    sendBroadcast(intent);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Settings sync failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private DataMap getHttp(URL url){
        try {
            DataMap data = new DataMap();
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp;
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();

            data.putString("body", json.toString());
            data.putInt("status", connection.getResponseCode());
            data.putString("url", url.toString());

            return data;
        } catch (IOException e) {
            Log.d("Weather", e.toString());
            e.printStackTrace();
        }

        return null;
    }
}
