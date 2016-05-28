package com.frodo.github.bean.dto.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GollumPage extends ShaUrl implements Parcelable {

    public static final Creator<GollumPage> CREATOR = new Creator<GollumPage>() {
        public GollumPage createFromParcel(Parcel source) {
            return new GollumPage(source);
        }

        public GollumPage[] newArray(int size) {
            return new GollumPage[size];
        }
    };
    @JsonProperty("page_name")
    public String name;
    public String title;
    public String summary;
    public String action;

    public GollumPage() {
    }

    protected GollumPage(Parcel in) {
        super(in);
        this.name = in.readString();
        this.title = in.readString();
        this.summary = in.readString();
        this.action = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.name);
        dest.writeString(this.title);
        dest.writeString(this.summary);
        dest.writeString(this.action);
    }
}
