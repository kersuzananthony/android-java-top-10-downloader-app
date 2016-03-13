package com.kersuzan.top10downloader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private Button buttonParse;
    private ListView listViewXML;
    private String mFileContents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Link UI
        this.buttonParse = (Button) findViewById(R.id.buttonParse);
        this.listViewXML = (ListView) findViewById(R.id.listViewXML);

        // Set onClickListener on button
        this.buttonParse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseApplication parseApplication = new ParseApplication(MainActivity.this.mFileContents);
                parseApplication.process();

                ArrayAdapter<Application> arrayAdapter = new ArrayAdapter<Application>(
                        MainActivity.this, R.layout.list_item, parseApplication.getApplications()
                );

                MainActivity.this.listViewXML.setAdapter(arrayAdapter);
            }
        });

        // Download data
        DownloadData downloadData = new DownloadData();
        downloadData.execute("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Inner class
    private class DownloadData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            MainActivity.this.mFileContents = downloadXMLFile(params[0]);

            if (mFileContents == null) {
                Log.d("Download data", "Error downloading data");
            }

            return MainActivity.this.mFileContents;
            // When succeeded, automatic calls onPostExecute function
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("Download data", "Result was: " + result);
            // Here we can update UI
        }

        private String downloadXMLFile(String urlPath) {
            StringBuilder tempBuffer = new StringBuilder();

            try {
                URL url = new URL(urlPath); // URL can throw exception IOException
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int response = connection.getResponseCode(); // 200, 404...
                Log.d("Download data", "Response code : " + response);
                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                int charRead;
                char[] inputBuffer = new char[500];

                while (true) {
                    charRead = inputStreamReader.read(inputBuffer);

                    if (charRead <= 0) {
                        break; // Break the loop
                    }
                    
                    tempBuffer.append(String.copyValueOf(inputBuffer, 0, charRead));
                }

                return tempBuffer.toString();
            } catch (IOException e) {
                Log.d("Download data", "IO Exception reading data: " + e.getMessage());
                e.getStackTrace();
            } catch (SecurityException e) {
                Log.d("Download data", "Security exception");
            }

            return null;
        }


    }
}
