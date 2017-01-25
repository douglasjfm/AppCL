package com.fourgreenone.cidadelimpa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class ControleProximas extends AppCompatActivity {
	
	int[] idLabels;
	String[] desc;
	
	private static String serverHost;
	private static String serverFunc = "proximascl.php";
	private static String upLoadServerUri;// = "http://" + serverHost + "/" + serverFunc;
	ProgressDialog dialog;
	private Location local;
	private LocationListener locObservador;
	private LocationManager gerenteLoc;
	
	public ArrayList<DenunciaProxima> denuncias;
	
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
	public void onCreate(Bundle b){
		super.onCreate(b);
		
		ActionBar barra = getSupportActionBar();
		barra.setDisplayShowHomeEnabled(true);
		barra.setIcon(R.mipmap.ic_launcher48);
		barra.setTitle("Cidade Limpa - Proximidades");
		SharedPreferences prefs = this.getSharedPreferences(getString(R.string.cl_key_shared_prefs),Context.MODE_PRIVATE);
		serverHost = prefs.getString(getString(R.string.cl_servidor_id),"");
		upLoadServerUri = "http://" + serverHost + "/" + serverFunc;
		locObservador = new LocationListener() {
			@Override
		    public void onStatusChanged(String provider, int status, Bundle extras) {
		    	//local = gerenteLoc.getLastKnownLocation(provider);
		    }

		    public void onProviderEnabled(String provider) {}

		    public void onProviderDisabled(String provider) {}

			@Override
			public void onLocationChanged(Location location) {
				local = location;
				if (local.getAccuracy() < 200.0){
					//new ReqProximas().execute();
					gerenteLoc.removeUpdates(locObservador);
				}
			}
		  };
		
		gerenteLoc = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		local = gerenteLoc.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if(local == null)local = gerenteLoc.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		gerenteLoc.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				3000, 1, locObservador);
		
		if (true/*local!=null && local.getAccuracy() < 200.0*/){
			dialog = ProgressDialog.show(this, "", "Procurando...", true);
			gerenteLoc.removeUpdates(locObservador);
			new ReqProximas().execute();
		}
		else{
			Toast.makeText(this, "Aguardando posição mais precisa...", Toast.LENGTH_SHORT).show();
			onBackPressed();
		}
	}
	public void visual(){
		ArrayAdaptador adaptador;
		ListView lista;
		setContentView(R.layout.listacontas);
		lista = (ListView) findViewById(R.id.lista);
		adaptador = new ArrayAdaptador (this,R.layout.lista_item_proximas,denuncias);
		lista.setAdapter(adaptador);
		dialog.dismiss();
	}

	public void showImageProx(View v){
		ImageView iv = (ImageView) v.findViewById(R.id.lista_item_img_prox);
		String imgUri = iv.getContentDescription().toString();
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.parse(imgUri), "image/*");
		startActivity(intent);
	}

	private class ReqProximas extends AsyncTask<String, Void, String>{
		
		@Override
		protected void onPostExecute(String r){
			int i;
			int n = Integer.parseInt(desc[0]);
			n++;
			denuncias = new ArrayList<DenunciaProxima>();
			if (desc != null){
				for(i=1;i<n;i++){
					String[] par = desc[i].split("#");//par[0],par[1] lat lng par[4] end, par[2] id, par[3] - fname;
					denuncias.add(new DenunciaProxima(par[3],par[4],Integer.parseInt(par[2]),par[3].split("_")[1],par[0]+","+par[1]));
				}
				visual();
			}
		}
		@Override
		protected String doInBackground (String... args){
			// params comes from the execute() call: params[0] is the url.
            try {
            	enviarReq();
                return "";
            } catch (IOException e) {
                return "Falha na conexão com o servidor.";
            }
		}
		
		public void enviarReq () throws IOException{
			
			String parametros = "";
			String response = "";
			String userID;
			String[] descs;
			
			userID = getApplicationContext().getSharedPreferences(
			        getString(R.string.cl_key_shared_prefs), Context.MODE_PRIVATE).getString("userID", "erroReq");
			
	        if (local != null) parametros = "lat=" + local.getLatitude() + "&lng=" + local.getLongitude() + "&user=" + userID + "&p=1";

			try {
					URL url = new URL(upLoadServerUri + "?" + parametros);
					BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
					String strTemp = "";
					while (null != (strTemp = br.readLine())) {
						response += strTemp;
					}
			} catch (final IOException execao) {
				runOnUiThread(new Runnable(){
					@Override
					public void run() {
						Toast.makeText(getApplicationContext(), "Problemas na conexão.",Toast.LENGTH_LONG).show();
						//new ReqProximas().execute();
						execao.printStackTrace();
					}
					
				});
				return;
			}
			
			descs = response.split(";");
			desc = descs;
		}
	}
}
