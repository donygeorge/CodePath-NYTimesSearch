package com.donygeorge.nytimessearch.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import com.donygeorge.nytimessearch.R
import com.donygeorge.nytimessearch.adapters.ArticleArrayAdapter
import com.donygeorge.nytimessearch.models.Article
import com.donygeorge.nytimessearch.models.Filter
import com.donygeorge.nytimessearch.models.getArticles
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_search.*
import org.json.JSONObject
import org.parceler.Parcels


class SearchActivity : AppCompatActivity() {

    var mArticles : MutableList<Article> = mutableListOf()
    var mAdapter: ArticleArrayAdapter? = null
    var mFilter : Filter? = null
    var mQuery : String? = null
    val REQUEST_CODE = 200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        mAdapter = ArticleArrayAdapter(this, mArticles)
        gvResults.adapter = mAdapter
        onArticleSearch()
    }

    fun onArticleSearch() {

        var client = AsyncHttpClient()
        var url = "https://api.nytimes.com/svc/search/v2/articlesearch.json"

        client.get(url, getRequestParams(mFilter, mQuery), object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, response: JSONObject?) {
//                mAdapter!!.clear()
                var jsonArrayResults = response?.getJSONObject("response")?.getJSONArray("docs")
                mAdapter!!.addAll(getArticles(jsonArrayResults))
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>, res: String, t: Throwable) {
            }
        })
    }

    private fun getRequestParams(filter : Filter?, query : String?) : RequestParams {
        var requestParams  = RequestParams()
        requestParams.put("api_key", "c107d8e4c5fd4e9fbaa11329885ad250")
        if (!TextUtils.isEmpty(query)) {
            requestParams.put("q", query)
        }
        if (filter != null) {
            requestParams.put("sort", filter.getSortOrderQuery())
            requestParams.put("fq", filter.getNewsDeskQuery())
            requestParams.put("begin_date", filter.getStartDateQuery())
        }

        return requestParams
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

                mQuery = query
                onArticleSearch()

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

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.action_filter -> {
                val i = Intent(this, FilterActivity::class.java)
                i.putExtra("filter", Parcels.wrap(mFilter));
                startActivityForResult(i, REQUEST_CODE) // brings up the second activity
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data == null) return
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            mFilter = Parcels.unwrap<Filter>(data.getParcelableExtra("filter"))
            if (mFilter != null) {
                onArticleSearch()
            }
        }
    }
}
