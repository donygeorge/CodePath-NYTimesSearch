package com.donygeorge.nytimessearch.models

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class Article (jsonObject: JSONObject) {
    var webURL : String? = null
    var headline : String? = null
    var thumbnail : String? = null

    init {
        try {
            webURL = jsonObject.getString("web_url")
            headline = jsonObject.getJSONObject("headline").getString("main")
            var multimedia : JSONArray = jsonObject.getJSONArray("multimedia")
            if (multimedia.length() > 0) {
                var multimediaJSON = multimedia.getJSONObject(0)
                this.thumbnail = "http://nytimes.com/" + multimediaJSON.getString("url")
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