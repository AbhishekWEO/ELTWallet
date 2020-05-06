package com.crypto.eltwallet.adapter

import android.content.Context
import android.content.Intent
import android.os.Build
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
import com.crypto.eltwallet.activities.TransactionDetailsActivity
import com.crypto.eltwallet.model.WalletTransactionModel
import com.crypto.eltwallet.utilities.Conversions
import kotlinx.android.synthetic.main.layout_wallet_transaction.view.*
import java.util.ArrayList

class WalletTransactionAdapter(
    private val mValues: ArrayList<WalletTransactionModel.DataBean>,
    private val mValues_sent: ArrayList<WalletTransactionModel.DataBean>,
    private val mValues_receive: ArrayList<WalletTransactionModel.DataBean>,
    private val mContext: Context?,
    private val input_dollar: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{

    /*private val VIEW_TYPE_LOADING = 0
    private val VIEW_TYPE_NORMAL = 1
    private var isLoaderVisible = false*/

    companion object {
        var type = "all"
        val VIEW_TYPE_LOADING = 0
        val VIEW_TYPE_NORMAL = 1
        var isLoaderVisible = false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        /*val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_wallet_transaction, parent, false)
        return ViewHolder(view)*/

        val view: View
        if (viewType === VIEW_TYPE_NORMAL)
        {
            view = LayoutInflater.from(parent.context).inflate(R.layout.layout_wallet_transaction, parent, false)
            return MyViewHolder(view)
        }
        else// if (viewType === VIEW_TYPE_LOADING)
        {
            view = LayoutInflater.from(parent.context).inflate(R.layout.item_loading, parent, false)
            return ProgressHolder(view)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)
    {
        if (getItemViewType(position) === VIEW_TYPE_NORMAL)
        {
            holder as MyViewHolder

            if (type.equals("sent")) {

                val item_sent = mValues_sent[position]

                if (item_sent.is_sender.equals("true")) {

                    holder.walletName.text = mContext?.resources!!.getString(R.string.to) + " " + Conversions.showPattern(item_sent.to.toString())
                    holder.imageView.background = mContext.resources!!.getDrawable(R.drawable.ic_to)

                    try
                    {
                        holder.value.text = "-" + Conversions.eltDecimals(item_sent.value.toString().toDouble()) + " ELT"
                    }
                    catch(e : Exception){}


                    holder.value.setTextColor(mContext.getColor(R.color.softRed))

                    holder.timestamp.text = Conversions.getTimeFormat(item_sent.timestamp.toString())
                    holder.dollarPrice.text = "($" + Conversions.dollarDecimals(((item_sent.value)!!.toDouble() * input_dollar.toDouble())) + ")"


                    holder.section.setOnClickListener{

                        //clear()

                        val intent = Intent(mContext, TransactionDetailsActivity::class.java)
                        intent.putExtra("blockHash", item_sent.blockHash)
                        intent.putExtra("blockNumber", item_sent.blockNumber)
                        intent.putExtra("timestamp", item_sent.timestamp)
                        intent.putExtra("from", item_sent.from)
                        intent.putExtra("to", item_sent.to)
                        intent.putExtra("value", item_sent.value)
                        intent.putExtra("c", item_sent.gasPriceBean()!!.c.toString())
                        intent.putExtra("gas", item_sent.gas)
                        intent.putExtra("nonce", item_sent.nonce)
                        intent.putExtra("input", item_sent.input)
                        mContext?.startActivity(intent)

                    }

                }

            } else if (type.equals("received")) {

                val item_receive = mValues_receive[position]

                if (item_receive.is_sender.equals("false")) {

                    holder.walletName.text = mContext?.resources!!.getString(R.string.from) + " " + Conversions.showPattern(item_receive.from.toString())
                    holder.imageView.background = mContext.resources!!.getDrawable(R.drawable.ic_from)

                    try
                    {
                        holder.value.text = "+" + Conversions.eltDecimals(item_receive.value.toString().toDouble()) + " ELT"
                    }
                    catch(e : Exception){}


                    holder.value.setTextColor(mContext.getColor(R.color.green))

                    holder.timestamp.text = Conversions.getTimeFormat(item_receive.timestamp.toString())
                    holder.dollarPrice.text = "($" + Conversions.dollarDecimals(((item_receive.value)!!.toDouble() * input_dollar.toDouble())) + ")"
                }

                holder.section.setOnClickListener{

                    //clear()

                    val intent = Intent(mContext, TransactionDetailsActivity::class.java)
                    intent.putExtra("blockHash", item_receive.blockHash)
                    intent.putExtra("blockNumber", item_receive.blockNumber)
                    intent.putExtra("timestamp", item_receive.timestamp)
                    intent.putExtra("from", item_receive.from)
                    intent.putExtra("to", item_receive.to)
                    intent.putExtra("value", item_receive.value)
                    intent.putExtra("c", item_receive.gasPriceBean()!!.c.toString())
                    intent.putExtra("gas", item_receive.gas)
                    intent.putExtra("nonce", item_receive.nonce)
                    intent.putExtra("input", item_receive.input)
                    mContext?.startActivity(intent)

                }

            } else {

                val item = mValues[position]

                if (item.is_sender.equals("true")) {
                    holder.walletName.text = mContext?.resources!!.getString(R.string.to) + " " + Conversions.showPattern(item.to.toString())
                    holder.imageView.background = mContext.resources!!.getDrawable(R.drawable.ic_to)

                    try
                    {
                        holder.value.text = "-" + Conversions.eltDecimals(item.value.toString().toDouble()) + " ELT"
                    }
                    catch(e : Exception){}


                    holder.value.setTextColor(mContext.getColor(R.color.softRed))
                } else {
                    holder.walletName.text = mContext?.resources!!.getString(R.string.from) + " " + Conversions.showPattern(item.from.toString())
                    holder.imageView.background = mContext.resources!!.getDrawable(R.drawable.ic_from)

                    try
                    {
                        holder.value.text ="+" + Conversions.eltDecimals(item.value.toString().toDouble()) + " ELT"
                    }
                    catch(e : Exception){}


                    holder.value.setTextColor(mContext.getColor(R.color.green))
                }

                holder.timestamp.text = Conversions.getTimeFormat(item.timestamp.toString())
                holder.dollarPrice.text ="($" + Conversions.dollarDecimals(((item.value)!!.toDouble() * input_dollar.toDouble())) + ")"


                holder.section.setOnClickListener{

                    //clear()

                    val intent = Intent(mContext, TransactionDetailsActivity::class.java)
                    intent.putExtra("blockHash", item.blockHash)
                    intent.putExtra("blockNumber", item.blockNumber)
                    intent.putExtra("timestamp", item.timestamp)
                    intent.putExtra("from", item.from)
                    intent.putExtra("to", item.to)
                    intent.putExtra("value", item.value)
                    intent.putExtra("c", item.gasPriceBean()!!.c.toString())
                    intent.putExtra("gas", item.gas)
                    intent.putExtra("nonce", item.nonce)
                    intent.putExtra("input", item.input)
                    mContext?.startActivity(intent)

                }

            }
        }


    }

    override fun getItemViewType(position: Int): Int {

        if (isLoaderVisible)
        {
            if (type.equals("sent"))
            {
                return if (position == mValues_sent.size-1) VIEW_TYPE_LOADING else VIEW_TYPE_NORMAL

            } else if (type.equals("received"))
            {
                return if (position == mValues_receive.size-1) VIEW_TYPE_LOADING else VIEW_TYPE_NORMAL

            }
            else
            {
                return if (position == mValues.size-1) VIEW_TYPE_LOADING else VIEW_TYPE_NORMAL
            }
        }
        else
        {
            return VIEW_TYPE_NORMAL
        }
    }

    override fun getItemCount(): Int {

        if (type.equals("sent")) {

            return mValues_sent.size

        } else if (type.equals("received")) {

            return mValues_receive.size

        } else {

            return mValues.size

        }
    }

    inner class MyViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val relativeLayout: RelativeLayout = mView.relativeLayout
        val walletName: TextView = mView.walletName
        val imageView: ImageView = mView.imageView
        val timestamp: TextView = mView.timestamp
        val dollarPrice: TextView = mView.dollarPrice
        var value: TextView = mView.value
        var arrow: ImageView = mView.arrow
        var section: RelativeLayout = mView.section

        override fun toString(): String {
            return super.toString() + " wallet "
        }
    }

    //pp
    class ProgressHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var progressBar : ProgressBar?=null
        init {
            progressBar = itemView.findViewById(R.id.progressBar)
        }
    }

    fun addItems(mAll_items: List<WalletTransactionModel.DataBean>,
                 mSent_item: ArrayList<WalletTransactionModel.DataBean>,
                 mRreceive_item: ArrayList<WalletTransactionModel.DataBean>)
    {
        mValues.addAll(mAll_items)
        mValues_sent.addAll(mSent_item)
        mValues_receive.addAll(mRreceive_item)
        notifyDataSetChanged()
    }

    fun addLoading()
    {
        isLoaderVisible = true
        if (type.equals("sent"))
        {
            mValues_sent.add(WalletTransactionModel.DataBean())
            notifyItemInserted(mValues_sent.size - 1)
        }
        else if (type.equals("received"))
        {
            mValues_receive.add(WalletTransactionModel.DataBean())
            notifyItemInserted(mValues_receive.size - 1)
        }
        else
        {
            mValues.add(WalletTransactionModel.DataBean())
            notifyItemInserted(mValues.size - 1)
        }

    }
    fun removeLoading()
    {
        isLoaderVisible = false

        try
        {
            if (type.equals("sent"))
            {
                val position: Int = mValues_sent.size - 1
                val item: WalletTransactionModel.DataBean? = getItem(position)
                if (item != null)
                {
                    mValues_sent.removeAt(position)
                    notifyItemRemoved(position)
                }

            }
            else if (type.equals("received"))
            {
                val position: Int = mValues_receive.size - 1
                val item: WalletTransactionModel.DataBean? = getItem(position)
                if (item != null)
                {
                    mValues_receive.removeAt(position)
                    notifyItemRemoved(position)
                }

            }
            else
            {
                val position: Int = mValues.size - 1
                val item: WalletTransactionModel.DataBean? = getItem(position)
                if (item != null)
                {
                    mValues.removeAt(position)
                    notifyItemRemoved(position)
                }

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
        if (type.equals("sent")) 
        {
            mValues_sent.removeAt(mValues_sent.size - 1)
        } 
        else if (type.equals("received"))
        {
            mValues_receive.removeAt(mValues_receive.size - 1)
        } 
        else 
        {
            mValues.removeAt(mValues.size - 1)
        }

        notifyDataSetChanged()
    }

    fun getItem(position: Int): WalletTransactionModel.DataBean?
    {
        if (type.equals("sent"))
        {
            return mValues_sent.get(position)
        }
        else if (type.equals("received"))
        {
            return mValues_receive.get(position)
        }
        else
        {
            return mValues.get(position)
        }

    }

    fun clear()
    {
        mValues_sent.clear()
        mValues_receive.clear()
        mValues.clear()
        /*if (type.equals("sent"))
        {
            mValues_sent.clear()
        }
        else if (type.equals("received"))
        {
            mValues_receive.clear()
        }
        else
        {
            mValues.clear()
        }*/

        notifyDataSetChanged()
    }
}
