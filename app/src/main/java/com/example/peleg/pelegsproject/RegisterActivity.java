package com.example.peleg.pelegsproject;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class RegisterActivity extends AppCompatActivity implements
        View.OnClickListener, FirebaseAuth.AuthStateListener {
    public ProgressBar mProgress;
    private FirebaseAuth mAuth;
    private EditText mEmailField;
    private EditText mPasswordField;
    private ImageView imgPhoto;
    private static final int PICK_IMAGE_REQUEST = 100;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        mProgress = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(this);
        mEmailField = findViewById(R.id.field_email);
        mPasswordField = findViewById(R.id.field_password);
        imgPhoto = (ImageView)findViewById(R.id.imgUser);

        // Bitmap bitmap = ((BitmapDrawable) imgPhoto.getDrawable()).getBitmap();
        // Buttons

    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    // [END on_start_check_user]

    private void updateUI(FirebaseUser currentUser){
        if (currentUser!= null){

            mEmailField.setText(currentUser.getEmail());
            mPasswordField.setText(null);
         //   getImageFromFB(currentUser);
            findViewById(R.id.btnCreate).setEnabled(false);
            findViewById(R.id.btnSignOut).setEnabled(true);
        }
        else{
            findViewById(R.id.btnCreate).setEnabled(true);
            findViewById(R.id.btnSignOut).setEnabled(false);
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

    private void createAccount(String email, String password) {

        if(email==null&&password==null)
            Toast.makeText(getApplicationContext(), "please fill the email and password fields", Toast.LENGTH_LONG).show();
        else if(email==null)
            Toast.makeText(getApplicationContext(), "please fill the email field", Toast.LENGTH_LONG).show();
        else if(password==null)
            Toast.makeText(getApplicationContext(), "please fill the password field", Toast.LENGTH_LONG).show();
        else {
            showProgress();

            // [START create_user_with_email]
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information

                                FirebaseUser user = mAuth.getCurrentUser();
                                uploadImageIntoFB(user);
                                Intent intent = new Intent(getApplicationContext(), DashBoardActivity.class);
                                startActivity(intent);

                            } else {
                                Log.w("FBTicTao", "createUserWithEmail:failure", task.getException());
                                // If sign in fails, display a message to the user.
                                Toast.makeText(RegisterActivity.this, "Authentication failed:" + task.getException(),
                                        Toast.LENGTH_SHORT).show();
                                // updateUI(null);

                            }
                            hideProgress();

                            // [START_EXCLUDE]

                            // [END_EXCLUDE]
                        }

                    });

        }// [END create_user_with_email]
    }



    private void signOut() {
        mAuth.signOut();
        imgPhoto.setImageResource(R.drawable.ic_person_black_24dp);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch(i){
            case R.id.btnCreate:
                createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
                break;
            case R.id.btnSignOut:
                signOut();
                break;
            case R.id.btnGalery:
                pick();
                break;
        }
    }

    public void pick() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case PICK_IMAGE_REQUEST:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        imgPhoto.setImageBitmap(bitmap);
                        imgPhoto.setTag(R.id.tag_id1,bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private void getImageFromFB(final FirebaseUser user){

        DatabaseReference currUser = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(user.getUid());
        currUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User u = dataSnapshot.getValue(User.class);
                Glide.with(getApplicationContext() /* context */)//.asBitmap()
                        .load(u.getUrlPhoto())
                        .into(imgPhoto);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

// Download directly from StorageReference using Glide
// (See MyAppGlideModule for Loader registration)

    }
    private void uploadImageIntoFB(final FirebaseUser user){

        Bitmap bitmap = (Bitmap) imgPhoto.getTag(R.id.tag_id1);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageRef = storage.getReference().child("photos").child(user.getUid()+".jpeg");

        UploadTask uploadTask = storageRef.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        addNewUser(user, uri);
                        /*
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .setPhotoUri(uri)
                                .build();
                        user.updateProfile(profileUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                addNewUser(currentUser);
                            }
                        });*/


                    }
                });
            }
        });
    }





    private void addNewUser(final FirebaseUser user, Uri uri){

        String dname = user.getEmail().substring(0,user.getEmail().indexOf('@'));
        User u1 = new User(user.getUid(),dname,uri.toString());
        database.getReference().child("Users").child(user.getUid()).setValue(u1).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    Toast.makeText(getApplicationContext(), "user created", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "creation failed", Toast.LENGTH_LONG).show();
            }
        });


    }

}

