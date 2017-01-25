package com.fourgreenone.cidadelimpa;


import java.io.FileNotFoundException;
import java.io.IOException;
import android.app.Activity;
import android.content.Context;

public class PersistirDados {
	static void salvarPrefs (Context act) throws IOException{
//		String FILENAME = "clprefs";
//		SharedPreferences prefs;
//		int numDen;
//		
//		prefs = act.getSharedPreferences(act.getString(R.string.cl_key_shared_prefs), Context.MODE_PRIVATE);
//	
//		numDen = prefs.getInt("NumDen", 0);
//		
//		FileOutputStream fos;
//		try {
//			BufferedWriter bw;
//			fos = act.openFileOutput(FILENAME, Context.MODE_PRIVATE);
//			bw = new BufferedWriter (new OutputStreamWriter(fos));
//			
//			bw.write(prefs.getString("gmailUserID", "errorUser"));
//			bw.newLine();
//			bw.write(numDen + "");
//			bw.newLine();
//			
//			for (int i = 0; i < numDen; i++){
//				bw.write(prefs.getString("DenunId"+i, ""));
//				bw.newLine();
//				bw.write(prefs.getString("DenunData"+i, ""));
//				bw.newLine();
//				bw.write(prefs.getString("DenunEnd"+i, ""));
//				bw.newLine();
//				bw.write(prefs.getString("DenunFoto"+i, ""));
//				bw.newLine();
//				bw.write(prefs.getString("DenunVol"+i, ""));
//				bw.newLine();
//				bw.write(prefs.getString("DenunClasse"+i, ""));
//				bw.newLine();
//			}
//			
//			fos.close();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	static void recuperarDados(Activity act) throws FileNotFoundException{
//		String FILENAME = "clprefs";
//		SharedPreferences prefs;
//		SharedPreferences.Editor ed;
//		int numDen;
//		
//		prefs = act.getSharedPreferences(act.getString(R.string.cl_key_shared_prefs), Context.MODE_PRIVATE);
//		
//		ed = prefs.edit();
//		
//		BufferedReader br;
//		FileInputStream fis = act.openFileInput(FILENAME);
//		
//		br = new BufferedReader(new InputStreamReader(fis));
//		
//		try {
//			ed.putString("gmailUserID", br.readLine());
//			numDen = Integer.parseInt(br.readLine());
//			ed.putString("NumDen", numDen+"");
//			
//			for (int i = 0; i < numDen; i++){
//				ed.putString("DenunId" + i, br.readLine());
//				ed.putString("DenunData" + i, br.readLine());
//				ed.putString("DenunEnd" + i, br.readLine());
//				ed.putString("DenunFoto" + i, br.readLine());
//				ed.putString("DenunVol" + i, br.readLine());
//				ed.putString("DenunClasse" + i, br.readLine());
//			}
//			
//			ed.commit();
//			
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}
}
