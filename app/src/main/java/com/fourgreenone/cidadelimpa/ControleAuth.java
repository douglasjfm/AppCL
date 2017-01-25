package com.fourgreenone.cidadelimpa;



import java.io.FileNotFoundException;
import java.io.IOException;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ControleAuth extends AppCompatActivity {
	
	private View visualAuthGmail;
	
	private AccountManager accmgr;
	private Account[] gcontas;
	
	public String gmail;

	SharedPreferences sharedPref;
	SharedPreferences.Editor editor;
	
	@Override
	public void onPause(){
		super.onPause();
		finish();
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		try {
			PersistirDados.salvarPrefs(this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onCreate(Bundle savedInstance){
		super.onCreate(savedInstance);
		
		sharedPref = this.getSharedPreferences(
		        getString(R.string.cl_key_shared_prefs), Context.MODE_PRIVATE);
		editor = sharedPref.edit();
		
		setContentView(R.layout.authcl);
		
		ActionBar barra = getSupportActionBar();
		barra.setDisplayShowHomeEnabled(true);
		barra.setIcon(R.mipmap.ic_launcher48);
		barra.setTitle("Cidade Limpa - Configuração");
				
		accmgr = AccountManager.get(this);
		gcontas = accmgr.getAccountsByType("com.google");
								
		this.visualAuthGmail = findViewById(R.id.gmailgroup);
		
		this.visualAuthGmail.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (gcontas.length > 0)
					buildAlertMessageEmails();
			}
		});
	}
	
	@Override
	public void onResume(){
		super.onResume();
		
		try {
			PersistirDados.recuperarDados(this);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		sharedPref = this.getSharedPreferences(
		        getString(R.string.cl_key_shared_prefs), Context.MODE_PRIVATE);
		
		if (sharedPref.contains("gmailUserID")){
			Intent intento;
			intento = new Intent(getApplicationContext(),ControleMenu.class);
 		   	startActivity(intento);
 		   	finish();
		}
	}
	
	@SuppressLint("NewApi")
	private void buildAlertMessageEmails() {
	    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    
	    final String[] nomesContas = new String[gcontas.length];
	    for (int i=0;i < gcontas.length; i++)
	    		nomesContas[i] = gcontas[i].name;
	    
	    ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this,R.layout.lista_item_txt,nomesContas);
	    ListView lista = new ListView(this);
	    lista.setAdapter(adaptador);
	    
	    builder.setTitle("Escolha sua conta")
	           .setCancelable(true)
	           /*.setView(lista)*/
	           .setAdapter(adaptador, new OnClickListener(){
	        	   @Override
	        	   public void onClick (DialogInterface d, int p){
	        		   Intent intento;
	        		   gmail = nomesContas[p];
	        		   
	        		   editor.putString("gmailUserID", gmail);
	        		   editor.putInt("NumDen", 0);
	        		   editor.commit();
	        		   
	        		   try {
						PersistirDados.salvarPrefs(getApplicationContext());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        		   
	        		   intento = new Intent(getApplicationContext(),ControleMenu.class);
	        		   intento.putExtra("userID", gmail);
	        		   startActivity(intento);
	        	   }
	           });
	    
	    final AlertDialog alert = builder.create();
	    alert.show();
	}
}
