package es.ambimetrics.android.agenda.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TodoTable {

  // Database table
  public static final String TABLE_CONTACTO = "contacto";
  public static final String COLUMN_ID = "_id";
  public static final String COLUMN_NOMBRE = "nombre";
  public static final String COLUMN_APELLIDOS = "apellidos";
  public static final String COLUMN_TELEFONO = "telefono";
  public static final String COLUMN_EMAIL = "email";

  // Database creation SQL statement
  private static final String DATABASE_CREATE = "create table " 
      + TABLE_CONTACTO
      + "(" 
      + COLUMN_ID + " integer primary key autoincrement, " 
      + COLUMN_NOMBRE + " text not null, " 
      + COLUMN_APELLIDOS + " text not null," 
      + COLUMN_TELEFONO
      + " text not null," 
      + COLUMN_EMAIL
      + " text not null" 
      + ");";

  public static void onCreate(SQLiteDatabase database) {
    database.execSQL(DATABASE_CREATE);
  }

  public static void onUpgrade(SQLiteDatabase database, int oldVersion,
      int newVersion) {
    Log.w(TodoTable.class.getName(), "Upgrading database from version "
        + oldVersion + " to " + newVersion
        + ", which will destroy all old data");
    database.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTO);
    onCreate(database);
  }
} 