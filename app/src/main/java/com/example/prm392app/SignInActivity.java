package com.example.prm392app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.regex.Pattern;

public class SignInActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button buttonSignIn;
    private TextView textViewSignUp;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonSignIn = findViewById(R.id.buttonSignIn);
        textViewSignUp = findViewById(R.id.textViewSignUp);

        buttonSignIn.setOnClickListener(v -> signIn());
        textViewSignUp.setOnClickListener(v -> {
            Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    private void signIn() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (!isValidEmail(email)) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String userId = mAuth.getCurrentUser().getUid();
                        mDatabase.child("users").child(userId).get().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                DataSnapshot snapshot = task1.getResult();
                                String role = snapshot.child("role").getValue(String.class);
                                if ("student".equals(role)) {
                                    Toast.makeText(this, "Logged in as student!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else if ("recruiter".equals(role)) {
                                    Toast.makeText(this, "Logged in as recruiter!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(SignInActivity.this, RecruiterActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(this, "Invalid role, logging out...", Toast.LENGTH_SHORT).show();
                                    mAuth.signOut();
                                }
                            } else {
                                Toast.makeText(this, "Error fetching role: " + task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                mAuth.signOut();
                            }
                        });
                    } else {
                        Toast.makeText(this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }
}
