package com.example.challenger;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;


public class NewUserActivity extends AppCompatActivity {

    EditText name, email, username, password, passwordretype;
    Button createuser, cancel;
    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newuser);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        passwordretype = findViewById(R.id.passwordrepeat);

        createuser = findViewById(R.id.createuser);
        cancel = findViewById(R.id.cancel);

        try{
            //socket = IO.socket("http://85.166.159.139:3001");
            socket = IO.socket(getResources().getString(R.string.ipaddress));
            //socket = IO.socket("http://10.0.0.1:2525");
            socket.connect();
            socket.emit("message", "an android unit joined");
            Toast.makeText(this, "joined", Toast.LENGTH_LONG).show();
        }catch(URISyntaxException e){
            e.printStackTrace();
        }

        createuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    check(name, email, username, password, passwordretype);
                }catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                //finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    public void check(EditText name, EditText email, EditText username, EditText password, EditText passwordretype) throws NoSuchAlgorithmException{
        byte [] saltet = ("BonesNissen123").getBytes(); //getSalt();
        if (name.getText().toString().equals("") || email.getText().toString().equals("") || username.getText().toString().equals("") || password.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(), "Alle felt må fylles ut", Toast.LENGTH_LONG).show();
        }else {
            if(password.getText().toString().equals(passwordretype.getText().toString())){
                if(checkpassword(password.getText().toString())){
                    String sendnewuser = name.getText().toString()+"//"+email.getText().toString()+"//"+username.getText().toString()+"//"+SHA_256_Cryptation(password.getText().toString(), saltet);
                    socket.emit("newuser", sendnewuser);
                }else {
                    Toast.makeText(getApplicationContext(), "Passordet må være mellom 5-15 karaterer, kun bestå av bokstaver og tall, og inneholde minst en bokstav og et tall", Toast.LENGTH_LONG).show();
                }
            }else {
                Toast.makeText(getApplicationContext(), "Passordene stemmer ikke", Toast.LENGTH_LONG).show();
            }
        }
    }
    public boolean checkpassword (String password){
        if(password == null) return false;
        if (password.length() < 5 || password.length() > 15) return false;
        boolean containsUpperCase = false;
        boolean containsLowerCase = false;
        boolean containsDigit = false;
        for(char ch: password.toCharArray()){
            if(Character.isUpperCase(ch)) containsUpperCase = true;
            if(Character.isLowerCase(ch)) containsLowerCase = true;
            if(Character.isDigit(ch)) containsDigit = true;
        }
        return containsUpperCase && containsLowerCase && containsDigit;
    }
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
    private static byte[] getSalt() throws NoSuchAlgorithmException
    {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        String soft = "DetteErEtBonesSalt";
        byte[] softnes = soft.getBytes();
        return softnes;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        socket.disconnect();
    }
}
