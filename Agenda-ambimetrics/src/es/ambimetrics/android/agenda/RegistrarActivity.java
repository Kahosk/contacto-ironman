package es.ambimetrics.android.agenda;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;


import es.ambimetrics.android.agenda.contentprovider.MyUsuarioContentProvider;

import es.ambimetrics.android.agenda.database.UsuarioTable;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class RegistrarActivity extends Activity {
	/**
	 * A dummy authentication store containing known user names and passwords.
	 * TODO: remove after connecting to a real authentication system.
	 
	private static final String[] DUMMY_CREDENTIALS = new String[] {
			"foo@example.com:hello", "bar@example.com:world" };

	
	*  The default email to populate the email field with.
	   public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";
	*/
	
	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	
	private String mNombre;
	private String mApellidos;
	private String mTelefono;
	
	private String mEmail;
	private String mPassword;

	// UI references.
	private EditText mNombreView;
	private EditText mApellidosView;
	private EditText mTelefonoView;
	
	private EditText mEmailView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	
	private Uri usuarioUri = null;

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		setContentView(R.layout.activity_registrar);
		setupActionBar();

		// Set up the login form.
		mNombreView = (EditText) findViewById(R.id.nombre);
		mApellidosView = (EditText) findViewById(R.id.apellidos);
		mTelefonoView = (EditText) findViewById(R.id.telefono);
		
		//mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
		mEmailView = (EditText) findViewById(R.id.email);
		//mEmailView.setText(mEmail);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptRegister();							
							return true;
						}
						return false;
					}
				});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

	    Bundle extras = getIntent().getExtras();

	    // Check from the saved Instance
	    usuarioUri = (bundle == null) ? null : (Uri) bundle
	        .getParcelable(MyUsuarioContentProvider.CONTENT_ITEM_TYPE);

	    // Or passed from the other activity
	    if (extras != null) {
	    	usuarioUri = extras
	          .getParcelable(MyUsuarioContentProvider.CONTENT_ITEM_TYPE);
	    }
	    if(usuarioUri != null)
	    	fillData(usuarioUri);	
		
		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptRegister();
					}
				});
	}
	
	  private void fillData(Uri uri) {
		  
		    String[] projection = { UsuarioTable.COLUMN_NOMBRE, UsuarioTable.COLUMN_APELLIDOS,
		        UsuarioTable.COLUMN_TELEFONO,UsuarioTable.COLUMN_EMAIL, 
		        UsuarioTable.COLUMN_PASSWORD};
		    Cursor cursor = getContentResolver().query(uri, projection, null, null,
		        null);
		    if (cursor != null) {
		      cursor.moveToFirst();
		      mNombreView.setText(cursor.getString(cursor
		          .getColumnIndexOrThrow(UsuarioTable.COLUMN_NOMBRE)));
		      mApellidosView.setText(cursor.getString(cursor
		          .getColumnIndexOrThrow(UsuarioTable.COLUMN_APELLIDOS)));
		      mTelefonoView.setText(cursor.getString(cursor
		          .getColumnIndexOrThrow(UsuarioTable.COLUMN_TELEFONO)));
		      mEmailView.setText(cursor.getString(cursor
		              .getColumnIndexOrThrow(UsuarioTable.COLUMN_EMAIL)));
		      mPasswordView.setText(cursor.getString(cursor
		              .getColumnIndexOrThrow(UsuarioTable.COLUMN_PASSWORD)));
		      
		      // Always close the cursor
		      cursor.close();
		    }
		  }

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			// Show the Up button in the action bar.
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			// TODO: If Settings has multiple levels, Up should navigate up
			// that hierarchy.
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.registrar, menu);
		return true;
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptRegister() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mNombreView.setError(null);
		mApellidosView.setError(null);
		mTelefonoView.setError(null);
		
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mNombre = mNombreView.getText().toString();
		mApellidos = mApellidosView.getText().toString();
		mTelefono = mTelefonoView.getText().toString();
		
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}


		if (TextUtils.isEmpty(mTelefono)) {
			mTelefonoView.setError(getString(R.string.error_field_required));
			focusView = mTelefonoView;
			cancel = true;
		}
		if (TextUtils.isEmpty(mApellidos)) {
			mApellidosView.setError(getString(R.string.error_field_required));
			focusView = mApellidosView;
			cancel = true;
		}
		if (TextUtils.isEmpty(mNombre)) {
			mNombreView.setError(getString(R.string.error_field_required));
			focusView = mNombreView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}
	private void saveState() {
	    String nombre = mNombreView.getText().toString();
	    String apellidos = mApellidosView.getText().toString();
	    String telefono = mTelefonoView.getText().toString();
	    String email = mEmailView.getText().toString();
	    String password = mPasswordView.getText().toString();
	    
		ContentValues values = new ContentValues();
	    values.put(UsuarioTable.COLUMN_NOMBRE, nombre);
	    values.put(UsuarioTable.COLUMN_APELLIDOS, apellidos);
	    values.put(UsuarioTable.COLUMN_TELEFONO, telefono);
	    values.put(UsuarioTable.COLUMN_EMAIL, email);
	    values.put(UsuarioTable.COLUMN_PASSWORD, password);

	    if (usuarioUri == null) {
	      // New contacto
	      usuarioUri = getContentResolver().insert(MyUsuarioContentProvider.CONTENT_URI, values);
	    } else {
	      // Update contacto
	      getContentResolver().update(usuarioUri, values, null, null);
	    }
	  }
	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */

	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.
				
			try {
				// Simulate network access.
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				return false;
			}
			/*
			for (String credential : DUMMY_CREDENTIALS) {
				String[] pieces = credential.split(":");
				if (pieces[0].equals(mEmail)) {
					// Account exists, return true if the password matches.
					return pieces[1].equals(mPassword);
				}
			}
			*/
			saveState();
			//makeToast("Usuario "+mEmail+" registrado.");
			// TODO: register the new account here.
			
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);

			if (success) {
				finish();
			} else {
				mPasswordView
						.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
}
