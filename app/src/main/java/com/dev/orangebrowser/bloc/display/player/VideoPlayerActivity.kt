package com.dev.orangebrowser.bloc.display.player

import android.content.pm.ActivityInfo
import android.os.Bundle
import com.dev.base.BaseNotchActivity
import com.dev.base.extension.enterToImmersiveMode
import com.dev.base.extension.showToast
import com.dev.orangebrowser.R
import com.dev.view.notchtools.NotchTools
import com.dev.view.notchtools.core.NotchProperty
import com.dev.view.notchtools.core.OnNotchCallBack
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer

class VideoPlayerActivity : BaseNotchActivity(), OnNotchCallBack {

    override fun useDataBinding(): Boolean {
        return false
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_video_player
    }

    override fun initView(savedInstanceState: Bundle?) {
        NotchTools.getFullScreenTools()
            .fullScreenUseStatus(this, this)
    }

    override fun onRestart() {
        super.onRestart()
        //restart的时候恢复到竖直
        if (resources.configuration.orientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
        val localPath=intent.getStringExtra(LOCALPATH)
        when {
            localPath.isNotBlank() -> startPlay(localPath)
            intent.getStringExtra(URL).isNotBlank() -> startPlay(intent.getStringExtra(URL))
            else -> showToast(getString(R.string.tip_no_usable_source))
        }
    }
    private fun startPlay(path:String){
        findViewById<StandardGSYVideoPlayer>(R.id.content_item_video)?.apply {
            val gsyVideoOption = GSYVideoOptionBuilder()
            gsyVideoOption
                .setIsTouchWiget(true)
                .setRotateViewAuto(false)
                .setLockLand(false)
                .setAutoFullWithSize(true)
                .setShowFullAnimation(true)
                .setNeedLockFull(true)
                .setUrl(path)
                .setCacheWithPlay(true)
                .setLooping(true)
                .setEnlargeImageRes(R.drawable.ic_fullscreen_white_24dp)
                .setShrinkImageRes(R.drawable.ic_fullscreen_exit_white_24dp)
                .build(this)
            val orientationUtils = OrientationUtils(this@VideoPlayerActivity, this)
            this.fullscreenButton.setOnClickListener {
                orientationUtils.resolveByClick()
            }
            this.backButton.setOnClickListener {
                if (orientationUtils.isLand > 0) {
                    orientationUtils.resolveByClick()
                } else {
                    this@VideoPlayerActivity.onBackPressed()
                }
            }
            startPlayLogic()
        }
    }
    //自动播放
    override fun onResume() {
        GSYVideoManager.instance().start()
        enterToImmersiveMode()
        super.onResume()
    }

    override fun onPause() {
        GSYVideoManager.instance().pause()
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        GSYVideoManager.releaseAllVideos()
    }


    //全屏时处理顶部高度
    override fun onNotchPropertyCallback(notchProperty: NotchProperty) {

    }

    companion object {
        const val URL = "url"
        const val REFERER = "referer"
        const val LOCALPATH ="localPath"
    }
}