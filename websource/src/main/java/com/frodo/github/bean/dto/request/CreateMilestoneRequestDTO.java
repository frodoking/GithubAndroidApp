package com.frodo.github.bean.dto.request;

import android.os.Parcel;
import android.os.Parcelable;

import com.frodo.github.bean.dto.response.IssueState;

/**
 * Created by Bernat on 15/04/2015.
 */
public class CreateMilestoneRequestDTO implements Parcelable {

    public static final Creator<CreateMilestoneRequestDTO> CREATOR =
            new Creator<CreateMilestoneRequestDTO>() {
                public CreateMilestoneRequestDTO createFromParcel(Parcel source) {
                    return new CreateMilestoneRequestDTO(source);
                }

                public CreateMilestoneRequestDTO[] newArray(int size) {
                    return new CreateMilestoneRequestDTO[size];
                }
            };
    public String title;
    public String description;
    public String due_on;
    public IssueState state;

    public CreateMilestoneRequestDTO(String title) {
        this.title = title;
    }

    protected CreateMilestoneRequestDTO(Parcel in) {
        this.title = in.readString();
        this.description = in.readString();
        this.due_on = in.readString();
        int tmpState = in.readInt();
        this.state = tmpState == -1 ? null : IssueState.values()[tmpState];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.due_on);
        dest.writeInt(this.state == null ? -1 : this.state.ordinal());
    }
}
