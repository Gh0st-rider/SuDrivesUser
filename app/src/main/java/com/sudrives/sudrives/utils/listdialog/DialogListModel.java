package com.sudrives.sudrives.utils.listdialog;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Nikhat on 10/16/2018.
 */

public class DialogListModel implements Parcelable {

    public String name;
    public Integer imgId;

    public DialogListModel(String name, Integer imgId) {
        this.name = name;
        this.imgId = imgId;
    }
      public DialogListModel(Integer imgId, String name) {

        this.imgId = imgId;
        this.name = name;
    }


    public DialogListModel(Parcel in) {
        name = in.readString();
        imgId=in.readInt();
    }

    public static final Creator<DialogListModel> CREATOR = new Creator<DialogListModel>() {
        @Override
        public DialogListModel createFromParcel(Parcel in) {
            return new DialogListModel(in);
        }

        @Override
        public DialogListModel[] newArray(int size) {
            return new DialogListModel[size];
        }
    };



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeInt(imgId);
    }
}
