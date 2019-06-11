/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.dev.browser.feature.downloads

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import com.dev.browser.R
import com.dev.util.DensityUtil

/**
 * A confirmation dialog to be called before a download is triggered.
 * Meant to be used in collaboration with [DownloadsFeature]
 *
 * [SimpleDownloadDialogFragment] is the default dialog use by DownloadsFeature if you don't provide a value.
 * It is composed by a title, a negative and a positive bottoms. When the positive button is clicked
 * the download it triggered.
 *
 */

class SimpleDownloadDialogFragment : DownloadDialogFragment() {
    override fun getLayoutId(): Int {
        return R.layout.fragment_mozac_feature_download
    }

    override fun initViewAndData(rootView: View, bundle: Bundle?) {
        return with(requireBundle()) {
            val fileName = getString(KEY_FILE_NAME, "")
            val titleText = getInt(KEY_TITLE_TEXT, R.string.mozac_feature_downloads_dialog_title)
            val positiveButtonText = getInt(KEY_POSITIVE_TEXT,R.string.mozac_feature_downloads_dialog_download)
            val negativeButtonText = getInt(KEY_NEGATIVE_TEXT, R.string.mozac_feature_downloads_dialog_cancel)
            rootView.findViewById<AppCompatTextView>(R.id.title).setText(titleText)
            rootView.findViewById<AppCompatTextView>(R.id.file).text = fileName
            rootView.findViewById<AppCompatTextView>(R.id.cancel_button).apply {
                setText(negativeButtonText)
                setOnClickListener {
                    dismiss()
                }
            }
            rootView.findViewById<AppCompatTextView>(R.id.ok_button).apply {
                setText(positiveButtonText)
                setOnClickListener {
                    dismiss()
                    onStartDownload()
                }
            }
            rootView.findViewById<View>(R.id.container).apply {
                setOnClickListener {
                    dismiss()
                }
            }
        }
    }

    companion object {
        /**
         * A builder method for creating a [SimpleDownloadDialogFragment]
         */
        fun newInstance(
            @StringRes dialogTitleText: Int = R.string.mozac_feature_downloads_dialog_download,
            @StringRes positiveButtonText: Int = R.string.mozac_feature_downloads_dialog_download,
            @StringRes negativeButtonText: Int = R.string.mozac_feature_downloads_dialog_cancel,
            @StyleRes themeResId: Int = 0,
            cancelable: Boolean = false
        ): SimpleDownloadDialogFragment {
            val fragment = SimpleDownloadDialogFragment()
            val arguments = fragment.arguments ?: Bundle()

            with(arguments) {
                putInt(KEY_TITLE_TEXT, dialogTitleText)

                putInt(KEY_POSITIVE_TEXT, positiveButtonText)

                putInt(KEY_NEGATIVE_TEXT, negativeButtonText)

                putInt(KEY_THEME_ID, themeResId)

                putBoolean(KEY_CANCELABLE, cancelable)
            }

            fragment.arguments = arguments

            return fragment
        }

        const val KEY_POSITIVE_TEXT = "KEY_POSITIVE_TEXT"

        const val KEY_NEGATIVE_TEXT = "KEY_NEGATIVE_TEXT"

        const val KEY_TITLE_TEXT = "KEY_TITLE_TEXT"

        const val KEY_THEME_ID = "KEY_THEME_ID"

        const val KEY_CANCELABLE = "KEY_CANCELABLE"
    }

    private fun requireBundle(): Bundle {
        return arguments ?: throw IllegalStateException("Fragment $this arguments is not set.")
    }
}
