package com.crypto.eltwallet.utilities

import android.content.Context
import android.content.SharedPreferences

class SharedPreferenceUtil(context: Context) {

    lateinit var _prefs: SharedPreferences
    lateinit var _editor: SharedPreferences.Editor

    init {
        _prefs = context.getSharedPreferences(
            "PREF_CLIM8",
            Context.MODE_PRIVATE
        )
        _editor = this._prefs!!.edit()
    }


    fun getString(key: String, defaultvalue: String): String? {
        return if (this._prefs == null) {
            defaultvalue
        } else this._prefs.getString(key, defaultvalue)
    }

    fun setString(key: String, value: String) {
        if (this._editor == null) {
            return
        }
        this._editor.putString(key, value)
    }

    fun getBoolean(key: String, defaultvalue: Boolean?): Boolean? {
        return if (this._prefs == null) {
            defaultvalue
        } else this._prefs.getBoolean(key, defaultvalue!!)
    }

    fun setBoolean(key: String, value: Boolean?) {
        if (this._editor == null) {
            return
        }
        this._editor.putBoolean(key, value!!)
    }

    fun getInt(key: String, defaultvalue: Int): Int {
        return if (this._prefs == null) {
            defaultvalue
        } else this._prefs.getInt(key, defaultvalue)
    }

    fun setInt(key: String, value: Int) {
        if (this._editor == null) {
            return
        }
        this._editor.putInt(key, value)
    }

    fun clearAll() {
        if (this._editor == null) {
            return
        }
        this._editor.clear().commit()

    }

    fun removeOneItem(key: String) {
        if (this._editor == null) {
            return
        }
        this._editor.remove(key)
    }

    fun save() {
        if (this._editor == null) {
            return
        }
        this._editor.commit()
    }


}