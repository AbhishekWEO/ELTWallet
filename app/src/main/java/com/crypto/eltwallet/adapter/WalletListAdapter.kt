package com.crypto.eltwallet.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.crypto.eltwallet.R
import com.crypto.eltwallet.activities.WalletDetailsActivity
import com.crypto.eltwallet.model.WalletListModel
import com.crypto.eltwallet.utilities.CapitalUtils
import com.crypto.eltwallet.utilities.ConstantsRequest
import com.crypto.eltwallet.utilities.Conversions
import kotlinx.android.synthetic.main.layout_wallet.view.*


class WalletListAdapter(
    private val mValues: List<WalletListModel.DataBean>,
    private val mContext : Context?,
    private val dollar_price : Double
) : RecyclerView.Adapter<WalletListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_wallet, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]

        holder.walletName.text = CapitalUtils.capitalize(item.walletName)
        holder.walletAddress.text = item.walletAddress

        Log.e("walletAddress", item.walletAddress)

        var w : String = item.walletBal.toString()
        var elt = w.substring(0, w.length - 4)

        holder.walletBal.text = Conversions.eltDecimals(elt.toDouble()) + " ELT"
        holder.dollarPrice.text = "($" + Conversions.dollarDecimals((elt.toDouble()) * dollar_price) + ")"

        holder.current_section.setOnClickListener(View.OnClickListener {

            val intent = Intent(mContext, WalletDetailsActivity::class.java)
            intent.putExtra(ConstantsRequest.WALLET_ID, item.id)
            intent.putExtra(ConstantsRequest.WALLET_NAME, CapitalUtils.capitalize(item.walletName))
            intent.putExtra(ConstantsRequest.WALLET_ADDRESS, item.walletAddress)
            intent.putExtra(ConstantsRequest.WALLET_BALANCE, Conversions.eltDecimals(elt.toDouble()))
            intent.putExtra(ConstantsRequest.DOLLAR_PRICE, Conversions.dollarDecimals((elt.toDouble()) * dollar_price))
            intent.putExtra("dollar", dollar_price.toString())
            mContext?.startActivity(intent)

        })


    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val walletName: TextView = mView.walletName
        val walletAddress: TextView = mView.walletAddress
        val walletBal: TextView = mView.walletBal
        val dollarPrice: TextView = mView.dollarPrice
        val arrow : ImageView = mView.arrow
        val current_section : RelativeLayout = mView.current_section

        override fun toString(): String {
            return super.toString() + " wallet "
        }
    }
}
