package com.smis.views.dialogs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.smis.R;
import com.smis.data.ChatMessage;
import com.smis.data.Chatroom;
import com.smis.views.activities.ChatActivity;
import com.smis.data.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class NewQuizDialog extends DialogFragment {
    private static final String TAG = "NewQuizDialog";
    //private SeekBar mSeekBar;
    private EditText mQuizName;
    private TextView mCreateQuiz, mSecurityLevel;
    private int mUserSecurityLevel;
    //private int mSeekProgress;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_new_chatroom, container, false);
        mQuizName = view.findViewById(R.id.input_quiz_name);
        //mSeekBar = view.findViewById(R.id.input_security_level);
        mCreateQuiz = view.findViewById(R.id.create_quiz);
        //mSecurityLevel = view.findViewById(R.id.security_level);
        //mSeekProgress = 0;
        //mSecurityLevel.setText(String.valueOf(mSeekProgress));
        getUserSecurityLevel();

        mCreateQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!mQuizName.getText().toString().equals("")) {
                    Log.d(TAG, "onClick: creating new chat room");


                    if (mUserSecurityLevel >= 2) {

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                        //get the new chatroom unique id
                        String chatroomId = reference
                                .child(getString(R.string.dbnode_quizzes))
                                .push().getKey();

                        //create the chatroom
                        Chatroom chatroom = new Chatroom();
                        //chatroom.setSecurity_level(String.valueOf(mSeekBar.getProgress()));
                        chatroom.setChatroom_name(mQuizName.getText().toString());
                        chatroom.setCreator_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        chatroom.setChatroom_id(chatroomId);


                        //insert the new chatroom into the database
                        reference
                                .child(getString(R.string.dbnode_chatrooms))
                                .child(chatroomId)
                                .setValue(chatroom);

                        //create a unique id for the message
                        String messageId = reference
                                .child(getString(R.string.dbnode_chatrooms))
                                .push().getKey();

                        //insert the first message into the chatroom
                        ChatMessage message = new ChatMessage();

                        message.setMessage("Welcome to the new chatroom!");
                        message.setTimestamp(getTimestamp());
                        reference
                                .child(getString(R.string.dbnode_chatrooms))
                                .child(chatroomId)
                                .child(getString(R.string.field_chatroom_messages))
                                .child(messageId)
                                .setValue(message);
                        //((ChatActivity) getActivity()).init();
                        ((ChatActivity) getActivity()).getChatrooms();
                        getDialog().dismiss();
                    } else {
                        Toast.makeText(getActivity(), "Insufficient security level", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getActivity(), "Chatroom name cannot be empty", Toast.LENGTH_SHORT).show();
                }

            }
        });

//        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                mSeekProgress = i;
//                mSecurityLevel.setText(String.valueOf(mSeekProgress));
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });

        return view;
    }

    private void getUserSecurityLevel() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference.child(getString(R.string.dbnode_users))
                .orderByKey()
                //OR could use ->.orderByChild(getString(R.string.field_user_id))
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //alternatively could have used:
                //DataSnapshot singleSnapshot = dataSnapshot.getChildren().iterator().next();
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: users security level: "
                            + singleSnapshot.getValue(User.class).getSecurity_level());

                    mUserSecurityLevel = Integer.parseInt(String.valueOf(
                            singleSnapshot.getValue(User.class).getSecurity_level()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    //Return the current timestamp in the form of a string

    private String getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));
        return sdf.format(new Date());
    }
}
