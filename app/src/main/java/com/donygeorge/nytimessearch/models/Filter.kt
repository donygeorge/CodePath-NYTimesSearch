package com.donygeorge.nytimessearch.models

import org.parceler.Parcel
import java.util.*

enum class SortOrder {
    OLDEST, NEWEST
}

enum class NewsDesk {
    ARTS,
    FASHION_AND_STYLE,
    SPORTS
}

@Parcel
data class Filter (val date: Date? = null, val sortOrder: SortOrder = SortOrder.NEWEST, val newsDesk : List<NewsDesk>? = null)

fun value(newsDesk : NewsDesk) : String? {
    return when (newsDesk) {
        NewsDesk.ARTS -> "Arts"
        NewsDesk.FASHION_AND_STYLE -> "Fashion & Style"
        NewsDesk.SPORTS -> "Sports"
        else -> null
    }
}