package com.smis;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.smis.models.Users;
import com.smis.utilities.UniversalImageLoader;
import com.smis.utilities.User;

public class HomeActivity extends AppCompatActivity {
    //private int mNewSecurityLevel;
    Button mClassroom, mQuiz, mChat, mAutomata, mSettings, mSignOut;
    String userId, UserName;
    private String mSecL;
    private String TAG = "HomeActivity";
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase, reference, users;
    //DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initImageLoader();
        getUserSecurityLevel();
        getUserDetails();
        setupFirebaseAuth();
        users = FirebaseDatabase.getInstance().getReference("users");
        User user = new User();
        user.setSecurity_level(mSecL);

        //Toast.makeText(this, "SECURITY LEVEL IS: " + mNewSecurityLevel,Toast.LENGTH_SHORT).show();
        mClassroom = findViewById(R.id.classroomBtn);
        mQuiz = findViewById(R.id.quizBtn);
        mChat = findViewById(R.id.chatBtn);
        mAutomata = findViewById(R.id.automataBtn);
        mSettings = findViewById(R.id.settingsBtn);
        mSignOut = findViewById(R.id.signOutBtn);


        mClassroom.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ClassroomActivity.class);
                startActivity(intent);
            }
        }));

        mQuiz.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                users.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Users user = dataSnapshot.child(userId).getValue(Users.class);
                        Intent intent = new Intent(HomeActivity.this, QuizActivity.class);
                        //TODO
                        Common.currentUsers = user;
                        startActivity(intent);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }));

        mChat.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(HomeActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        }));

        mAutomata.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AutomataSimulatorActivity.class);
                startActivity(intent);
            }
        }));

        mSettings.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        }));

        mSignOut.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseAuth.getInstance().signOut();
            }
        }));
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
        //isActivityRunning = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
        //isActivityRunning = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAuthenticationState();
    }

    private void checkAuthenticationState() {
        Log.d("TAG", "Check Auth State: checking auth state.");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.d("TAG", "checkAuthState: user is null, redirecting to Login Activity");
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            Log.d("TAG", "Users is Authenticated.");

        }
    }

    private void setupFirebaseAuth() {
        Log.d("TAG", "setupFirebaseAuth: started.");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    userId = user.getUid();
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    getUserDetails();
                    Log.d("TAG", "onAuthStateChanged:signed_in:" + user.getUid());

                } else {
                    Log.d("TAG", "onAuthStateChanged:signed_out");
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }

    private void getUserDetails() {
        try {
            mDatabase.child("users").child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User details = dataSnapshot.getValue(User.class);
                    UserName = details.getName();
                    //TODO
                    //get user securityLevel here
                    mSecL = details.getSecurity_level();
                    //mNewSecurityLevel = Integer.parseInt(details.getSecurity_level());
                    //Toast.makeText(HomeActivity.this, "SL: " + mSecL, Toast.LENGTH_SHORT).show();

                    // phone = details.getPhone();
                    // email = details.getEmail();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            //Toast.makeText(HomeActivity.this, "Error occured" + e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * init universal image loader
     */
    private void initImageLoader() {
        UniversalImageLoader imageLoader = new UniversalImageLoader(HomeActivity.this);
        ImageLoader.getInstance().init(imageLoader.getConfig());
    }


    private void getUserSecurityLevel() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.dbnode_users))
                .orderByChild(getString(R.string.field_user_id))
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: datasnapshot: " + dataSnapshot);
                DataSnapshot singleSnapshot = dataSnapshot.getChildren().iterator().next();
                int securityLevel = Integer.parseInt(singleSnapshot.getValue(User.class).getSecurity_level());
                Log.d(TAG, "onDataChange: user has a security level of: " + securityLevel);
                //mSecurityLevel = securityLevel;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
