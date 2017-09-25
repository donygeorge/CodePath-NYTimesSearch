package com.donygeorge.nytimessearch.adapters

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.customtabs.CustomTabsIntent
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import com.donygeorge.nytimessearch.R
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
        lateinit var tvSource: TextView
        lateinit var tvDate: TextView
        lateinit var view : View

        init {
            view = inView
            tvTitle = view.findViewById<View>(R.id.tvTitle) as TextView
            tvSnippet = view.findViewById<View>(R.id.tvSnippet) as TextView
            tvSource = view.findViewById<View>(R.id.tvSource) as TextView
            tvDate = view.findViewById<View>(R.id.tvDate) as TextView
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
        if (article.source != null) {
            textViewHolder!!.tvSource.visibility = View.VISIBLE
            textViewHolder!!.tvSource.text = article!!.source
        } else {
            textViewHolder!!.tvSource.visibility = View.INVISIBLE
        }
        val dateAsString = article.dateAsString()
        if (dateAsString != null) {
            textViewHolder!!.tvDate.visibility = View.VISIBLE
            textViewHolder!!.tvDate.text = dateAsString
        } else {
            textViewHolder!!.tvDate.visibility = View.INVISIBLE
        }
        if (viewHolder.itemViewType == IMAGE_TYPE) {
            val imageViewHolder = viewHolder as ImageViewHolder
            val thumbnail = article.thumbnail
            if (!TextUtils.isEmpty(thumbnail)) {
                val ratio = article.thumbnailHeight.toDouble() / article.thumbnailWidth
                imageViewHolder.ivImage.setHeightRatio(ratio)
                Glide.with(mContext)
                        .load(thumbnail)
                        .placeholder(R.mipmap.loading_image)
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

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, "Shared from NYTimesSearch: " + url)
        val bitmap = BitmapFactory.decodeResource(mContext!!.getResources(), android.R.drawable.ic_menu_share)
        val requestCode = 100
        val pendingIntent = PendingIntent.getActivity(mContext,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        builder.setActionButton(bitmap, "Share Link", pendingIntent, true);
        customTabsIntent.launchUrl(mContext, Uri.parse(url));
    }
}
