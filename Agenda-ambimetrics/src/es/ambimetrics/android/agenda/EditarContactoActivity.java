package es.ambimetrics.android.agenda;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import es.ambimetrics.android.agenda.contentprovider.MyAgendaContentProvider;
import es.ambimetrics.android.agenda.database.ContactosTable;

/*
 * EditarContactoActivity allows to enter a new todo item 
 * or to change an existing
 */
public class EditarContactoActivity extends Activity {
  private EditText mNombre;
  private EditText mApellidos;
  private EditText mTelefono;
  private EditText mEmail;

  private Uri contactoUri;

  @Override
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    setContentView(R.layout.edit_contacto);

    mNombre = (EditText) findViewById(R.id.edit_nombre);
    mApellidos = (EditText) findViewById(R.id.edit_apellidos);
    mTelefono = (EditText) findViewById(R.id.edit_telefono);
    mEmail = (EditText) findViewById(R.id.edit_email);
    Button confirmButton = (Button) findViewById(R.id.edit_button);

    Bundle extras = getIntent().getExtras();

    // Check from the saved Instance
    contactoUri = (bundle == null) ? null : (Uri) bundle
        .getParcelable(MyAgendaContentProvider.CONTENT_ITEM_TYPE);

    // Or passed from the other activity
    if (extras != null) {
      contactoUri = extras
          .getParcelable(MyAgendaContentProvider.CONTENT_ITEM_TYPE);

      fillData(contactoUri);
    }

    confirmButton.setOnClickListener(new View.OnClickListener() {
      public void onClick(View view) {
        if (TextUtils.isEmpty(mNombre.getText().toString())) {
          makeToast();
        } else {
          setResult(RESULT_OK);
          finish();
        }
      }

    });
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
    saveState();
    outState.putParcelable(MyAgendaContentProvider.CONTENT_ITEM_TYPE, contactoUri);
  }

  @Override
  protected void onPause() {
    super.onPause();
    saveState();
  }

  private void saveState() {
    String nombre = mNombre.getText().toString();
    String apellidos = mApellidos.getText().toString();
    String telefono = mTelefono.getText().toString();
    String email = mEmail.getText().toString();
    // Only save if either nombre or apellidos or telefono
    // is available

    if (nombre.length()==0 && telefono.length() == 0 && apellidos.length() == 0) {
      return;
    }

    ContentValues values = new ContentValues();
    values.put(ContactosTable.COLUMN_NOMBRE, nombre);
    values.put(ContactosTable.COLUMN_APELLIDOS, apellidos);
    values.put(ContactosTable.COLUMN_TELEFONO, telefono);
    values.put(ContactosTable.COLUMN_EMAIL, email);

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
} 