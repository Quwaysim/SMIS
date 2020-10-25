package com.smis.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.smis.views.activities.DownloadActivity;
import com.smis.R;
import com.smis.data.Files;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.MyviewHolder> {

    private static final int WRITE_REQUEST_CODE = 300;
    Context context;
    ArrayList<Files> posts;
    String url;

    public FilesAdapter(Context c, ArrayList<Files> s) {
        context = c;
        posts = s;
    }

    @NonNull
    @Override
    public MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyviewHolder(LayoutInflater.from(context).inflate(R.layout.files_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyviewHolder holder, int position) {
        holder.userName.setText(posts.get(position).getUserName());
        holder.fileName.setText(posts.get(position).getFileName());

        Picasso.with(context)
                .load(posts.get(position).getFileUrl())
                .placeholder(R.drawable.file)
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }


    class MyviewHolder extends RecyclerView.ViewHolder {
        TextView userName, fileName, downloadText;
        ImageView imageView;

        public MyviewHolder(final View view) {
            super(view);
            userName = view.findViewById(R.id.name);
            fileName = view.findViewById(R.id.filename);
            downloadText = view.findViewById(R.id.download);
            imageView = view.findViewById(R.id.image);
            //view = (CardView) view.findViewById(R.id.card);
           /* itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        //String fileName = posts.get(pos).getFileName();
                        //if (fileName.equals("image")) {


                  //  }
                }
        }


    }); */

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int pos = getAdapterPosition();
                    url = posts.get(pos).getFileUrl();
                    String fileName = posts.get(pos).getFileName();
                    Intent intent = new Intent(view.getContext(), DownloadActivity.class);
                    intent.putExtra("url", url);
                    intent.putExtra("file", fileName);
                    view.getContext().startActivity(intent);


                }

            });
        }
    }
}