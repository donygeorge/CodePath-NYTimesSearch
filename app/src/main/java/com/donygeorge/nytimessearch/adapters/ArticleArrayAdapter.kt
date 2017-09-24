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
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TEXT_TYPE = 0
    private val IMAGE_TYPE = 1

    var mArticles : List<Article>? = null
    var mContext : Context? = null

    init {
        mArticles = articles
        mContext = context
    }

    public class ImageViewHolder (inView : View) : TextViewHolder(inView) {

        lateinit var ivImage: DynamicHeightImageView

        init {
            view = inView
            ivImage = view.findViewById<View>(R.id.ivImage) as DynamicHeightImageView
        }
    }

    public open class TextViewHolder (inView : View) : RecyclerView.ViewHolder(inView) {

        lateinit var tvTitle: TextView
        lateinit var tvSnippet: TextView
        lateinit var view : View

        init {
            view = inView
            tvTitle = view.findViewById<View>(R.id.tvTitle) as TextView
            tvSnippet = view.findViewById<View>(R.id.tvSnippet) as TextView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        val context = parent?.getContext() ?: mContext
        val inflater = LayoutInflater.from(context)
        var viewHolder : RecyclerView.ViewHolder
        when (viewType) {
            IMAGE_TYPE -> {
                val view = inflater.inflate(R.layout.item_article_image, parent, false)
                return ImageViewHolder(view)
            }
            else -> {
                val view = inflater.inflate(R.layout.item_article_text, parent, false)
                return TextViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder?, position: Int) {
        val article = mArticles!!.get(position)
        val textViewHolder = viewHolder as TextViewHolder
        textViewHolder!!.tvTitle.text = article!!.headline
        textViewHolder!!.tvSnippet.text = article!!.snippet
        textViewHolder!!.view.setOnClickListener { loadURL(article.webURL) }

        if (viewHolder.itemViewType == IMAGE_TYPE) {
            val imageViewHolder = viewHolder as ImageViewHolder
            val thumbnail = article.thumbnail
            if (!TextUtils.isEmpty(thumbnail)) {
                val ratio = article.thumbnailHeight.toDouble() / article.thumbnailWidth
                imageViewHolder.ivImage.setHeightRatio(ratio)
                Glide.with(mContext)
                        .load(thumbnail)
                        .placeholder(R.mipmap.ic_launcher)
                        .into(imageViewHolder.ivImage)
            }
        }
    }

    override fun getItemCount(): Int {
        return mArticles!!.size
    }

    override fun getItemViewType(position: Int): Int {
        if (mArticles!!.get(position).hasImage()) {
            return IMAGE_TYPE
        }
        return TEXT_TYPE
    }

    private fun loadURL(url : String?) {
        if (url == null) return
        val i = Intent(mContext, WebViewActivity::class.java)
        i.putExtra("url", url);
        mContext!!.startActivity(i)
    }
}
