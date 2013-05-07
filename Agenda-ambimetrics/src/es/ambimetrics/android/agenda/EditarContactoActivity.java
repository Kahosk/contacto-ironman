package es.ambimetrics.android.agenda;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import es.ambimetrics.android.agenda.contentprovider.MyAgendaContentProvider;
import es.ambimetrics.android.agenda.database.ContactosTable;

/*
 * EditarContactoActivity allows to enter a new contacto item 
 * or to change an existing
 */
public class EditarContactoActivity extends Activity {
  private EditText mNombre;
  private EditText mApellidos;
  private EditText mTelefono;
  private EditText mEmail;
  private EditText mPais;
  private EditText mCiudad;
  private EditText mProvincia;
  private ImageView mFoto;
 
  
  private Uri contactoUri = null;
  
  //Foto
  private int SELECT_IMAGE = 237487;
  private int TAKE_PICTURE = 829038;

  @Override
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    setContentView(R.layout.edit_contacto);

    mNombre = (EditText) findViewById(R.id.edit_nombre);
    mApellidos = (EditText) findViewById(R.id.edit_apellidos);
    mTelefono = (EditText) findViewById(R.id.edit_telefono);
    mEmail = (EditText) findViewById(R.id.edit_email);
    mPais = (EditText) findViewById(R.id.edit_pais);
    mProvincia = (EditText) findViewById(R.id.edit_provincia);
    mCiudad = (EditText) findViewById(R.id.edit_ciudad);
    mFoto = (ImageView) findViewById(R.id.edit_foto);
    mFoto.setImageDrawable(null);
    
    Button addFoto = (Button) findViewById(R.id.edit_fotoButton);
    //Button confirmButton = (Button) findViewById(R.id.edit_button);
    
    Bundle extras = getIntent().getExtras();

    // Check from the saved Instance
    contactoUri = (bundle == null) ? null : (Uri) bundle
        .getParcelable(MyAgendaContentProvider.CONTENT_ITEM_TYPE);

    // Or passed from the other activity
    if (extras != null) {
      contactoUri = extras
          .getParcelable(MyAgendaContentProvider.CONTENT_ITEM_TYPE);
    }
    if(contactoUri != null)
    	fillData(contactoUri);	
    addFoto.setOnClickListener(new View.OnClickListener() {   
    	 public void onClick(View v) {
    	  dialogPhoto();
    	 }
    	}); 
  }
  public void onClickConfirm(View view) {
      if (TextUtils.isEmpty(mNombre.getText().toString())) {
        makeToast();
      } else {
        setResult(RESULT_OK);
        saveState();
        /*
        Intent i = new Intent(this, VerContactoActivity.class);
        i.putExtra(MyAgendaContentProvider.CONTENT_ITEM_TYPE, contactoUri);
        startActivity(i);
	    */  
        finish();
      }
    }

  private void fillData(Uri uri) {
    String[] projection = { ContactosTable.COLUMN_APELLIDOS,
        ContactosTable.COLUMN_TELEFONO,ContactosTable.COLUMN_EMAIL, ContactosTable.COLUMN_NOMBRE,
        ContactosTable.COLUMN_PAIS,ContactosTable.COLUMN_PROVINCIA,ContactosTable.COLUMN_CIUDAD, ContactosTable.COLUMN_FOTO };
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
    //saveState();
    outState.putParcelable(MyAgendaContentProvider.CONTENT_ITEM_TYPE, contactoUri);
  }

  @Override
  protected void onPause() {
    super.onPause();
    //saveState();
  }

  private void saveState() {
    String nombre = mNombre.getText().toString();
    String apellidos = mApellidos.getText().toString();
    String telefono = mTelefono.getText().toString();
    String email = mEmail.getText().toString();
    String pais = mPais.getText().toString();
    String provincia = mProvincia.getText().toString();
    String ciudad = mCiudad.getText().toString();
    BitmapDrawable foto = null;
    foto = (BitmapDrawable)mFoto.getDrawable();
    byte[] bitmapbytes = null;
    if(foto!=null){
	    Bitmap bmp = foto.getBitmap();
	 	ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.PNG, 0, bos);
		bitmapbytes = bos.toByteArray();
		bmp.recycle();
		try {
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    // Only save if either nombre or apellidos or telefono
    // is available

    if (nombre.length()==0 && apellidos.length() == 0) {
      return;
    }

    ContentValues values = new ContentValues();
    values.put(ContactosTable.COLUMN_NOMBRE, nombre);
    values.put(ContactosTable.COLUMN_APELLIDOS, apellidos);
    values.put(ContactosTable.COLUMN_TELEFONO, telefono);
    values.put(ContactosTable.COLUMN_EMAIL, email);
    values.put(ContactosTable.COLUMN_PAIS, pais);
    values.put(ContactosTable.COLUMN_PROVINCIA, provincia);
    values.put(ContactosTable.COLUMN_CIUDAD, ciudad);
    if(bitmapbytes!=null)
    	values.put(ContactosTable.COLUMN_FOTO, bitmapbytes);

    if (contactoUri == null) {
      // New contacto
      contactoUri = getContentResolver().insert(MyAgendaContentProvider.CONTENT_URI, values);
    } else {
      // Update contacto
      getContentResolver().update(contactoUri, values, null, null);
    }
  }

  private void makeToast() {
    Toast.makeText(EditarContactoActivity.this, "Porfavor inserta un nombre",
        Toast.LENGTH_LONG).show();
  }
  
  
  
  private void dialogPhoto(){
	try{
		final CharSequence[] items = {"Seleccionar de la galería", "Hacer una foto"};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Seleccionar una foto");
		builder.setItems(items, new DialogInterface.OnClickListener() {
	    public void onClick(DialogInterface dialog, int item) {
	    	switch(item){
	    	case 0:
	    		Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
	    		intent.setType("image/*");
	    		startActivityForResult(intent, SELECT_IMAGE);   
	    		break;
	    	case 1:
	    	   startActivityForResult(new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE), TAKE_PICTURE);
	    	   break;
	    	}        
	    }
		});
		AlertDialog alert = builder.create();
		alert.show(); 
		} catch(Exception e){}
	}
  
  
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK){
			Uri selectedImage = data.getData();
			InputStream is;
			try{
				if (requestCode == SELECT_IMAGE){
					    //mFoto.setImageURI(selectedImage);
					    is = getContentResolver().openInputStream(selectedImage);
					    BufferedInputStream bis = new BufferedInputStream(is);
					    Bitmap bitmap = BitmapFactory.decodeStream(bis);
					    while (bitmap.getWidth()>1000 && bitmap.getHeight()>1000){
					    	bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth()/2, bitmap.getHeight()/2,true);
					    }
					    bitmap = corpBitmapSquare(bitmap);
					    bitmap = Bitmap.createScaledBitmap(bitmap, 500, 500,true);
					    mFoto.setImageBitmap(bitmap);
	  
					} 
				if(requestCode == TAKE_PICTURE){
						//mFoto.setImageURI(selectedImage);
					    is = getContentResolver().openInputStream(selectedImage);
					    BufferedInputStream bis = new BufferedInputStream(is);
					    Bitmap bitmap = BitmapFactory.decodeStream(bis);
					    while (bitmap.getWidth()>1000 && bitmap.getHeight()>1000){
					    	bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth()/2, bitmap.getHeight()/2,true);
					    }
					    bitmap = corpBitmapSquare(bitmap);
					    bitmap = Bitmap.createScaledBitmap(bitmap, 500, 500,true);
					    bitmap = rotateBitmap(bitmap, 90);
					    mFoto.setImageBitmap(bitmap);
					}
				
				
				
			} catch(Exception e){}
		}
		
	}   
	public static Bitmap rotateBitmap(Bitmap original, int rotate) {
        Bitmap rotatedBitmap;
		Matrix mat = new Matrix();
	    mat.postRotate(rotate);
	    rotatedBitmap = Bitmap.createBitmap(original, 0, 0, original.getWidth(), original.getHeight(), mat, true);
		original.recycle();
		return rotatedBitmap;
	}
	public static Bitmap corpBitmapSquare(Bitmap srcBmp) {
		Bitmap dstBmp;
		if (srcBmp.getWidth() >= srcBmp.getHeight()){

			  dstBmp = Bitmap.createBitmap(
			     srcBmp, 
			     srcBmp.getWidth()/2 - srcBmp.getHeight()/2,
			     0,
			     srcBmp.getHeight(), 
			     srcBmp.getHeight()
			     );

			}else{

			  dstBmp = Bitmap.createBitmap(
			     srcBmp,
			     0, 
			     srcBmp.getHeight()/2 - srcBmp.getWidth()/2,
			     srcBmp.getWidth(),
			     srcBmp.getWidth() 
			     );
			}
		return dstBmp;
	}
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
      super.onConfigurationChanged(newConfig);

    }

	
} 