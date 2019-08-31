package com.example.newbiz;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import static com.example.newbiz.MyOrders.CONNECTION;

public class SignUp extends AppCompatActivity {
    private TextView signUp_title,tvEmail_SignUp,tvPwd_SignUp,tvName_SignUp,tvContact,tvAddress_SignUp;
    private EditText etEmail_SignUp,etPwd_SignUp,etName_SignUp,etContact,etAddress_SignUp;
    private Button btn_SignUp;
    public static final String MY_PREFS_NAME = "User";
private String name,email,address,contact,pwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signUp_title=findViewById(R.id.signUp_title);


        etName_SignUp=findViewById(R.id.etName_SignUp);
        etEmail_SignUp=findViewById(R.id.etUName_SignUp);
        etAddress_SignUp=findViewById(R.id.etAddress_SignUp);  //input of address should not be compulsory
        etContact=findViewById(R.id.etContact);
        etPwd_SignUp=findViewById(R.id.etPwd_SignUp);




        tvName_SignUp=findViewById(R.id.tvName_SignUp);
        tvEmail_SignUp=findViewById(R.id.tvUName_SignUp);
        tvAddress_SignUp=findViewById(R.id.tvAddress_SignUp);
        tvContact=findViewById(R.id.tvContact);
        tvPwd_SignUp=findViewById(R.id.tvPwd_SignUp);
        btn_SignUp=findViewById(R.id.btn_SignUp);


        btn_SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 name=etName_SignUp.getText().toString().trim();
                 email=etEmail_SignUp.getText().toString().trim();
                 address=etAddress_SignUp.getText().toString().trim();
                 contact=etContact.getText().toString().trim();
                 pwd= etPwd_SignUp.getText().toString().trim();

                if(name.isEmpty() || email.isEmpty() || contact.isEmpty() || pwd.isEmpty()){
                    Snackbar.make(view,"Please fill the required details.",Snackbar.LENGTH_SHORT).show();
                }
                else{
                    //fire the query
                    BackgroundTask backgroundTask=new BackgroundTask();
                    backgroundTask.execute(name,email,address,contact,pwd,"signUp.php");


                }
            }
        });

    }

    public class BackgroundTask extends AsyncTask<String,Void,String> {
        AlertDialog.Builder builder;
        private static final String KEY_SUCCESS ="success" ;
        private static final String KEY_DATA ="data" ;
        String result="  ";
        String connstr= CONNECTION ;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            //go to main activity
            //set shared preferences
            SharedPreferences.Editor editor=getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE).edit();
            editor.putBoolean("loggedIn",true);
            editor.putString("name",name);
            editor.putString("email",email);
            editor.putString("address",address);
            editor.putString("contact",contact);
            editor.putString("pwd",pwd);

            editor.apply();

            MainActivity mainActivity=new MainActivity();
            mainActivity.setFragment(MainActivity.FRAGMENT_HOME);


                    }

        @Override
        protected String doInBackground(String... voids) {

            String name=voids[0];
            String email=voids[1];
            String address=voids[2];
            String contact=voids[3];
            String pwd=voids[4];


            String extension=voids[5];
            //can be: selectOrder.php,insert.php


            try {
                URL url=new URL(connstr+extension);
                HttpURLConnection http= (HttpURLConnection) url.openConnection();
                http.setRequestMethod("POST");
                http.setDoInput(true);
                http.setDoOutput(true);

                OutputStream ops=http.getOutputStream();

                BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(ops,"utf-8"));

                String data=  URLEncoder.encode("name","UTF-8")+"="+ URLEncoder.encode(name,"UTF-8");
                writer.write(data);
                writer.flush();


                data=  URLEncoder.encode("email","UTF-8")+"="+ URLEncoder.encode(email,"UTF-8");
                writer.write(data);
                writer.flush();


                data=  URLEncoder.encode("address","UTF-8")+"="+ URLEncoder.encode(address,"UTF-8");
                writer.write(data);
                writer.flush();

                data=  URLEncoder.encode("contact","UTF-8")+"="+ URLEncoder.encode(contact,"UTF-8");
                writer.write(data);
                writer.flush();



                data=  URLEncoder.encode("pwd","UTF-8")+"="+ URLEncoder.encode(pwd,"UTF-8");
                writer.write(data);
                writer.flush();

                writer.close();
                ops.close();


                InputStream ips=http.getInputStream();

                BufferedReader reader=new BufferedReader(new InputStreamReader(ips,"ISO-8859-1"));

                String line="";

                while((line=reader.readLine())!=null){

                    result +=line;

                }

                reader.close();
                ips.close();
                http.disconnect();

                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.i("Message1",e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("Message2",e.getMessage());
            }


            return result;
        }
    }



}