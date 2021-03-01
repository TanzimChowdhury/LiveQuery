package com.example.livequery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;

import java.net.URI;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {

    private int numPokes;
    ParseLiveQueryClient parseLiveQueryClient = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ParseInstallation.getCurrentInstallation().saveInBackground();
        initializeUI();
        initializeLiveQuery();
    }

    private void initializeLiveQuery() {
        // Init Live Query Client

        try {
            Toast.makeText(this, "Connection Established", Toast.LENGTH_SHORT).show();
            parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient(new URI("wss://test450.b4a.io/"));
        } catch (URISyntaxException e) {
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        //Subscribe to events
        if (parseLiveQueryClient != null) {
            ParseQuery<ParseObject> parseQuery = new ParseQuery("Message");
            parseQuery.whereEqualTo("destination", "pokelist");
            SubscriptionHandling<ParseObject> subscriptionHandling =
                    parseLiveQueryClient.subscribe(parseQuery);

            //This will be triggered when a new object is created
            subscriptionHandling.handleEvent(SubscriptionHandling.Event.CREATE, (query, object) -> {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> {
                    TextView pokeText = findViewById(R.id.pokeText);
                    numPokes++;
                    if(numPokes == 1) {
                        pokeText.setText("Poked " + numPokes + " time");
                    }
                    else {
                        pokeText.setText("Poked " + numPokes + " times");
                    }
                });
            });
        }

    }

    private void initializeUI() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public void createMessage(View view) {
        //Creating new object for Message Class
        ParseObject entity = new ParseObject("Message");

        entity.put("destination", "pokelist");

        // Saves the new object.
        // Notice that the SaveCallback is totally optional!
        entity.saveInBackground(e -> {
            if (e==null){
                //Save was done
            }else{
                //Something went wrong
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}