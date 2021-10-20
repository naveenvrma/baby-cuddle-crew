package com.example.babycuddlecrew.domain;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class User implements Parcelable {

    @SerializedName("userId")
    private String userId;

    @SerializedName("profileImage")
    private String profileImage;

    @SerializedName("firstName")
    private String firstName;

    @SerializedName("lastName")
    private String lastName;

    @SerializedName("mail")
    private String mail;

    @SerializedName("role")
    private String role;

    @SerializedName("gender")
    private String gender;

    @SerializedName("username")
    private String username;

    @SerializedName("password")
    private String password;

    @SerializedName("phoneNumber")
    private String phoneNumber;

    @SerializedName("favoritePosts")
    private String favoritePosts;

    public User (){

    }

    public User(String profileImage, String firstName, String lastName, String mail, String gender,
                String role, String username, String password, String phoneNumber, String favoritePosts) {
       this.profileImage = profileImage;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mail = mail;
        this.role = role;
        this.gender = gender;
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.favoritePosts = favoritePosts;
    }

    protected User(Parcel in) {
        profileImage = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        mail = in.readString();
        role = in.readString();
        gender = in.readString();
        username = in.readString();
        password = in.readString();
        phoneNumber = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFavoritePosts() {
        return favoritePosts;
    }

    public void setFavoritePosts(String favoritePosts) {
        this.favoritePosts = favoritePosts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;

        if (getProfileImage() != null ? !getProfileImage().equals(user.getProfileImage()) : user.getProfileImage() != null)
            return false;
        if (getFirstName() != null ? !getFirstName().equals(user.getFirstName()) : user.getFirstName() != null)
            return false;
        if (getLastName() != null ? !getLastName().equals(user.getLastName()) : user.getLastName() != null)
            return false;
        if (getMail() != null ? !getMail().equals(user.getMail()) : user.getMail() != null)
            return false;
        if (getRole() != null ? !getRole().equals(user.getRole()) : user.getRole() != null)
            return false;
        if (getGender() != null ? !getGender().equals(user.getGender()) : user.getGender() != null)
            return false;
        if (getUsername() != null ? !getUsername().equals(user.getUsername()) : user.getUsername() != null)
            return false;
        if (getPassword() != null ? !getPassword().equals(user.getPassword()) : user.getPassword() != null)
            return false;
        return getPhoneNumber() != null ? getPhoneNumber().equals(user.getPhoneNumber()) : user.getPhoneNumber() == null;
    }

    @Override
    public String toString() {
        return "User{" +
                "ProfileImage='" + profileImage + '\'' +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", mail='" + mail + '\'' +
                ", role='" + role + '\'' +
                ", gender='" + gender + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(profileImage,firstName, lastName, mail, role, gender, username, password, phoneNumber);
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(profileImage);
        parcel.writeString(firstName);
        parcel.writeString(lastName);
        parcel.writeString(mail);
        parcel.writeString(role);
        parcel.writeString(gender);
        parcel.writeString(username);
        parcel.writeString(password);
        parcel.writeString(phoneNumber);
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

}
