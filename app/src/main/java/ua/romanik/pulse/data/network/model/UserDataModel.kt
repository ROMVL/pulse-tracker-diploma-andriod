package ua.romanik.pulse.data.network.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class UserDataModel(
    @SerializedName("email") val email : String? = null,
    @SerializedName("firstName") val firstName : String? = null,
    @SerializedName("lastName") val lastName : String? = null,
    @SerializedName("age") val age : Int? = null,
    @SerializedName("relativePhone") val relativePhone : String? = null,
    @SerializedName("relativeName") val relativeName : String? = null,
    @SerializedName("uuid") val uuid: String = UUID.randomUUID().toString()
) : Parcelable