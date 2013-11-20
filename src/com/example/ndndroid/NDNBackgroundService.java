package com.example.ndndroid;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;


public class NDNBackgroundService extends Service{

	private static final String TAG = NDNBackgroundService.class.getSimpleName();
	private ArrayList<FaceInfo> faceTable = new ArrayList<FaceInfo>();
	Object Lock = new Object();

	@Override
	public IBinder onBind(Intent arg0) {

		return apiEndpoint;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "Creating NDN Background Service");

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "Destroying NDN Background Service");

	}
	public String createNewInterface(String mac, String prefix) {
		String text=null;
		String s = new String(" ");
		Process p, p2, p3;
		try {  


			p = Runtime.getRuntime().exec(new String[]{"/data/data/com.example.ndndroid/ndnldc", "-c", "-p", "ether", "-h" , text , "-i" , "wlan0"});
			p.waitFor();

			BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			// read the output from the command

			String error = new String();
			System.out.println("Here is the standard output of the command:\n");
			// boolean found = false;
			while ((s = stdError.readLine()) != null ) {
				//Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();

				//if (s.contains("/data/data/com.example.ndndroid/ndnld")) {
				error.concat(s);
				//Toast.makeText(getApplicationContext(), "Error : " + s, Toast.LENGTH_LONG).show(); 
				//found = true;
				//break;
				//}

			}

			s = stdInput.readLine();

			Integer faceNum = Integer.parseInt(s);
			p = Runtime.getRuntime().exec(new String[]{"/data/data/com.example.ndndroid/ndnldc", "-r", "-f", faceNum.toString(), "-n", prefix});
			stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));    
			s = stdInput.readLine();
			//Toast.makeText(getApplicationContext(), "Result " + s, Toast.LENGTH_LONG).show();



		} catch (IOException e) {  
			// TODO Code to run in input/output exception  
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s;
	}

	public String checkCommandOutput() {
		String output = new String();
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader("/cache/command_output.txt"));

			String line;
			while ((line = br.readLine()) != null) {
				output.concat(line + "\n");
			}
			br.close();
		} catch (FileNotFoundException e) {

			return null;
		} catch (IOException e) {

			return output;
		}
		return output;
	}

	public boolean checkNDNStatus() {
		Process p = null;
		String s;
		boolean found = false;


		try {
			p = Runtime.getRuntime().exec(new String[]{"ps","|", "grep","ndnld"});
			p.waitFor();

			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((s = stdInput.readLine()) != null ) {
				//Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();

				if (s.contains("ndnld")) {
					//Toast.makeText(getApplicationContext(), "process started successfully", Toast.LENGTH_LONG).show(); 
					found = true;
					break;
				}
				//Thread.sleep(1000);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return found;

	}
	public String startNDN(boolean checkCurrentStatus) {
		Process p, p2;  
		String error = null;
		try {

			if (checkNDNStatus() && checkCurrentStatus) {
				return null;
			}

			Log.i(TAG, "Starting NDNLD");
			// Perform su to get root privileges
			p = Runtime.getRuntime().exec("/system/bin/chmod 777 /data/data/com.example.ndndroid/run.sh");
			p.waitFor();

			p2 = Runtime.getRuntime().exec(new String[]{"su","-c", "/data/data/com.example.ndndroid/run.sh"});
			p2.waitFor();
			Log.i(TAG, "Checking NDN Status"); 
			BufferedReader stdError = new BufferedReader(new InputStreamReader(p2.getErrorStream()));

			boolean found = checkNDNStatus();
			String s;

			if (found == false) {
				error = new String(" "); 
				Log.i(TAG, "Checking NDN Status : False"); 
				//Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
				while ((s = stdError.readLine()) != null) {
					error.concat(s);                		  
				}
			}


		} catch (IOException e) {  
			// TODO Code to run in input/output exception  
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		Log.i(TAG, "Returning error"); 
		return error;
	}

	private NDNBackgroundServiceApi.Stub apiEndpoint = new NDNBackgroundServiceApi.Stub() {

		@Override
		public String startNDNBackgroundService() throws RemoteException {
			return startNDN(true); 

		}

		public String addNewConnection(String mac, String prefix) throws RemoteException {
			String result = null;
			result = createNewInterface(mac, prefix);
			if (result == null) {
				synchronized(Lock) {
					addFace(mac, prefix);
				}

				return null;
			}
			else {
				return result;
			}

		}


		public boolean resetServices() {
			return resetNDNService();
		}

	};
	
	private void addFace(String mac, String prefix) {
		faceTable.add(new FaceInfo(mac, prefix));
		
	}

	private boolean resetNDNService() {
		boolean ret = true;
		
		startNDN(false);
		synchronized(Lock) {
			for (FaceInfo temp : faceTable) {
                if (createNewInterface(temp.mac, temp.prefix) != null) {
                	ret = false;
                }
			}
		}
		return ret;
	}

}
