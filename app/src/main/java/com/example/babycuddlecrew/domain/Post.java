package com.example.babycuddlecrew.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Post implements Parcelable {

    @SerializedName("postId")
    private String postId;

    public Post(String title, String description, String category, double latitude, double longitude, String image, long timeFrom, long timeTo, String days, String phoneNumber, boolean isFavorite) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.latitude = latitude;
        this.longitude = longitude;
        this.image = image;
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
        this.days = days;
        this.phoneNumber = phoneNumber;
        this.isFavorite = isFavorite;
    }

    @SerializedName("userId")
    private String userId;
    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String description;
    @SerializedName("category")
    private String category;
    @SerializedName("latitude")
    private double latitude;
    @SerializedName("longitude")
    private double longitude;
    @SerializedName("image")
    private String image;
    @SerializedName("timefrom")
    private long timeFrom;
    @SerializedName("timeto")
    private long timeTo;
    @SerializedName("days")
    private String days;
    @SerializedName("phonenumber")
    private String phoneNumber;
    @SerializedName("isfavorite")
    private boolean isFavorite;

    protected Post(Parcel in) {
        postId = in.readString();
        userId = in.readString();
        title = in.readString();
        description = in.readString();
        category = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        image = in.readString();
        timeFrom = in.readLong();
        timeTo = in.readLong();
        days = in.readString();
        phoneNumber = in.readString();
        isFavorite = in.readByte() != 0;
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getTimeFrom() {
        return timeFrom;
    }

    public void setTimeFrom(long timeFrom) {
        this.timeFrom = timeFrom;
    }

    public long getTimeTo() {
        return timeTo;
    }

    public void setTimeTo(long timeTo) {
        this.timeTo = timeTo;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public Post() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(postId);
        parcel.writeString(userId);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(category);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeString(image);
        parcel.writeLong(timeFrom);
        parcel.writeLong(timeTo);
        parcel.writeString(days);
        parcel.writeString(phoneNumber);
        parcel.writeByte((byte) (isFavorite ? 1 : 0));
    }
}