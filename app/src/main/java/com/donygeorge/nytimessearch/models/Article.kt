package com.donygeorge.nytimessearch.models

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class Article (jsonObject: JSONObject) {
    lateinit var webURL : String
    lateinit var headline : String
    var thumbnail : String? = null
    var snippet : String? = null
    var thumbnailHeight : Int = 0
    var thumbnailWidth : Int = 0

    init {
        try {
            webURL = jsonObject.getString("web_url")
            headline = jsonObject.getJSONObject("headline").getString("main")
            snippet = jsonObject.getString("snippet")
            var multimedia : JSONArray = jsonObject.getJSONArray("multimedia")
            if (multimedia.length() > 0) {
                var multimediaJSON = multimedia.getJSONObject(0)
                this.thumbnail = "http://nytimes.com/" + multimediaJSON.getString("url")
                this.thumbnailHeight = multimediaJSON.getInt("height")
                this.thumbnailWidth = multimediaJSON.getInt("width")
            } else {
                this.thumbnail = ""
            }
        } catch (e : JSONException) {
            e.printStackTrace()
        }
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