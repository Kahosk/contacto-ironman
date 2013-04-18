package es.ambimetrics.android.agenda;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import es.ambimetrics.android.agenda.contentprovider.MyAgendaContentProvider;
import es.ambimetrics.android.agenda.database.ContactosTable;


/*
 * VerContactoActivity permite mostrar un contacto completo de la base de datos
 */
public class VerContactoActivity extends Activity {
  private TextView mNombre;
  private TextView mApellidos;
  private Button mTelefono;
  private TextView mEmail;
  private TextView mPais;
  private TextView mProvincia;
  private TextView mCiudad;
  private ImageView mFoto;
  private Uri contactoUri;

  @Override
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    setContentView(R.layout.ver_contacto);

    mNombre = (TextView) findViewById(R.id.ver_nombre);
    mApellidos = (TextView) findViewById(R.id.ver_apellidos);
    mTelefono = (Button) findViewById(R.id.callButton);
    mEmail = (TextView) findViewById(R.id.ver_email);
    mPais = (TextView) findViewById(R.id.ver_pais);
    mProvincia = (TextView) findViewById(R.id.ver_provincia);
    mCiudad = (TextView) findViewById(R.id.ver_ciudad);
    
    mFoto = (ImageView) findViewById(R.id.ver_foto);
    
    
    Button editButton = (Button) findViewById(R.id.ver_edit_button);


    Bundle extras = getIntent().getExtras();
    // Passed from the other activity
      contactoUri = extras
          .getParcelable(MyAgendaContentProvider.CONTENT_ITEM_TYPE);

    fillData(contactoUri);
    mTelefono.setOnClickListener(new View.OnClickListener() {
    	 
        @Override
        public void onClick(View v) {
            call();
        }
    });

  }

  private void fillData(Uri uri) {
    String[] projection = { ContactosTable.COLUMN_APELLIDOS,
        ContactosTable.COLUMN_TELEFONO,ContactosTable.COLUMN_EMAIL, ContactosTable.COLUMN_NOMBRE,
        ContactosTable.COLUMN_PAIS,ContactosTable.COLUMN_PROVINCIA,ContactosTable.COLUMN_CIUDAD,ContactosTable.COLUMN_FOTO };
    Cursor cursor = getContentResolver().query(uri, projection, null, null,
        null);
    if (cursor != null) {
      cursor.moveToFirst();
      mNombre.setText(cursor.getString(cursor
          .getColumnIndexOrThrow(ContactosTable.COLUMN_NOMBRE)));
      mApellidos.setText(cursor.getString(cursor
          .getColumnIndexOrThrow(ContactosTable.COLUMN_APELLIDOS)));
      mTelefono.setText(cursor.getString(cursor
          .getColumnIndexOrThrow(ContactosTable.COLUMN_TELEFONO)));
      mEmail.setText(cursor.getString(cursor
              .getColumnIndexOrThrow(ContactosTable.COLUMN_EMAIL)));
      mPais.setText(cursor.getString(cursor
              .getColumnIndexOrThrow(ContactosTable.COLUMN_PAIS)));
      mProvincia.setText(cursor.getString(cursor
              .getColumnIndexOrThrow(ContactosTable.COLUMN_PROVINCIA)));
      mCiudad.setText(cursor.getString(cursor
              .getColumnIndexOrThrow(ContactosTable.COLUMN_CIUDAD)));
      
      byte[] blob = cursor.getBlob(cursor
              .getColumnIndexOrThrow(ContactosTable.COLUMN_FOTO));
      
      if (blob!=null){
	      Bitmap bmp = BitmapFactory.decodeByteArray(blob, 0, blob.length);
	      //Log.d(getResources().getString(R.string.bmp_size_key), bmp.toString());
	      mFoto.setImageBitmap(bmp);
      }
      
      
      // Always close the cursor
      cursor.close();
    }
  }

  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
     }

  @Override
  protected void onPause() {
    super.onPause();
  }
  
	  public void onClickEdit(View view) {
	
		Intent i;
	    i = new Intent(this, EditarContactoActivity.class);
		startActivity(i);
  	
	  	
	    i.putExtra(MyAgendaContentProvider.CONTENT_ITEM_TYPE, contactoUri);
	    startActivity(i);
	    finish();
  }

 


	private void call() {
	    try {
	        Intent callIntent = new Intent(Intent.ACTION_CALL);
	        String telefono = mTelefono.getText().toString();
	        callIntent.setData(Uri.parse("tel:"+telefono));
	        startActivity(callIntent);
	    } catch (ActivityNotFoundException activityException) {
	        Log.e("dialing-example", "Call failed", activityException);
	    }
	}
}