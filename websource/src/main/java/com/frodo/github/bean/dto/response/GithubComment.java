package com.frodo.github.bean.dto.response;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Bernat on 23/08/2014.
 */
public class GithubComment extends ShaUrl implements Parcelable {

    private static final int MAX_MESSAGE_LENGHT = 146;
    public String id;
    public String body;
    public String body_html;
    public User user;
    public Date created_at;
    public Date updated_at;

    public GithubComment() {
    }

    protected GithubComment(Parcel in) {
        super(in);
        this.id = in.readString();
        this.body = in.readString();
        this.body_html = in.readString();
        this.user = in.readParcelable(User.class.getClassLoader());
        long tmpCreated_at = in.readLong();
        this.created_at = tmpCreated_at == -1 ? null : new Date(tmpCreated_at);
        long tmpUpdated_at = in.readLong();
        this.updated_at = tmpUpdated_at == -1 ? null : new Date(tmpUpdated_at);
    }

    public String shortMessage() {
        if (body != null) {
            if (body.length() > MAX_MESSAGE_LENGHT) {
                return body.substring(0, MAX_MESSAGE_LENGHT).concat("...");
            } else {
                return body;
            }
        }
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GithubComment> CREATOR = new Creator<GithubComment>() {
        @Override
        public GithubComment createFromParcel(Parcel in) {
            return new GithubComment(in);
        }

        @Override
        public GithubComment[] newArray(int size) {
            return new GithubComment[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.id);
        dest.writeString(this.body);
        dest.writeString(this.body_html);
        dest.writeParcelable(this.user, 0);
        dest.writeLong(created_at != null ? created_at.getTime() : -1);
        dest.writeLong(updated_at != null ? updated_at.getTime() : -1);
    }
}
