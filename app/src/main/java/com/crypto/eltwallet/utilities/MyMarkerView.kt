package com.crypto.eltwallet.utilities

import com.github.mikephil.charting.utils.MPPointF
import android.content.Context
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import kotlinx.android.synthetic.main.custom_marker_view_layout.view.*

class MyMarkerView(context: Context, layoutResource: Int, closeValue: Float, dateValue : String) : MarkerView(context, layoutResource) {

    var value: Float = closeValue
    var datev: String = dateValue

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    override fun refreshContent(e: Entry?, highlight: Highlight?) {

        close.text = " ($" + value + ")"
        date.text = datev

//        if (e is CandleEntry) {
//
//            val ce = e as CandleEntry?
//
////            tvContent.text = "" + Utils.formatNumber(ce!!.high, 0, true)
//        } else {
//
////            tvContent.text = "" + Utils.formatNumber(e!!.y, 0, true)
//        }

        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
    }
}
