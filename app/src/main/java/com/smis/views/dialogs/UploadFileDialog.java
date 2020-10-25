package com.smis.views.dialogs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.smis.R;

public class UploadFileDialog extends DialogFragment {
    private static final String TAG = "UploadFileDialog";
    private static final int FILE_SELECT_CODE = 0;//random number
    OnFileReceivedListener mOnFileReceived;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.upload_file_dialog, container, false);

        //Initialize the textview for choosing an image from memory
        TextView uploadFile = view.findViewById(R.id.upload_textview);
        uploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: accessing phones memory.");
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, FILE_SELECT_CODE);
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Results when selecting new image from phone memory
        if (requestCode == FILE_SELECT_CODE && resultCode == Activity.RESULT_OK) {
            Uri selectedFileUri = data.getData();
            String FileName = data.getType();
            Log.d(TAG, "onActivityResult: image: " + selectedFileUri);

            //send the fileuri and fragment to the interface
            mOnFileReceived.getFilePath(selectedFileUri);
            showToast();
            getDialog().dismiss();
        }
    }

    @Override
    public void onAttach(Context context) {
        try {
            mOnFileReceived = (OnFileReceivedListener) getActivity();
        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException", e.getCause());
        }
        super.onAttach(context);
    }

    public void showToast() {
        Toast.makeText(getContext(), "File selected, Please press the upload button to begin upload", Toast.LENGTH_SHORT).show();
    }

    public interface OnFileReceivedListener {
        void getFilePath(Uri filePath);
    }
}
