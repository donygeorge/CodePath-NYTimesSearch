package com.donygeorge.nytimessearch.adapters

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.donygeorge.nytimessearch.R
import com.donygeorge.nytimessearch.activities.WebViewActivity
import com.donygeorge.nytimessearch.models.Article


class ArticleArrayAdapter(context: Context, articles: List<Article>) : ArrayAdapter<Article>(context, android.R.layout.simple_list_item_1, articles) {

    internal inner class ViewHolder(view: View) {

        @BindView(R.id.ivImage) lateinit var ivImage: ImageView
        @BindView(R.id.tvTitle) lateinit var tvTitle: TextView

        init {
            ButterKnife.bind(view)
            ivImage = view.findViewById<View>(R.id.ivImage) as ImageView
            tvTitle = view.findViewById<View>(R.id.tvTitle) as TextView
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val article = this.getItem(position)
        val viewHolder: ViewHolder

        if (convertView == null) {
            val inflater = LayoutInflater.from(context)
            convertView = inflater.inflate(R.layout.item_article, parent, false)
            viewHolder = ViewHolder(convertView)
            convertView!!.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }

        viewHolder.tvTitle.text = article!!.headline
        convertView.setOnClickListener { loadURL(article.webURL) }

        val thumbnail = article.thumbnail
        if (!TextUtils.isEmpty(thumbnail)) {
            Glide.with(context)
                    .load(thumbnail)
                    .placeholder(R.mipmap.ic_launcher)
                    .into(viewHolder.ivImage)
        }

        return convertView
    }

    private fun loadURL(url : String?) {
        if (url == null) return
        val i = Intent(context, WebViewActivity::class.java)
        i.putExtra("url", url);
        context.startActivity(i)
    }
}
