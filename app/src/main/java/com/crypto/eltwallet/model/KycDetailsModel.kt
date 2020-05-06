package com.crypto.eltwallet.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class KycDetailsModel
{

    @SerializedName("status")
    @Expose
    private var status: String? = null
    @SerializedName("text")
    @Expose
    private var text: String? = null
    @SerializedName("limit")
    @Expose
    private var limit: Int? = null
    @SerializedName("count_as")
    @Expose
    private var countAs: String? = null

    fun getStatus(): String? {
        return status
    }

    fun setStatus(status: String?) {
        this.status = status
    }

    fun getText(): String? {
        return text
    }

    fun setText(text: String?) {
        this.text = text
    }

    fun getLimit(): Int? {
        return limit
    }

    fun setLimit(limit: Int?) {
        this.limit = limit
    }

    fun getCountAs(): String? {
        return countAs
    }

    fun setCountAs(countAs: String?) {
        this.countAs = countAs
    }
}