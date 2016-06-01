package com.frodo.github.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.frodo.github.bean.dto.response.Repo;

import java.util.List;

/**
 * Created by frodo on 2016/4/30.
 */
public class ShowCase implements Parcelable {
    public static final Creator<ShowCase> CREATOR = new Creator<ShowCase>() {
        public ShowCase createFromParcel(Parcel source) {
            return new ShowCase(source);
        }

        public ShowCase[] newArray(int size) {
            return new ShowCase[size];
        }
    };

    public String name;
    public String slug;
    public String description;
    public String image_url;
    public String image;
    public List<Repo> repositories;

    public ShowCase() {
        super();
    }

    protected ShowCase(Parcel in) {
        this.name = in.readString();
        this.slug = in.readString();
        this.description = in.readString();
        this.image_url = in.readString();
        this.image = in.readString();
        this.repositories = in.createTypedArrayList(Repo.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.slug);
        dest.writeString(this.description);
        dest.writeString(this.image_url);
        dest.writeString(this.image);
        dest.writeString(this.image);
        dest.writeTypedList(repositories);
    }
}
