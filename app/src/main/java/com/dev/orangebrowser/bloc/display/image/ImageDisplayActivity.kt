package com.dev.orangebrowser.bloc.display.image

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.dev.base.BaseNotchActivity
import com.dev.base.extension.onGlobalLayoutComplete
import com.dev.orangebrowser.R
import com.dev.orangebrowser.data.model.SimpleImage
import com.dev.orangebrowser.databinding.ActivityImageDisplayBinding
import com.dev.orangebrowser.extension.appComponent
import com.dev.view.StatusBarUtil
import com.dev.view.biv.concept.view.BigImageView
import com.dev.view.notchtools.NotchTools
import com.dev.view.notchtools.core.NotchProperty
import com.dev.view.notchtools.core.OnNotchCallBack
import com.dev.view.recyclerview.CustomBaseViewHolder
import com.dev.view.recyclerview.adapter.base.BaseQuickAdapter
import java.io.File

class ImageDisplayActivity : BaseNotchActivity(), OnNotchCallBack {
    lateinit var viewModel: ImageDisplayViewModel
    lateinit var binding: ActivityImageDisplayBinding

    override fun onBackPressed() {
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
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
        binding.header.onGlobalLayoutComplete {
            hide()
        }
    }

    lateinit var data:ArrayList<SimpleImage>
    var currentPosition:Int=0
    override fun initData(savedInstanceState: Bundle?) {
        binding.activity = this
        binding.viewModel = viewModel
        viewModel.changeColor(resources.getColor(R.color.colorBlack))
        currentPosition=intent.getIntExtra(POSITION,0)
        data=intent.getParcelableArrayListExtra<SimpleImage>(IMAGES)

        val adapter = object : BaseQuickAdapter<SimpleImage, CustomBaseViewHolder>(
            R.layout.item_big_image,
            data
        ) {
            override fun convert(helper: CustomBaseViewHolder, item: SimpleImage) {
                helper.addOnClickListener(R.id.image)
                val headers=HashMap<String,String>()
                if (item.referer!=null){
                    headers["referer"] = item.referer!!
                }
                if (item.path!=null){
                    helper.itemView.findViewById<BigImageView>(R.id.image).showImage(Uri.fromFile(File(item.path!!)), headers)
                }else if(item.url!=null){
                    helper.itemView.findViewById<BigImageView>(R.id.image).showImage(Uri.parse(item.url), headers)
                }

            }
        }
        adapter.setOnItemChildClickListener { _, _, _ ->
            showStatusBar = !showStatusBar
            if (showStatusBar) {
                show()
            } else {
                hide(true)
            }
        }
        binding.viewPager.adapter=adapter
        binding.viewPager.setCurrentItem(currentPosition,false)
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
    companion object{
        const val POSITION="position"
        const val IMAGES="images"
    }
}