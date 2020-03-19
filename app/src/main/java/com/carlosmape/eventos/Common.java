package com.carlosmape.eventos;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class Common {
    static final String SERVER_URL = "http://curso-firebase.000webhostapp.com/";
    static String PROJECT_ID = "eventos2020-fdd8a";

    String registerId = "";

    static void showDialog(final Context context, final String message) {
        Intent intent = new Intent(context, Dialog.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("mensaje", message);
        context.startActivity(intent);
    }

    public static class registerDeviceInWebServerTask extends AsyncTask<Void, Void, String> {
        String response = "error";
        Context context;
        String registerTaskId = "";

        public void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... arg0) {
            try {
                Uri.Builder constructorParametros = new Uri.Builder().appendQueryParameter("iddevice", registerTaskId).appendQueryParameter("idapp", PROJECT_ID);
                String parametros = constructorParametros.build().getEncodedQuery();
                String url = SERVER_URL + "registrar.php";
                URL address = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) address.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Accept-Language", "UTF-8");
                connection.setDoOutput(true);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
                outputStreamWriter.write(parametros.toString());
                outputStreamWriter.flush();
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    response = "ok";
                } else {
                    response = "error";
                }
            } catch (IOException e) {
                response = "error";
            }
            return response;
        }

        public void onPostExecute(String res) {
        }
    }

    public static void saveRegisterID(Context context, String registerId) {
        registerDeviceInWebServerTask task = new registerDeviceInWebServerTask();
        task.context = context;
        task.registerTaskId = registerId;
        task.execute();
    }
}
