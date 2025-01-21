package com.xinchaongaymoi.hotelbookingapp.model

import android.os.Parcel
import android.os.Parcelable

data class ImageItem(
    val url: String = ""
) : Parcelable {

    // Constructor để đọc từ Parcel
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: ""
    )

    // Ghi các thuộc tính vào Parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(url)
    }

    // Phương thức mô tả đặc tính của đối tượng (đối với Parcelable)
    override fun describeContents(): Int {
        return 0
    }

    // Companion object để tạo Parcelable
    companion object CREATOR : Parcelable.Creator<ImageItem> {
        override fun createFromParcel(parcel: Parcel): ImageItem {
            return ImageItem(parcel)
        }

        override fun newArray(size: Int): Array<ImageItem?> {
            return arrayOfNulls(size)
        }
    }
}

