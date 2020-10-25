package com.smis.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smis.Common;
import com.smis.ItemClickListener;
import com.smis.R;
import com.smis.RankingCallback;
import com.smis.ScoreDetail;
import com.smis.ViewHolder.RankingViewHolder;
import com.smis.models.QuestionScore;
import com.smis.models.Ranking;


/**
 * A simple {@link Fragment} subclass.
 */
public class RankingFragment extends Fragment {

    DatabaseReference questionScore, rankingTbl;
    int sum = 0;
    RecyclerView rankingList;
    LinearLayoutManager layoutManager;
    FirebaseRecyclerAdapter<Ranking, RankingViewHolder> adapter;


    public RankingFragment() {

    }

    public static RankingFragment rankingFragment() {
        RankingFragment rankingFragment = new RankingFragment();
        return rankingFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        questionScore = FirebaseDatabase.getInstance().getReference().child("Quiz").child("Question_Score");
        rankingTbl = FirebaseDatabase.getInstance().getReference().child("Quiz").child("Ranking");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_ranking, container, false);
        rankingList = view.findViewById(R.id.rakinglist);
        layoutManager = new LinearLayoutManager(getActivity());
        rankingList.setHasFixedSize(true);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        rankingList.setLayoutManager(layoutManager);


        updateScore(Common.currentUsers.getName(), new RankingCallback<Ranking>() {
            @Override
            public void callBack(Ranking ranking) {
                rankingTbl.child(ranking.getName())
                        .setValue(ranking);
//                showRanking();

            }
        });

        adapter = new FirebaseRecyclerAdapter<Ranking, RankingViewHolder>(
                Ranking.class,
                R.layout.layout_ranking,
                RankingViewHolder.class,
                rankingTbl.orderByChild("score")
        ) {
            @Override
            protected void populateViewHolder(RankingViewHolder viewHolder, final Ranking model, int position) {
                viewHolder.txt_name.setText(model.getName());
                viewHolder.txt_score.setText(String.valueOf(model.getScore()));
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongPressed) {
                        Intent scoreDetail = new Intent(getActivity(), ScoreDetail.class);
                        scoreDetail.putExtra("viewUser", model.getName());
                        startActivity(scoreDetail);
                    }
                });
            }
        };

        adapter.notifyDataSetChanged();
        rankingList.setAdapter(adapter);
        return view;
    }

    private void updateScore(final String userName, final RankingCallback<Ranking> callback) {
        questionScore.orderByChild("user").equalTo(userName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            QuestionScore ques = data.getValue(QuestionScore.class);
                            sum += Integer.parseInt(ques.getScore());
                        }
                        Ranking ranking = new Ranking(userName, sum);
                        callback.callBack(ranking);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


    }

}
