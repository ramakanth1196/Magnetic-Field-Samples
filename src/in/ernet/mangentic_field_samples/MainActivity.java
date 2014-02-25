package in.ernet.mangentic_field_samples;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {
    
	public final static String EXTRA_MESSAGE = "in.ernet.magnetic_field_samples.MESSAGE";
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
		EditText editText1 = (EditText) findViewById(R.id.edit_location);
		String mes = editText1.getText().toString();
		
		EditText editText2 = (EditText) findViewById(R.id.edit_direction);
		
		String sage = editText2.getText().toString();
		
		String message = mes + "_"  + sage;
		
		intent.putExtra(EXTRA_MESSAGE, message);
		startActivity(intent);
	}
	
}

