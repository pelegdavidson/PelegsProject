package com.example.peleg.pelegsproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SigninActivity extends AppCompatActivity implements
        View.OnClickListener, FirebaseAuth.AuthStateListener {

    private FirebaseAuth mAuth;
    private EditText mEmailField;
    private EditText mPasswordField;
    public ProgressBar mProgress;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(this);
        mEmailField = findViewById(R.id.field_email1);
        mPasswordField = findViewById(R.id.field_password1);
        mProgress = findViewById(R.id.progressBar);

    }
    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser != null){
            Intent i = new Intent(getApplicationContext(), DashBoardActivity.class);
            startActivity(i);
        }
    }
    private void signIn(String email, String password) {

        if(email==null&&password==null)
            Toast.makeText(getApplicationContext(), "please fill the email and password fields", Toast.LENGTH_LONG).show();
        else if(email==null)
            Toast.makeText(getApplicationContext(), "please fill the email field", Toast.LENGTH_LONG).show();
        else if(password==null)
            Toast.makeText(getApplicationContext(), "please fill the password field", Toast.LENGTH_LONG).show();
        else {
            showProgress();
            // [START sign_in_with_email]
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information


                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(SigninActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();

                            }

                            hideProgress();
                            // [END_EXCLUDE]
                        }
                    });

        } // [END sign_in_with_email]
    }
    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch(i){
            case R.id.btnlogin:
                signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
                break;
            case R.id.btnregister:
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
                break;
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        hideProgress();
    }
    public void showProgress() {
        mProgress.setVisibility(View.VISIBLE);
    }

    public void hideProgress() {
        mProgress.setVisibility(View.INVISIBLE);
    }
}
