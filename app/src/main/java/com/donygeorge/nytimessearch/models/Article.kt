package com.donygeorge.nytimessearch.models

import android.text.TextUtils
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class Article (jsonObject: JSONObject) {
    lateinit var webURL : String
    lateinit var headline : String
    var date : Date? = null
    var thumbnail : String? = null
    var snippet : String? = null
    var source : String? = null
    var thumbnailHeight : Int = 0
    var thumbnailWidth : Int = 0

    init {
        try {
            webURL = jsonObject.getString("web_url")
            headline = jsonObject.getJSONObject("headline").getString("main")
            snippet = jsonObject.getString("snippet")
            source = jsonObject.getString("source")
            var multimedia : JSONArray = jsonObject.getJSONArray("multimedia")
            if (multimedia.length() > 0) {
                var multimediaJSON = multimedia.getJSONObject(0)
                this.thumbnail = "http://nytimes.com/" + multimediaJSON.getString("url")
                this.thumbnailHeight = multimediaJSON.getInt("height")
                this.thumbnailWidth = multimediaJSON.getInt("width")
            } else {
                this.thumbnail = ""
            }
            this.date = dateFromString(jsonObject.getString("pub_date"))

        } catch (e : JSONException) {
            e.printStackTrace()
        }
    }

    fun hasImage() : Boolean {
        return !TextUtils.isEmpty(thumbnail);
    }

    fun dateAsString() : String? {
        if (date == null) return null
        val sdf = SimpleDateFormat("MMM dd, yyyy")
        return sdf.format(date)
    }

    private fun dateFromString(dateString : String) : Date {
        val format = SimpleDateFormat("yyyy-mm-dd", Locale.ENGLISH)
        return format.parse(dateString)
    }
}

public fun getArticles (jsonArray : JSONArray?) : List<Article>{
    var list = mutableListOf<Article>()
    if (jsonArray == null)
        return list

    for (i in 0..(jsonArray!!.length() - 1)) {
        list.add(Article(jsonArray!!.getJSONObject(i)))
    }
    return list
}