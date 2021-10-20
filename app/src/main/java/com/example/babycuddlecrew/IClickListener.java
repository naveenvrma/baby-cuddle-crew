package com.example.babycuddlecrew;

public interface IClickListener {
    void onFavClickListener(int pos);

    void onItemClickListener(int pos);

    void onUploadCompleteListener(String filepath);
}
