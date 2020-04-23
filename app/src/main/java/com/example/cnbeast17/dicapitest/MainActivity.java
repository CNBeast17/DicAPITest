package com.example.cnbeast17.dicapitest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

//add dependencies to your class
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    TextView definitionTxt=null;
    Button definitionBtn;
    EditText inputWordET;
    String inputWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        definitionBtn = (Button)findViewById(R.id.definitionBtn);
        inputWordET = (EditText)findViewById(R.id.inputWord);
        definitionTxt = (TextView)findViewById(R.id.definitionTxt);

        definitionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputWord = inputWordET.getText().toString();
                new CallbackTask().execute(dictionaryEntries());
            }
        });

    }

    private String dictionaryEntries() {
        final String language = "en-gb";
        String word = inputWord;
        final String fields = "definitions";
        final String strictMatch = "true";
        final String word_id = word.toLowerCase();
        return "https://od-api.oxforddictionaries.com:443/api/v2/entries/" + language + "/" + word_id + "?" + "fields=" + fields + "&strictMatch=" + strictMatch;
    }


    //in android calling network requests on the main thread forbidden by default
    //create class to do async job
    private class CallbackTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {

            //TODO: replace with your own app id and app key
            final String app_id = "c2e7b1fe";
            final String app_key = "c36dcd311d964bfd7041f090522ec733";
            try {
                URL url = new URL(params[0]);
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Accept","application/json");
                urlConnection.setRequestProperty("app_id",app_id);
                urlConnection.setRequestProperty("app_key",app_key);

                // read the output from the server
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();


                String line = null;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line + "\n");
                }
                return stringBuilder.toString();

            }
            catch (Exception e) {
                e.printStackTrace();
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            String definition="";
            try {
               JSONObject response = new JSONObject(result);
                JSONArray wordObject = response.getJSONArray("results");
                JSONObject re = wordObject.getJSONObject(0);
                JSONArray lexis = re.getJSONArray("lexicalEntries");
                JSONObject lexi = lexis.getJSONObject(0);
                JSONArray entries = lexi.getJSONArray("entries");
                JSONObject entry = entries.getJSONObject(0);
                JSONArray senses = entry.getJSONArray("senses");
                JSONObject sense = senses.getJSONObject(0);
                JSONArray definitions = sense.getJSONArray("definitions");
                definition = definitions.getString(0);

                definitionTxt.setText(definition);

            } catch (JSONException e) {
                Log.e("DicAPITest", "unexpected JSON exception", e);
            }

            Toast.makeText(MainActivity.this, definition, Toast.LENGTH_LONG).show();
        }
    }
}