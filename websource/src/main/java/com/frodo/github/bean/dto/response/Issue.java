package com.frodo.github.bean.dto.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

/**
 * Created by Bernat on 20/07/2014.
 */

public class Issue extends GithubComment implements Parcelable {

    public static final Creator<Issue> CREATOR = new Creator<Issue>() {
        public Issue createFromParcel(Parcel source) {
            return new Issue(source);
        }

        public Issue[] newArray(int size) {
            return new Issue[size];
        }
    };
    public int number;
    public IssueState state;
    public boolean locked;
    public String title;
    public List<Label> labels;
    public User assignee;
    public Milestone milestone;
    public int comments;
    public PullRequest pull_request;
    public Date closed_at;
    public User closed_by;
    public String repository_url;
    public String labels_url;
    public String comments_url;
    public String events_url;

    public Issue() {
    }

    protected Issue(Parcel in) {
        super(in);
        this.number = in.readInt();
        int tmpState = in.readInt();
        this.state = tmpState == -1 ? null : IssueState.values()[tmpState];
        this.locked = in.readByte() != 0;
        this.title = in.readString();
        this.labels = in.createTypedArrayList(Label.CREATOR);
        this.assignee = in.readParcelable(User.class.getClassLoader());
        this.milestone = in.readParcelable(Milestone.class.getClassLoader());
        this.comments = in.readInt();
        this.pull_request = in.readParcelable(PullRequest.class.getClassLoader());
        long tmpClosed_at = in.readLong();
        this.closed_at = tmpClosed_at == -1 ? null : new Date(tmpClosed_at);
        this.closed_by = in.readParcelable(User.class.getClassLoader());
        this.repository_url = in.readString();
        this.labels_url = in.readString();
        this.comments_url = in.readString();
        this.events_url = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.number);
        dest.writeInt(this.state == null ? -1 : this.state.ordinal());
        dest.writeByte(locked ? (byte) 1 : (byte) 0);
        dest.writeString(this.title);
        dest.writeTypedList(labels);
        dest.writeParcelable(this.assignee, 0);
        dest.writeParcelable(this.milestone, 0);
        dest.writeInt(this.comments);
        dest.writeParcelable(this.pull_request, 0);
        dest.writeLong(closed_at != null ? closed_at.getTime() : -1);
        dest.writeParcelable(this.closed_by, 0);

        dest.writeString(this.repository_url);
        dest.writeString(this.labels_url);
        dest.writeString(this.comments_url);
        dest.writeString(this.events_url);
    }
}
