package com.example.prm392app;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword, editTextName, editTextUniversity, editTextCompany;
    private Button buttonSignUp;
    private TextView textViewSignIn;
    private Spinner userTypeSpinner;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextName = findViewById(R.id.editTextName);
        editTextUniversity = findViewById(R.id.editTextUniversity);
        editTextCompany = findViewById(R.id.editTextCompany);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        textViewSignIn = findViewById(R.id.textViewSignIn);
        userTypeSpinner = findViewById(R.id.userType);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.user_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userTypeSpinner.setAdapter(adapter);

        userTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String userType = parent.getItemAtPosition(position).toString();
                editTextName.setVisibility(userType.equals("student") ? View.VISIBLE : View.GONE);
                editTextUniversity.setVisibility(userType.equals("student") ? View.VISIBLE : View.GONE);
                editTextCompany.setVisibility(userType.equals("recruiter") ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                editTextName.setVisibility(View.GONE);
                editTextUniversity.setVisibility(View.GONE);
                editTextCompany.setVisibility(View.GONE);
            }
        });

        buttonSignUp.setOnClickListener(v -> signUp());
        textViewSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
            startActivity(intent);
        });
    }

    private void signUp() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String userType = userTypeSpinner.getSelectedItem().toString();

        if (!isValidEmail(email)) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isValidPassword(password)) {
            Toast.makeText(this, "Password must be at least 6 characters, contain at least 1 uppercase letter, and 1 special character", Toast.LENGTH_LONG).show();
            return;
        }

        if (userType.equals("student")) {
            String name = editTextName.getText().toString().trim();
            String university = editTextUniversity.getText().toString().trim();
            if (name.isEmpty() || university.isEmpty()) {
                Toast.makeText(this, "Please enter your name and university", Toast.LENGTH_SHORT).show();
                return;
            }
            registerUser(userType, email, password, name, university, null);
        } else if (userType.equals("recruiter")) {
            String company = editTextCompany.getText().toString().trim();
            if (company.isEmpty()) {
                Toast.makeText(this, "Please enter your company name", Toast.LENGTH_SHORT).show();
                return;
            }
            registerUser(userType, email, password, null, null, company);
        }
    }

    private void registerUser(String role, String email, String password, String name, String university, String company) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        User user = new User(role, email, name, university, company);
                        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).setValue(user)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Sign up successful!", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> Toast.makeText(this, "Failed to save user data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(this, "Sign up failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        // Check for minimum length of 6
        if (password.length() < 6) {
            return false;
        }
        // Check for at least 1 uppercase letter
        Pattern upperCasePattern = Pattern.compile("[A-Z]");
        if (!upperCasePattern.matcher(password).find()) {
            return false;
        }
        // Check for at least 1 special character
        Pattern specialCharPattern = Pattern.compile("[!@#$%^&*(),.?\":{}|<>]");
        return specialCharPattern.matcher(password).find();
    }
}

class User {
    public String role, email, name, university, company;

    public User(String role, String email, String name, String university, String company) {
        this.role = role;
        this.email = email;
        this.name = name;
        this.university = university;
        this.company = company;
    }
}