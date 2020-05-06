package com.crypto.eltwallet.pagination

import android.nfc.tech.MifareUltralight
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class PaginationListener(var layoutManager: LinearLayoutManager)  : RecyclerView.OnScrollListener() {
    companion object
    {
        var PAGE_START : Int = 1
        var PAGE_SIZE : Int = 20
    }


    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int)
    {
        super.onScrolled(recyclerView, dx, dy)
        val visibleItemCount = layoutManager.childCount
        val totalItemCount = layoutManager.itemCount
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
        if (!isLoading() && !isLastPage())
        {
            if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0 &&
                totalItemCount >= MifareUltralight.PAGE_SIZE)
            {
                loadMoreItems()
            }
        }
    }

    protected abstract fun loadMoreItems()
    abstract fun isLastPage(): Boolean
    abstract fun isLoading(): Boolean
}