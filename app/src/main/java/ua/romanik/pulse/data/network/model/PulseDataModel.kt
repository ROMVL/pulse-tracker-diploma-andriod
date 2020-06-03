package ua.romanik.pulse.data.network.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.sql.Timestamp

@Parcelize
data class PulseDataModel(
    val id: Long? = null,
    val pulseValue: String? = null,
    val time: Timestamp? = null,
    val userEmail: String? = null
) : Parcelable