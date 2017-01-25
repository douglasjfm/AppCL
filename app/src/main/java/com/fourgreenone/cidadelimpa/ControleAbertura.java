package com.fourgreenone.cidadelimpa;

import java.io.IOException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class ControleAbertura extends AppCompatActivity {

	public View vsIniciar;

	@Override
	public void onPause(){
		super.onPause();
		finish();
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		try {
			PersistirDados.salvarPrefs(getApplicationContext());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SharedPreferences sharedPref = this.getSharedPreferences(
		        getString(R.string.cl_key_shared_prefs), Context.MODE_PRIVATE);
		
		if (sharedPref.contains("gmailUserID")){
			startActivity(new Intent(this, ControleMenu.class));
			finish();
		}
		
		setContentView(R.layout.activity_fullscreen);
		
		android.support.v7.app.ActionBar barra = getSupportActionBar();
		barra.setDisplayShowHomeEnabled(true);
		barra.setIcon(R.mipmap.ic_launcher48);
		barra.setTitle("Cidade Limpa - Bem Vindo");
		
		vsIniciar = findViewById(R.id.dummy_button);

		vsIniciar.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				buildAlertMessageIntructs();
			}
		});
	}
	
	@Override
	public void onResume(){
		super.onResume();
		SharedPreferences sharedPref = this.getSharedPreferences(
		        getString(R.string.cl_key_shared_prefs), Context.MODE_PRIVATE);
		
		if (sharedPref.contains("gmailUserID")){
			Intent intento;
			intento = new Intent(this,ControleMenu.class);
 		   	startActivity(intento);
 		   	finish();
		}
	}
	
	private void buildAlertMessageIntructs() {
	    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setMessage("Mande denuncias com fotos de locais onde você encontrar entulhos, e o Cidade Limpa automaticamente enviara para as autoridades competentes tanto a imagem como o local da denuncia, informado pelo seu GPS. Você também pode realizar denuncias anonimamente.")
	           .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	               public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
	            	   	Intent intento;
	   					intento = new Intent(getApplicationContext(), ControleAuth.class);
	   					startActivity(intento);
	   					finish();
	               }
	           });
	    final AlertDialog alert = builder.create();
	    alert.show();
	}
}
