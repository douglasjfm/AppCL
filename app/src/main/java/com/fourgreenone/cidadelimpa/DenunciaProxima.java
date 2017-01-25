package com.fourgreenone.cidadelimpa;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

class DenunciaProxima {
	public String foto;
	public String endereco;
	public String cc;
	public int id;
	public String data;
	
	DenunciaProxima(String imgurl, String e, int i, String d, String latlng){
		foto = imgurl;
		endereco = e;
		id = i;
		data = d;
		cc = latlng;
		data();
		//endereco();
	}
	
	public void endereco (){
		new EnderecoAPI().execute(endereco);
	}
	public void enderecoNovo(String ret){
		this.endereco = ret;
	}
	public void data(){
		String dia,mes,ano;
		dia = data.substring(6, 8);
		mes = data.substring(4, 6);
		ano = data.substring(0, 4);
		
		data = dia+"/"+mes+"/"+ano;
	}
	
	class EnderecoAPI extends AsyncTask<String, Void, String>{

		public String googleGEOAPI = "http://maps.googleapis.com/maps/api/geocode/json?latlng=";
		public String ret = "";
		
		@Override
		protected void onPostExecute(String r){
			if (ret != "")enderecoNovo(ret);
		}
		
		@Override
		protected String doInBackground(String... params) {
			try {
				URL url = new URL(googleGEOAPI + params[0] + "&sensor=true");
				BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
				JSONObject json;
				String strTemp = "";
				String response = "";
				while (null != (strTemp = br.readLine())) {
					response += strTemp;
				}
				json = new JSONObject (response);
				JSONArray arr = json.getJSONArray("results");
				ret = arr.getJSONObject(0).getString("formatted_address");
			} catch (final IOException execao) {
				return null;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "";
		}
		
	}
}
