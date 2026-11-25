package com.example.a38_karhutla.model

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Category(
    val id: Int,
    val name: String,
    val description: String,
    val iconUrl: String
) : Parcelable {
    override fun toString(): String {
        return name // Berguna saat digunakan di Spinner ArrayAdapter
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(iconUrl)
    }

    companion object CREATOR : Parcelable.Creator<Category> {
        override fun createFromParcel(parcel: Parcel): Category {
            return Category(
                parcel.readInt(),
                parcel.readString() ?: "",
                parcel.readString() ?: "",
                parcel.readString() ?: ""
            )
        }

        override fun newArray(size: Int): Array<Category?> {
            return arrayOfNulls(size)
        }
    }
}
