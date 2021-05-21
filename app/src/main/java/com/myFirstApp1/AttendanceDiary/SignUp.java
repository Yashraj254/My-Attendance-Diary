package com.myFirstApp1.AttendanceDiary;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class SignUp extends AppCompatActivity {
    TextInputEditText emailId, passwordId;
    TextView textView;
    Toast toast;
    Button signIn;
    private FirebaseAuth mAuth;
    private static final String TAG = "SignUp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        emailId = findViewById(R.id.emailLogin);
        passwordId = findViewById(R.id.passwordLogin);

        signIn = (AppCompatButton) findViewById(R.id.signId);
        textView = findViewById(R.id.textView);



        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailId.getText().toString();
                String password = passwordId.getText().toString();


                mAuth = FirebaseAuth.getInstance();
                
                try {
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                           toastMethod("An email has been sent to your mail id for verification.");

                                            }
                                        });
                                        // Sign in success, update UI with the signed-in user's information


                                    } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                        if(mAuth.getCurrentUser().isEmailVerified())
                                       toastMethod("User with this email already registered.");
                                        else
                                            toastMethod("An email has been sent to your mail id for verification.");

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        if (!Common.isConnectedToInternet(getApplicationContext())) {
                                         toastMethod("Check your network connection.");
                                        } else {
                                           toastMethod("Check your email id and password");
                                        }

                                    }
                                }
                            });
                } catch (Exception e) {
                  toastMethod("Enter email id and password.");

                }
            }
        });
    }
    public  void toastMethod(String toastMessage)
    {
        if(toast!=null)
            toast.cancel();
        toast = Toast.makeText(SignUp.this,toastMessage,Toast.LENGTH_SHORT);
        toast.show();
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (toast!=null)
        toast.cancel();
    }

}