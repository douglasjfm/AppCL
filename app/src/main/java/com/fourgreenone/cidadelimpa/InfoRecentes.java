package com.fourgreenone.cidadelimpa;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

public class InfoRecentes {
	
	public SharedPreferences prefs;
	public AtividadeRecente[] acts;
	
	public int q;
	
	InfoRecentes(Context c){
		prefs = c.getSharedPreferences(
		        c.getString(R.string.cl_key_shared_prefs), Context.MODE_PRIVATE);
		q = prefs.getInt("NumDen", 0);
		prefs.contains("NumDen");
		this.acts = null;
		if (q != 0){
		
			acts = new AtividadeRecente[q];
			
			for(int i=0;i < q; i++){
				acts[i] = new AtividadeRecente();
				acts[i].data = prefs.getString("DenunData"+i, "");
				acts[i].end = prefs.getString("DenunEnd"+i, "");
				acts[i].id = prefs.getString("DenunId"+i, "");
				acts[i].foto = prefs.getString("DenunFoto"+i, "");
				acts[i].vol = prefs.getString("DenunVol"+i, "");
				acts[i].classe = prefs.getString("DenunClasse"+i, "");
			}
		}
	}
	
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	static void insere (String id, String end, String vol, String classe, String foto, Context c){
		String data, dia, mes, ano;
		int index = 0;
		SharedPreferences p = c.getSharedPreferences(
		        c.getString(R.string.cl_key_shared_prefs), Context.MODE_PRIVATE);
		SharedPreferences.Editor ed;
		ed = p.edit();
		
		if (p.contains("NumDen")){
			index = p.getInt("NumDen", 0);
			ed.putInt("NumDen", index + 1);
		}
		else{
			ed.putInt("NumDen", 1);
			//ed.apply();
			//ed.commit();
		}
		
		//index ++;
		data = foto.split("_")[1];
		
		dia = data.substring(6, 8);
		mes = data.substring(4, 6);
		ano = data.substring(0, 4);
		
		data = dia+"/"+mes+"/"+ano;
		
		ed.putString("DenunId"+index, id);
		ed.putString("DenunData"+index, data);
		ed.putString("DenunEnd"+index, end);
		ed.putString("DenunFoto"+index, foto);
		ed.putString("DenunVol"+index, vol);
		ed.putString("DenunClasse"+index, classe);
		
		//ed.commit();
		ed.apply();
	}
}
