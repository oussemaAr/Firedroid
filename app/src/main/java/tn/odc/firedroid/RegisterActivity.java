package tn.odc.firedroid;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

import tn.odc.firedroid.model.User;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private EditText mEmail;
    private EditText mPassword;
    private EditText mPseudo;
    private EditText mPhone;
    private ImageView mPicture;

    private FirebaseAuth mAuth;
    private DatabaseReference mReference;
    private StorageReference storageReference;

    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 2000;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // init mAuth && mReference && storageReference
        mAuth = FirebaseAuth.getInstance();
        mReference = FirebaseDatabase.getInstance().getReference("users");
        storageReference = FirebaseStorage.getInstance().getReference();

        mEmail = findViewById(R.id.input_email);
        mPassword = findViewById(R.id.input_password);
        mPseudo = findViewById(R.id.input_name);
        mPhone = findViewById(R.id.input_phone);
        mPicture = findViewById(R.id.profile_picture);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");

    }

    /**
     * Called to create the account
     * @param view
     */
    public void createAccount(View view) {
        String sEmail = mEmail.getText().toString();
        String sPassword = mPassword.getText().toString();
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(sEmail, sPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isComplete() && task.isSuccessful()) {
                            Log.d(TAG, "user uid : " + mAuth.getCurrentUser().getUid());
                            uploadPicture();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Login Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void uploadPicture() {
        if (filePath != null) {

            final StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());

            UploadTask uploadTask = ref.putFile(filePath);

            uploadTask
                    .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            return ref.getDownloadUrl();
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            createUser(task.getResult().toString());
                        }
                    });


        }
    }

    /**
     * Create User
     * @param url the image url
     */

    private void createUser(String url) {
        User user = new User();
        user.email = mEmail.getText().toString();
        user.phone = mPhone.getText().toString();
        user.pseudo = mPseudo.getText().toString();
        user.picture = url;
        mReference.child(mAuth.getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
               sendVerificationEmail();

            }
        });

    }

    /**
     * Send verification email
     */
    private void sendVerificationEmail() {
        mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, "Register Successfully", Toast.LENGTH_LONG).show();
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
    }

    /**
     * Navigate back to login
     */
    public void navigateLogin(View view) {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        finish();
    }

    /**
     * Open Picture chooser for image
     */
    public void choosePicture(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    /**
     * Launched after the choose of the image
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                mPicture.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
