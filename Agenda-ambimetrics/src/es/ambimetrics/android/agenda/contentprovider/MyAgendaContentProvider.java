package es.ambimetrics.android.agenda.contentprovider;

import java.util.Arrays;
import java.util.HashSet;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import es.ambimetrics.android.agenda.database.AgendaDatabaseHelper;
import es.ambimetrics.android.agenda.database.ContactosTable;
import es.ambimetrics.android.agenda.database.UsuarioTable;

public class MyAgendaContentProvider extends ContentProvider {

  // database
  private AgendaDatabaseHelper database;

  // Used for the UriMacher
  private static final int CONTACTOS = 10;
  private static final int CONTACTO_ID = 20;
  private static final int USUARIO = 30;
  private static final int USUARIO_ID = 40;
  
  private static final String AUTHORITY = "es.ambimetrics.android.agenda.contentprovider";

  private static final String BASE_PATH1 = "contacto";
  private static final String BASE_PATH2 = "usuario";
  public static final Uri CONTENT_URI1 = Uri.parse("content://" + AUTHORITY
      + "/" + BASE_PATH1);
  public static final Uri CONTENT_URI2 = Uri.parse("content://" + AUTHORITY
	      + "/" + BASE_PATH2);

  public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
      + "/agenda";
  public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
      + "/contacto";
  public static final String CONTENT_ITEM_TYPE2 = ContentResolver.CURSOR_ITEM_BASE_TYPE
	      + "/usuario";

  private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
  static {
    sURIMatcher.addURI(AUTHORITY, BASE_PATH1, CONTACTOS);
    sURIMatcher.addURI(AUTHORITY, BASE_PATH1 + "/#", CONTACTO_ID);
    sURIMatcher.addURI(AUTHORITY, BASE_PATH2, USUARIO);
    sURIMatcher.addURI(AUTHORITY, BASE_PATH2 + "/#", USUARIO_ID);
  }

  @Override
  public boolean onCreate() {
    database = new AgendaDatabaseHelper(getContext());
    return false;
  }

  @Override
  public Cursor query(Uri uri, String[] projection, String selection,
      String[] selectionArgs, String sortOrder) {

    // Uisng SQLiteQueryBuilder instead of query() method
    SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

    // Check if the caller has requested a column which does not exists
    checkColumns(projection);

    // Set the table
    //queryBuilder.setTables(ContactosTable.TABLE_CONTACTO);

    int uriType = sURIMatcher.match(uri);
    switch (uriType) {
    case CONTACTO_ID:
        // Adding the ID to the original query
        queryBuilder.appendWhere(ContactosTable.COLUMN_ID + "="
            + uri.getLastPathSegment());
    case CONTACTOS:
    	queryBuilder.setTables(ContactosTable.TABLE_CONTACTO);
    	break;
    case USUARIO_ID:
    	// Adding the ID to the original query
        queryBuilder.appendWhere(UsuarioTable.COLUMN_ID + "="
            + uri.getLastPathSegment());
    case USUARIO:
    	queryBuilder.setTables(UsuarioTable.TABLE_USUARIO);
    	break;

    default:
      throw new IllegalArgumentException("Unknown URI: " + uri);
    }

    SQLiteDatabase db = database.getWritableDatabase();
    Cursor cursor = queryBuilder.query(db, projection, selection,
        selectionArgs, null, null, sortOrder);
    // Make sure that potential listeners are getting notified
    cursor.setNotificationUri(getContext().getContentResolver(), uri);

    return cursor;
  }

  @Override
  public String getType(Uri uri) {
    return null;
  }

  @Override
  public Uri insert(Uri uri, ContentValues values) {
    int uriType = sURIMatcher.match(uri);
    SQLiteDatabase sqlDB = database.getWritableDatabase();
    //int rowsDeleted = 0;
    long id = 0;
    switch (uriType) {
    //No tiene sentido...
    case CONTACTOS:
      id = sqlDB.insert(ContactosTable.TABLE_CONTACTO, null, values);
      getContext().getContentResolver().notifyChange(uri, null);
      return Uri.parse(BASE_PATH1 + "/" + id);
      case USUARIO:
    	id = sqlDB.insert(UsuarioTable.TABLE_USUARIO, null, values);
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH2 + "/" + id);
	default:
      throw new IllegalArgumentException("Unknown URI: " + uri);
    }

  }

  @Override
  public int delete(Uri uri, String selection, String[] selectionArgs) {
    int uriType = sURIMatcher.match(uri);
    SQLiteDatabase sqlDB = database.getWritableDatabase();
    int rowsDeleted = 0;
    switch (uriType) {
    case CONTACTOS:
      rowsDeleted = sqlDB.delete(ContactosTable.TABLE_CONTACTO, selection,
          selectionArgs);
      break;
    case CONTACTO_ID:
      String id = uri.getLastPathSegment();
      if (TextUtils.isEmpty(selection)) {
        rowsDeleted = sqlDB.delete(ContactosTable.TABLE_CONTACTO,
            ContactosTable.COLUMN_ID + "=" + id, 
            null);
      } else {
        rowsDeleted = sqlDB.delete(ContactosTable.TABLE_CONTACTO,
            ContactosTable.COLUMN_ID + "=" + id 
            + " and " + selection,
            selectionArgs);
      }
      break;
      
    case USUARIO:
        rowsDeleted = sqlDB.delete(UsuarioTable.TABLE_USUARIO, selection,
            selectionArgs);
        break;
    default:
      throw new IllegalArgumentException("Unknown URI: " + uri);
    }
    getContext().getContentResolver().notifyChange(uri, null);
    return rowsDeleted;
  }

  @Override
  public int update(Uri uri, ContentValues values, String selection,
      String[] selectionArgs) {

    int uriType = sURIMatcher.match(uri);
    SQLiteDatabase sqlDB = database.getWritableDatabase();
    int rowsUpdated = 0;
    switch (uriType) {
    case CONTACTOS:
      rowsUpdated = sqlDB.update(ContactosTable.TABLE_CONTACTO, 
          values, 
          selection,
          selectionArgs);
      break;
    case CONTACTO_ID:
      String id = uri.getLastPathSegment();
      if (TextUtils.isEmpty(selection)) {
        rowsUpdated = sqlDB.update(ContactosTable.TABLE_CONTACTO, 
            values,
            ContactosTable.COLUMN_ID + "=" + id, 
            null);
      } else {
        rowsUpdated = sqlDB.update(ContactosTable.TABLE_CONTACTO, 
            values,
            ContactosTable.COLUMN_ID + "=" + id 
            + " and " 
            + selection,
            selectionArgs);
      }
      break;
      
    case USUARIO:
    	rowsUpdated = sqlDB.update(UsuarioTable.TABLE_USUARIO, 
    	          values, 
    	          selection,
    	          selectionArgs);
    	break;
    default:
      throw new IllegalArgumentException("Unknown URI: " + uri);
    }
    getContext().getContentResolver().notifyChange(uri, null);
    return rowsUpdated;
  }

  private void checkColumns(String[] projection) {
    String[] available = { ContactosTable.COLUMN_NOMBRE,
        ContactosTable.COLUMN_APELLIDOS, ContactosTable.COLUMN_TELEFONO,ContactosTable.COLUMN_EMAIL,
        ContactosTable.COLUMN_FOTO, ContactosTable.COLUMN_PAIS, ContactosTable.COLUMN_PROVINCIA,
        ContactosTable.COLUMN_CIUDAD, ContactosTable.COLUMN_ID, UsuarioTable.COLUMN_NOMBRE,
        UsuarioTable.COLUMN_APELLIDOS, UsuarioTable.COLUMN_TELEFONO,UsuarioTable.COLUMN_EMAIL,
        UsuarioTable.COLUMN_ID, UsuarioTable.COLUMN_PASSWORD };
    if (projection != null) {
      HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
      HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
      // Check if all columns which are requested are available
      if (!availableColumns.containsAll(requestedColumns)) {
        throw new IllegalArgumentException("Unknown columns in projection");
      }
    }
  }

} 