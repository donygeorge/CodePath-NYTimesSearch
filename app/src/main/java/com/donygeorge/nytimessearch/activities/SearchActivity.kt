package com.donygeorge.nytimessearch.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.donygeorge.nytimessearch.R
import com.donygeorge.nytimessearch.adapters.ArticleArrayAdapters
import com.donygeorge.nytimessearch.models.Article
import com.donygeorge.nytimessearch.models.getArticles
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_search.*
import org.json.JSONArray
import org.json.JSONObject




class SearchActivity : AppCompatActivity() {

    var articles : MutableList<Article> = mutableListOf()
    var adapter : ArticleArrayAdapters? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        adapter = ArticleArrayAdapters(this, articles)

        btnSearch.setOnClickListener { onArticleSearch(it) }
        gvResults.adapter = adapter
    }

    fun onArticleSearch(view : View?) {
        val query = etQuery!!.text.toString()
        var client = AsyncHttpClient()
        var url = "https://api.nytimes.com/svc/search/v2/articlesearch.json"
        var requestParams  = RequestParams()
        requestParams.put("api_key", "c107d8e4c5fd4e9fbaa11329885ad250")
        requestParams.put("q", query)

        client.get(url, requestParams, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, response: JSONObject?) {
                var jsonArrayResults : JSONArray?
                jsonArrayResults = response?.getJSONObject("response")?.getJSONArray("docs")
                adapter!!.addAll(getArticles(jsonArrayResults))
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>, res: String, t: Throwable) {
            }
        })
    }

}
