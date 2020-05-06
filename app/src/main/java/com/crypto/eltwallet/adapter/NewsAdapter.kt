package com.crypto.eltwallet.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.crypto.eltwallet.R
import com.crypto.eltwallet.activities.NewsDetailActivity
import com.crypto.eltwallet.model.NewsList
import com.crypto.eltwallet.utilities.ConstantsRequest
import com.makeramen.roundedimageview.RoundedImageView
import kotlinx.android.synthetic.main.news_individual_item.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class NewsAdapter(var context : Context, var newsData : List<NewsList.Datum>) : RecyclerView.Adapter<NewsAdapter.MyViewHolder>()
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.news_individual_item,null)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return newsData.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context).load(newsData.get(position).imageurl).into(holder.img_newsPic)
        holder.tv_title.setText(newsData.get(position).title)
        holder.tv_newsDesc.setText(newsData.get(position).body)

        //13-11-2019 11:02: AM
        var d = Date(newsData.get(position).publishedOn!!.toLong() * 1000)
        var f: DateFormat = SimpleDateFormat("dd-MM-yyyy' 'HH:mm:ss")

        Log.e("dssd", "onBindViewHolder: " + f.format(d))
        holder.tv_dateTime.setText(f.format(d))


        holder.cardView.setOnClickListener {

            var intent = Intent(context,NewsDetailActivity::class.java)
            intent.putExtra(ConstantsRequest.NEWS_URL,newsData.get(position).url)
            context.startActivity(intent)
            (context as Activity).overridePendingTransition(R.anim.slide_up, R.anim.slide_down_bit)
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var cardView : CardView = itemView.cardView
        var img_newsPic : RoundedImageView = itemView.img_newsPic
        var tv_title : TextView = itemView.tv_title
        var tv_dateTime : TextView = itemView.tv_dateTime
        var tv_newsDesc : TextView = itemView.tv_newsDesc

    }
}