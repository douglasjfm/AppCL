package com.fourgreenone.cidadelimpa;

import java.util.ArrayList;

import com.koushikdutta.ion.Ion;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


class ArrayAdaptador extends ArrayAdapter<DenunciaProxima>{
	
	Ion ion;
	Activity act;
	Object[] objs;
	int viewld;
	String serverFotos;// = "http://douglasjfm.byethost22.com/deposito/";
	public ArrayAdaptador(Context context, int resource, ArrayList<DenunciaProxima> objects) {
		super(context, resource, objects);
		String serverUrl;
		act = (Activity) context;
		objs = objects.toArray();
		this.viewld = resource;
		serverUrl = context.getSharedPreferences(context.getString(R.string.cl_key_shared_prefs), Context.MODE_PRIVATE)
				.getString(context.getString(R.string.cl_servidor_id),"");
		serverFotos = "http://" + serverUrl + "/deposito/";
	}
	
	@SuppressLint({ "ViewHolder", "InflateParams" })
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		LayoutInflater inflater = act.getLayoutInflater();
		View item = inflater.inflate(viewld, null);
		ImageView imageView = (ImageView) item.findViewById(R.id.lista_item_img_prox);
		//imageView.setImageBitmap((Bitmap)((DenunciaProxima) objs[position]).foto);
		
		Ion.with(imageView)
		.placeholder(R.drawable.common_plus_signin_btn_text_light_focused)
		.error(R.drawable.abc_spinner_mtrl_am_alpha)
		.load(serverFotos + (( (DenunciaProxima) objs[position]).foto));

		imageView.setContentDescription(serverFotos + (( (DenunciaProxima) objs[position]).foto));
		
		TextView txt = (TextView) item.findViewById(R.id.lista_txt_prox);
		txt.setText("Denúncia " + ((DenunciaProxima) objs[position]).id);
		TextView txt2 = (TextView) item.findViewById(R.id.proxEndereco);
		txt2.setText(((DenunciaProxima) objs[position]).data + " " + ((DenunciaProxima) objs[position]).endereco);
		return item;
	}
	
}
