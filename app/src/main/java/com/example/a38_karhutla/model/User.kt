package com.example.a38_karhutla.model

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: Int,
    val username: String,
    val name: String,
    val role: String // "user" atau "admin"
) : Parcelable {
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(username)
        parcel.writeString(name)
        parcel.writeString(role)
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(
                parcel.readInt(),
                parcel.readString() ?: "",
                parcel.readString() ?: "",
                parcel.readString() ?: ""
            )
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}
