package com.techo.chattv.model

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;

open class Channel(
    var channelName: String? = "",
    var channelUrl: String? = "",
    var channelImg: String? = "",
    var channelGroup: String? = "",
    var channelDrmKey: String? = "",
    var channelDrmType: String? = ""
) : RealmObject(),Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(channelName)
        parcel.writeString(channelUrl)
        parcel.writeString(channelImg)
        parcel.writeString(channelGroup)
        parcel.writeString(channelDrmKey)
        parcel.writeString(channelDrmType)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Channel> {
        override fun createFromParcel(parcel: Parcel): Channel {
            return Channel(parcel)
        }

        override fun newArray(size: Int): Array<Channel?> {
            return arrayOfNulls(size)
        }
    }
}



