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
	private Sensor mRotationVector;
	
	private static final String SAMPLES_DIR = Environment.getExternalStorageDirectory() + File.separator + "magnetic_samples";	
	private static final String TAG = "Recording Magnetic Samples";
	
	private String location = "0";
	private Integer time= 3000;
	private long mLastMagneticFieldTimestamp;
	private long mLastRVTimestamp;
    
    private long timestamp;
    private float[] rv = {0.0f,0.0f,0.0f};
    private long old_time_stamp ;
    private long new_time_stamp ;     
  
    private float[] mRotationMatrix = {0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f,0.0f};
    
	private FileWriter mCompassLogFileWriter;
	private FileWriter mRVLogFileWriter;
	
	private boolean mIsSampling = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record_samples);
		Intent intent = getIntent();
		
		location = (String) intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
		time = (Integer) intent.getIntExtra(MainActivity.SAMPLE_TIME,3000);
		
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        
		// Get reference to Magnetometer
		if (null == (mCompass = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)))
			finish();
	
		if (null == (mRotationVector = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)))
			finish();
	
		old_time_stamp = System.currentTimeMillis();
		this.startSampling();
		}
	
		// Register listener
		@Override
		protected void onResume() {
			super.onResume();
			mSensorManager.registerListener(this, mCompass,SensorManager.SENSOR_DELAY_NORMAL);			
			mSensorManager.registerListener(this, mRotationVector,SensorManager.SENSOR_DELAY_NORMAL);
		}
		
		// Unregister listener
		@Override
		protected void onPause() {
			super.onPause();
			 //System.out.println("PAUSE");
			finishRecordSamples();
		}        
		
		protected void onStop()
		{   super.onStop();
		    //System.out.println("STOP");
		    finishRecordSamples();	
		}
		
		protected void onDestroy()
		{   super.onDestroy();
			//System.out.println("DESTROY");	
		    finishRecordSamples();
		}
		                                                                    
		// Process new reading                                              
		@Override                                                           
		public void onSensorChanged(SensorEvent event) {                    
			synchronized (this) {
			long deltaT = event.timestamp;                                                               
			if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) 
			{     
				if (mLastMagneticFieldTimestamp == 0) 
					{
		    			mLastMagneticFieldTimestamp = event.timestamp;
					}
			    timestamp = mLastMagneticFieldTimestamp;
				deltaT -= mLastMagneticFieldTimestamp;
				mLastMagneticFieldTimestamp = event.timestamp;	
				if (this.isSampling()) 
					{ try {
							mCompassLogFileWriter.write("" + timestamp + "," + deltaT + "," + event.values[0] +  ","  + event.values[1] + ","  + event.values[2] + "\n");
							} 
						catch (IOException e) 
							{
							Log.e(TAG, "Log file write for magnetometer failed!!!\n", e);
							e.printStackTrace();
							throw new RuntimeException(e);
							}
						new_time_stamp = System.currentTimeMillis();
					}
			}
		    if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) 
		    {     
			    if (mLastRVTimestamp == 0) 
			    	{
			    		mLastRVTimestamp = event.timestamp;
					}
				timestamp = mLastRVTimestamp;
				deltaT -= mLastRVTimestamp;
				mLastRVTimestamp = event.timestamp;
				 rv[0] = event.values[0];
				 rv[1] = event.values[1];
			     rv[2] = event.values[2];
			   //  rv[3] = event.values[3];
			     SensorManager.getRotationMatrixFromVector(mRotationMatrix, rv);
			    if (this.isSampling()) 
			    	{  
						try {   
								mRVLogFileWriter.write("" + timestamp + "," + deltaT + "," + rv[0] +  ","  + rv[1] + ","  + rv[2] + "," + 
						                               mRotationMatrix[0] +  ","  + mRotationMatrix[1] + ","  + mRotationMatrix[2] + "," +  mRotationMatrix[3] +  "," +
										               mRotationMatrix[4] +  ","  + mRotationMatrix[5] + ","  + mRotationMatrix[6] + "," +  mRotationMatrix[7] +  "," + 
						                               mRotationMatrix[8] +  ","  + mRotationMatrix[9] + ","  + mRotationMatrix[10] + "," +  mRotationMatrix[11] +  "," + 
										               mRotationMatrix[12] +  ","  + mRotationMatrix[13] + ","  + mRotationMatrix[14] + "," +  mRotationMatrix[15] + "\n");
							} 
						catch (IOException e) 
							{
								Log.e(TAG, "Log file write for Rotation Vector failed!!!\n", e);
								e.printStackTrace();
								throw new RuntimeException(e);
							}
						new_time_stamp = System.currentTimeMillis();
					}
		    }
		    System.out.println("Here");
		    if(this.isSampling())
			{ if (new_time_stamp - old_time_stamp > time)
		  		{  this.stopSampling();
		  		   finishRecordSamples();
		  		   finish();
		  		}
			}
		  }	
		}		
	  public void startSampling()
	        {	try {
					String r = (String) (DateFormat.format("yyyy-MM-dd-hh-mm-ss", new java.util.Date()) );
					String logFileBaseName = "Loc_" + location + "_" + r;
					mCompassLogFileWriter = new FileWriter(new File(SAMPLES_DIR, logFileBaseName + ".magnet.csv"));
					mRVLogFileWriter = new FileWriter(new File(SAMPLES_DIR, logFileBaseName + ".RV.csv"));
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
					mRVLogFileWriter.flush();
					mRVLogFileWriter.close();
				  
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
		   //System.out.println("unRegister");
		   mSensorManager.unregisterListener(this);
	    }
		@Override                                                           
		public void onAccuracyChanged(Sensor sensor, int accuracy) {        
			// NA                                                           
		}                                                                   	

}
