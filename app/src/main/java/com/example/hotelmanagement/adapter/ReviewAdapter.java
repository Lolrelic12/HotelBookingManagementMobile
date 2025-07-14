package com.example.hotelmanagement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hotelmanagement.R;
import com.example.hotelmanagement.dto.ReviewReponse;
import java.util.List;
import java.util.Locale;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private List<ReviewReponse> reviewList;

    public ReviewAdapter(List<ReviewReponse> reviewList) {
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        ReviewReponse review = reviewList.get(position);
        holder.tvUserName.setText(review.getUserName() != null ?
                review.getUserName() : "Anonymous");
        holder.ratingBar.setRating((float) review.getRating());
        holder.tvRating.setText(String.format(Locale.getDefault(), "%.1f", review.getRating()));
        holder.tvContent.setText(review.getContent() != null ?
                review.getContent() : "No comment");
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvRating, tvContent;
        RatingBar ratingBar;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvContent = itemView.findViewById(R.id.tvContent);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}