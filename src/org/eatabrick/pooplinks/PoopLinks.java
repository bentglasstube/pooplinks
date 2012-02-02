package org.eatabrick.pooplinks;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.content.Intent;
import android.widget.Toast;

public class PoopLinks extends Activity {
  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.main, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    Intent intent = null;
    switch (item.getItemId()) {
      case R.id.history:
        intent = new Intent(PoopLinks.this, History.class);
        startActivityForResult(intent, 0);
        return true;
      case R.id.settings:
        intent = new Intent(PoopLinks.this, Preferences.class);
        startActivityForResult(intent, 0);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }
}
