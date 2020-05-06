package com.crypto.eltwallet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.crypto.eltwallet.R
import com.crypto.eltwallet.interfaces.DocsIdInterface
import com.crypto.eltwallet.model.GetKycDocTypesModel
import kotlinx.android.synthetic.main.layout_kyc_doc_types.view.*

class KycDocTypeAdapter(
    private val mValues: List<GetKycDocTypesModel.DataBean>,
    private val mContext : Context?,
    private var mCallback: DocsIdInterface
) : RecyclerView.Adapter<KycDocTypeAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_kyc_doc_types, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]

        holder.name.text = item.name

        holder.ll_main.setOnClickListener {
            mCallback.docsId(mValues[position].id!!, mValues[position].name!!)
        }

    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val name: TextView = mView.name
        var ll_main : LinearLayout = mView.ll_main

        override fun toString(): String {
            return super.toString() + " wallet "
        }
    }
}
