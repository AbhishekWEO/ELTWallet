package com.crypto.eltwallet.utilities

class MEssageEvent
{
    var which_time:String?=null


    fun MEssageEvent(which_time: String?) {
        this.which_time = which_time
    }

    fun getWhichTime(): String? {
        return which_time
    }

    fun setWhichTime(which_time: String?) {
        this.which_time = which_time
    }
}