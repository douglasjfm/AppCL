package com.fourgreenone.cidadelimpa;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ControleRecentes extends AppCompatActivity{
	
	@Override
	public void onCreate(Bundle b){
		super.onCreate(b);
		
		ActionBar barra = getSupportActionBar();
		barra.setDisplayShowHomeEnabled(true);
		barra.setIcon(R.mipmap.ic_launcher48);
		barra.setTitle("Cidade Limpa - Atividades Recentes");
		
		ArrayAdaptadorRecentes adaptador;
		ListView lista;
		InfoRecentes infos;
		
		setContentView(R.layout.listarecentes);
		
		infos = new InfoRecentes(this);
		
		lista = (ListView) findViewById(R.id.listarecentes);
		adaptador = new ArrayAdaptadorRecentes(this,R.layout.lista_item_recentes,infos.acts);
		lista.setAdapter(adaptador);
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
	
	public class ArrayAdaptadorRecentes extends ArrayAdapter<AtividadeRecente>{

		public Activity act;
		public int res;
		public AtividadeRecente[] objs;
		
		public ArrayAdaptadorRecentes(Context context, int resource, AtividadeRecente[] objects) {
			super(context, resource, objects);
			act = (Activity) context;
			res = resource;
			objs = objects;
		}
		
		@SuppressLint("ViewHolder")
		@Override
		public View getView(int position, View view, ViewGroup parent){
			
			LayoutInflater inflater = act.getLayoutInflater();
			View item = inflater.inflate(res, null);
			ImageView imageView = (ImageView) item.findViewById(R.id.lista_imagem_recentes);
			TextView desc = (TextView) item.findViewById(R.id.tvCity);
			TextView end = (TextView) item.findViewById(R.id.tvCondition);
			
			if (desc != null && end != null){
				desc.setText("Entulho "+getClasseById(objs[position].classe) + ", " + getVolumeById(objs[position].vol) + " porte");
				end.setText(objs[position].end + "");
			}
			
			if (imageView != null){
				getIconRecentes(objs[position].foto, imageView);
			}
			
			return item;
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
		
		void getIconRecentes (String fname, ImageView mImageView){
			// Get the dimensions of the View
		    int targetW = 60;
		    int targetH = 60;
		    //String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
		    // Get the dimensions of the bitmap
		    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		    bmOptions.inJustDecodeBounds = true;
		    BitmapFactory.decodeFile(fname, bmOptions);
		    int photoW = bmOptions.outWidth;
		    int photoH = bmOptions.outHeight;

		    // Determine how much to scale down the image
		    int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

		    // Decode the image file into a Bitmap sized to fill the View
		    bmOptions.inJustDecodeBounds = false;
		    bmOptions.inSampleSize = scaleFactor;
		    //bmOptions.inPurgeable = true;

		    Bitmap bitmap = BitmapFactory.decodeFile(fname, bmOptions);
		    mImageView.setImageBitmap(bitmap);
			}
		
	}
}
