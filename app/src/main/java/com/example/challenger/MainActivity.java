package com.example.challenger;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    EditText brukernavn, passord;
    Button login, forgotpassoword, newuser;
    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        brukernavn = findViewById(R.id.brukernavn);
        passord = findViewById(R.id.passord);
        login = findViewById(R.id.login);
        forgotpassoword = findViewById(R.id.forgotpassword);
        newuser = findViewById(R.id.newuser);

        try{
            //socket = IO.socket("http://85.166.159.139:3001");
            socket = IO.socket(getResources().getString(R.string.ipaddress));
            //socket = IO.socket("http://10.0.0.1:2525");
            socket.connect();
            Toast.makeText(this, "joined", Toast.LENGTH_LONG).show();
        }catch(URISyntaxException e){
            e.printStackTrace();
        }

        socket.on("loginconfirmed", onlogin);



        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte [] salt = ("BonesNissen123").getBytes();
                JSONObject jippi = new JSONObject();
                try {
                    jippi.put("username", brukernavn.getText().toString());
                    jippi.put("password", SHA_256_Cryptation(passord.getText().toString(),salt));
                    socket.emit("connectionLogin", jippi);
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });

        forgotpassoword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        newuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewUserActivity.class);
                startActivity(intent);
            }
        });
    }
    private Emitter.Listener onlogin = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //final Object d = args[0];
                    JSONObject data = (JSONObject) args[0];
                    try {
                        if (data.getString("message").equals("success")){
                            Toast.makeText(getApplicationContext(), "login successful", Toast.LENGTH_LONG).show();
                            String brukernavn = data.getString("session");
                            Intent intent = new Intent(MainActivity.this, UserMainWindowActivity.class);
                            intent.putExtra("theUsername", brukernavn);
                            startActivity(intent);
                        }else{
                            Toast.makeText(getApplicationContext(), data.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private static String SHA_256_Cryptation(String password, byte[] salt)
    {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] bytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return generatedPassword;
    }

}
