package com.example.babycuddlecrew.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.babycuddlecrew.IClickListener;
import com.example.babycuddlecrew.R;
import com.example.babycuddlecrew.Utils;
import com.example.babycuddlecrew.domain.Post;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private final IClickListener onClickListener;
    //this context we will use to inflate the layout
    private Context mCtx;

    //we are storing all the products in a list
    private List<Post> postList;

    //getting the context and product list with constructor
    public PostAdapter(Context mCtx, List<Post> postList, IClickListener onClickListener) {
        this.mCtx = mCtx;
        this.postList = postList;
        this.onClickListener = onClickListener;
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.layout_post_cards, null);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {

        Post post = postList.get(position);
        holder.txtPostTitle.setText(post.getTitle());
        holder.txtCategory.setText(post.getCategory());
        Glide.with(mCtx).load(post.getImage()).into(holder.imgPost);
        setFavoriteIcon(holder.imgFavorite, post.getPostId());
    }

    private void setFavoriteIcon(ImageView imgFavorite, String postId) {
        ArrayList<String> favoritePosts = Utils.getCurrentUserFavoritePosts(mCtx);
        imgFavorite.setSelected(favoritePosts.contains(postId));
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }


    class PostViewHolder extends RecyclerView.ViewHolder {

        TextView txtPostTitle, txtCategory;
        ImageView imgPost, imgFavorite;

        public PostViewHolder(View itemView) {
            super(itemView);

            txtPostTitle = itemView.findViewById(R.id.txtPostTitle);
            txtCategory = itemView.findViewById(R.id.txtCategory);
            imgPost = itemView.findViewById(R.id.imgPost);
            imgFavorite = itemView.findViewById(R.id.imgFavorite);

            imgFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickListener != null) {
                        onClickListener.onFavClickListener(getAdapterPosition());
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickListener != null) {
                        onClickListener.onItemClickListener(getAdapterPosition());
                    }
                }
            });
        }
    }
}
