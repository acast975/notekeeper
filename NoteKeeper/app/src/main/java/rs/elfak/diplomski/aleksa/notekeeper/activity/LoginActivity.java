package rs.elfak.diplomski.aleksa.notekeeper.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import rs.elfak.diplomski.aleksa.notekeeper.R;
import rs.elfak.diplomski.aleksa.notekeeper.Session;
import rs.elfak.diplomski.aleksa.notekeeper.http.HttpConnector;
import rs.elfak.diplomski.aleksa.notekeeper.http.UserVolleyCallback;
import rs.elfak.diplomski.aleksa.notekeeper.model.User;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    EditText _emailText;
    EditText _passwordText;
    Button _loginButton;
    TextView _signupLink;
    User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        _emailText = (EditText) findViewById(R.id.input_email);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _loginButton = (Button) findViewById(R.id.btn_login);
        _signupLink = (TextView) findViewById(R.id.link_signup);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed("Login failed");
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = _emailText.getText().toString();
        final String password = _passwordText.getText().toString();

        HttpConnector.getUserByEmail(email, this, new UserVolleyCallback() {
            @Override
            public void success(final List<User> users) {
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                if(users.size() == 1) {
                                    User user = users.get(0);
                                    LoginActivity.this.user = user;
                                    if(password.equals(user.getPassword())){
                                        onLoginSuccess();
                                    }
                                    else {
                                        onLoginFailed("Wrong password");
                                    }
                                }
                                else {
                                    onLoginFailed("Login failed");
                                }
                                progressDialog.dismiss();
                            }
                        }, 3000);
            }

            @Override
            public void error(final String message) {
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                progressDialog.dismiss();
                                onLoginFailed(message);
                            }
                        }, 3000);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        Session session = new Session(this);
        session.setUsername(user.getUsername());
        session.setId(user.getId());
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

    public void onLoginFailed(String message) {
        Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() || password.isEmpty()) {
            Toast.makeText(this, "Please enter all information required", Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
    }
}
