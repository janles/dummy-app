package com.example.neuegruppeerstellen;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;


public class Startscreen extends Activity{
	private static final float ROTATE_FROM = 0.0f;
	private static final float ROTATE_TO = -10.0f * 360.0f;// 3.141592654f *
															// 32.0f;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//set up notitle 
        requestWindowFeature(Window.FEATURE_NO_TITLE);  
        //set up full screen
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
//                WindowManager.LayoutParams.FLAG_FULLSCREEN); 
		
		setContentView(R.layout.startscreen);
		
		
		ImageView favicon = (ImageView) findViewById(R.id.favicon);

		RotateAnimation r; // = new RotateAnimation(ROTATE_FROM, ROTATE_TO);
		r = new RotateAnimation(ROTATE_FROM, ROTATE_TO, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		r.setDuration((long) 2 * 15000);
		r.setRepeatCount(0);
		favicon.startAnimation(r);
		
		new Handler().postDelayed(new Runnable() {
	        @Override
	        public void run() {
	        	startActivity(new Intent(Startscreen.this, MeineGruppen.class));
	        	//Toast.makeText(getApplicationContext(), "msg msg", Toast.LENGTH_LONG).show();
	            finish();
	        }
	    }, 5000);
	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
