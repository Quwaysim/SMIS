package com.smis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class automata4 extends Fragment {
    private Button mRun;
    private EditText ans1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.automata4_layout, container, false);

        mRun = view.findViewById(R.id.runBtn);
//        mNext = view.findViewById(R.id.nextBtn);
        ans1 = view.findViewById(R.id.ans1);

        mRun.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String answer = ans1.getText().toString();
                if (answer.equals("bb") || answer.equals("ababa") || answer.equals("abb")) {
                    Toast.makeText(getActivity(), "Accepted", Toast.LENGTH_SHORT).show();
                } else if (ans1.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Please, input a string", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Rejected", Toast.LENGTH_SHORT).show();
                }

            }
        }));

//        mNext.setOnClickListener((new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ((AutomataSimulatorActivity)getActivity()).setViewPager(3);
//            }
//        }));

        return view;
    }
}
