/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package com.dev.browser.feature.downloads

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AlertDialog
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
//TODO:自定义样式
class SimpleDownloadDialogFragment : DownloadDialogFragment() {

    @VisibleForTesting
    internal var testingContext: Context? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL,R.style.Dialog_FullScreen)
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        fun getBuilder(themeID: Int): AlertDialog.Builder {
            val context = testingContext ?: requireContext()
            return if (themeID == 0) AlertDialog.Builder(context) else AlertDialog.Builder(context, themeID)
        }

        return with(requireBundle()) {
            val fileName = getString(KEY_FILE_NAME, "")
            val dialogTitleText = getInt(KEY_TITLE_TEXT, R.string.mozac_feature_downloads_dialog_title)
            val positiveButtonText = getInt(KEY_POSITIVE_TEXT,R.string.mozac_feature_downloads_dialog_download)
            val negativeButtonText = getInt(KEY_NEGATIVE_TEXT, R.string.mozac_feature_downloads_dialog_cancel)
            val themeResId = getInt(KEY_THEME_ID, 0)
            val cancelable = getBoolean(KEY_CANCELABLE, false)
            val textView=TextView(requireContext())
            textView.gravity= Gravity.CENTER
            textView.textSize=DensityUtil.dip2px(context,16.0f).toFloat()
            textView.setTextColor(Color.BLACK)
            textView.text=context!!.getText(dialogTitleText)
            textView.height=DensityUtil.dip2px(context,24.0f)
            getBuilder(themeResId)
                .setCustomTitle(textView)
                .setMessage(fileName)
                .setPositiveButton(positiveButtonText) { _, _ ->
                    onStartDownload()
                }
                .setNegativeButton(negativeButtonText) { _, _ ->
                    dismiss()
                }
                .setCancelable(cancelable)
                .create()
        }
    }
    //设置背景色透明
    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setGravity(Gravity.BOTTOM)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val params=this.attributes
            params.dimAmount=0.0f
            this.attributes=params
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
        return arguments ?: throw IllegalStateException("Fragment " + this + " arguments is not set.")
    }
}
