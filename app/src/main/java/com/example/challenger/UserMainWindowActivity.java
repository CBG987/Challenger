package com.example.challenger;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Calendar;

public class UserMainWindowActivity extends AppCompatActivity {

    TextView thechallenge, counter;
    Button pluss1, pluss5, pluss10, minus1, minus5;
    private Socket socket; int today;
    String brukernavnet = "";

    TableRow.LayoutParams rowlayout1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f);
    TableRow.LayoutParams rowlayout2 = new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT);

    //TableLayout layout;

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
        //layout = findViewById(R.id.liste);


        Intent thisIntent = getIntent();
        brukernavnet = thisIntent.getStringExtra("theUsername");
        //Toast.makeText(this, brukernavnet, Toast.LENGTH_LONG).show();

        try{
            socket = IO.socket(getResources().getString(R.string.ipaddress));
            //socket = IO.socket("http://10.0.0.1:2525");
            socket.connect();
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
        socket.on("getcounter", getCounter);

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
                //layout.removeAllViews();
                socket.disconnect();
                Toast.makeText(this, "Logger ut ...", Toast.LENGTH_SHORT).show();
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
                        JSONObject bruker = data.getJSONObject("message");
                        String navn = bruker.getString("navn");
                        String epost = bruker.getString("epost");
                        String brukernavn = bruker.getString("brukernavn");
                        today = Integer.parseInt(bruker.getString("today"));
                        counter.setText(today);
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
                    theRows(args);
                }
            });
        }
    };
    private Emitter.Listener getCounter = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Calendar calendar = Calendar.getInstance();
                    int idag = calendar.get(Calendar.DATE);

                    JSONObject data = (JSONObject) args[0];
                    try {
                        String info = data.getString("message");

                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            });
        }
    };
    public void sendChallenge(int counter){
        JSONObject a = new JSONObject();
        try {
            a.put("username", brukernavnet);
            a.put("counter", counter);
            socket.emit("sendUpdate", a);
        }catch (JSONException e){
            e.printStackTrace();
        }

    }
    public void theRows(Object... args){
        TableLayout layout = findViewById(R.id.liste);
        layout.removeAllViews();
        JSONObject data = (JSONObject) args[0];
        try {
            Log.d("UserMainWindowActivity", "Henter all brukerinformasjon");
            String info = data.getString("message");
            String[] d = info.split("undefined");
            String[] auser = d[1].split("/-/");
            for (int i = 0; i<auser.length; i++){
                TextView txt1, txt2, txt3, txt4;
                TableRow row1;
                String[] theuser = auser[i].split("//");
                Log.d("UserMainWindowActivity", "Henter brukerinformasjon");
                txt1 = new TextView(UserMainWindowActivity.this);
                txt2 = new TextView(UserMainWindowActivity.this);
                txt3 = new TextView(UserMainWindowActivity.this);
                txt4 = new TextView(UserMainWindowActivity.this);
                row1 = new TableRow(UserMainWindowActivity.this);
                txt1.setLayoutParams(rowlayout1); txt2.setLayoutParams(rowlayout1);
                txt3.setLayoutParams(rowlayout1); txt4.setLayoutParams(rowlayout1);
                txt1.setTextSize(20); txt2.setTextSize(20);
                txt3.setTextSize(20); txt4.setTextSize(20);
                txt1.setText(theuser[1]); txt2.setText(theuser[2]);
                txt3.setText(theuser[3]); txt4.setText(theuser[4]);
                row1.addView(txt1); row1.addView(txt2);
                row1.addView(txt3); row1.addView(txt4);row1.setLayoutParams(rowlayout2);
                layout.addView(row1);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}
