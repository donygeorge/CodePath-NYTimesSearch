package com.donygeorge.nytimessearch.activities

import android.os.Bundle
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.Menu
import com.donygeorge.nytimessearch.R
import com.donygeorge.nytimessearch.adapters.ArticleArrayAdapter
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
    var adapter : ArticleArrayAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        adapter = ArticleArrayAdapter(this, articles)
        gvResults.adapter = adapter
    }

    fun onArticleSearch(query : String?) {
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        var inflater = menuInflater
        inflater.inflate(R.menu.search_menu, menu);
        var searchItem = menu!!.findItem(R.id.action_search);
        val searchView = MenuItemCompat.getActionView(searchItem) as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();

                onArticleSearch(query)

                return true;
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }
        })
        searchItem.expandActionView();
        searchView.requestFocus();
        return super.onCreateOptionsMenu(menu)
    }
}
