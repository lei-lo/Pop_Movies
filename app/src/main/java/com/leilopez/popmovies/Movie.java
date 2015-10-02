package com.leilopez.popmovies;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {

    private String mId;
    private String mImagePath;
    private String mTitle;
    private String mReleaseDate;
    private String mVoteAverage;
    private String mOverview;

    public Movie() {
    }
    public Movie(String id,
                 String imagePath,
                 String title,
                 String releaseDate,
                 String voteAverage,
                 String overview) {

        this.setId(id);
        this.setImagePath(imagePath);
        this.setTitle(title);
        this.setReleaseDate(releaseDate);
        this.setVoteAverage(voteAverage);
        this.setOverview(overview);
    }

    public Movie(Parcel in) {
        mId = in.readString();
        mImagePath = in.readString();
        mTitle = in.readString();
        mReleaseDate = in.readString();
        mVoteAverage = in.readString();
        mOverview = in.readString();
    }

    public static final Parcelable.Creator<Movie> CREATOR
            = new Parcelable.ClassLoaderCreator<Movie>() {

        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie createFromParcel(Parcel in, ClassLoader classLoader) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mImagePath);
        dest.writeString(mTitle);
        dest.writeString(mReleaseDate);
        dest.writeString(mVoteAverage);
        dest.writeString(mOverview);
    }

    public String getId() {
        return mId;
    }

    public void setId(String mId) {
        this.mId = mId;
    }

    public String getImagePath() {
        return mImagePath;
    }

    public void setImagePath(String mImagePath) {
        this.mImagePath = mImagePath;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public void setReleaseDate(String mReleaseDate) {
        this.mReleaseDate = mReleaseDate;
    }

    public String getVoteAverage() {
        return mVoteAverage;
    }

    public void setVoteAverage(String mVoteAverage) {
        this.mVoteAverage = mVoteAverage;
    }

    public String getOverview() {
        return mOverview;
    }

    public void setOverview(String mOverview) {
        this.mOverview = mOverview;
    }
}
