package com.crypto.eltwallet.utilities

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class TimeDateFormatter : IAxisValueFormatter {

    override fun getFormattedValue(unixSeconds: Float, axis: AxisBase): String {
        val date = Date(unixSeconds.toLong())
        //  SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:MM", Locale.ENGLISH);
        val sdf = SimpleDateFormat("MM-dd", Locale.ENGLISH)

        return sdf.format(date)
    }
}
