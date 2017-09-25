package com.donygeorge.nytimessearch.activities

import android.content.res.Configuration
import android.graphics.Rect
import android.os.Bundle
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.donygeorge.nytimessearch.R
import com.donygeorge.nytimessearch.adapters.ArticleArrayAdapter
import com.donygeorge.nytimessearch.fragments.SettingsFragment
import com.donygeorge.nytimessearch.helpers.EndlessRecyclerViewScrollListener
import com.donygeorge.nytimessearch.models.Article
import com.donygeorge.nytimessearch.models.Filter
import com.donygeorge.nytimessearch.models.getArticles
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_search.*
import org.json.JSONObject



class SearchActivity : AppCompatActivity(), SettingsFragment.SettingsFragmentListener {

    lateinit var mArticles : MutableList<Article>
    lateinit var mAdapter: ArticleArrayAdapter
    lateinit var mStaggeredGridLayoutManager : StaggeredGridLayoutManager
    var mFilter : Filter? = null
    var mQuery : String? = null
    lateinit var mScrollListener : EndlessRecyclerViewScrollListener

    inner class SpacesItemDecoration(private val mSpace: Int) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            outRect.left = mSpace
            outRect.right = mSpace
            outRect.bottom = mSpace
            outRect.top = mSpace

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        supportActionBar!!.setTitle(R.string.title)

        mArticles = mutableListOf()
        mAdapter = ArticleArrayAdapter(this, mArticles)
        rvResults.adapter = mAdapter
        mStaggeredGridLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        setupLayoutManager(mStaggeredGridLayoutManager)
        rvResults.layoutManager = mStaggeredGridLayoutManager
        mScrollListener = object : EndlessRecyclerViewScrollListener(mStaggeredGridLayoutManager) {

            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                onArticleSearch(page)
            }
        }
        rvResults.setOnScrollListener(mScrollListener)

        rvResults.addItemDecoration(SpacesItemDecoration(10))

        onArticleSearch(0)
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        setupLayoutManager(mStaggeredGridLayoutManager)
    }

    fun setupLayoutManager(layoutManager : StaggeredGridLayoutManager) {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager.setSpanCount(2);
        } else {
            //show in two columns
            layoutManager.setSpanCount(3);
        }
    }

    private fun onClearSearch() {
        mArticles.clear()
        mAdapter.notifyDataSetChanged()
        mScrollListener.resetState()
    }

    fun onArticleSearch(page : Int) {
        var client = AsyncHttpClient()
        var url = "https://api.nytimes.com/svc/search/v2/articlesearch.json"

        client.get(url, getRequestParams(mFilter, mQuery, page), object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, response: JSONObject?) {
                var jsonArrayResults = response?.getJSONObject("response")?.getJSONArray("docs")
                mArticles.addAll(getArticles(jsonArrayResults))
                mAdapter?.notifyDataSetChanged()
            }

            override fun onFailure(statusCode: Int, headers: Array<Header>, res: String, t: Throwable) {
            }
        })
    }

    private fun getRequestParams(filter : Filter?, query : String?, page : Int) : RequestParams {
        var requestParams  = RequestParams()
        requestParams.put("api_key", "c107d8e4c5fd4e9fbaa11329885ad250")
        if (!TextUtils.isEmpty(query)) {
            requestParams.put("q", query)
        }
        requestParams.put("page", page)
        if (filter != null) {
            requestParams.put("sort", filter.getSortOrderQuery())
            requestParams.put("begin_date", filter.getStartDateQuery())
            if (filter.newsDesks!!.size > 0 ) {
                requestParams.put("fq", filter.getNewsDeskQuery())
            }
        } else {
            requestParams.put("sort", "newest")
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
                onClearSearch()
                onArticleSearch(0)

                return true;
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.action_filter -> {
                val settingsFragment = SettingsFragment.newInstance(mFilter);
                settingsFragment.show(supportFragmentManager, "fragment_settings");
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onSettingsSaved(filter : Filter) {
        if (filter != null && !filter.equals(mFilter)) {
            mFilter = filter
            onClearSearch()
            onArticleSearch(0)
        }
    }
}
