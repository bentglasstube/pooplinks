package org.eatabrick.pooplinks;

import android.app.Activity;
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
  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

    String uri = getIntent().getExtras().getString(Intent.EXTRA_TEXT);
    String user = settings.getString("name", "Some Asshole");
    String message;

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
        message = "Error: " + response.substring(10, response.length() - 2);
      } else {
        message = response.substring(11, response.length() - 2);
      }
    } catch (UnsupportedEncodingException e) {
      Log.w("PoopLinks", "Unsupported encoding");
      message = "Unsupported encoding";
    } catch (IOException e) {
      Log.w("PoopLinks", "I/O exception");
      message = "I/O exception";
    }

    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

    finish();
  }
}
