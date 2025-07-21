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
import android.content.Context;
import com.example.hotelmanagement.extensions.JwtHelper;
import com.example.hotelmanagement.services.api.TokenManager;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private List<ReviewReponse> reviewList;
    private Context context;
    private String currentUserId;
    private String currentUserName;

    public ReviewAdapter(List<ReviewReponse> reviewList, Context context) {
        this.reviewList = reviewList;
        this.context = context;
        initCurrentUserInfo();
    }

    private void initCurrentUserInfo() {
        try {
            TokenManager tokenManager = new TokenManager(context);
            String token = tokenManager.getToken();
            if (token != null && !token.isEmpty()) {
                JwtHelper jwtHelper = new JwtHelper(token);
                if (!jwtHelper.isExpired()) {
                    currentUserId = jwtHelper.getUserId();
                    currentUserName = jwtHelper.getUsername();
                }
            }
        } catch (Exception e) {
            currentUserId = null;
            currentUserName = null;
        }
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

        String displayName;
        if (currentUserId != null && currentUserId.equals(review.getUserId())) {
            displayName = "You (" + (currentUserName != null ? currentUserName : "Me") + ")";
        } else {
            displayName = review.getUserName() != null && !review.getUserName().trim().isEmpty()
                    ? review.getUserName()
                    : "Anonymous User";
        }

        holder.tvUserName.setText(displayName);
        holder.ratingBar.setRating((float) review.getRating());
        holder.tvRating.setText(String.format(Locale.getDefault(), "%.1f", review.getRating()));
        holder.tvContent.setText(review.getContent() != null && !review.getContent().trim().isEmpty()
                ? review.getContent()
                : "No comment");
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