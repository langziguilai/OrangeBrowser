package com.dev.browser.feature.contextmenu

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.annotation.VisibleForTesting
import com.dev.browser.R
import com.dev.browser.concept.EngineSession.Companion.OFFLINE_IMAGE_PATH
import com.dev.browser.concept.HitResult
import com.dev.browser.feature.tabs.TabsUseCases
import com.dev.browser.session.Download
import com.dev.browser.session.Session
import com.dev.browser.support.Consumable
import com.dev.browser.support.DownloadUtils
import com.google.android.material.snackbar.Snackbar

data class ContextMenuCandidate(
    val id: String,
    val label: String,
    val showFor: (Session, HitResult) -> Boolean,
    val action: (Session, HitResult) -> Unit
) {
    companion object {
        @VisibleForTesting
        internal var snackbarDelegate = SnackbarDelegate()

        /**
         * Returns the default list of context menu candidates.
         *
         * Use this list if you do not intend to customize the context menu items to be displayed.
         */
        fun defaultCandidates(
            context: Context,
            tabsUseCases: TabsUseCases,
            snackBarParentView: View
        ): List<ContextMenuCandidate> = listOf(
            createOpenInNewTabCandidate(context, tabsUseCases, snackBarParentView),
            createOpenInPrivateTabCandidate(context, tabsUseCases, snackBarParentView),
            createCopyLinkCandidate(context, snackBarParentView),
            createShareLinkCandidate(context),
            createOpenImageInNewTabCandidate(context, tabsUseCases, snackBarParentView),
            createSaveImageCandidate(context),
            createCopyImageLocationCandidate(context, snackBarParentView)
        )

        /**
         * Context Menu item: "Open Link in New Tab".
         */
        fun createOpenInNewTabCandidate(
            context: Context,
            tabsUseCases: TabsUseCases,
            snackBarParentView: View
        ) = ContextMenuCandidate(
            id = "mozac.feature.contextmenu.open_in_new_tab",
            label = context.getString(R.string.mozac_feature_contextmenu_open_link_in_new_tab),
            showFor = { session, hitResult -> hitResult.isLink() && !session.private },
            action = { parent, hitResult ->
                val session = tabsUseCases.addTab.invoke(
                    hitResult.getLink(), selectTab = false, startLoading = true, parent = parent
                )
                tabsUseCases.selectTab.invoke(session)
            }
        )

        /**
         * Context Menu item: "Open Link in Private Tab".
         */
        fun createOpenInPrivateTabCandidate(
            context: Context,
            tabsUseCases: TabsUseCases,
            snackBarParentView: View
        ) = ContextMenuCandidate(
            id = "mozac.feature.contextmenu.open_in_private_tab",
            label = context.getString(R.string.mozac_feature_contextmenu_open_link_in_private_tab),
            showFor = { _, hitResult -> hitResult.isLink() },
            action = { parent, hitResult ->
                val session = tabsUseCases.addPrivateTab.invoke(
                    hitResult.src, selectTab = false, startLoading = true, parent = parent
                )
                tabsUseCases.selectTab.invoke(session)
            }
        )

        /**
         * Context Menu item: "Open Image in New Tab".
         */
        fun createOpenImageInNewTabCandidate(
            context: Context,
            tabsUseCases: TabsUseCases,
            snackBarParentView: View
        ) = ContextMenuCandidate(
            id = "mozac.feature.contextmenu.open_image_in_new_tab",
            label = context.getString(R.string.mozac_feature_contextmenu_open_image_in_new_tab),
            showFor = { _, hitResult -> hitResult.isImage() },
            action = { parent, hitResult ->
                val session = if (parent.private) {
                    tabsUseCases.addPrivateTab.invoke(
                        hitResult.src, selectTab = false, startLoading = true, parent = parent
                    )
                } else {
                    tabsUseCases.addTab.invoke(
                        hitResult.src, selectTab = false, startLoading = true, parent = parent
                    )
                }
                tabsUseCases.selectTab.invoke(session)
            }
        )

        /**
         * Context Menu item: "Save image".
         */
        fun createSaveImageCandidate(
            context: Context
        ) = ContextMenuCandidate(
            id = "mozac.feature.contextmenu.save_image",
            label = context.getString(R.string.mozac_feature_contextmenu_save_image),
            showFor = { _, hitResult -> hitResult.isImage() },
            action = { session, hitResult ->
                session.download = Consumable.from(
                    Download(
                        url=hitResult.src,
                        fileName = DownloadUtils.guessFileName(null, hitResult.src, null),
                        referer = session.url,
                        cookies = session.getCookies(hitResult.src),
                        destinationDirectory = OFFLINE_IMAGE_PATH
                    )
                )
            }
        )

        /**
         * Context Menu item: "Share Link".
         */
        fun createShareLinkCandidate(
            context: Context
        ) = ContextMenuCandidate(
            id = "mozac.feature.contextmenu.share_link",
            label = context.getString(R.string.mozac_feature_contextmenu_share_link),
            showFor = { _, hitResult -> hitResult.isLink() },
            action = { _, hitResult ->
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    putExtra(Intent.EXTRA_TEXT, hitResult.getLink())
                }

                val shareIntent = Intent.createChooser(
                    intent,
                    context.getString(R.string.mozac_feature_contextmenu_share_link)
                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                context.startActivity(shareIntent)
            }
        )

        /**
         * Context Menu item: "Copy Link".
         */
        fun createCopyLinkCandidate(
            context: Context,
            snackBarParentView: View
        ) = ContextMenuCandidate(
            id = "mozac.feature.contextmenu.copy_link",
            label = context.getString(R.string.mozac_feature_contextmenu_copy_link),
            showFor = { _, hitResult -> hitResult.isLink() },
            action = { _, hitResult ->
                val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(hitResult.getLink(), hitResult.getLink())
                clipboardManager.primaryClip = clip
                snackbarDelegate.show(
                    snackBarParentView = snackBarParentView,
                    text = R.string.mozac_feature_contextmenu_snackbar_text_copied,
                    duration = Snackbar.LENGTH_SHORT
                )
            }
        )

        /**
         * Context Menu item: "Copy Image Location".
         */
        fun createCopyImageLocationCandidate(
            context: Context,
            snackBarParentView: View
        ) = ContextMenuCandidate(
            id = "mozac.feature.contextmenu.copy_image_location",
            label = context.getString(R.string.mozac_feature_contextmenu_copy_image_location),
            showFor = { _, hitResult -> hitResult.isImage() },
            action = { _, hitResult ->
                val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(hitResult.getLink(), hitResult.src)
                clipboardManager.primaryClip = clip

                snackbarDelegate.show(
                    snackBarParentView = snackBarParentView,
                    text = R.string.mozac_feature_contextmenu_snackbar_text_copied,
                    duration = Snackbar.LENGTH_SHORT
                )
            }
        )
    }
}

// Some helper methods to work with HitResult. We may want to improve the API of HitResult and remove some of the
// helpers eventually: https://github.com/mozilla-mobile/android-components/issues/1443

private fun HitResult.isImage(): Boolean =
    (this is HitResult.IMAGE || this is HitResult.IMAGE_SRC) && src.isNotEmpty()

private fun HitResult.isLink(): Boolean =
    ((this is HitResult.UNKNOWN && src.isNotEmpty()) || this is HitResult.IMAGE_SRC) &&
            getLink().startsWith("http")

private fun HitResult.getLink(): String = when (this) {
    is HitResult.UNKNOWN -> src
    is HitResult.IMAGE_SRC -> uri
    else -> "about:blank"
}

internal open class SnackbarDelegate {
    open fun show(
        snackBarParentView: View,
        text: Int,
        duration: Int,
        action: Int = 0,
        listener: ((v: View) -> Unit)? = null
    ) {
        val snackbar = Snackbar.make(
            snackBarParentView,
            text,
            duration
        )

        if (action != 0 && listener != null) {
            snackbar.setAction(action, listener)
        }

        snackbar.show()
    }
}