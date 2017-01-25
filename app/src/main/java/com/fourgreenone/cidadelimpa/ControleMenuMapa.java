package com.fourgreenone.cidadelimpa;

import java.io.IOException;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.future.ResponseFuture;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Point;

public class ControleMenuMapa extends FragmentActivity implements OnMapReadyCallback {
	
	public MapFragment mapa;
	public GoogleMap gMapa;
	
	public Marker mim;
	
	public Location local;
	
	public String userID;
	
	public LocationListener locObservador;
	
	public LocationManager gerenteLoc;
	
	public double[] pontos;
	public int[] idLabels;
	public String[] dados;
	public String[] fotoslnk;
	
	public boolean infoWFlag = false;
	
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
	public void onPause(){
    	super.onPause();
    	SharedPreferences prefs = this.getSharedPreferences(
    			getString(R.string.cl_key_shared_prefs), Context.MODE_PRIVATE);
    	if (prefs.contains("DIDs")){
    		String key = prefs.getString("DIDs", "");
    		SharedPreferences.Editor ed = prefs.edit();
    		String[]ids = key.split(",");
    		int i;
    		for (i=0; i<ids.length;i++){
    			if (prefs.contains("InfoW" + ids[i]))
    				ed.remove("InfoW" + ids[i]);
    		}
    		ed.remove("DIDs");
    		ed.apply();
    	}
	}
    
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle b) {
		super.onCreate(b);
		
		SharedPreferences prefs = this.getSharedPreferences(
	        getString(R.string.cl_key_shared_prefs), Context.MODE_PRIVATE);
		userID = prefs.getString("gmailUserID", "adm");

        locObservador = new LocationListener() {
			
			@Override
		    public void onStatusChanged(String provider, int status, Bundle extras) {
		    	//local = gerenteLoc.getLastKnownLocation(provider);
		    }

		    public void onProviderEnabled(String provider) {}

		    public void onProviderDisabled(String provider) {}

			@Override
			public void onLocationChanged(Location location) {
				//gMapa.setMyLocationEnabled(true);
				local = location;
				if (mim != null) mim.remove();
				mim = gMapa.addMarker(new MarkerOptions()
				.position(new LatLng(local.getLatitude(),local.getLongitude())).title("Você")
				.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
				//Toast.makeText(getApplicationContext(), ""+local.getAccuracy(), Toast.LENGTH_SHORT).show();
				//new ReqCoords().execute();
				gMapa.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(local.getLatitude(), local.getLongitude())));
			}
		  };
		gerenteLoc = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
		local = gerenteLoc.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (local == null) local = gerenteLoc.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        setContentView(R.layout.maptest);
        mapa = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapa.getMapAsync(this);
	}
	
	@SuppressLint("InflateParams")
	public void preparaInfoW(){
		GoogleMap.InfoWindowAdapter infoWAdaptador = new GoogleMap.InfoWindowAdapter(){
						
			public String data(String dt){
				String dia,mes,ano;
				dia = dt.substring(6, 8);
				mes = dt.substring(4, 6);
				ano = dt.substring(0, 4);
				
				return dt = dia+"/"+mes+"/"+ano;
			}
			
			@Override
			public View getInfoContents(Marker m) {
				
				View info;
				String[] inf;
                String serverFotos;
				SharedPreferences prefs;
				prefs = getApplicationContext().getSharedPreferences(
						getString(R.string.cl_key_shared_prefs), Context.MODE_PRIVATE);
				LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService
					      (Context.LAYOUT_INFLATER_SERVICE);
				inf = m.getTitle().split("#");
                serverFotos = "http://" + prefs.getString(getString(R.string.cl_servidor_id),"") + "/" + "deposito/";
				if (inf.length == 1){
					info = inflater.inflate(R.layout.titlevoce, null);
					TextView tv = (TextView) info.findViewById(R.id.titlevoce);
					tv.setText(inf[0]);
					return info;
				}
				info = inflater.inflate(R.layout.clinfomap, null);
				ImageView imageView = (ImageView) info.findViewById(R.id.imagemapinfo);
				
				infoWFlag = prefs.contains("InfoW" + inf[0]);
				
		        if (infoWFlag == false){
		        	
		        	SharedPreferences.Editor ed;
		        	ed = prefs.edit();
		        	ed.putBoolean("InfoW"+inf[0], true);
		        	ed.commit();
		        	
		        	infoWFlag = true;
		        	Picasso.with(getApplicationContext()).load(serverFotos + inf[3]).into(imageView,new InfoWindowRefresher(m));
		        }
		        else Picasso.with(getApplicationContext()).load(serverFotos + inf[3]).into(imageView);
				
					
				TextView txt = (TextView) info.findViewById(R.id.title);
				txt.setText("Denúncia " + inf[0]);
				TextView txt2 = (TextView) info.findViewById(R.id.snippet);
				txt2.setText("Tipo " + inf[1] + ", " + inf[2] + " porte\n" + data(inf[3].split("_")[1]));
				
				return info;
			}
			@Override
			public View getInfoWindow(Marker arg0) {
				// TODO Auto-generated method stub
				return null;
			}
			
		};
		gMapa.setInfoWindowAdapter(infoWAdaptador);
	}
	
	@Override
	public void onMapReady(GoogleMap map) {
		gMapa = map;
		
		preparaInfoW();
		
		gerenteLoc.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				3000, 1, locObservador);
		
		if (local!=null) mim = gMapa.addMarker(new MarkerOptions()
		.position(new LatLng(local.getLatitude(),local.getLongitude())).title("Você")
		.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
		
		ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
		new Runnable(){

			@Override
			public void run() {
				ajustaPos();
			}
		};
		if(netInfo != null && netInfo.isConnected())
			new ReqCoords().execute();
		else
			Toast.makeText(this, "Cidade Limpa: Sem Conexão", Toast.LENGTH_LONG).show();
	}
	
	String getClasseById (String c){
		switch(Integer.parseInt(c)){
		case 1: return "Concreto"; 
		case 2: return "Ferro"; 
		case 3: return "Tijolos"; 
		case 4: return "Madeira"; 
		case 5: return "Gesso"; 
		case 6: return "Misto"; 
		default: return "Variados";
		}
	}
	
	String getVolumeById (String c){
		switch(Integer.parseInt(c)){
		case 1: return "pequeno"; 
		case 2: return "médio"; 
		case 3: return "grande";
		}
		return "n/a";
	}
	
	public void plotar (){
		int j = 0;
		
		String didKey = "";
		SharedPreferences prefs = getApplicationContext().getSharedPreferences(
				getString(R.string.cl_key_shared_prefs), Context.MODE_PRIVATE);
		
		if (pontos != null && pontos.length > 0)
		for (int i = 0; i < pontos.length; i += 2){
			String[] spl = dados[j].split("&");
			gMapa.addMarker(new MarkerOptions()
	        .position(new LatLng(pontos[i] , pontos[i+1]))
	        .title(idLabels[j]+"#"+getClasseById(spl[0])+"#"+getVolumeById(spl[1])+"#"+fotoslnk[j]));
			didKey += idLabels[j] + ",";
			j++;
		}
		prefs.edit().putString("DIDs", didKey).apply();
	}
	
	public void ajustaPos(){
		Point pontovc;
		
		if (local != null)
		{
			pontovc = gMapa.getProjection().toScreenLocation(new LatLng(local.getLatitude(),local.getLongitude()));
			while (pontovc.x == 0 && pontovc.y == 0){
				pontovc = gMapa.getProjection().toScreenLocation(new LatLng(local.getLatitude(),local.getLongitude()));
			}
			gMapa.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(local.getLatitude(), local.getLongitude()), 14.0f));
		}
	}
	
	private class ReqCoords extends AsyncTask<String, Void, String>{
		
		@Override
		protected void onPostExecute(String r){
			ajustaPos();
			plotar();
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
			
//			String parametros = "";
			ResponseFuture<String> response;
            SharedPreferences prefs = getApplicationContext().
					getSharedPreferences(getString(R.string.cl_key_shared_prefs),Context.MODE_PRIVATE);
			String[] txtcoords;
			double[] coords;
			int[] ids;
            String serverPontos = "http://" + prefs.getString(getString(R.string.cl_servidor_id),"") + "/proximascl.php";
			
			response = Ion.with(getApplicationContext())
			.load(serverPontos)
			.noCache()
			.setBodyParameter("lat", local.getLatitude() + "")
			.setBodyParameter("lng", local.getLongitude() + "")
			.setBodyParameter("p", "0")
			.asString();
			
			while(!response.isDone()){}
			String resposta = response.tryGet();

			if ((resposta != null) && (!resposta.isEmpty())) {
                txtcoords = resposta.split(";");
                int npontos = Integer.parseInt(txtcoords[0]);
                String[] tv = new String[npontos];
                coords = new double[npontos * 2];
                ids = new int[npontos];
                fotoslnk = new String[npontos];

                int j = 0, k = 0;
                npontos++;
                for (int i = 1; i < npontos; i++) {
                    String[] splited = txtcoords[i].split("#");
                    if (splited[0].matches("-?[0-9]{1,13}(\\.[0-9]*)")) {
                        coords[k] = Double.parseDouble(splited[0]);
                        coords[k + 1] = Double.parseDouble(splited[1]);
                        ids[j] = Integer.parseInt(splited[2]);
                        fotoslnk[j] = splited[3];
                        tv[i - 1] = splited[5] + "&" + splited[6];
                        j++;
                        k += 2;
                    }
                }
                pontos = coords;
                idLabels = ids;
                dados = tv;
            } else {
                pontos = null;
                idLabels = null;
                dados = null;
            }
		}
		
	}
	
	private class InfoWindowRefresher implements Callback {
		   private Marker markerToRefresh;

		   private InfoWindowRefresher(Marker markerToRefresh) {
		        this.markerToRefresh = markerToRefresh;
		    }

		    @Override
		    public void onSuccess() {
		        markerToRefresh.showInfoWindow();
		    }

		    @Override
		    public void onError() {}

		}

}
