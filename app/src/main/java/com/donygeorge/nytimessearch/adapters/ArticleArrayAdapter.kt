package com.donygeorge.nytimessearch.adapters

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import com.donygeorge.nytimessearch.R
import com.donygeorge.nytimessearch.activities.WebViewActivity
import com.donygeorge.nytimessearch.helpers.DynamicHeightImageView
import com.donygeorge.nytimessearch.models.Article


class ArticleArrayAdapter(context: Context, articles: List<Article>)
    : RecyclerView.Adapter<ArticleArrayAdapter.ViewHolder>() {

    var mArticles : List<Article>? = null
    var mContext : Context? = null

    init {
        mArticles = articles
        mContext = context
    }

    public class ViewHolder (inView : View) : RecyclerView.ViewHolder(inView) {

        lateinit var ivImage: DynamicHeightImageView
        lateinit var tvTitle: TextView
        lateinit var tvSnippet: TextView
        lateinit var view : View

        init {
            view = inView
            ivImage = view.findViewById<View>(R.id.ivImage) as DynamicHeightImageView
            tvTitle = view.findViewById<View>(R.id.tvTitle) as TextView
            tvSnippet = view.findViewById<View>(R.id.tvSnippet) as TextView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val context = parent?.getContext() ?: mContext
        val inflater = LayoutInflater.from(context)
        val convertView = inflater.inflate(R.layout.item_article, parent, false)
        val viewHolder = ViewHolder(convertView)
        return viewHolder
    }

    override fun onBindViewHolder(viewHolder: ViewHolder?, position: Int) {
        val article = mArticles!!.get(position)
        viewHolder!!.tvTitle.text = article!!.headline
        viewHolder!!.tvSnippet.text = article!!.snippet
        viewHolder!!.view.setOnClickListener { loadURL(article.webURL) }

        val thumbnail = article.thumbnail
        if (!TextUtils.isEmpty(thumbnail)) {
            val ratio = article.thumbnailHeight.toDouble() / article.thumbnailWidth
            viewHolder.ivImage.setHeightRatio(ratio)
            Glide.with(mContext)
                    .load(thumbnail)
                    .placeholder(R.mipmap.ic_launcher)
                    .into(viewHolder.ivImage)
        }
    }

    override fun getItemCount(): Int {
        return mArticles!!.size
    }

    private fun loadURL(url : String?) {
        if (url == null) return
        val i = Intent(mContext, WebViewActivity::class.java)
        i.putExtra("url", url);
        mContext!!.startActivity(i)
    }
}
