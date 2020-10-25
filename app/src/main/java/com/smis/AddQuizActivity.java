package com.smis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.smis.models.Category;
import com.smis.models.Question;

import java.nio.charset.Charset;
import java.util.Random;

public class AddQuizActivity extends AppCompatActivity {
    private EditText editQuizName, editQuestion, editOptionA, editOptionB, editOptionC, editOptionD, editCorrectOption;
    private String Question,AnswerA,AnswerB,AnswerC,AnswerD,CorrectAnswer,CategoryId,Name, IsImageQuestion = "No";
    private DatabaseReference mDatabase, reference;
    private FirebaseDatabase database;
    private RelativeLayout layout;
    private Integer value;
    private ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_quiz);
        layout = findViewById(R.id.layout);
        editQuizName = findViewById(R.id.quizName);
        editQuestion = findViewById(R.id.question);
        editOptionA = findViewById(R.id.optionA);
        editOptionB = findViewById(R.id.optionB);
        editOptionC = findViewById(R.id.optionC);
        editOptionD = findViewById(R.id.optionD);
        editCorrectOption = findViewById(R.id.correctOption);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Quiz").child("Questions");
        reference = FirebaseDatabase.getInstance().getReference().child("categories");
        //generating a random string for Id
        Random r = new java.util.Random ();
        CategoryId = Long.toString (r.nextLong () & Long.MAX_VALUE, 36);
    }

    public void Next(View view) {
        Name = editQuizName.getText().toString().toUpperCase();
        if (TextUtils.isEmpty(Name)){
            Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show();
        }else {
            layout.setVisibility(View.GONE);
            value = 1;
        }
    }

    public void Submit(View view) {

        Question = editQuestion.getText().toString();
        AnswerA = editOptionA.getText().toString();
        AnswerB = editOptionB.getText().toString();
        AnswerC = editOptionC.getText().toString();
        AnswerD = editOptionD.getText().toString();
        CorrectAnswer = editCorrectOption.getText().toString();

        if (!(TextUtils.isEmpty(Question) && TextUtils.isEmpty(AnswerA) && TextUtils.isEmpty(AnswerB) && TextUtils.isEmpty(AnswerC) &&
                TextUtils.isEmpty(AnswerD) && TextUtils.isEmpty(CorrectAnswer))) {
            if (value == 1){
                AddCategory(Name);
            }
            AddToDatabase(Question,AnswerA,AnswerB,AnswerC,AnswerD,CorrectAnswer,CategoryId, IsImageQuestion);
        }else {
            Toast.makeText(this, "Invalid input in your fields", Toast.LENGTH_SHORT).show();
        }
    }

    private void AddCategory(String Name) {
        Category details = new Category(Name);
        reference.child(CategoryId).setValue(details).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
               // displayDialog();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddQuizActivity.this, "An error has occured" + e.toString(), Toast.LENGTH_LONG).show();
                        layout.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void AddToDatabase(String Question, String AnswerA, String AnswerB, String AnswerC, String AnswerD, String CorrectAnswer, String CategoryId, String IsImageQuestion) {
        displayLoader();
        Random r = new java.util.Random ();
        String Id = Long.toString (r.nextLong () & Long.MAX_VALUE, 36);
        Question details = new Question(Question, AnswerA, AnswerB, AnswerC, AnswerD, CorrectAnswer, CategoryId, IsImageQuestion);
        mDatabase.child(Id).setValue(details).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                displayDialog();
                value = 2;
                pDialog.dismiss();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddQuizActivity.this, "An error has occured" + e.toString(), Toast.LENGTH_LONG).show();
                      pDialog.dismiss();
                    }
                });
    }

    private void displayDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AddQuizActivity.this);
        alertDialogBuilder.setTitle("Data sent successfully");
        alertDialogBuilder
                .setCancelable(false)
                .setIcon(R.drawable.ic_done)
                .setMessage("Do you wish to add another question?")
                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                onBackPressed();
                            }
                        })
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                editQuestion.setText("");
                                Question = "";
                                editOptionA.setText("");
                                AnswerA = "";
                                editOptionB.setText("");
                                AnswerB = "";
                                editOptionC.setText("");
                                AnswerC = "";
                                editOptionD.setText("");
                                AnswerD = "";
                                editCorrectOption.setText("");
                                CorrectAnswer = "";
                            }
                        });


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void displayLoader() {
        pDialog = new ProgressDialog(AddQuizActivity.this);
        pDialog.setMessage("Sending data.. Please wait...");
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        pDialog.show();

    }
}
