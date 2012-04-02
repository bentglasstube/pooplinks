package org.eatabrick.pooplinks;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.net.Uri;

public class History extends ListActivity {
  private List<Map<String, String>> links = new ArrayList<Map<String, String>>();
  private SimpleAdapter adapter;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    adapter = new SimpleAdapter(
      this, 
      links, 
      android.R.layout.simple_list_item_2, 
      new String[] {"title", "author"},
      new int[] {android.R.id.text1, android.R.id.text2}
    );

    setContentView(R.layout.history);
    setListAdapter(adapter);

    new ReadRSSTask().execute();
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    Intent intent = null;
    switch (item.getItemId()) {
      case R.id.refresh:
        links.clear();
        adapter.notifyDataSetChanged();
        new ReadRSSTask().execute();
        return true;
      case R.id.settings:
        intent = new Intent(this, Preferences.class);
        startActivityForResult(intent, 0);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override public void onListItemClick(ListView parent, View v, int position, long id) {
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setData(Uri.parse(links.get(position).get("link")));
    startActivity(intent);
  }

  private void readRSS() {
    try {
      URL feed = new URL("http://eab.so/rss");
      SAXParserFactory factory = SAXParserFactory.newInstance();
      SAXParser parser = factory.newSAXParser();
      XMLReader reader = parser.getXMLReader();
      RSSHandler handler = new RSSHandler();

      reader.setContentHandler(handler);
      InputSource input = new InputSource(feed.openStream());
      reader.parse(input);
    } catch (MalformedURLException e) {
      Log.w("PoopLinks", "Malformed URL");
    } catch (ParserConfigurationException e) {
      Log.w("PoopLinks", "Parser configuration error");
    } catch (SAXException e) {
      Log.w("PoopLinks", "SAX error");
    } catch (IOException e) {
      Log.w("PoopLinks", "I/O exception");
    }
  }

  private void displayRSS() {
    adapter.notifyDataSetChanged();
  }

  private class RSSHandler extends DefaultHandler {
    final int stateNone   = 0;
    final int stateTitle  = 1;
    final int stateLink   = 2;
    final int stateAuthor = 3;

    boolean stateItem = false;
    int state = stateNone;
    Map<String, String> item;

    @Override public void startDocument() throws SAXException { }

    @Override public void endDocument() throws SAXException { }

    @Override public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      if (localName.equalsIgnoreCase("item")) {
        stateItem = true;
        item = new HashMap<String, String>(3);
      } else if (stateItem && localName.equalsIgnoreCase("title")) {
        state = stateTitle;
      } else if (stateItem && localName.equalsIgnoreCase("link")) {
        state = stateLink;
      } else if (stateItem && localName.equalsIgnoreCase("author")) {
        state = stateAuthor;
      } else {
        state = stateNone;
      }
    }

    @Override public void endElement(String uri, String localName, String qName) throws SAXException {
      if (localName.equalsIgnoreCase("item")) { 
        stateItem = false; 
        links.add(item);
      }
      state = stateNone;
    }

    @Override public void characters(char[] ch, int start, int length) throws SAXException {
      String text = new String(ch, start, length);
      switch (state) {
        case stateTitle:
          item.put("title", text);
          break;
        case stateAuthor:
          item.put("author", text);
          break;
        case stateLink:
          item.put("link", text);
          break;
        default:
          break;
      }
    }
  }

  private class ReadRSSTask extends AsyncTask<Void, Void, Void> {
    protected Void doInBackground(Void... rubbish) {
      readRSS();
      return null;
    }

    protected void onPostExecute(Void result) {
      displayRSS();
    }
  }
}

