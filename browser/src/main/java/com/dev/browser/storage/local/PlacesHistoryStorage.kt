/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.dev.browser.storage.local

import android.content.Context
import com.dev.browser.concept.storage.*
import com.dev.browser.database.BrowserDatabase
import com.dev.browser.database.history.VisitHistoryEntity
import com.dev.browser.storage.memory.AUTOCOMPLETE_SOURCE_NAME
import com.dev.browser.utils.segmentAwareDomainMatch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.withContext


/**
 * Implementation of the [HistoryStorage] which is backed by a Rust Places lib via [PlacesConnection].
 */
open class PlacesHistoryStorage(context: Context) : HistoryStorage {
    private val scope by lazy { CoroutineScope(Dispatchers.IO) }
    private val historyDao by lazy { BrowserDatabase.get(context).historyDao() }

    override suspend fun recordVisit(uri: String, visitType: VisitType) {
        withContext(scope.coroutineContext) {
            val existHistoryEntity = historyDao.getVisitHistoryByUri(uri)
            if (existHistoryEntity==null){
                historyDao.insert(
                    VisitHistoryEntity(
                        url = uri,
                        title = "",
                        date = System.currentTimeMillis(),
                        visitType = visitType.type
                    )
                )
                return@withContext
            }
            if(existHistoryEntity.visitType==VisitType.NO_EXIST.type){
                    existHistoryEntity.visitType=visitType.type
                    historyDao.update(existHistoryEntity)
             }
        }
    }

    override suspend fun recordObservation(uri: String, observation: PageObservation) {
        // NB: visitType 'UPDATE_PLACE' means "record meta information about this URL".
        withContext(scope.coroutineContext) {
            val existHistoryEntity = historyDao.getVisitHistoryByUri(uri)
            if (existHistoryEntity != null) {
                existHistoryEntity.title = observation.title ?: ""
                historyDao.update(existHistoryEntity)
            }else{
                historyDao.insert(
                    VisitHistoryEntity(
                        url = uri,
                        title = "",
                        date = System.currentTimeMillis(),
                        visitType = VisitType.NO_EXIST.type
                    )
                )
            }

        }
    }

    override suspend fun getVisited(uris: List<String>): List<Boolean> {
        return withContext(scope.coroutineContext) {
             val visitedUris=historyDao.getVisitHistoryByUris(uris)
             uris.map {uri->
                 visitedUris.any { it.url == uri }
             }

        }
    }

    override suspend fun getVisited(): List<String> {
        return withContext(scope.coroutineContext) {
            historyDao.getVisitHistoryList().map { it.url }
        }
    }

    override suspend fun getDetailedVisits(start: Long, end: Long): List<VisitInfo> {
        return withContext(scope.coroutineContext) {
             historyDao.getVisitHistoryListByRange(start,end).map {
                 VisitInfo(url=it.url,title = it.title,visitTime = it.date,visitType = GetVisitTypeByValue(it.visitType))
             }
        }
    }

    override fun cleanup() {
        scope.coroutineContext.cancelChildren()
    }

    //TODO:从其他地方获取，不用从记录中获取
    override fun getSuggestions(query: String, limit: Int): List<SearchResult> {
        require(limit >= 0) { "Limit must be a positive integer" }
//        return historyDao.getVisitHistoryByQuery(query, limit = limit).map {
//            SearchResult(it.url, it.url, 1, it.title)
//        }
        return listOf()
    }

    override fun getAutocompleteSuggestion(query: String): HistoryAutocompleteResult? {
         val history = historyDao.getVisitHistoryByQueryOne(query) ?: return null
        val resultText = segmentAwareDomainMatch(query, arrayListOf(history.url))
        return resultText?.let {
            HistoryAutocompleteResult(
                input = query,
                text = it.matchedSegment,
                url = it.url,
                source = AUTOCOMPLETE_SOURCE_NAME,
                totalItems = 1
            )
        }
    }
}
