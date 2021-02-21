package com.minerdev.greformanager

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlin.coroutines.Continuation

class HousePagingSource : PagingSource<Int?, House?>() {
    override fun getRefreshKey(pagingState: PagingState<Int?, House?>): Int? {
        return 0
    }

    override fun load(loadParams: LoadParams<Int>, continuation: Continuation<LoadResult<Int?, House?>?>): Any? {
        return null
    }
}