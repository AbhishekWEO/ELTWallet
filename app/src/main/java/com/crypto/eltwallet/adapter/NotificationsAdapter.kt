package com.crypto.eltwallet.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.crypto.eltwallet.R
import com.crypto.eltwallet.model.NotificationsModel
import com.crypto.eltwallet.pagination.PaginationListener
import com.crypto.eltwallet.utilities.Conversions
import com.crypto.eltwallet.utilities.DialogBox
import kotlinx.android.synthetic.main.layout_notifications.view.*
import java.util.ArrayList


class NotificationsAdapter(
    val mValues: ArrayList<NotificationsModel.DataBean>,
    private val mContext : Context?) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    private val VIEW_TYPE_LOADING = 0
    private val VIEW_TYPE_NORMAL = 1
    private var isLoaderVisible = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        /*val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_notifications, parent, false)
        return ViewHolder(view)*/
        val view: View
        if (viewType === VIEW_TYPE_NORMAL)
        {
            view = LayoutInflater.from(parent.context).inflate(R.layout.layout_notifications, parent, false)
            return MyViewHolder(view)
        }
        else// if (viewType === VIEW_TYPE_LOADING)
        {
            view = LayoutInflater.from(parent.context).inflate(R.layout.item_loading, parent, false)
            return ProgressHolder(view)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)
    {
        if (getItemViewType(position) === VIEW_TYPE_NORMAL)
        {
            holder as MyViewHolder
            val item = mValues[position]

            var id: String? = item.id
            var userid_s: String? = item.userid_s
            var userid_r: String? = item.userid_r
            val address_from: String? = item.address_from
            val address_to: String? = item.address_to

            val amt: String? = item.amt

//        var amt : String?
//
//        if(item.amt.toString().length > 0) {
//
//            var w : String = item.amt.toString()
//            var elt = w.substring(0, w.length - 4)
//
//            amt = Conversions.eltDecimals(elt.toDouble())
//
//        }
//        else {
//
//            amt = item.amt
//
//        }

            val status: String? = item.status
            val transaction_response: String? = item.transaction_response
            val created: String? = item.created

            holder.created.setText(Conversions.getTimeFormatFromInstant(created.toString()))

            if(status.equals("send")) {

                val amount = Conversions.eltDecimals(amt.toString().toDouble()).toDouble()

                holder.imageView.background = mContext?.resources!!.getDrawable(R.drawable.ic_to)
                holder.message.text = mContext.resources.getString(R.string.you_have_sent) + " " + Html.fromHtml("<b>$amount ELT</b>") + " " +
                        mContext.resources.getString(R.string.to).toLowerCase() + " " + Conversions.showPattern(address_to.toString())

            }
            else if(status.equals("receive")) {

                val amount = Conversions.eltDecimals(amt.toString().toDouble()).toDouble()

                holder.imageView.background = mContext?.resources!!.getDrawable(R.drawable.ic_from)
                holder.message.text = mContext.resources.getString(R.string.you_have_received) + " " + Html.fromHtml("<b>$amount ELT</b>") + " " +
                        mContext.resources.getString(R.string.from).toLowerCase() + " " + Conversions.showPattern(address_to.toString())

            }
            else if(status.equals("create")) {

                holder.imageView.background = mContext?.resources!!.getDrawable(R.drawable.ic_create)
                holder.message.text = mContext.resources.getString(R.string.you_have_created) + " " + Conversions.showPattern(address_from.toString())

            }
            else if(status.equals("failed")) {

                val amount = Conversions.eltDecimals(amt.toString().toDouble()).toDouble()

                holder.imageView.background = mContext?.resources!!.getDrawable(R.drawable.smiley1)
                holder.message.text = mContext.resources.getString(R.string.your_transaction_of) + " " + Html.fromHtml("<b>$amount ELT</b>") + " " +
                        mContext.resources.getString(R.string.is_failed_to) + " " + Conversions.showPattern(address_from.toString()  )

            }
            else {

                holder.imageView.background = mContext?.resources!!.getDrawable(R.drawable.ic_to)

            }

            holder.relativeLayout.setOnClickListener{
                if(status.equals("failed")) {

                    Log.e("amount", amt.toString())
                    DialogBox.showNotificationFailedMessage(mContext, transaction_response.toString(), address_from.toString(), address_to.toString(), amt.toString())

                }
            }
        }
        else
        {

        }

    }

    override fun getItemViewType(position: Int): Int
    {
        if (isLoaderVisible)
        {
            return if (position == mValues!!.size - 1) VIEW_TYPE_LOADING else VIEW_TYPE_NORMAL
        }
        else
        {
            return VIEW_TYPE_NORMAL
        }
    }

    //override fun getItemCount(): Int = mValues.size
    override fun getItemCount(): Int
    {
        return if (mValues == null) 0 else mValues!!.size
    }

    inner class MyViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val relativeLayout: RelativeLayout = mView.relativeLayout
        val imageView: ImageView = mView.imageView
        val message: TextView = mView.message
        val created: TextView = mView.created

        override fun toString(): String {
            return super.toString() + " wallet "
        }
    }
    ///01 05 2020

    class ProgressHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var progressBar : ProgressBar?=null
        init {
            progressBar = itemView.findViewById(R.id.progressBar)
        }
    }

    fun addItems(postItems: List<NotificationsModel.DataBean>)
    {
        mValues.addAll(postItems)
        notifyDataSetChanged()
    }

    fun addLoading()
    {
        isLoaderVisible = true
        mValues.add(NotificationsModel.DataBean())
        notifyItemInserted(mValues.size - 1)

        /*if (mValues.size >= PaginationListener.PAGE_SIZE)
        {
            isLoaderVisible = true
            mValues.add(NotificationsModel.DataBean())
            notifyItemInserted(mValues.size - 1)
        }*/
    }
    fun removeLoading()
    {
        isLoaderVisible = false
        val position: Int = mValues.size - 1
        try
        {
            val item: NotificationsModel.DataBean? = getItem(position)
            if (item != null)
            {
                mValues.removeAt(position)
                notifyItemRemoved(position)
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }

    }

    fun emptyData()
    {
        isLoaderVisible = false
        mValues.removeAt(mValues.size - 1)
        notifyDataSetChanged()
    }

    fun getItem(position: Int): NotificationsModel.DataBean?
    {
        return mValues.get(position)
    }

    fun clear()
    {
        mValues.clear()
        notifyDataSetChanged()
    }
}