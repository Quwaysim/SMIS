package com.smis;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.smis.models.Files;
import com.smis.utilities.FilesAdapter;
import com.smis.utilities.UniversalImageLoader;
import com.smis.utilities.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class ClassroomActivity extends AppCompatActivity {

    private final int PICK_IMAGE_REQUEST = 71;
    ProgressBar mProgressBar;
    String UserName, userId, FileUrl, FileName, FilePath;
    ArrayList<Files> list;
    FilesAdapter adapter;
    DatabaseReference users;

    //int FILE_SELECT_CODE = 0;
    //Boolean okay = false;
    //StorageReference sReference = FirebaseStorage.getInstance().getReference();
    //DatabaseReference category;

    /* @Override
     public void getFilePath(Uri filePath) {
         if (!filePath.toString().equals("")) {
             mSelectedFileUrl = filePath;
             Log.d("TAG", "getFilePath: got the file uri: " + mSelectedFileUrl);
             //ImageLoader.getInstance().displayImage(filePath.toString(), mProfile_image);
         }
     } */

    FloatingActionButton select_file;
    private String mSecL;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Uri mSelectedFileUrl;
    private RecyclerView mRecyclerView;
    private DatabaseReference mDatabase, reference;
    private ProgressDialog pDialog;
    private long date;

    //private RecyclerView.LayoutManager mLayoutManager;
    //private FirebaseDatabase firebaseDatabase;
    //private double progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom);
        Log.d("TAG", "onCreate Started");

        FloatingActionButton upload_file = findViewById(R.id.upload_file_fab);
        reference = FirebaseDatabase.getInstance().getReference().child("files");
        select_file = findViewById(R.id.select_file_fab);

        users = FirebaseDatabase.getInstance().getReference("users");
        mProgressBar = findViewById(R.id.upload_progressBar);
        mRecyclerView = findViewById(R.id.rv_row);

        setupFirebaseAuth();
        initImageLoader();
        getFiles();

        /*
         * RecyclerView Implementation
         */
        mRecyclerView = findViewById(R.id.rv_row);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        date = new Date().getTime();

        select_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_IMAGE_REQUEST);

                //UploadFileDialog dialog = new UploadFileDialog();
                //dialog.show(getSupportFragmentManager(), getString(R.string.dialog_upload_file));
            }
        });

        /*upload_file.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedFileUrl != null) {
                    executeUploadTask();
                } else {
                    Toast.makeText(ClassroomActivity.this, "Please, first select a file to upload", Toast.LENGTH_SHORT).show();
                }
            }
        })); */
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mSelectedFileUrl = data.getData();
            FileName = data.getType();
            executeUploadTask();
        }
    }

    private void getFiles() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list = new ArrayList<>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Files s = dataSnapshot1.getValue(Files.class);
                    list.add(s);
                }
                adapter = new FilesAdapter(ClassroomActivity.this, list);
                mRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ClassroomActivity.this, "Opsss.... Something is wrong", Toast.LENGTH_SHORT).show();

            }
        });
    }

    //This method is used to start the upload task to the firebase storage
    private void executeUploadTask() {

        //specify where the photo will be stored
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        final StorageReference ref = FirebaseStorage.getInstance().getReference()
                .child("classroom_files/" + Math.random() * 100000 + mSelectedFileUrl.getPathSegments().get(0));
        UploadTask uploadTask = ref.putFile(mSelectedFileUrl);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    final Uri downloadUri = task.getResult();
                    FileUrl = downloadUri.toString();
                    FilePath = downloadUri.getPath();
                    progressDialog.dismiss();
                    sendFileDetailsToFirestore(UserName, FileName, FileUrl, FilePath, date);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ClassroomActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                        .getTotalByteCount());
                progressDialog.setMessage("Uploaded " + (int) progress + "%");
                //progressDialog.dismiss();
            }
        });
    }

    private void sendFileDetailsToFirestore(String UserName, String FileName, String FileUrl, String FilePath, long date) {
        displayLoader();
        //generating a random string
        Random r = new java.util.Random();
        String s = Long.toString(r.nextLong() & Long.MAX_VALUE, 36);
        Files file = new Files(UserName, FileName, FileUrl, FilePath, date);
        mDatabase.child("files").child(s).setValue(file)

                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        pDialog.dismiss();
                        Toast.makeText(ClassroomActivity.this, "File added successfully", Toast.LENGTH_SHORT).show();
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pDialog.dismiss();
                        Toast.makeText(ClassroomActivity.this, "An error has occured" + e.toString(), Toast.LENGTH_LONG).show();
                    }

                });
    }

    private void displayLoader() {
        pDialog = new ProgressDialog(ClassroomActivity.this);
        pDialog.setMessage("Adding File.. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

    }

    private void initImageLoader() {
        UniversalImageLoader imageLoader = new UniversalImageLoader(ClassroomActivity.this);
        ImageLoader.getInstance().init(imageLoader.getConfig());
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
            Intent intent = new Intent(ClassroomActivity.this, LoginActivity.class);
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
                    Intent intent = new Intent(ClassroomActivity.this, LoginActivity.class);
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
                    // phone = details.getPhone();
                    // email = details.getEmail();

                    //get user securityLevel here
                    mSecL = details.getSecurity_level();
                    if (!mSecL.equals("2")) {
                        select_file.hide();
                    }
                    //mNewSecurityLevel = Integer.parseInt(details.getSecurity_level());
                    //Toast.makeText(ClassroomActivity.this, " Classroom SL: " + mSecL, Toast.LENGTH_SHORT).show();


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            Toast.makeText(ClassroomActivity.this, "Error occured" + e.toString(), Toast.LENGTH_LONG).show();
        }
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

    public void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        if (mProgressBar.getVisibility() == View.VISIBLE) {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    /*
     * Upload process of the classroom files
     */
    /* @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Results when selecting new image from phone memory
        if (requestCode == FILE_SELECT_CODE && resultCode == Activity.RESULT_OK) {
            Uri selectedImageUri = data.getData();
             FileName = data.getType();
            Log.d(TAG, "onActivityResult: image: " + selectedImageUri);

        }
    } */
    /* final StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("classroom/" + Math.random() * 100000 + mSelectedFileUrl.getPathSegments().get(0) );

        // Create file metadata including the content type
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("classroom/files")
                .setContentLanguage("en") //see nodes below
                .build();

        //if the file size is valid then we can submit to database
        UploadTask uploadTask = null;
        uploadTask = storageReference.putFile(mSelectedFileUrl, metadata);
        //uploadTask = storageReference.putBytes(mBytes); //without metadata

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //Now insert the download url into the firebase database
                Task<Uri> firebaseURL = taskSnapshot.getStorage().getDownloadUrl();
                Toast.makeText(ClassroomActivity.this, "Upload Success", Toast.LENGTH_SHORT).show();
                okay = true;
                //mSelectedFileUrl = null;
                //Log.d("newTag", "onSuccess: firebase download url = " + firebaseURL.toString());
//                FirebaseDatabase.getInstance().getReference()
//                        .child(getString(R.string.dbnode_users))
//                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                        .child(getString(R.string.field_profile_image))
//                        .setValue(firebaseURL.toString());
                hideProgressBar();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(ClassroomActivity.this, "could not upload file", Toast.LENGTH_SHORT).show();
                hideProgressBar();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double currentProgress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                if (currentProgress > (progress + 15)) {
                    progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    Log.d(TAG, "onProgress: Upload is " + progress + "% done");
                    Toast.makeText(ClassroomActivity.this, progress + "%", Toast.LENGTH_SHORT).show();
                }
            }
        });
        */
    /*//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.sign_out:
//                FirebaseAuth.getInstance().signOut();
//                Log.d("TAG", "Signed Out Btn Clicked");
//                //Intent intent = new Intent(ClassroomActivity.this, LoginActivity.class);
//                //startActivity(intent);
//                //checkAuthenticationState();
//                return true;
//            case R.id.automata:
//                Log.d(TAG, "onOptionsItemSelected: automata");
//                Intent automata = new Intent(ClassroomActivity.this, AutomataSimulatorActivity.class);
//                startActivity(automata);
//                return true;
//            case R.id.chat:
//                Log.d("TAG", "Chat Button Clicked");
//                Intent intent = new Intent(ClassroomActivity.this, ChatActivity.class);
//                startActivity(intent);
//                return true;
//            case R.id.settings:
//                Log.d("TAG", "Settings Button Clicked!");
//                Intent settingsIntent = new Intent(ClassroomActivity.this, SettingsActivity.class);
//                startActivity(settingsIntent);
//                return true;
//            case R.id.quiz:
//                Log.d("TAG", "Quiz Button Clicked!");
//                users.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        Users user = dataSnapshot.child(userId).getValue(Users.class);
//                        Intent intent = new Intent(ClassroomActivity.this, QuizActivity.class);
//                        //TODO
//                        Common.currentUsers = user;
//                        startActivity(intent);
//
//                    }
//
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//
//
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }


    /*
     * This method here is for testing purposes only
     */
    /*//    public String downloadFile() {
//
//        sReference.child("classroom/" + FirebaseAuth.getInstance().getCurrentUser().getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//                downloadURL = uri.toString();
//                //DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
//                //ref.child(getString(R.string.dbnode_users)).child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                //.child(getString(R.string.field_profile_image)).setValue(uri.toString());
//                Toast.makeText(ClassroomActivity.this, "download url at: " + uri.toString(), Toast.LENGTH_SHORT).show();
//                // Got the download URL for 'users/me/profile.png'
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                Toast.makeText(ClassroomActivity.this, "Download Failed. Please Try Again Later", Toast.LENGTH_SHORT).show();
//                // Handle any errors
//            }
//        });
//        return downloadURL;
//    }


//    private void getUserDetails() {
//        try {
//            mDatabase.child("users").child(userId).addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    User details = dataSnapshot.getValue(User.class);
//                    UserName = details.getName();
//
//                    //get user securityLevel here
//                    mSecL = details.getSecurity_level();
//                    //mNewSecurityLevel = Integer.parseInt(details.getSecurity_level());
//                    Toast.makeText(ClassroomActivity.this, "SL: " + mSecL, Toast.LENGTH_SHORT).show();
//
//                    // phone = details.getPhone();
//                    // email = details.getEmail();
//
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                }
//            });
//        } catch (Exception e) {
//            //Toast.makeText(HomeActivity.this, "Error occured" + e.toString(), Toast.LENGTH_LONG).show();
//        }
//    }*/

}