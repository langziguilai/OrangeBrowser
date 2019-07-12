package com.dev.orangebrowser.bloc.display.video

import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.dev.base.BaseNotchActivity
import com.dev.base.extension.getProperty
import com.dev.base.extension.hide
import com.dev.base.extension.onGlobalLayoutComplete
import com.dev.base.extension.show
import com.dev.orangebrowser.R
import com.dev.orangebrowser.data.model.SimpleVideo
import com.dev.orangebrowser.databinding.ActivityVideoDisplayBinding
import com.dev.orangebrowser.eventbus.ChangeRecyclerViewIndexEvent
import com.dev.orangebrowser.extension.appComponent
import com.dev.view.GlideHelper
import com.dev.view.StatusBarUtil
import com.dev.view.notchtools.NotchTools
import com.dev.view.notchtools.core.NotchProperty
import com.dev.view.notchtools.core.OnNotchCallBack
import com.dev.view.recyclerview.CustomBaseViewHolder
import com.dev.view.recyclerview.adapter.base.BaseQuickAdapter
import com.hw.ycshareelement.YcShareElement
import com.hw.ycshareelement.transition.IShareElements
import com.hw.ycshareelement.transition.ShareElementInfo
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import org.greenrobot.eventbus.EventBus

class VideoDisplayActivity : BaseNotchActivity(), OnNotchCallBack, IShareElements {
    lateinit var viewModel: VideoDisplayViewModel
    lateinit var binding: ActivityVideoDisplayBinding

    override fun onBackPressed() {
        finishAfterTransition()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        YcShareElement.setEnterTransitions(this, this)
        //注入
        appComponent.inject(this)
        viewModel = ViewModelProviders.of(this, factory).get(VideoDisplayViewModel::class.java)
        super.onCreate(savedInstanceState)
    }

    override fun useDataBinding(): Boolean {
        return true
    }

    override fun getContentView(): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.activity_video_display, mContentContainer, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    private var showStatusBar = false
    override fun initView(savedInstanceState: Bundle?) {
        NotchTools.getFullScreenTools()
            .fullScreenUseStatus(this, this)
        binding.header.onGlobalLayoutComplete {
            hide()
        }
    }

    lateinit var data: ArrayList<SimpleVideo>
    var currentPosition: Int = 0
    lateinit var adapter: BaseQuickAdapter<SimpleVideo, CustomBaseViewHolder>
    override fun initData(savedInstanceState: Bundle?) {

        binding.activity = this
        binding.viewModel = viewModel
        viewModel.changeColor(resources.getColor(R.color.colorBlack))
        currentPosition = intent.getIntExtra(POSITION, 0)
        data = intent.getParcelableArrayListExtra<SimpleVideo>(VIDEOES)

        adapter = object : BaseQuickAdapter<SimpleVideo, CustomBaseViewHolder>(
            R.layout.item_big_video,
            data
        ) {
            override fun convert(helper: CustomBaseViewHolder, item: SimpleVideo) {
                helper.addOnClickListener(R.id.poster)

                val player=helper.itemView.findViewById<CustomGsyVideoPlayer>(R.id.content_item_video)?.apply {
                    val gsyVideoOption = GSYVideoOptionBuilder()
                    gsyVideoOption
                        .setIsTouchWiget(true)
                        .setRotateViewAuto(false)
                        .setLockLand(false)
                        .setAutoFullWithSize(true)
                        .setShowFullAnimation(true)
                        .setNeedLockFull(true)
                        .setUrl(item.path)
                        .setCacheWithPlay(true)
                        .setLooping(true)
                        .setEnlargeImageRes(R.drawable.ic_fullscreen_white_24dp)
                        .setShrinkImageRes(R.drawable.ic_fullscreen_exit_white_24dp)
                        .setGSYVideoProgressListener { progress, _, _, _ ->
//                            if (progress > 0) {
//                                this.show()
//                            }
                        }
                        .build(this)
                    val orientationUtils = OrientationUtils(this@VideoDisplayActivity, this)
                    this.fullscreenButton.setOnClickListener {
                        orientationUtils.resolveByClick()
                    }
                    this.backButton.setOnClickListener {
                        if (orientationUtils.isLand > 0) {
                            orientationUtils.resolveByClick()
                        } else {
                            this@VideoDisplayActivity.onBackPressed()
                        }
                    }
                    startPlayLogic()
                    hide()
                }
                helper.itemView.findViewById<ImageView>(R.id.poster)?.apply {
                    if (item.localPoster!=null && item.localPoster!!.isNotBlank()){
                        GlideHelper.loadLocalImage(this, item.localPoster)
                    }else{
                        GlideHelper.loadRemoteImage(this, item.poster, item.referer)
                    }
                    transitionName = item.path
                    postDelayed({
                        player?.show()
                        player?.hideUi()
                    },500)
                    postDelayed({
                        this.hide()
                    },1000)
                }
            }
        }

        binding.viewPager.adapter = adapter
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                //暂停播放上一个
                try {
                    GSYVideoManager.instance().stop()
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                }
                //
                getSelectVideoView(position)?.startPlayLogic()
                EventBus.getDefault().postSticky(ChangeRecyclerViewIndexEvent(position))
            }
        })
        binding.viewPager.setCurrentItem(currentPosition, false)
        YcShareElement.postStartTransition(this)
    }

    //自动播放
    override fun onResume() {
        GSYVideoManager.instance().start()
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
    private fun getShareElement(position: Int): ShareElementInfo<SimpleVideo> {
        val simpleVideo = data[position]
        val posterImageView = getSelectPosterImageView(position)
        return ShareElementInfo<SimpleVideo>(posterImageView!!, simpleVideo)
    }

    private fun getSelectPosterImageView(position: Int): ImageView? {
        val recyclerView = binding.viewPager.getProperty("mRecyclerView") as RecyclerView
        return adapter.getViewByPosition(recyclerView, position, R.id.poster) as? ImageView
    }

    private fun getSelectVideoView(position: Int): StandardGSYVideoPlayer? {
        val recyclerView = binding.viewPager.getProperty("mRecyclerView") as RecyclerView
        return adapter.getViewByPosition(recyclerView, position, R.id.content_item_video) as? StandardGSYVideoPlayer
    }

    private fun hide(delay: Boolean = false) {
        if (delay) {
            binding.header.postDelayed({
                viewModel.changeColor(resources.getColor(R.color.colorBlack))
            }, 100)
        } else {
            viewModel.changeColor(resources.getColor(R.color.colorBlack))
        }
        hideBar()
        binding.header.animate().apply {
            val y = NotchTools.getFullScreenTools().getStatusHeight(window) + binding.header.height
            translationY(-y.toFloat())
            alpha(0f)
            duration = 200
            interpolator = AccelerateInterpolator()
            withEndAction {
                binding.header.hide()
            }
        }.start()
        StatusBarUtil.setDarkIcon(this)
    }

    private fun hideBar() {
        NotchTools.getFullScreenTools()
            .fullScreenUseStatus(this, this)
    }

    //全屏时处理顶部高度
    override fun onNotchPropertyCallback(notchProperty: NotchProperty) {
        val statusBarHeight = NotchTools.getFullScreenTools().getStatusHeight(window)
        if (binding.header.paddingTop < statusBarHeight) {
            binding.header.setPadding(
                binding.header.paddingLeft,
                binding.header.paddingTop + statusBarHeight,
                binding.header.paddingRight, binding.header.paddingBottom
            )
        }
    }

    companion object {
        const val POSITION = "position"
        const val VIDEOES = "videos"
    }

    var shareElementInfo: ShareElementInfo<SimpleVideo>? = null
    override fun getShareElements(): Array<ShareElementInfo<SimpleVideo>> {
        shareElementInfo = getShareElement(binding.viewPager.currentItem)
        return arrayOf(shareElementInfo!!)
    }

    override fun finishAfterTransition() {
        YcShareElement.finishAfterTransition(this, this)
        super.finishAfterTransition()
    }


}