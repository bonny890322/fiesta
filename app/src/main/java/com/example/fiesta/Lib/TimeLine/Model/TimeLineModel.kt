package com.network.fiesta.Lib.TimeLine.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class TimeLineModel(
    var showName: String,
    var showId: String,
    var date: String,
    var detail: String,
    var status: OrderStatus,
    var buttonVis: Boolean
) : Parcelable
