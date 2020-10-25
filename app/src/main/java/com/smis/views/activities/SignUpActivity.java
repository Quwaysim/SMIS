package com.smis.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.smis.R;
import com.smis.data.User;

import static android.text.TextUtils.isEmpty;

public class SignUpActivity extends AppCompatActivity {
    //domain name for school email address
    //TODO Quwaysim change the domain name(s) back to futminna's
    private static final String lecturers_domain_name = "gmail.com";
    private static final String students_domain_name = "st.futminna.edu.ng";
    //widgets
    private EditText mEmail, mPassword, mConfirmPassword;
    private Button mSignUp;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        //referencing the widgets
        mEmail = findViewById(R.id.email_lecturer);
        mPassword = findViewById(R.id.password_lecturer);
        mConfirmPassword = findViewById(R.id.confirm_password_lec);
        mSignUp = findViewById(R.id.sign_up_btn_lec);
        mProgressBar = findViewById(R.id.SignUpProgressBar);

        mSignUp.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //avoiding passing of empty/null valued EditText Fields
                if (!isEmpty(mEmail.getText().toString()) && !isEmpty(mPassword.getText().toString()) && !isEmpty(mConfirmPassword.getText().toString())) {
                    if (isValidDomain(mEmail.getText().toString())) {
                        if (doStringsMatch(mPassword.getText().toString(), mConfirmPassword.getText().toString())) {
                            registerNewUser(mEmail.getText().toString(), mPassword.getText().toString());
                            //Toast.makeText(SignUpActivity.this, "It Works!", Toast.LENGTH_LONG).show();
                            redirectToSignIn();
                        } else {
                            Toast.makeText(SignUpActivity.this, "Passwords Do not Match!", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(SignUpActivity.this, "Please, Enter a Valid Lecturer or Student's Email Address!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(SignUpActivity.this, "All Fields Must be Filled!", Toast.LENGTH_LONG).show();
                }
            }
        }));
        hideSoftKeyboard();
    }

    private void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(SignUpActivity.this, "Verification Email Sent, Please Check Your Mail", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SignUpActivity.this, "Couldn't Send Verification Email", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    //creating new user (on firebase)
    private void registerNewUser(final String email, String password) {
        showProgressBar();
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //send email verification
                            sendVerificationEmail();
                            User user = new User();
                            //TODO
                            user.setName(email.substring(0, email.indexOf(".")));
                            user.setPhone("1");
                            user.setProfile_image("");
                            if ((email.substring(email.indexOf("@") + 1).equals(lecturers_domain_name))) {
                                user.setSecurity_level("2");
                            } else {
                                user.setSecurity_level("1");
                            }
                            user.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            FirebaseDatabase.getInstance().getReference()
                                    .child(getString(R.string.dbnode_users))
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    FirebaseAuth.getInstance().signOut();
                                    redirectToSignIn();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    FirebaseAuth.getInstance().signOut();
                                    redirectToSignIn();
                                    Toast.makeText(SignUpActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {

                            Toast.makeText(SignUpActivity.this, "Unable to Register, Something Went Wrong", Toast.LENGTH_LONG).show();
                        }
                        hideProgressBar();
                    }
                }
        );
    }

    //to check if the password input value is the same as the confirmPassword input value
    public boolean doStringsMatch(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }

    //to check if the email address entered is really a lecturer's email address
    public boolean isValidDomain(String email) {
        String domain = email.substring(email.indexOf("@") + 1).toLowerCase();
        boolean validDomain = false;
        if (domain.equals(lecturers_domain_name)) {
            validDomain = domain.equals(lecturers_domain_name);
        } else if (domain.equals(students_domain_name)) {
            validDomain = domain.equals(students_domain_name);
        }
        return validDomain;
    }

    //redirects to the sign in activity
    public void redirectToSignIn() {
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        //Quwaysim populate the sign In activity editTexts with the email and password
        //intent.putExtra(mEmail.getText().toString(),)
        startActivity(intent);
        finish();
    }

    public void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        if (mProgressBar.getVisibility() == View.VISIBLE) {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}