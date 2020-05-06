package com.crypto.eltwallet.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class NewsList
{
    @SerializedName("Type")
    @Expose
    private var type: Int? = null
    @SerializedName("Message")
    @Expose
    private var message: String? = null
    @SerializedName("Promoted")
    @Expose
    private var promoted: List<Any?>? = null
    @SerializedName("Data")
    @Expose
    private var data: List<Datum>? = null
    @SerializedName("RateLimit")
    @Expose
    private var rateLimit: RateLimit? = null
    @SerializedName("HasWarning")
    @Expose
    private var hasWarning: Boolean? = null

    fun getType(): Int? {
        return type
    }

    fun setType(type: Int?) {
        this.type = type
    }

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String?) {
        this.message = message
    }

    fun getPromoted(): List<Any?>? {
        return promoted
    }

    fun setPromoted(promoted: List<Any?>?) {
        this.promoted = promoted
    }

    fun getData(): List<Datum>? {
        return data
    }

    fun setData(data: List<Datum>?) {
        this.data = data
    }

    fun getRateLimit(): RateLimit? {
        return rateLimit
    }

    fun setRateLimit(rateLimit: RateLimit?) {
        this.rateLimit = rateLimit
    }

    fun getHasWarning(): Boolean? {
        return hasWarning
    }

    fun setHasWarning(hasWarning: Boolean?) {
        this.hasWarning = hasWarning
    }

    public class RateLimit {


    }

    class Datum {
        @SerializedName("id")
        @Expose
        var id: String? = null
        @SerializedName("guid")
        @Expose
        var guid: String? = null
        @SerializedName("published_on")
        @Expose
        var publishedOn: Int? = null
        @SerializedName("imageurl")
        @Expose
        var imageurl: String? = null
        @SerializedName("title")
        @Expose
        var title: String? = null
        @SerializedName("url")
        @Expose
        var url: String? = null
        @SerializedName("source")
        @Expose
        var source: String? = null
        @SerializedName("body")
        @Expose
        var body: String? = null
        @SerializedName("tags")
        @Expose
        var tags: String? = null
        @SerializedName("categories")
        @Expose
        var categories: String? = null
        @SerializedName("upvotes")
        @Expose
        var upvotes: String? = null
        @SerializedName("downvotes")
        @Expose
        var downvotes: String? = null
        @SerializedName("lang")
        @Expose
        var lang: String? = null
        @SerializedName("source_info")
        @Expose
        var sourceInfo: SourceInfo? = null

    }

    class SourceInfo {
        @SerializedName("name")
        @Expose
        var name: String? = null
        @SerializedName("lang")
        @Expose
        var lang: String? = null
        @SerializedName("img")
        @Expose
        var img: String? = null

    }
}