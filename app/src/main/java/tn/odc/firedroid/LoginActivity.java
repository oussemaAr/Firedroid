package tn.odc.firedroid;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText mEmail;
    private EditText mPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Init authentication module
        mAuth = FirebaseAuth.getInstance();

        mEmail = findViewById(R.id.input_email);
        mPassword = findViewById(R.id.input_password);
    }


    /**
     * Make Login request
     */
    public void makeLogin(View view) {
        String sEmail = mEmail.getText().toString();
        String sPassword = mPassword.getText().toString();
        mAuth.signInWithEmailAndPassword(sEmail, sPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isComplete() && task.isSuccessful()) {
                            if (mAuth.getCurrentUser().isEmailVerified()) {
                                Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_LONG).show();
                                Log.d(TAG, "user uid : " + mAuth.getCurrentUser().getUid());
                                startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
                            } else {
                                sendVerificationEmail();
                            }

                        } else {
                            Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /**
     * Start navigation to register activity
     */
    public void navigateRegister(View view) {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        finish();
    }


    /**
     * Send Verification mail
     */
    private void sendVerificationEmail() {
        mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(LoginActivity.this, "Please verify your email", Toast.LENGTH_SHORT).show();
            }
        });
    }
}