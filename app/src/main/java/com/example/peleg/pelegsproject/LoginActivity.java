package com.example.peleg.pelegsproject;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity  implements
        View.OnClickListener{
    boolean okgo = false;
    private FirebaseAuth mAuth;
    private EditText mEmailField;
    private EditText mPasswordField;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        mEmailField = findViewById(R.id.field_email);
        mPasswordField = findViewById(R.id.field_password);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void signIn(String email, String password) {

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            Toast.makeText(LoginActivity.this, "Login successful",Toast.LENGTH_SHORT).show();
                            okgo = true;
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            okgo = false;

                        }

                    }
                });
        // [END sign_in_with_email]
    }

    private void createAccount(String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(LoginActivity.this, "User Created",Toast.LENGTH_SHORT).show();
                            okgo = true;
                        } else {

                            Toast.makeText(LoginActivity.this, "Authentication failed:" + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                            okgo = false;
                            // updateUI(null);

                        }

                    }

                });

        // [END create_user_with_email]
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch(i){
            case R.id.btnCreateAccount:
                createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
                break;
            case R.id.btnSigninAccount:
                signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
                break;
            case R.id.btnSignOut:
                signOut();
                break;
            case R.id.btnGo:
                if(okgo){
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
                break;
            default:
                break;
        }
    }



    private void signOut() {
        mAuth.signOut();
        Toast.makeText(getApplicationContext(),"Signed out",Toast.LENGTH_SHORT).show();
        okgo = false;
    }






}
