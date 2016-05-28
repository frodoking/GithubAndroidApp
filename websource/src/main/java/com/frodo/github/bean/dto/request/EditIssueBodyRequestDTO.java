package com.frodo.github.bean.dto.request;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bernat on 17/05/2015.
 */
public class EditIssueBodyRequestDTO extends EditIssueRequestDTO implements Parcelable {
    public static final Creator<EditIssueBodyRequestDTO> CREATOR =
            new Creator<EditIssueBodyRequestDTO>() {
                public EditIssueBodyRequestDTO createFromParcel(Parcel source) {
                    return new EditIssueBodyRequestDTO(source);
                }

                public EditIssueBodyRequestDTO[] newArray(int size) {
                    return new EditIssueBodyRequestDTO[size];
                }
            };
    public String body;

    public EditIssueBodyRequestDTO() {
    }

    protected EditIssueBodyRequestDTO(Parcel in) {
        this.body = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.body);
    }
}
