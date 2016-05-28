package com.frodo.github.bean.dto.request;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Bernat on 17/05/2015.
 */
public class EditIssueLabelsRequestDTO extends EditIssueRequestDTO implements Parcelable {
    public static final Creator<EditIssueLabelsRequestDTO> CREATOR =
            new Creator<EditIssueLabelsRequestDTO>() {
                public EditIssueLabelsRequestDTO createFromParcel(Parcel source) {
                    return new EditIssueLabelsRequestDTO(source);
                }

                public EditIssueLabelsRequestDTO[] newArray(int size) {
                    return new EditIssueLabelsRequestDTO[size];
                }
            };
    public String[] labels;

    public EditIssueLabelsRequestDTO() {
    }

    protected EditIssueLabelsRequestDTO(Parcel in) {
        this.labels = in.createStringArray();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(this.labels);
    }
}
