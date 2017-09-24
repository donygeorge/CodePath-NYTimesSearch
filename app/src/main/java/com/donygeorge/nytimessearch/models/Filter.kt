package com.donygeorge.nytimessearch.models

import org.parceler.Parcel
import java.text.SimpleDateFormat
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
data class Filter (val date: Date? = null, val sortOrder: SortOrder = SortOrder.NEWEST, val newsDesks : List<NewsDesk>? = null) {
    fun getNewsDeskQuery() : String {
        var resultStrings = mutableListOf<String>()
        for (newsDesk in newsDesks!!) {
            resultStrings.add(value(newsDesk))
        }
        var result = "news_desk:(" + resultStrings.joinToString(" ", "\"", "\"") + ")"
        return result
    }

    fun getSortOrderQuery() : String {
        when (sortOrder) {
            SortOrder.OLDEST -> return "oldest"
            SortOrder.NEWEST -> return "newest"
        }
    }

    fun getStartDateQuery() : String {
        var dateFormat = SimpleDateFormat("yyyyMMdd");
        return dateFormat.format(date)
    }
}

fun value(newsDesk : NewsDesk) : String {
    return when (newsDesk) {
        NewsDesk.ARTS -> "Arts"
        NewsDesk.FASHION_AND_STYLE -> "Fashion & Style"
        NewsDesk.SPORTS -> "Sports"
    }
}