package es.ambimetrics.android.agenda;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;
import es.ambimetrics.android.agenda.contentprovider.MyAgendaContentProvider;
import es.ambimetrics.android.agenda.database.UsuarioTable;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		registrado();
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
		startActivityForResult(intent,1);

	}/** Called when the user clicks the login button */
	public void loginUser(View view) {
	    // Do something in response to button
		entrar();

	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		  if (requestCode == 1) {

		     if(resultCode == RESULT_OK){      
		         //String error=data.getStringExtra("result");
		        	 entrar();
		     }
		     if (resultCode == RESULT_CANCELED) {    
		         //Write your code if there's no result
		         Toast toast1 =
		                 Toast.makeText(getApplicationContext(),
		                 		"Error: El usuario ya esta registrado" , Toast.LENGTH_LONG);
		      
		             toast1.show();
		     }
		  }
		}//onActivityResult
	public void entrar() {

		Intent intent = new Intent(this, AgendaActivity.class);
		startActivity(intent);
		finish();

	}
	public void registrado() {
	}
}
