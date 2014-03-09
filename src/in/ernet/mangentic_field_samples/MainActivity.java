package in.ernet.mangentic_field_samples;

import in.ernet.mangentic_field_samples.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
    
	public final static String EXTRA_MESSAGE = "in.ernet.magnetic_field_samples.MESSAGE";
	public final static String SAMPLE_TIME = "in.ernet.magnetic_field_samples.TIME_SAMPLE";
	
	public static int loc = 0;
	public static int time = 3000;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void RecordSamplesMethod(View view){
		Intent intent = new Intent(this, RecordSamples.class);
		EditText editText1 = (EditText) findViewById(R.id.edit_path);
		String mes = editText1.getText().toString();
		EditText editText2 = (EditText) findViewById(R.id.edit_direction);		
		String sage = editText2.getText().toString();
		String message = sage+mes+ "_" + loc;
		EditText time_sample = (EditText) findViewById(R.id.sample_time);
		time = (int) Integer.valueOf(time_sample.getText().toString())*1000;
		
		intent.putExtra(EXTRA_MESSAGE, message);
		intent.putExtra(SAMPLE_TIME, time);
		
		startActivity(intent);
	}
	
	public void IncrementMethod(View view){
		loc += 1;
		TextView startx = (TextView) findViewById(R.id.location);  	
		startx.setText(Integer.toString(loc));		
	}
		
	public void DecrementMethod(View view){
		loc -= 1; 
		TextView startx = (TextView) findViewById(R.id.location);  	
		startx.setText(Integer.toString(loc));
	}	
}

