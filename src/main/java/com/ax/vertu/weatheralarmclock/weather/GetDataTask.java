package com.ax.vertu.weatheralarmclock.weather;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by VERTU on 2017/1/5.
 */
public class GetDataTask extends AsyncTask<String, Void, String> {
    private HttpURLConnection httpURLConnection;
    private URL url;
    private InputStream in;
    private BufferedReader reader;
    private GetDataListener listener;

    public GetDataTask(URL url, GetDataListener listener){
        this.url = url;
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setReadTimeout(5000);
            in = httpURLConnection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * onPostExecute
     * @param s 是doInBackground的返回值
     */
    @Override
    protected void onPostExecute(String s) {
        listener.getData(s);
        super.onPostExecute(s);
    }
}
