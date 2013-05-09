package es.ambimetrics.android.agenda;

import java.io.ByteArrayOutputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
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
    
    
    //Button editButton = (Button) findViewById(R.id.ver_edit_button);


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
    	/*
    	JSONObject cadena= fillJSON(cursor, projection);
    	
    	setJSON(cadena);

		*/
    	
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
		mFoto.setImageBitmap(bmp);
		}
/*
      if (blob!=null){
	      Bitmap bmp = BitmapFactory.decodeByteArray(blob, 0, blob.length);
	      //Log.d(getResources().getString(R.string.bmp_size_key), bmp.toString());
	     
      }
  */    
      
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
	private String getStringFromBitmap(Bitmap bitmapPicture) {
		 /*
		 * This functions converts Bitmap picture to a string which can be
		 * JSONified.
		 * */
		 final int COMPRESSION_QUALITY = 100;
		 String encodedImage;
		 ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
		 bitmapPicture.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY,
		 byteArrayBitmapStream);
		 byte[] b = byteArrayBitmapStream.toByteArray();
		 encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
		 return encodedImage;
		 }
	
	private Bitmap getBitmapFromString(String stringPicture) {
		/*
		* This Function converts the String back to Bitmap
		* */
		byte[] decodedString = Base64.decode(stringPicture, Base64.DEFAULT);
		Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
		return decodedByte;
		}
	private JSONObject fillJSON(Cursor cursor, String[] projection){
		JSONObject cadena = new JSONObject(); //Creamos un objeto de tipo JSON
    	
		try { 
    	for (int i=0;i<projection.length;i++){
    		if(projection[i].equals(ContactosTable.COLUMN_FOTO)){
    			byte[] blob = cursor.getBlob(cursor
    	                .getColumnIndexOrThrow(ContactosTable.COLUMN_FOTO));
    			if (blob!=null){
    				String encodedImage = Base64.encodeToString(blob, Base64.DEFAULT);
    				cadena.put(ContactosTable.COLUMN_FOTO, encodedImage);
    			}
    		}else{
    		cadena.put(projection[i], cursor.getString(cursor
                    .getColumnIndexOrThrow(projection[i])));//Le asignamos los datos que necesitemos
    		}
    	}
        } catch (JSONException e) {
        	e.printStackTrace();
        }		
		return cadena;
	}
	private void setJSON(JSONObject cadena){
        cadena.toString(); //Para obtener la cadena de texto de tipo JSON
        
        try {  
	    	mNombre.setText(cadena.getString(ContactosTable.COLUMN_NOMBRE));
	    	mApellidos.setText(cadena.getString(ContactosTable.COLUMN_APELLIDOS));
	      	mTelefono.setText(cadena.getString(ContactosTable.COLUMN_TELEFONO));
	      	mEmail.setText(cadena.getString(ContactosTable.COLUMN_EMAIL));
			mPais.setText(cadena.getString(ContactosTable.COLUMN_PAIS));
			mProvincia.setText(cadena.getString(ContactosTable.COLUMN_PROVINCIA));
			mCiudad.setText(cadena.getString(ContactosTable.COLUMN_CIUDAD));
			byte[] decodedString = Base64.decode(cadena.getString(ContactosTable.COLUMN_FOTO), Base64.DEFAULT);
			if (decodedString!=null){
			Bitmap bmp = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
			mFoto.setImageBitmap(bmp);
			}
        } catch (JSONException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
		}
	
	}
	
	
}