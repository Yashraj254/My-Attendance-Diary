package com.myFirstApp1.AttendanceDiary;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    TextInputEditText emailId, passwordId;
    CheckBox checkBox;
    Toast toast;
    TextView signUpId, forgotPassword;
    String mail;
    Button signIn;
    SharedPreferences.Editor editor;
    SharedPreferences sharedPref;
    private FirebaseAuth mAuth;
    private static final String TAG = "LOGINActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailId = findViewById(R.id.emailLogin);
        passwordId = findViewById(R.id.passwordLogin);

        signIn = findViewById(R.id.signId);
        signUpId = findViewById(R.id.signUpId);
        checkBox = findViewById(R.id.checkBox);
        forgotPassword = findViewById(R.id.forgotPassword);

        sharedPref = this.getSharedPreferences("Check", MODE_PRIVATE);
        editor = sharedPref.edit();

        SharedPreferences sharedPreferences = getSharedPreferences("Check", MODE_PRIVATE);

        boolean checkPassword = sharedPreferences.getBoolean("Checked", false);


        String email = sharedPreferences.getString("Email", null);
        String password = sharedPreferences.getString("Password", null);
        mAuth = FirebaseAuth.getInstance();

        if (checkPassword) {
            checkBox.setChecked(true);
            if (email != null && password != null) {
                emailId.setText(email);
                passwordId.setText(password);
            }
        }

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText resetPassword = new EditText(v.getContext());
                AlertDialog.Builder passwordReset = new AlertDialog.Builder(v.getContext());
                passwordReset.setTitle("Reset password?");
                passwordReset.setMessage("Enter your mail id to receive the password reset link.");
                passwordReset.setView(resetPassword);

                passwordReset.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mail = resetPassword.getText().toString();
                        if (mail != null)
                            Log.d(TAG, "Enter string: " + mail);
                        if (mail.length() == 0) {
                            toastMethod("Enter your email.");
                        } else {
                            mAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    toastMethod("Password reset link has been sent on your email.");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    if (!Common.isConnectedToInternet(getApplicationContext())) {
                                        toastMethod("Check your network connection.");
                                    } else {
                                        toastMethod("Invalid email.");
                                    }
                                }
                            });
                        }
                    }
                });
                passwordReset.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                passwordReset.create().show();
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailId.getText().toString();
                String password = passwordId.getText().toString();
                if (email == null || password == null) {
                    toastMethod("Enter your email and password.");
                }

                mAuth = FirebaseAuth.getInstance();
                try {
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's informationbar.setVisibility(View.INVISIBLE);
                                        if(mAuth.getCurrentUser().isEmailVerified())
                                        {

                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);}
                                        else{
                                            toastMethod("Please verify your email id.");
                                        }
                                    } else {
                                        // If sign in fails, display a message to the user.

                                        if (!Common.isConnectedToInternet(getApplicationContext())) {
                                            toastMethod("Check your network connection.");
                                        } else {
                                            toastMethod("Invalid email or password.");
                                        }
                                    }
                                }

                            });
                } catch (Exception e) {
                    toastMethod("Enter email id and password.");

                }
                if (checkBox.isChecked()) {
                    editor.putBoolean("Checked", true);
                    editor.putString("Email", email);
                    editor.putString("Password", password);
                    editor.apply();
                } else {
                    editor.putBoolean("Checked", false);
                    editor.putString("Email", email);
                    editor.putString("Password", password);
                    editor.apply();
                }
            }
        });
    }

    public void onSignUp(View view) {

        Intent intent = new Intent(LoginActivity.this, SignUp.class);
        startActivity(intent);
    }

    public void toastMethod(String toastMessage) {
        if (toast != null)
            toast.cancel();
        toast = Toast.makeText(LoginActivity.this, toastMessage, Toast.LENGTH_SHORT);
        toast.show();
    }


    @Override
    protected void onPause() {
        if(toast!=null)
        toast.cancel();
        super.onPause();
    }


}