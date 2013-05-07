package es.ambimetrics.android.agenda.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AgendaDatabaseHelper extends SQLiteOpenHelper {

  private static final String DATABASE_NAME = "contactotable.db";
  private static final int DATABASE_VERSION = 4;

  public AgendaDatabaseHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  // Method is called during creation of the database
  @Override
  public void onCreate(SQLiteDatabase database) {
    ContactosTable.onCreate(database);
    UsuarioTable.onCreate(database);
  }

  // Method is called during an upgrade of the database,
  // e.g. if you increase the database version
  @Override
  public void onUpgrade(SQLiteDatabase database, int oldVersion,
      int newVersion) {
    ContactosTable.onUpgrade(database, oldVersion, newVersion);
    UsuarioTable.onUpgrade(database, oldVersion, newVersion);
  }
} 