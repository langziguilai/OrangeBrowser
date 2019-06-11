/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.dev.browser.feature.session

import com.dev.browser.concept.history.HistoryTrackingDelegate
import com.dev.browser.concept.storage.HistoryStorage
import com.dev.browser.concept.storage.PageObservation
import com.dev.browser.concept.storage.VisitType


/**
 * Implementation of the [HistoryTrackingDelegate] which delegates work to an instance of [HistoryStorage].
 */
class HistoryDelegate(private val historyStorage: HistoryStorage) : HistoryTrackingDelegate {
    override suspend fun onVisited(uri: String, isReload: Boolean) {
        val visitType = when (isReload) {
            true -> VisitType.RELOAD
            false -> VisitType.LINK
        }
        historyStorage.recordVisit(uri, visitType)
    }

    override suspend fun onTitleChanged(uri: String, title: String) {
        historyStorage.recordObservation(uri, PageObservation(title = title))
    }

    override suspend fun getVisited(uris: List<String>): List<Boolean> {
        return historyStorage.getVisited(uris)
    }

    override suspend fun getVisited(): List<String> {
        return historyStorage.getVisited()
    }
}
