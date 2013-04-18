package es.ambimetrics.android.agenda;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import es.ambimetrics.android.agenda.contentprovider.MyAgendaContentProvider;
import es.ambimetrics.android.agenda.database.ContactosTable;

/*
 * AgendaActivity muestra los contactos existentes
 * en una lista
 * 
 * Puedes crear nuevos con el boton "Añadir"
 * Puedes eliminar pulsando un contacto durante un rato
 */

public class AgendaActivity extends ListActivity implements
    LoaderManager.LoaderCallbacks<Cursor>
{
  private static final int DIALOG_ALERT = 10;
  private static final int ACTIVITY_CREATE = 0;
  private static final int ACTIVITY_EDIT = 1;
  private static final int DELETE_ID = Menu.FIRST + 1;
  // private Cursor cursor;
  private SimpleCursorAdapter adapter;
  private Uri direccion;

  
/** Called when the activity is first created. */

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.list_contacto);
    this.getListView().setDividerHeight(2);
    fillData();
    registerForContextMenu(getListView());
  }

  // Create the menu based on the XML defintion
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.listmenu, menu);
    return true;
  }

  // Reaction to the menu selection
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    case R.id.insert:
      createContacto();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public boolean onContextItemSelected(MenuItem item) {
    switch (item.getItemId()) {
    case DELETE_ID:
    	// 1. Instantiate an AlertDialog.Builder with its constructor
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);

    	// 2. Chain together various setter methods to set the dialog characteristics
    	builder.setMessage(R.string.dialog_delete);

    	builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                getContentResolver().delete(direccion, null, null);
                fillData();           	
            	dialog.cancel();
            }
        });
    	builder.setNegativeButton(R.string.negative, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            	dialog.cancel();
            }
        });   	
    	
    	
    	// 3. Get the AlertDialog from create()
    	AlertDialog dialog = builder.create();
    	dialog.show();

        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
                .getMenuInfo();
        Uri uri = Uri.parse(MyAgendaContentProvider.CONTENT_URI + "/"
                + info.id);
        direccion = uri;


    return true;
    }
    return super.onContextItemSelected(item);
  }
  
  private void createContacto() {
	Intent i;
    i = new Intent(this, EditarContactoActivity.class);
    startActivity(i);
  }

  // Opens the second activity if an entry is clicked
  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
    Intent i = new Intent(this, VerContactoActivity.class);
    Uri contactoUri = Uri.parse(MyAgendaContentProvider.CONTENT_URI + "/" + id);
    i.putExtra(MyAgendaContentProvider.CONTENT_ITEM_TYPE, contactoUri);

    startActivity(i);
  }

  

  private void fillData() {
    // Fields from the database (projection)
    // Must include the _id column for the adapter to work

    String[] from = new String[] {ContactosTable.COLUMN_NOMBRE, ContactosTable.COLUMN_APELLIDOS};

    // Fields on the UI to which we map
    int[] to = new int[] { R.id.label, R.id.label2};

    getLoaderManager().initLoader(0, null, this);
    adapter = new SimpleCursorAdapter(this, R.layout.row_contacto, null, from,
        to, 0);

    setListAdapter(adapter);
  }

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v,
      ContextMenuInfo menuInfo) {
    super.onCreateContextMenu(menu, v, menuInfo);
    menu.add(0, DELETE_ID, 0, R.string.menu_delete);
  }

  // Creates a new loader after the initLoader () call
  @Override
  public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    String[] projection = { ContactosTable.COLUMN_ID, ContactosTable.COLUMN_NOMBRE, ContactosTable.COLUMN_APELLIDOS };
    CursorLoader cursorLoader = new CursorLoader(this,
        MyAgendaContentProvider.CONTENT_URI, projection, null, null, ContactosTable.COLUMN_NOMBRE+", "+ContactosTable.COLUMN_APELLIDOS);
    return cursorLoader;
  }

  @Override
  public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    adapter.swapCursor(data);
  }

  @Override
  public void onLoaderReset(Loader<Cursor> loader) {
    // data is not available anymore, delete reference
    adapter.swapCursor(null);
  }

} 