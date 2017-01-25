package com.fourgreenone.cidadelimpa;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;

public class ControleAtivarGPS extends AppCompatActivity {
	
	ProgressBar imgGPS;
	public int progress;
	public Handler gpsH;
	ProgressDialog dialog;
	public LocationManager gerenteLoc;
	public LocationListener locObservador;
	public Location local;
	
	@Override
	public void onCreate(Bundle b){
		super.onCreate(b);
		
		setContentView(R.layout.ativargps);
		
		ActionBar barra = getSupportActionBar();
		barra.setDisplayShowHomeEnabled(true);
		barra.setIcon(R.mipmap.ic_launcher48);
		barra.setTitle("Cidade Limpa - Localização");
		
		local = null;

		gerenteLoc = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
		locObservador = new LocationListener() {
		    public void onStatusChanged(String provider, int status, Bundle extras) {}
		    public void onProviderEnabled(String provider) {
		    	//ImageView infoGPS = (ImageView) findViewById(R.id.infoGPS);
		    	//infoGPS.setImageResource(R.drawable.imggps2);
		    	}
		    public void onProviderDisabled(String provider) {
		    	//ImageView infoGPS = (ImageView) findViewById(R.id.infoGPS);
		    	//infoGPS.setImageResource(R.drawable.gpsimg);
		    }
			@Override
			public void onLocationChanged(Location location) {
				local = location;
			}
		  };
		
		
		gerenteLoc.requestLocationUpdates(LocationManager.GPS_PROVIDER, 900, 15, locObservador);
				
        // Start lengthy operation in a background thread
		
		dialog = ProgressDialog.show(ControleAtivarGPS.this, "", "Aguarde o GPS", true);
		
        new Thread(new Runnable() {
            public void run() {
                while (local == null || local.getAccuracy() > 100.0){}
        		dialog.dismiss();
        		fim();
            }
        }).start();
	}
	
	void fim(){
		finish();
	}
}
