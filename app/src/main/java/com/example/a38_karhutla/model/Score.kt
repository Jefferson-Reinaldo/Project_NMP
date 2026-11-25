package com.example.a38_karhutla.model

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Score(
    val id: Int,
    val userId: Int,
    val username: String,
    val categoryId: Int,
    val categoryName: String,
    val score: Int,
    val totalQuestions: Int,
    val date: String
) : Parcelable {
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(userId)
        parcel.writeString(username)
        parcel.writeInt(categoryId)
        parcel.writeString(categoryName)
        parcel.writeInt(score)
        parcel.writeInt(totalQuestions)
        parcel.writeString(date)
    }

    companion object CREATOR : Parcelable.Creator<Score> {
        override fun createFromParcel(parcel: Parcel): Score {
            return Score(
                parcel.readInt(),
                parcel.readInt(),
                parcel.readString() ?: "",
                parcel.readInt(),
                parcel.readString() ?: "",
                parcel.readInt(),
                parcel.readInt(),
                parcel.readString() ?: ""
            )
        }

        override fun newArray(size: Int): Array<Score?> {
            return arrayOfNulls(size)
        }
    }
}
