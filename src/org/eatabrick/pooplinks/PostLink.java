package org.eatabrick.pooplinks;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;
import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.message.BasicNameValuePair;

public class PostLink extends Activity {
  private class PostLinkTask extends AsyncTask<String, Integer, String> {
    protected String doInBackground(String... params) {
      String uri = params[0];
      String user = params[1];

      try {
        List<NameValuePair> data = new ArrayList<NameValuePair>();
        data.add(new BasicNameValuePair("uri", uri));
        data.add(new BasicNameValuePair("user", user));

        UrlEncodedFormEntity body = new UrlEncodedFormEntity(data, "UTF-8");

        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost("http://eab.so/");
        request.setEntity(body);

        String response = client.execute(request, new BasicResponseHandler());

        if (response.contains("error")) {
          return getString(R.string.error_from_site, response.substring(10, response.length() - 2));
        } else {
          return response.substring(11, response.length() - 2);
        }
      } catch (UnsupportedEncodingException e) {
        Log.w("PoopLinks", "Unsupported encoding");
        return getString(R.string.error_unsupported_encoding);
      } catch (IOException e) {
        Log.w("PoopLinks", "I/O exception");
        return getString(R.string.error_io_exception);
      }
    }

    protected void onProgressUpdate(Integer... progress) {
      // do nothing
    }

    protected void onPostExecute(String message) {
      Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

    String uri = getIntent().getExtras().getString(Intent.EXTRA_TEXT);
    String user = settings.getString("name", "Some Asshole");

    new PostLinkTask().execute(uri, user);

    finish();
  }
}
