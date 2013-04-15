package es.ambimetrics.android.agenda;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import es.ambimetrics.android.agenda.contentprovider.MyAgendaContentProvider;
import es.ambimetrics.android.agenda.database.ContactosTable;

/*
 * VerContactoActivity permite mostrar un contacto completo de la base de datos
 */
public class VerContactoActivity extends Activity {
  private TextView mNombre;
  private TextView mApellidos;
  private TextView mTelefono;
  private TextView mEmail;
  private Uri contactoUri;

  @Override
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    setContentView(R.layout.ver_contacto);

    mNombre = (TextView) findViewById(R.id.ver_nombre);
    mApellidos = (TextView) findViewById(R.id.ver_apellidos);
    mTelefono = (TextView) findViewById(R.id.ver_telefono);
    mEmail = (TextView) findViewById(R.id.ver_email);
    Button editButton = (Button) findViewById(R.id.ver_edit_button);

    Bundle extras = getIntent().getExtras();
    // Passed from the other activity
      contactoUri = extras
          .getParcelable(MyAgendaContentProvider.CONTENT_ITEM_TYPE);

    fillData(contactoUri);
    

  }

  private void fillData(Uri uri) {
    String[] projection = { ContactosTable.COLUMN_APELLIDOS,
        ContactosTable.COLUMN_TELEFONO,ContactosTable.COLUMN_EMAIL, ContactosTable.COLUMN_NOMBRE };
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
	  	Intent i = new Intent(this, EditarContactoActivity.class);
	    i.putExtra(MyAgendaContentProvider.CONTENT_ITEM_TYPE, contactoUri);
	    startActivity(i);
	    finish();
  }

} 