package com.example.ndndroid;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import android.os.IBinder;

import com.example.ndndroid.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.res.AssetManager;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class Start_screen extends Activity {
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = true;
	
	private String ndnStatus;

	private NDNBackgroundServiceApi api; 

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise,
	 * will show the system UI visibility upon interaction.
	 */
	private static final boolean TOGGLE_ON_CLICK = true;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	private SystemUiHider mSystemUiHider;

	private static final String TAG = NDNBackgroundService.class.getSimpleName();
	
	private final Context context = this;

	private ServiceConnection serviceConnection; 

	/* Routine to copy file */
	private void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while((read = in.read(buffer)) != -1){
			out.write(buffer, 0, read);
		}
	}

	private class recoupProcess extends Thread {
		Process p2, p3;
		public void run() {
			try {
				p3 = Runtime.getRuntime().exec(new String[]{"ps","|", "grep","ndnld"});
				BufferedReader stdInput = new BufferedReader(new InputStreamReader(p3.getInputStream()));
				// read the output from the command
				String s;
				//System.out.println("Here is the standard output of the command:\n");
				boolean found = false;

				while ((s = stdInput.readLine()) != null ) {
					//Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();

					if (s.contains("/data/data/com.example.ndndroid/ndnld")) {
						Toast.makeText(getApplicationContext(), "process tested succesfully", Toast.LENGTH_LONG).show(); 
						found = true;
						break;
					}
				}
				//TODO: Ajith: Add sleep here.
				//Thread.sleep(1000);
				//System.out.println(s);
				// read any errors from the attempted command
				if (found == false) {
					p2 = Runtime.getRuntime().exec(new String[]{"su","-c", "/data/data/com.example.ndndroid/run.sh"});

					BufferedReader stdError = new BufferedReader(new InputStreamReader(p2.getErrorStream()));

					stdInput = new BufferedReader(new InputStreamReader(p2.getInputStream()));
					while ((s = stdError.readLine()) != null) {
						Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();                		  
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_start_screen);

		//startService(new Intent(NDNBackgroundService.class.getName()));


		final View controlsView = findViewById(R.id.fullscreen_content_controls);
		final View contentView = findViewById(R.id.fullscreen_content);
		((TextView)contentView).setText("NDN Control"); 


		AssetManager mngr = getAssets();
		InputStream in = null;
		OutputStream out = null;

		//String [] files = null;



		try {
			in = mngr.open("ndnldexe");
			File outFile = new File("/data/data/com.example.ndndroid/", "ndnld");
			out = new FileOutputStream(outFile);
			copyFile(in, out);
			in.close();
			out.flush();
			out.close();
			in = mngr.open("ndnldcexe");
			outFile = new File("/data/data/com.example.ndndroid/", "ndnldc");
			out = new FileOutputStream(outFile);
			copyFile(in, out);
			in.close();
			out.flush();
			out.close();
			in = null;
			out = null;
			in = mngr.open("runsh");
			outFile = new File("/data/data/com.example.ndndroid/", "run.sh");
			out = new FileOutputStream(outFile);
			copyFile(in, out);
			in.close();
			out.flush();
			out.close();
			in = null;
			out = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		mSystemUiHider = SystemUiHider.getInstance(this, contentView,
				HIDER_FLAGS);
		mSystemUiHider.setup();
		mSystemUiHider
		.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
			// Cached values.
			int mControlsHeight;
			int mShortAnimTime;

			@Override
			@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
			public void onVisibilityChange(boolean visible) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
					// If the ViewPropertyAnimator API is available
					// (Honeycomb MR2 and later), use it to animate the
					// in-layout UI controls at the bottom of the
					// screen.
					if (mControlsHeight == 0) {
						mControlsHeight = controlsView.getHeight();
					}
					if (mShortAnimTime == 0) {
						mShortAnimTime = getResources().getInteger(
								android.R.integer.config_shortAnimTime);
					}
					controlsView
					.animate()
					.translationY(visible ? 0 : mControlsHeight)
					.setDuration(mShortAnimTime);
				} else {
					// If the ViewPropertyAnimator APIs aren't
					// available, simply show or hide the in-layout UI
					// controls.
					controlsView.setVisibility(visible ? View.VISIBLE
							: View.GONE);
				}

				if (visible && AUTO_HIDE) {
					// Schedule a hide().
					delayedHide(AUTO_HIDE_DELAY_MILLIS);
				}
			}
		});

		// Set up the user interaction to manually show or hide the system UI.
		contentView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (TOGGLE_ON_CLICK) {
					mSystemUiHider.toggle();
				} else {
					mSystemUiHider.show();
				}
			}
		});


		//Change button text
		final Button startButton = (Button) findViewById(R.id.dummy_button);
		startButton.setText("Start NDNLD");
		startButton.setOnClickListener(new Button.OnClickListener() {
			@SuppressWarnings("deprecation")
			public void onClick(View v) {
				//Process p, p1, p2, p3;  
				try {


					Intent intent = new Intent(NDNBackgroundService.class.getName());

					// start the service explicitly.
					// otherwise it will only run while the IPC connection is up.       
					startService(intent);
					serviceConnection = new ServiceConnection() {
						@SuppressWarnings("deprecation")
						@Override
						public void onServiceConnected(ComponentName name, IBinder service) {
							Log.i(TAG, "Service connection established");


							api = NDNBackgroundServiceApi.Stub.asInterface(service);
							try {
								ndnStatus = api.startNDNBackgroundService();
								if (ndnStatus == null) {

									String result = null;
									Toast.makeText(getApplicationContext(), "NDNLD running" , Toast.LENGTH_LONG).show();
									Intent myIntent = new Intent(Start_screen.this, NDNLDC_Control.class);
									Start_screen.this.startActivity(myIntent);
				                    
									Log.i(TAG, "Activity created, result = " + result);
								} else {
									// Creating alert Dialog with one Button
									 
						            AlertDialog alertDialog1 = new AlertDialog.Builder(
						            		Start_screen.this).create();
						 
						            // Setting Dialog Title
						            alertDialog1.setTitle("Failed to start ndnld");
						 
						            // Setting Dialog Message
						            alertDialog1.setMessage("Make sure ccnx is running " + ndnStatus);
						 
						            		 
						            // Setting OK Button
						            alertDialog1.setButton("OK", new DialogInterface.OnClickListener() {
						            	 
						                public void onClick(DialogInterface dialog, int which) {
						                    // Write your code here to execute after dialog
						                    // closed
						                    
						                }
						            });
						 
						            // Showing Alert Message
						            alertDialog1.show();
								}
							} catch (RemoteException e) {
								Log.e(TAG, "Failed to connect to background service", e);
							}


						}

						@Override
						public void onServiceDisconnected(ComponentName name) {
							Log.i(TAG, "Service connection closed");     
						}
					};
					bindService(intent, serviceConnection, 0);
					


					// Perform su to get root privileges
					/*p = Runtime.getRuntime().exec("/system/bin/chmod 777 /data/data/com.example.ndndroid/run.sh");
	                  p.waitFor();

	                  p2 = Runtime.getRuntime().exec(new String[]{"su","-c", "/data/data/com.example.ndndroid/run.sh"});
	                  //p2.waitFor(); // Buggy? Ajith: Check on phone.

	                  BufferedReader stdError = new BufferedReader(new InputStreamReader(p2.getErrorStream()));

	                  p3 = Runtime.getRuntime().exec(new String[]{"ps","|", "grep","ndnld"});
	                  //p3.waitFor(); // Buggy? Ajith: Check on phone.

	                  BufferedReader stdInput = new BufferedReader(new InputStreamReader(p3.getInputStream()));
	                  // read the output from the command
	                  //System.out.println("Here is the standard output of the command:\n");
	                  boolean found = false;

	                  String s;
	                  while ((s = stdInput.readLine()) != null ) {
	                	  //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();

	                	  if (s.contains("/data/data/com.example.ndndroid/ndnld")) {
	                		  Toast.makeText(getApplicationContext(), "process started succesfully", Toast.LENGTH_LONG).show(); 
	                	      found = true;
	                	      break;
	                	  }
	                	  //Thread.sleep(1000);
	                  }
	                  //System.out.println(s);
	                  // read any errors from the attempted command
	                  if (found == false) {
	                	  stdInput = new BufferedReader(new InputStreamReader(p2.getInputStream()));
	                	  Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
	                	  while ((s = stdError.readLine()) != null) {
		                	  Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();                		  
	                	  }
	                  }*/

					/*System.out.println("Here is the standard error of the command (if any):\n");
	                  while ((s = stdError.readLine()) != null) {
	                	  Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
	                	  System.out.println(s);
	                  }*/

					/*InputStream stdin = p.getInputStream();
	                  InputStreamReader isr = new InputStreamReader(stdin);
	                  BufferedReader br = new BufferedReader(isr);

	                  String line = null;
	                  System.out.println("<OUTPUT>");

	                  //while ( (line = br.readLine()) != null)
	                  //     Log.e("ERROR", line);

	                  System.out.println("</OUTPUT>");
	                  //Integer exitVal = p2.waitFor();
	                  //Log.e("ERROR", exitVal.toString());*/

				} finally {

				}

			}
		});

		// Upon interacting with UI controls, delay any scheduled hide()
		// operations to prevent the jarring behavior of controls going away
		// while interacting with the UI.
		findViewById(R.id.dummy_button).setOnTouchListener(
				mDelayHideTouchListener);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		delayedHide(100);
	}

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			return false;
		}
	};

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
		}
	};

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}
}
