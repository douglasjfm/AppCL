package com.fourgreenone.cidadelimpa;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ControlePerfil extends AppCompatActivity {

	private static int countupdate;
    private static String servidor;
	
	@Override
	public void onCreate (Bundle b){
		super.onCreate(b);
		TextView email;
		countupdate = 0;
        servidor = "";
		SharedPreferences prefs = this.getSharedPreferences(
		        getString(R.string.cl_key_shared_prefs), Context.MODE_PRIVATE);
        servidor = prefs.getString(getString(R.string.cl_servidor_id),"cl.sem.servidor");

		String userID = prefs.getString("gmailUserID", "errorUser");
		
		ActionBar barra = getSupportActionBar();
		barra.setDisplayShowHomeEnabled(true);
		barra.setIcon(R.mipmap.ic_launcher48);
		barra.setTitle("Cidade Limpa - Perfil");
		
		setContentView(R.layout.perfil);
		
		email = (TextView) findViewById(R.id.perfiltxt);
		email.setText(userID);
	}

	public void updateServer(View v){
		countupdate++;
		if (countupdate == 8){
            countupdate = 0;
			Toast.makeText(this,"servidor: " + servidor,Toast.LENGTH_LONG).show();
		}
	}
}
