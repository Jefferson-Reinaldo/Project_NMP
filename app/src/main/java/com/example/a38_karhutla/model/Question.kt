package com.example.a38_karhutla.model

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Question(
    val id: Int,
    val categoryId: Int,
    var questionText: String,
    var optionA: String,
    var optionB: String,
    var optionC: String,
    var optionD: String,
    var correctAnswer: String, // "A", "B", "C", atau "D"
    var imageUrl: String = "",
    var isActive: Int = 1,      // 1: Aktif, 0: Nonaktif
    var selectedAnswer: String = "" // Jawaban sementara peserta saat mengerjakan ujian
) : Parcelable {
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(categoryId)
        parcel.writeString(questionText)
        parcel.writeString(optionA)
        parcel.writeString(optionB)
        parcel.writeString(optionC)
        parcel.writeString(optionD)
        parcel.writeString(correctAnswer)
        parcel.writeString(imageUrl)
        parcel.writeInt(isActive)
        parcel.writeString(selectedAnswer)
    }

    companion object CREATOR : Parcelable.Creator<Question> {
        override fun createFromParcel(parcel: Parcel): Question {
            return Question(
                parcel.readInt(),
                parcel.readInt(),
                parcel.readString() ?: "",
                parcel.readString() ?: "",
                parcel.readString() ?: "",
                parcel.readString() ?: "",
                parcel.readString() ?: "",
                parcel.readString() ?: "",
                parcel.readString() ?: "",
                parcel.readInt(),
                parcel.readString() ?: ""
            )
        }

        override fun newArray(size: Int): Array<Question?> {
            return arrayOfNulls(size)
        }
    }
}
