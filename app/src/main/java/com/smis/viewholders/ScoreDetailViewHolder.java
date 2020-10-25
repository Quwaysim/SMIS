package com.smis.viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.smis.R;

public class ScoreDetailViewHolder extends RecyclerView.ViewHolder {

    public TextView txt_name, txt_score;

    public ScoreDetailViewHolder(View itemView) {
        super(itemView);
        txt_name = itemView.findViewById(R.id.txt_name);
        txt_score = itemView.findViewById(R.id.txt_score);
    }
}
