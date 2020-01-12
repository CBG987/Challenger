package com.example.challenger;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
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

    TextView thechallenge, counter;
    Button pluss1, pluss5, pluss10, minus1, minus5;
    private Socket socket;
    TextView txt1;
    TextView txt2;
    TableRow.LayoutParams rowlayout1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f);
    TableRow.LayoutParams rowlayout2 = new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
    TableRow row1;
    TableLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.users_main_window);

        thechallenge = findViewById(R.id.thechallenge);
        thechallenge.setText("Pushups");
        counter = findViewById(R.id.counter);
        pluss1 = findViewById(R.id.pluss1);
        pluss5 = findViewById(R.id.pluss5);
        pluss10 = findViewById(R.id.pluss10);
        minus1 = findViewById(R.id.minus1);
        minus5 = findViewById(R.id.minus5);
        layout = findViewById(R.id.liste);


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
        socket.emit("getallusers","NO");
        socket.on("userinfo", getInfo);
        socket.on("alluserinfo", getallInfo);

        pluss1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendChallenge(1);
            }
        });
        pluss5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendChallenge(10);
            }
        });
        pluss10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendChallenge(10);
            }
        });
        minus1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendChallenge(-1);
            }
        });
        minus5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendChallenge(-5);
            }
        });
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
                        JSONObject bruker = data.getJSONObject("message");
                        String navn = bruker.getString("navn");
                        String epost = bruker.getString("epost");
                        String brukernavn = bruker.getString("brukernavn");
                        String pushup = bruker.getString("pushups");
                        counter.setText(pushup);
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            });
        }
    };
    private Emitter.Listener getallInfo = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //final Object d = args[0];
                    JSONObject data = (JSONObject) args[0];
                    try {
                        Toast.makeText(getApplicationContext(), "getting  information", Toast.LENGTH_LONG).show();
                        Log.d("UserMainWindowActivity", "Henter all brukerinformasjon");
                        String info = data.getString("message");
                        String[] d = info.split("undefined");
                        String[] auser = d[1].split("/-/");
                        for (int i = 0; i<auser.length; i++){
                            String[] theuser = auser[i].split("//");
                            Log.d("UserMainWindowActivity", "Henter brukerinformasjon");
                            txt1 = new TextView(UserMainWindowActivity.this);
                            txt2 = new TextView(UserMainWindowActivity.this);
                            row1 = new TableRow(UserMainWindowActivity.this);
                            txt1.setLayoutParams(rowlayout1); txt2.setLayoutParams(rowlayout1);
                            txt1.setTextSize(20); txt2.setTextSize(20);
                            txt1.setText(theuser[1]);
                            txt2.setText(theuser[2]);
                            row1.addView(txt1); row1.addView(txt2);
                            row1.setLayoutParams(rowlayout2); layout.addView(row1);
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            });
        }
    };
    public void sendChallenge(int counter){

    }
}
