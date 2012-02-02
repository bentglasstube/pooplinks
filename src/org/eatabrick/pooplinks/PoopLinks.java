package org.eatabrick.pooplinks;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.ComponentName;
import android.widget.Toast;

public class PoopLinks extends Activity {
  private SharedPreferences settings;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

    if (Intent.ACTION_SEND.equals(getIntent().getAction())) {
      String uri = getIntent().getExtras().getString(Intent.EXTRA_TEXT);

      Intent intent = new Intent();
      intent.setComponent(new ComponentName("org.eatabrick.pooplinks", "org.eatabrick.pooplinks.PostService"));
      intent.setAction("org.eatabrick.pooplinks.POST_LINK");
      intent.putExtra("org.eatabrick.pooplinks.link", uri);
      intent.putExtra("org.eatabrick.pooplinks.name", settings.getString("name", "Some Asshole"));
      startService(intent);

      finish();
    } else {
      setContentView(R.layout.main);
    }
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.main, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.history:
        Toast.makeText(PoopLinks.this, "Not yet implemented", Toast.LENGTH_SHORT).show();
        return true;
      case R.id.settings:
        Intent intent = new Intent(PoopLinks.this, Preferences.class);
        startActivityForResult(intent, 0);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }
}
