package es.ambimetrics.android.agenda;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

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
	
	/** Called when the user clicks the registrar button */
	public void registrarUser(View view) {
	    // Do something in response to button
		Intent intent = new Intent(this, RegistrarActivity.class);
		startActivity(intent);

	}/** Called when the user clicks the login button */
	public void loginUser(View view) {
	    // Do something in response to button
		Intent intent = new Intent(this, AgendaActivity.class);
		startActivity(intent);

	}

}
