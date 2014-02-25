package in.ernet.mangentic_field_samples;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class RecordSamples extends Activity implements SensorEventListener{

	private SensorManager mSensorManager;
	private Sensor mCompass;
	
	private static final String SAMPLES_DIR = Environment.getExternalStorageDirectory() + File.separator + "magnetic_samples";	
	private static final String TAG = "Recording Magnetic Samples";
	
	private String location = "0";
    private long mLastMagneticFieldTimestamp;
    private long timestamp;
    private long old_time_stamp ;
    private long new_time_stamp ; 
    
    
	private FileWriter mCompassLogFileWriter;
	private boolean mIsSampling = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record_samples);
		Intent intent = getIntent();
		
		location = (String) intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        
		// Get reference to Magnetometer
		if (null == (mCompass = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)))
			finish();
	
		old_time_stamp = System.currentTimeMillis();
		this.startSampling();
		}
	
		// Register listener
		@Override
		protected void onResume() {
			super.onResume();
			mSensorManager.registerListener(this, mCompass,SensorManager.SENSOR_DELAY_NORMAL);
			
		}
		
		// Unregister listener
		@Override
		protected void onPause() {
			super.onPause();
			 System.out.println("PAUSE");
			finishRecordSamples();
		}        
		
		protected void onStop()
		{   super.onStop();
		    System.out.println("STOP");
		    finishRecordSamples();	
		}
		
		protected void onDestroy()
		{   super.onDestroy();
			System.out.println("DESTROY");	
		    finishRecordSamples();
		}
		                                                                    
		// Process new reading                                              
		@Override                                                           
		public void onSensorChanged(SensorEvent event) {                    
			synchronized (mCompassLogFileWriter) {
			long deltaT = event.timestamp;                                                               
			if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {     
		                                                                    
				if (mLastMagneticFieldTimestamp == 0) {
		    		mLastMagneticFieldTimestamp = event.timestamp;
				}
			    timestamp = mLastMagneticFieldTimestamp;
				deltaT -= mLastMagneticFieldTimestamp;
			  	mLastMagneticFieldTimestamp = event.timestamp;	
			    
			  	if(this.isSampling()) {
					try {
						mCompassLogFileWriter.write("" + timestamp + "," + deltaT + "," + event.values[0] +  ","  + event.values[1] + ","  + event.values[2] + "\n");
						} 
					catch (IOException e) {
						Log.e(TAG, "Log file write for magnetometer failed!!!\n", e);
						e.printStackTrace();
						throw new RuntimeException(e);
						}
					new_time_stamp = System.currentTimeMillis();
					if (new_time_stamp - old_time_stamp > 5*1000)
				  		{  this.stopSampling();
				  		   finishRecordSamples();
				  		   System.out.println("TIME_OUT");
				  		   finish();
				  		}
				}	
			 }	
		   }
		}
		
	  public void startSampling()
	        {	try {
					String r = (String) (DateFormat.format("yyyy-MM-dd-hh-mm-ss", new java.util.Date()) );
					String logFileBaseName = "Loc_" + location + "_" + r;
					mCompassLogFileWriter = new FileWriter(new File(SAMPLES_DIR, logFileBaseName + ".magnet.csv"));
				  } catch (IOException e) {
					Log.e(TAG, "Creating and opening log files failed!", e);
					e.printStackTrace();
					throw new RuntimeException(e);
				}                                                                                                                             
			    mIsSampling = true;
	        }    
	   
	   public void stopSampling()
	    	{   mIsSampling = false;
			   try {
					mCompassLogFileWriter.flush();
					mCompassLogFileWriter.close();
				   } catch (IOException e) {
					Log.e(TAG, "Flushing and closing log files failed!" , e);
					e.printStackTrace();
					throw new RuntimeException(e);
				}
	    	}
	    
	   public boolean isSampling()
	    	{  	return mIsSampling; 
	    	}
	   
	  public void finishRecordSamples() {
		   System.out.println("unRegister");
		   mSensorManager.unregisterListener(this);
	    }
		@Override                                                           
		public void onAccuracyChanged(Sensor sensor, int accuracy) {        
			// NA                                                           
		}                                                                   	

}
