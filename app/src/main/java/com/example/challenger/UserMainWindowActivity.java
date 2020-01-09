package com.example.challenger;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class UserMainWindowActivity extends AppCompatActivity {

    TextView thename, serverrestart;
    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.users_main_window);

        thename = findViewById(R.id.thename);
        serverrestart = findViewById(R.id.serverrestart);

        Intent thisIntent = getIntent();
        String brukernavnet = thisIntent.getStringExtra("theUsername");
        Toast.makeText(this, brukernavnet, Toast.LENGTH_LONG).show();

        try{
            socket = IO.socket(getResources().getString(R.string.ipaddress));
            //socket = IO.socket("http://10.0.0.1:2525");
            socket.connect();
            Toast.makeText(this, "joined", Toast.LENGTH_LONG).show();
        }catch(URISyntaxException e){
            e.printStackTrace();
        }
        JSONObject jippi = new JSONObject();
        try {
            jippi.put("username", brukernavnet);
            socket.emit("getuserinfo", jippi);
        }catch (JSONException e){
            e.printStackTrace();
        }

        socket.on("userinfo", getInfo);
        socket.on("serverrestart", serverrestarts);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.topmenu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.loggut : {
                Toast.makeText(this, "Logger ut ...", Toast.LENGTH_LONG).show();
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
    private Emitter.Listener getInfo = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //final Object d = args[0];
                    JSONObject data = (JSONObject) args[0];
                    try {
                        Toast.makeText(getApplicationContext(), "getting information", Toast.LENGTH_LONG).show();
                        String brukerStrengen = data.getString("message");
                        thename.setText(brukerStrengen);
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            });
        }
    };
    private Emitter.Listener serverrestarts = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    serverrestart.setText("Serveren vil nå starte på nytt");
                }
            });
        }
    };
}
