package es.ambimetrics.android.agenda;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import es.ambimetrics.android.agenda.contentprovider.MyTodoContentProvider;
import es.ambimetrics.android.agenda.database.TodoTable;

/*
 * TodoDetailActivity allows to enter a new todo item 
 * or to change an existing
 */
public class TodoSeeActivity extends Activity {
  private TextView mCategory;
  private TextView mTitleText;
  private TextView mBodyText;
  private TextView mBodyText2;
  private Uri todoUri;

  @Override
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    setContentView(R.layout.todo_see);

    mCategory = (TextView) findViewById(R.id.category);
    mTitleText = (TextView) findViewById(R.id.todo_see_summary);
    mBodyText = (TextView) findViewById(R.id.todo_see_description);
    mBodyText2 = (TextView) findViewById(R.id.todo_see_description2);
    Button editButton = (Button) findViewById(R.id.todo_see_button);

    Bundle extras = getIntent().getExtras();
    // Passed from the other activity
      todoUri = extras
          .getParcelable(MyTodoContentProvider.CONTENT_ITEM_TYPE);

    fillData(todoUri);
    

  }

  private void fillData(Uri uri) {
    String[] projection = { TodoTable.COLUMN_SUMMARY,
        TodoTable.COLUMN_DESCRIPTION,TodoTable.COLUMN_DESCRIPTION2, TodoTable.COLUMN_CATEGORY };
    Cursor cursor = getContentResolver().query(uri, projection, null, null,
        null);
    if (cursor != null) {
      cursor.moveToFirst();
      mCategory.setText(cursor.getString(cursor
          .getColumnIndexOrThrow(TodoTable.COLUMN_CATEGORY)));
      mTitleText.setText(cursor.getString(cursor
          .getColumnIndexOrThrow(TodoTable.COLUMN_SUMMARY)));
      mBodyText.setText(cursor.getString(cursor
          .getColumnIndexOrThrow(TodoTable.COLUMN_DESCRIPTION)));
      mBodyText2.setText(cursor.getString(cursor
              .getColumnIndexOrThrow(TodoTable.COLUMN_DESCRIPTION2)));

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
	  	Intent i = new Intent(this, TodoDetailActivity.class);
	    i.putExtra(MyTodoContentProvider.CONTENT_ITEM_TYPE, todoUri);
	    startActivity(i);
	    finish();
  }

} 