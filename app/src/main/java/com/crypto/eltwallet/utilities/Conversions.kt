package com.crypto.eltwallet.utilities

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class Conversions{

    companion object{

        fun eltDecimals (number : Double) : String {

            var data : String = String.format("%.6f", number)

            return data
        }

        fun dollarDecimals (number : Double) : String {

            var data : String = String.format("%.2f", number)

            return data
        }

        fun showPattern (walletAddress : String) : String {

            return walletAddress.substring(0, 2) + "*****" + walletAddress.substring(walletAddress.length - 4)
        }

        fun getTimeFormat(unixSeconds: String): String {

            val date = Date(unixSeconds.toLong()*1000)
            //  SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:MM", Locale.ENGLISH);
            val sdf = SimpleDateFormat("dd-MM-YYYY hh:mm a", Locale.ENGLISH)

            return sdf.format(date)
        }

        fun getTimeFormatChartDay(unixSeconds: String): String {

            val date = Date(unixSeconds.toLong())
            val sdf = SimpleDateFormat("dd-MM", Locale.ENGLISH)

            return sdf.format(date)
        }

        fun getTimeFormatChartMonth(unixSeconds: String): String {

            val date = Date(unixSeconds.toLong())
            //  SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:MM", Locale.ENGLISH);
            val sdf = SimpleDateFormat("MMM", Locale.ENGLISH)

            return sdf.format(date)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun getTimeFormatFromInstant(instant: String): String {

            val instant = Instant.parse(instant)

//            val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
//                .withLocale(Locale.ENGLISH)
//                .withZone(ZoneId.systemDefault())

            val formatter = DateTimeFormatter.ofPattern("dd-MM-YYYY hh:mm a")
                .withLocale(Locale.ENGLISH)
                .withZone(ZoneId.systemDefault())

            val output : String = formatter.format(instant)

            return output
        }
    }
}