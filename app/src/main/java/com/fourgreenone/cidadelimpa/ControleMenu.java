package com.fourgreenone.cidadelimpa;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.GpsStatus.Listener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ControleMenu extends AppCompatActivity{
	
	public View vsRecentes, vsOcorrencias, vsRelatar, vsMapa, vsPerfil;
	
	public LocationManager gerenteLoc;
	public LocationListener locObservador;
	private SharedPreferences prefs;
	private AlertDialog alert;
	private String[] fpathfotos;
	private String userID;
	
	public String anonimo = null;

	private Location local;
	
	private int serverResponseCode = 0;
	private ProgressDialog dialog = null;
	private String serverHost;
	private String upLoadServerUri;
    
	private File uploadFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    private String uploadFileName = "";

	private void checkserver(){
		SharedPreferences lpref = this.getSharedPreferences(getString(R.string.cl_key_shared_prefs), Context.MODE_PRIVATE);
        SharedPreferences.Editor edt;
		try {
			URL url = new URL("http://noronhashouse.com/cl/endereco.txt");
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			InputStream in = new BufferedInputStream(urlConnection.getInputStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String read;
            serverHost = "";
			while((read=br.readLine()) != null) {
				serverHost = serverHost + read;
			}
			in.close();
            edt = lpref.edit();
            edt.putString(getString(R.string.cl_servidor_id),serverHost);
            edt.apply();
		}catch (IOException e){
		}
	}
	
	@Override
	public void onCreate (Bundle b) {
		super.onCreate(b);
		setContentView(R.layout.menu);
		
		ActionBar barra = getSupportActionBar();
		barra.setDisplayShowHomeEnabled(true);
		barra.setIcon(R.mipmap.ic_launcher48);
		barra.setTitle("Cidade Limpa - Menu");
		
		if(locObservador == null) locObservador = new LocationListener() {
			@Override
		    public void onStatusChanged(String provider, int status, Bundle extras) {}
			@Override
		    public void onProviderEnabled(String provider) {
		    	ImageView infoGPS = (ImageView) findViewById(R.id.infoGPS);
		    	infoGPS.setImageResource(R.mipmap.imggps2);
		    	}
			@Override
		    public void onProviderDisabled(String provider) {
		    	ImageView infoGPS = (ImageView) findViewById(R.id.infoGPS);
		    	infoGPS.setImageResource(R.mipmap.gpsimg);
		    }
			@Override
			public void onLocationChanged(Location location) {
				local = location;
			}
		  };
		gerenteLoc = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
		if (gerenteLoc.isProviderEnabled(LocationManager.GPS_PROVIDER )) {
			gerenteLoc.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 15, locObservador);
	        ImageView infoGPS = (ImageView) findViewById(R.id.infoGPS);
	        infoGPS.setImageResource(R.mipmap.imggps2);
	    }
		else{
			ImageView infoGPS = (ImageView) findViewById(R.id.infoGPS);
	        infoGPS.setImageResource(R.mipmap.gpsimg);
		}
		
		this.fpathfotos = new String[3];
				
		prefs = this.getSharedPreferences(getString(R.string.cl_key_shared_prefs), Context.MODE_PRIVATE);
		
		this.vsRecentes = findViewById(R.id.menurecentes);
		this.vsOcorrencias = findViewById(R.id.menuocorrencias);
		this.vsRelatar = findViewById(R.id.menurelatar);
		this.vsMapa = findViewById(R.id.menumapa);
		this.vsPerfil = findViewById(R.id.menuperfil);

        Thread getCLServer = new Thread(new Runnable() {
            @Override
            public void run() {
                checkserver();
            }
        });
        getCLServer.start();
	}
	
	void animaIcone(View v){
		Animation animacao = AnimationUtils.loadAnimation(this, R.anim.abc_slide_out_top);
		v.startAnimation(animacao);
	}
	
	public void relatar(View v){
		animaIcone(v);
		
		gerenteLoc = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
		
		if (!gerenteLoc.isProviderEnabled(LocationManager.GPS_PROVIDER )) {
	        buildAlertMessageNoGps();
	    }
		else {
			Intent intento;
			intento = new Intent(this,ControleAtivarGPS.class);
			startActivityForResult(intento,10);
			//dispatchTakePictureIntent();
		}
	}
	
	public void mapaResiduos (View v){
		//v.setBackgroundColor(Color.argb(80, 196, 57, 255));

		Intent intento;
		
		gerenteLoc = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
		
		animaIcone(v);
		
		if (!gerenteLoc.isProviderEnabled(LocationManager.GPS_PROVIDER )) {
	        buildAlertMessageNoGps();
	    }
		else{
			intento = new Intent (this,ControleMenuMapa.class);
			startActivity(intento);
		}
	}
	
	public void cbInfoGPS (View v){
		gerenteLoc = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
		
		if (gerenteLoc.isProviderEnabled(LocationManager.GPS_PROVIDER)){
			relatar(v);
		}
		else{
			startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS),3);
		}
	}
	
	public void proximas (View v){
		//v.setBackgroundColor(Color.argb(80, 196, 57, 255));
		Intent intento;
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo;
		
		networkInfo = connMgr.getActiveNetworkInfo();
		
		gerenteLoc = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
		if (!gerenteLoc.isProviderEnabled(LocationManager.GPS_PROVIDER )) {
			animaIcone(v);
	        buildAlertMessageNoGps();
	    }
		else if (networkInfo != null && networkInfo.isConnected()){
			animaIcone(v);
			//intento = new Intent(this,ControleAtivarGPS.class);
			intento = new Intent(this,ControleProximas.class);
			startActivityForResult(intento,20);
		}
		else{
			Toast.makeText(this, "Sem conexão", Toast.LENGTH_SHORT).show();
		}
	}

	public void recentes (View v){
		Intent intento;
		
		animaIcone(v);
		
		prefs = this.getSharedPreferences(
		        getString(R.string.cl_key_shared_prefs), Context.MODE_PRIVATE);
		
		if (prefs.contains("NumDen") && prefs.getInt("NumDen", 0) > 0){
			intento = new Intent(getApplicationContext(),ControleRecentes.class);
			startActivity(intento);
		}
		else{
			Toast.makeText(this, "Sem atividaes recentes", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void perfil (View v){
		Intent intento;
		
		animaIcone(v);
		
		intento = new Intent(this,ControlePerfil.class);
		startActivity(intento);
	}
	
	public void anonimidade (View v){
		if(((CheckBox) v).isChecked()){
			anonimo = "1";
		}
		else{
			anonimo = null;
		}
	}
	
	@Override
	public void onResume(){
		super.onResume();
		
		ImageView infoGPS = (ImageView) findViewById(R.id.infoGPS);
		
		gerenteLoc = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
		
		if(locObservador == null) locObservador = new LocationListener() {
		    public void onStatusChanged(String provider, int status, Bundle extras) {}
			@Override
		    public void onProviderEnabled(String provider) {
		    	ImageView infoGPS = (ImageView) findViewById(R.id.infoGPS);
		    	infoGPS.setImageResource(R.mipmap.imggps2);
		    }
			@Override
		    public void onProviderDisabled(String provider) {
		    	ImageView infoGPS = (ImageView) findViewById(R.id.infoGPS);
		    	infoGPS.setImageResource(R.mipmap.gpsimg);
		    }
			@Override
			public void onLocationChanged(Location location) {
				local = location;
			}
		  };
		
		if (infoGPS!=null && gerenteLoc != null && gerenteLoc.isProviderEnabled(LocationManager.GPS_PROVIDER ))
	    {
			infoGPS.setImageResource(R.mipmap.imggps2);
			local = gerenteLoc.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			gerenteLoc.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locObservador);
	    }
		else if (infoGPS!=null)
	        infoGPS.setImageResource(R.mipmap.gpsimg);
	}
	
	String mCurrentPhotoPath;
	
	@Override
	public void onActivityResult(int r, int b, Intent data){
		
		if (r==2 && b == -1){
			
			ActionBar barra = getSupportActionBar();
			barra.setDisplayShowHomeEnabled(true);
			barra.setTitle("Cidade Limpa - Denunciar");
			
			prefs = this.getSharedPreferences(
				    getString(R.string.cl_key_shared_prefs), Context.MODE_PRIVATE);
			
			fpathfotos = new String[3];
			fpathfotos[0] = prefs.getString("uploadFile", "");
			
			Bitmap foto = setPic(fpathfotos[0]);
			
			uploadFileName = fpathfotos[0];
			
			setContentView(R.layout.classedetritos);
			
			int id = Resources.getSystem().getIdentifier("btn_check_holo_light", "drawable", "android");
			((CheckBox)findViewById(R.id.checkBox1)).setButtonDrawable(id);
			
			ImageView imagemCapturada = (ImageView) findViewById(R.id.capturada);
			imagemCapturada.setImageBitmap(foto);
			
			if(locObservador == null)locObservador = new LocationListener() {

			    public void onStatusChanged(String provider, int status, Bundle extras) {
			    	//local = gerenteLoc.getLastKnownLocation(provider);
			    }

			    public void onProviderEnabled(String provider) {}

			    public void onProviderDisabled(String provider) {}

				@Override
				public void onLocationChanged(Location location) {
					local = location;
				}
			  };
			
				gerenteLoc = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
				gerenteLoc.requestLocationUpdates(LocationManager.GPS_PROVIDER,
					1000, 1, locObservador);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		
		if (r == 10){
			dispatchTakePictureIntent();
		}
		if (r == 20){
			Intent intento;
			intento = new Intent(this,ControleProximas.class);
			startActivity(intento);
		}
	}
	
	public void escolheVolume (View img){
		selectImgViewVolume((ImageView) img);
		img.setBackgroundColor(Color.argb(255, 255, 255, 255)); // #112233 -> gray scale...
	}
	
	public void escolheClasse (View img){
		selectImgViewClasse((ImageView) img);
		if (img.isSelected())img.setBackgroundColor(Color.argb(255, 255, 255, 255)); // #112233 -> gray scale...
		else img.setBackgroundColor(0);
	}
	
	private void dispatchTakePictureIntent() {
	    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    // Ensure that there's a camera activity to handle the intent
	    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
	        // Create the File where the photo should go
	        File photoFile = null;
	        try {
	            photoFile = createImageFile();
	        } catch (IOException ex) {
	            // Error occurred while creating the File
	            
	        }
	        // Continue only if the File was successfully created
	        if (photoFile != null) {
	            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(photoFile));
	            startActivityForResult(takePictureIntent, 2);
	        }
	    }
	}
	
	private Bitmap setPic(String fnamejpg) {
		
		fnamejpg = prefs.getString("uploadFile", "");
		
//		Toast.makeText(getApplicationContext(), fnamejpg,
//                Toast.LENGTH_LONG).show();
	    // Get the dimensions of the bitmap
	    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	    
	    int scaleFactor =0;//= Math.min(photoW/targetW, photoH/targetH);
	    
	    if(scaleFactor == 0) scaleFactor = 10;

	    // Decode the image file into a Bitmap sized to fill the View
	    bmOptions.inJustDecodeBounds = false;
	    bmOptions.inSampleSize = scaleFactor;
	    //bmOptions.inPurgeable = true;

	    Bitmap bitmap = BitmapFactory.decodeFile(fnamejpg, bmOptions);
	    return bitmap;
	}
	
	
    public void submeterDenuncia (View v){
	
	if (retornaSelClasse() == "" || retornaSelVolume() == ""){
		Toast.makeText(this, "Selecione o volume e o tipo!", Toast.LENGTH_LONG).show();
		return;
	}
	
	prefs = this.getSharedPreferences(
	        getString(R.string.cl_key_shared_prefs), Context.MODE_PRIVATE);

	userID = prefs.getString("gmailUserID", "errorUser");
	
		local = gerenteLoc.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (local == null){
			Toast.makeText(this, "Aguardando GPS", Toast.LENGTH_LONG)
			.show();
			return;
			}
		if (local.getAccuracy() > 100.0){
			Toast.makeText(this, "Aguardando posição mais precisa.", Toast.LENGTH_LONG)
			.show();
			return;
		}

		if (gerenteLoc != null)gerenteLoc.removeUpdates(locObservador);
	
		
		Button btn = (Button) v;
		if (btn != null) btn.setEnabled(false);
		
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			
			dialog = ProgressDialog.show(ControleMenu.this, "", "Enviando...", true);
			
			if (fpathfotos[0] == null) fpathfotos[0] = prefs.getString("uploadFile", "Error");
			new UploadAsync().execute(fpathfotos[0]);// Upload em outra thread.
			
			if (btn != null) btn.setEnabled(true);
			
			//onCreate(null);
		    } else {
		    	if (btn != null) btn.setEnabled(true);
		    	Toast.makeText(getApplicationContext(), "Cidade Limpa: Sem Conexão.", Toast.LENGTH_LONG).show();
		    }
	}
	private File createImageFile() throws IOException {
	    // Create an image file name
		SharedPreferences.Editor editor;
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    String imageFileName = "JPEG_" + timeStamp + "_";
	    File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
	    File image = File.createTempFile(
	        imageFileName,  /* prefix */
	        ".jpg",         /* suffix */
	        storageDir      /* directory */
	    );
	
	    // Save a file: path for use with ACTION_VIEW intents
	    String mCurrentPhotoPath = image.getAbsolutePath();
	    fpathfotos[0] = mCurrentPhotoPath;
	    prefs = this.getSharedPreferences(
		        getString(R.string.cl_key_shared_prefs), Context.MODE_PRIVATE);
	    editor = prefs.edit();
	    editor.putString("uploadFile", mCurrentPhotoPath);
	    editor.commit();
	    
	    return image;
	}

	private void buildAlertMessageNoGps() {
	    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setMessage("Ative seu GPS")
	           .setCancelable(true)
	           .setPositiveButton("Ativar", new DialogInterface.OnClickListener() {
	               public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
	            	   startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS),3);
	               }
	           });
	    final AlertDialog alert = builder.create();
	    alert.show();
	}
	
	private void buildAlertMessageObg() {
	    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setMessage("Parabéns e obrigado por ajudar a manter a cidade limpa.")
	           .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	               public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
	            	   //startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS),3);
	               }
	           });
	    final AlertDialog alert = builder.create();
	    alert.show();
	}
	
	private void selectImgViewClasse (ImageView iv){
		if (iv.isSelected()) iv.setSelected(false);
		else iv.setSelected(true);
	}
	
	private void selectImgViewVolume (ImageView iv){
		ImageView[] i;
		i = new ImageView[3];
		i[0] = (ImageView) findViewById(R.id.volume1);
		i[1] = (ImageView) findViewById(R.id.volume2);
		i[2] = (ImageView) findViewById(R.id.volume3);
		
		for(int j=0; j<3; j++){
			i[j].setSelected(false);
			i[j].setBackgroundColor(0);
		}
		iv.setSelected(true);
	}
	
	private String retornaSelClasse (){
		ImageView[] i;
		int j = 0;
		String ret="";
		i = new ImageView[6];
		i[0] = (ImageView) findViewById(R.id.classe1);
		i[1] = (ImageView) findViewById(R.id.classe2);
		i[2] = (ImageView) findViewById(R.id.classe3);
		i[3] = (ImageView) findViewById(R.id.classe4);
		i[4] = (ImageView) findViewById(R.id.classe5);
		i[5] = (ImageView) findViewById(R.id.classe6);

		while (j < 6){
			if (i[j].isSelected())ret += (j+1) + "";
			j++;
		}
		return ret;
	}
	
	private String retornaSelVolume (){
		ImageView[] i;
		int j = 0;
		i = new ImageView[3];
		i[0] = (ImageView) findViewById(R.id.volume1);
		i[1] = (ImageView) findViewById(R.id.volume2);
		i[2] = (ImageView) findViewById(R.id.volume3);

		while (j < 3 && i[j].isSelected()==false) j++;
		
		if (j == 3) return "";
		
		switch (i[j].getId()){
		case R.id.volume1 : return "1";
		case R.id.volume2 : return "2";
		case R.id.volume3 : return "3";
		default: return "";
		}
	}
	public void reenviar(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		 builder.setMessage("Erro ao enviar denuncia.")
		 .setCancelable(true)
		 .setPositiveButton("Reenviar", new DialogInterface.OnClickListener() {
			 public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
				 submeterDenuncia(null);
			 }
		 });
		alert = builder.create();
		alert.show();
	}
	
	private class UploadAsync extends AsyncTask<String, Void, String> {
				
        @Override
        protected String doInBackground(String... args) {
              
            // params comes from the execute() call: params[0] is the url.
            try {
                return uploadFile(args[0]);
            } catch (IOException e) {
                return "Falha ao enviar denuncia.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
        	if (result == "3"){
        		dialog.dismiss();
        		setContentView(R.layout.menu);//onCreate(null);
        		ActionBar barra = getSupportActionBar();
    			barra.setDisplayShowHomeEnabled(true);
    			barra.setTitle("Cidade Limpa - Menu");
        		buildAlertMessageObg();
        	}
        	else reenviar();
       }
        
        public String uploadFile(String sourceFileUri) throws IOException {
            
            
            String fileName = sourceFileUri;

            String parametros = "";

    		fpathfotos[1] = retornaSelVolume();
    		fpathfotos[2] = retornaSelClasse();
    		if (anonimo == null) anonimo = "0";
            if (local != null) parametros = "lat=" + local.getLatitude() + "&lng=" + local.getLongitude() + "&c=" + fpathfotos[2] + "&v=" + fpathfotos[1] + "&user=" + userID + "&anon=" + anonimo;
            else parametros = "lat=-8.01556&" + "lng=" + "-34.5522&" + "c=" + fpathfotos[2] + "&v="+ fpathfotos[1] + "&user=" + userID + "&anon=" + anonimo;
            	
            HttpURLConnection conn = null;
            DataOutputStream dos = null; 
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            File sourceFile = new File(sourceFileUri);
             
            if (!sourceFile.isFile()) {
                 
                 //dialog.dismiss();
                  
                 Log.e("uploadFile", "Source File not exist :"
                                     +uploadFilePath + "" + uploadFileName);
                  
                 runOnUiThread(new Runnable() {
                     public void run() {
                    	 dialog.dismiss();
                         Toast.makeText(getApplicationContext(),
                        		 "Cidade Limpa: Erro ao ler a foto.", Toast.LENGTH_SHORT).show();
                     }
                 });
                  
                 return "1";
            }
            else
            {
            	String endereco = "";
            	try {
    				URL url = new URL("http://maps.googleapis.com/maps/api/geocode/json?latlng=" + local.getLatitude() + "," + local.getLongitude() + "&sensor=true");
    				BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
    				JSONObject json;
    				String strTemp = "";
    				String response = "";
    				while (null != (strTemp = br.readLine())) {
    					response += strTemp;
    				}
    				json = new JSONObject (response);
    				JSONArray arr = json.getJSONArray("results");
    				endereco = arr.getJSONObject(0).getString("formatted_address");
    			} catch (final IOException execao) {
    				return null;
    			} catch (JSONException jsone) {
    				jsone.printStackTrace();
    			}
                 try {
                      
                     // open a URL connection to the Servlet
                     FileInputStream fileInputStream = new FileInputStream(sourceFile);
					 serverHost = prefs.getString(getString(R.string.cl_servidor_id),"");
                     upLoadServerUri = "http://" + serverHost + "/uploadcl.php";
                     URL url = new URL(upLoadServerUri + "?" + parametros + "&end=" + URLEncoder.encode(endereco, "UTF-8"));
                      
                     // Open a HTTP  connection to  the URL
                     conn = (HttpURLConnection) url.openConnection();
                     conn.setDoInput(true); // Allow Inputs
                     conn.setDoOutput(true); // Allow Outputs
                     conn.setUseCaches(false); // Don't use a Cached Copy
                     conn.setRequestMethod("POST");
                     conn.setRequestProperty("Connection", "Keep-Alive");
                     conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                     conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                     conn.setRequestProperty("uploaded_file", fileName);
                      
                     dos = new DataOutputStream(conn.getOutputStream());
                     
                     dos.writeBytes(twoHyphens + boundary + lineEnd);
                     
                     dos.writeBytes("Content-Disposition: form-data; name="+"file"+";filename="
                                                + fileName + lineEnd);
                      
                     dos.writeBytes(lineEnd);
            
                     // create a buffer of  maximum size
                     bytesAvailable = fileInputStream.available();
            
                     bufferSize = Math.min(bytesAvailable, maxBufferSize);
                     buffer = new byte[bufferSize];
            
                     // read file and write it into form...
                     bytesRead = fileInputStream.read(buffer, 0, bufferSize); 
                        
                     while (bytesRead > 0) {
                          
                       dos.write(buffer, 0, bufferSize);
                       bytesAvailable = fileInputStream.available();
                       bufferSize = Math.min(bytesAvailable, maxBufferSize);
                       bytesRead = fileInputStream.read(buffer, 0, bufferSize);  
                        
                      }
            
                     // send multipart form data necesssary after file data...
                     
                     
                     
                     dos.writeBytes(lineEnd);
                     dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            
                     // Responses from the server (code and message)
                     serverResponseCode = conn.getResponseCode();
                     String serverResponseMessage = conn.getResponseMessage();
                     byte[] ret = new byte[300];
                     conn.getInputStream().read(ret);
                     String retServ = new String(ret);
                     Log.i("uploadFile", "HTTP Response is : "
                             + serverResponseMessage + ": " + serverResponseCode + "Msg: " + retServ);
                      
                     if(serverResponseCode == 200 && retServ.startsWith("0")){
                    	 //final String tmsg = endereco + " " + fileName + fpathfotos[1] + fpathfotos[2];
                    	 final String[] newid = retServ.split(",");
                    	 InfoRecentes.insere(newid[1], endereco, fpathfotos[1], fpathfotos[2], fileName, getApplicationContext());
                         runOnUiThread(new Runnable() {
                              public void run() {
                                  Toast.makeText(getApplicationContext(), "Cidade Limpa: Denuncia "+newid[1]+" enviada.",
                                               Toast.LENGTH_LONG).show();
                              }
                          });
                         fileInputStream.close();
                         dos.flush();
                         dos.close();
                         return "3";
                     }
                     else if (serverResponseCode == 200 && !retServ.startsWith("0")){
                    	 
                    	 //runOnUiThread(new Runnable(){
                    		 //public void run(){
                    			 dialog.dismiss();
                    			 //alert.show();
                    			 
                    		//}
                    	// }
                    	 //);
                         fileInputStream.close();
                         dos.flush();
                         dos.close();
                    	 return "2";
                     }
                      
                     //close the streams //
                     fileInputStream.close();
                     dos.flush();
                     dos.close();
                       
                } catch (MalformedURLException ex) {
                     
                    dialog.dismiss(); 
                    ex.printStackTrace();
                     
                    runOnUiThread(new Runnable() {
                        public void run() {
                            //messageText.setText("MalformedURLException Exception : check script url.");
                            Toast.makeText(getApplicationContext(), "Cidade Limpa: Servidor não encontrado.",
                                                                Toast.LENGTH_SHORT).show();
                        }
                    });
                     
                    Log.e("Upload file to server", "error: " + ex.getMessage(), ex); 
                } catch (IOException e) {
                     
                    dialog.dismiss(); 
                    e.printStackTrace();
                     
                    runOnUiThread(new Runnable() {
                        public void run() {
                            //messageText.setText("Sem Conexão");
                            Toast.makeText(getApplicationContext(), "Cidade Limpa: Problemas na Conexão",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                    Log.e("Upload file Exception", "Exception : "
                                                     + e.getMessage(), e);
                    return "0";
                } catch (Exception e) {
                    
                   dialog.dismiss(); 
                   e.printStackTrace();
                    
                   runOnUiThread(new Runnable() {
                       public void run() {
                           //messageText.setText("Sem Conexão");
                           Toast.makeText(getApplicationContext(), "Cidade Limpa: Problemas na Execução",
                                   Toast.LENGTH_LONG).show();
                       }
                   });
                   Log.e("Upload file Exception", "Exception : "
                                                    + e.getMessage(), e);
                   return "0";
               }
                dialog.dismiss();      
                return "0";
                 
             } // End else block
           }//Close method
    }
	
	public class DetectGPS2 extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent)
		{
		        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED"))
		        {
		        	gerenteLoc = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
		        	if (gerenteLoc.isProviderEnabled(LocationManager.GPS_PROVIDER )) {
		    			gerenteLoc.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 15, locObservador);
		    	        ImageView infoGPS = (ImageView) findViewById(R.id.infoGPS);
		    	        infoGPS.setImageResource(R.mipmap.imggps2);
		    	    }
		    		else{
		    			ImageView infoGPS = (ImageView) findViewById(R.id.infoGPS);
		    	        infoGPS.setImageResource(R.mipmap.gpsimg);
		    		} 
		        }
		}
	}
}
