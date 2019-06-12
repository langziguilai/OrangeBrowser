package com.dev.orangebrowser.bloc.display.image

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.dev.base.BaseNotchActivity
import com.dev.base.extension.*
import com.dev.orangebrowser.R
import com.dev.orangebrowser.data.model.SimpleImage
import com.dev.orangebrowser.databinding.ActivityImageDisplayBinding
import com.dev.orangebrowser.eventbus.ChangeRecyclerViewIndexEvent
import com.dev.orangebrowser.extension.appComponent
import com.dev.view.GlideHelper
import com.dev.view.StatusBarUtil
import com.dev.view.biv.concept.view.BigImageView
import com.dev.view.biv.factory.GlideImageViewFactory
import com.dev.view.notchtools.NotchTools
import com.dev.view.notchtools.core.NotchProperty
import com.dev.view.notchtools.core.OnNotchCallBack
import com.dev.view.recyclerview.CustomBaseViewHolder
import com.dev.view.recyclerview.adapter.base.BaseQuickAdapter
import com.hw.ycshareelement.YcShareElement
import com.hw.ycshareelement.transition.IShareElements
import com.hw.ycshareelement.transition.ShareElementInfo
import org.greenrobot.eventbus.EventBus
import java.io.File

class ImageDisplayActivity : BaseNotchActivity(), OnNotchCallBack, IShareElements {
    lateinit var viewModel: ImageDisplayViewModel
    lateinit var binding: ActivityImageDisplayBinding

    override fun onBackPressed() {
        val item = data[binding.viewPager.currentItem]
        val view = binding.transitionImg.apply {
            if (item.path != null) {
                transitionName = item.path!!
            } else if (item.url != null) {
                transitionName = item.url!!
            }
        }
        shareElementInfo = ShareElementInfo(view, item)
        binding.container.hide()
        binding.transitionImg.show()
        val bigImageView = getSelectImageView(binding.viewPager.currentItem)
        if (bigImageView == null) {
            finishAfterTransition()
        }else{
            val minScale=bigImageView.ssiv.excute("minScale") as Float
            val currentScale=bigImageView.ssiv.scale
            if (Math.abs(minScale-currentScale)>0.01){
                bigImageView.ssiv.animateScale(minScale)?.apply {
                    withOnAnimationEventListener(object:SubsamplingScaleImageView.OnAnimationEventListener{
                        override fun onComplete() {
                            finishAfterTransition()
                        }

                        override fun onInterruptedByUser() {}

                        override fun onInterruptedByNewAnim() {}
                    })
                }?.start()
            }else{
                finishAfterTransition()
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        YcShareElement.setEnterTransitions(this, this)
        //注入
        appComponent.inject(this)
        viewModel = ViewModelProviders.of(this, factory).get(ImageDisplayViewModel::class.java)
        super.onCreate(savedInstanceState)
    }

    override fun useDataBinding(): Boolean {
        return true
    }

    override fun getContentView(): View? {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.activity_image_display, mContentContainer, false)
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

    lateinit var data: ArrayList<SimpleImage>
    var currentPosition: Int = 0
    lateinit var adapter: BaseQuickAdapter<SimpleImage, CustomBaseViewHolder>
    override fun initData(savedInstanceState: Bundle?) {
        binding.activity = this
        binding.viewModel = viewModel
        viewModel.changeColor(resources.getColor(R.color.colorBlack))
        currentPosition = intent.getIntExtra(POSITION, 0)
        data = intent.getParcelableArrayListExtra<SimpleImage>(IMAGES)

        adapter = object : BaseQuickAdapter<SimpleImage, CustomBaseViewHolder>(
            R.layout.item_big_image,
            data
        ) {
            override fun convert(helper: CustomBaseViewHolder, item: SimpleImage) {
                helper.addOnClickListener(R.id.image)
                val headers = HashMap<String, String>()
                if (item.referer != null) {
                    headers["referer"] = item.referer!!
                }
                helper.itemView.findViewById<BigImageView>(R.id.image).setImageViewFactory(GlideImageViewFactory())
                if (item.path != null) {
                    helper.itemView.findViewById<BigImageView>(R.id.image)
                        .showImage(Uri.fromFile(File(item.path!!)), headers)
                } else if (item.url != null) {
                    helper.itemView.findViewById<BigImageView>(R.id.image).showImage(Uri.parse(item.url), headers)
                }

            }
        }
        adapter.setOnItemChildClickListener { _, _, _ ->
            showStatusBar = !showStatusBar
            if (showStatusBar) {
                resumeImageSizeAndShowStatus()
            } else {
                hide(true)
            }
        }

        binding.viewPager.adapter = adapter
        binding.viewPager.setCurrentItem(currentPosition, false)
        binding.viewPager.registerOnPageChangeCallback(object:ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                EventBus.getDefault().postSticky(ChangeRecyclerViewIndexEvent(position))
                val item=data[position]
                if (item.path!=null){
                    GlideHelper.loadLocalImage(binding.transitionImg,item.path)
                } else if(item.url!=null){
                    GlideHelper.loadRemoteImage(binding.transitionImg,item.url,item.referer)
                }
            }
        })
        val item=data[currentPosition]
        if (item.path!=null){
            GlideHelper.loadLocalImage(binding.transitionImg,item.path)
        } else if(item.url!=null){
            GlideHelper.loadRemoteImage(binding.transitionImg,item.url,item.referer)
        }
        YcShareElement.postStartTransition(this)
    }

    private fun getShareElement(position: Int): ShareElementInfo<SimpleImage> {
        val simpleImage = data[position]
        val imageView = getSelectImageView(position)
        imageView?.apply {
            if (simpleImage.path != null) {
                ViewCompat.setTransitionName(this, simpleImage.path)
            } else if (simpleImage.url != null) {
                ViewCompat.setTransitionName(this, simpleImage.url)
            }
        }
        return ShareElementInfo<SimpleImage>(imageView!!, simpleImage)
    }

    private fun getSelectImageView(position: Int): BigImageView? {
        val recyclerView = binding.viewPager.getProperty("mRecyclerView") as RecyclerView
        return adapter.getViewByPosition(recyclerView, position, R.id.image) as? BigImageView
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
        }.start()
        StatusBarUtil.setDarkIcon(this)
    }

    private fun resumeImageSizeAndShowStatus(){
        val bigImageView = getSelectImageView(binding.viewPager.currentItem)
        if (bigImageView == null) {
              show()
        }else{
            val minScale=bigImageView.ssiv.excute("minScale") as Float
            val currentScale=bigImageView.ssiv.scale
            if (Math.abs(minScale-currentScale)>0.01){
                 bigImageView.ssiv.animateScale(minScale)?.apply {
                     withOnAnimationEventListener(object:SubsamplingScaleImageView.OnAnimationEventListener{
                         override fun onComplete() {
                             show()
                         }

                         override fun onInterruptedByUser() {}

                         override fun onInterruptedByNewAnim() {}
                     })
                 }?.start()
            }else{
                show()
            }
        }
    }
    private fun show() {
        binding.header.postDelayed({
            viewModel.changeColor(resources.getColor(R.color.white))
        }, 100)
        showBar()
        binding.header.animate().apply {
            translationY(0f)
            alpha(1f)
            duration = 200
            interpolator = DecelerateInterpolator()
        }.start()

        StatusBarUtil.setDarkIcon(this)
    }

    //显示Statusbar
    private fun showBar() {
        val uiOptions = window.decorView.systemUiVisibility
        var newUiOptions = uiOptions
        val isImmersiveModeEnabled = uiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY == uiOptions
        if (isImmersiveModeEnabled) {

            //先取 非 后再 与， 把对应位置的1 置成0，原本为0的还是0
            if (Build.VERSION.SDK_INT >= 14) {
                newUiOptions = newUiOptions and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION.inv()
            }
            if (Build.VERSION.SDK_INT >= 16) {
                newUiOptions = newUiOptions and View.SYSTEM_UI_FLAG_FULLSCREEN.inv()
            }

            if (Build.VERSION.SDK_INT >= 19) {
                newUiOptions = newUiOptions and View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY.inv()
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)//设置透明状态栏
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)//设置透明导航栏
                window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            }
            window.decorView.systemUiVisibility = newUiOptions
        }
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
        const val IMAGES = "images"
    }

    var shareElementInfo: ShareElementInfo<SimpleImage>? = null
    override fun getShareElements(): Array<ShareElementInfo<SimpleImage>> {
        if (shareElementInfo == null) {
            shareElementInfo = getShareElement(binding.viewPager.currentItem)
        }
        return arrayOf(shareElementInfo!!)
    }

    override fun finishAfterTransition() {
        YcShareElement.finishAfterTransition(this, this)
        super.finishAfterTransition()
    }


}