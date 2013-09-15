package com.example.ndndroid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.example.ndndroid.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class NDNLDC_Control extends Activity {
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = true;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_ndnldc__control);

		final View controlsView = findViewById(R.id.fullscreen_content_controls);
		final View contentView = findViewById(R.id.fullscreen_content);

		// Set up an instance of SystemUiHider to control the system UI for
		// this activity.
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
		final Button createFaceButton = (Button) findViewById(R.id.button1);
		final EditText mac = (EditText) findViewById(R.id.editText2);
		final EditText prefix = (EditText) findViewById(R.id.editText1);
		createFaceButton.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				String text=null;
				Process p, p2, p3;  
	               try {  
	                  // Perform su to get root privileges
	                  //p = Runtime.getRuntime().exec("/system/bin/chmod 777 /data/data/com.example.ndndroid/ndnld");
	                  //p.waitFor();
	                  
	                  //p = Runtime.getRuntime().exec("/system/bin/chmod 777 /data/data/com.example.ndndroid/ndnldc");
	                  //p.waitFor();
	                  text = mac.getText().toString();
	                  p = Runtime.getRuntime().exec(new String[]{"/data/data/com.example.ndndroid/ndnldc", "-c", "-p", "ether", "-h" , text , "-i" , "wlan0"});
	                  

	                  BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                      
	                  //p3 = Runtime.getRuntime().exec(new String[]{"ps","|", "grep","ndnld"});
	                  BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
	                  // read the output from the command
	                  String s;
	                  System.out.println("Here is the standard output of the command:\n");
	                 // boolean found = false;
	                  while ((s = stdError.readLine()) != null ) {
	                	  //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
	                	  
	                	  //if (s.contains("/data/data/com.example.ndndroid/ndnld")) {
	                		  Toast.makeText(getApplicationContext(), "Error : " + s, Toast.LENGTH_LONG).show(); 
	                	      //found = true;
	                	      //break;
	                	  //}
	                	  try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	                  }
	                  
	                  s = stdInput.readLine();
	                  
	                  
	                	  
	                  Integer faceNum = Integer.parseInt(s);
	                  p = Runtime.getRuntime().exec(new String[]{"/data/data/com.example.ndndroid/ndnldc", "-r", "-f", faceNum.toString(), "-n", prefix.getText().toString()});
	                  stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));    
	                  s = stdInput.readLine();
	                  Toast.makeText(getApplicationContext(), "Result " + s, Toast.LENGTH_LONG).show();
	                  
	                  
	                 
	               } catch (IOException e) {  
	                  // TODO Code to run in input/output exception  
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
