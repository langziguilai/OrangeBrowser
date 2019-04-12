package com.dev.orangebrowser.bloc.browser.integration


import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils
import com.dev.base.support.LifecycleAwareFeature
import com.dev.browser.engine.SystemEngineSession
import com.dev.browser.session.Session
import com.dev.browser.session.SessionManager
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.browser.BrowserFragment
import com.dev.orangebrowser.databinding.FragmentBrowserBinding
import com.dev.util.CommonUtil
import com.dev.view.StatusBarUtil

//根据webview的头部来更新显示的头部
class StyleIntegration(var binding: FragmentBrowserBinding,var fragment:BrowserFragment,var sessionManager:SessionManager, var session: Session):
    LifecycleAwareFeature {
    private var sessionObserver: Session.Observer
    init{
        session.thumbnail?.apply {
            if (session.themeColorMap.containsKey(session.url)){
                updateStyle(session.themeColorMap[session.url]!!)
            }else{
                val color=this.getPixel(5,5)
                updateStyle(color)
            }
        }
        sessionObserver=object:Session.Observer{
            override fun onThumbnailChanged(session: Session, bitmap: Bitmap?) {
                bitmap?.apply {
                    val engineSession= sessionManager.getOrCreateEngineSession(session) as SystemEngineSession
                    val dy=engineSession.webView.scrollY
                    //在尚未滑动的时候，可以通过截图来获取颜色，否则，不改变style
                    if (dy==0){
                        if (session.themeColorMap.containsKey(session.url)){
                            updateStyle(session.themeColorMap[session.url]!!)
                        }else{
                            bitmap?.apply {
                                val color=bitmap.getPixel(5,5)
                                updateStyle(color)
                                session.themeColorMap[session.url]=color
                            }
                        }
                    }
                }
            }

        }
    }

    private fun updateStyle(@ColorInt color:Int){
        val hsl= FloatArray(3)
        //获取明度和饱和度
        ColorUtils.colorToHSL(color,hsl)
        val bgDrawable=ColorDrawable(color)
        //1:set backgroundColor
        binding.topBar.background=bgDrawable
        binding.topMenuPanel.background=bgDrawable
        StatusBarUtil.setStatusBarColor(fragment.requireActivity(),color)
        //2:set text Color
        val saturation=hsl[1]  //饱和度：范围 [0...1]
        val lightness=hsl[2]   //明度:范围  [0...1]
        setTextColor(saturation,lightness)
    }
    private fun setTextColor(saturation:Float,lightness:Float){
        if (saturation<0.1f && lightness>0.9){
            setTextDarkMode()
        }else{
            setTextLightMode()
        }
    }

    private fun setTextLightMode(){
        StatusBarUtil.setDarkMode(fragment.requireActivity())
        val whiteColor=fragment.resources.getColor(R.color.colorWhite)
        binding.searchText.setTextColor(whiteColor)
        binding.searchText.setHintTextColor(whiteColor)
        binding.topMenu.setTextColor(whiteColor)
    }
    private fun setTextDarkMode(){
        StatusBarUtil.setLightMode(fragment.requireActivity())
        val blackColor=fragment.resources.getColor(R.color.colorBlack)
        binding.searchText.setTextColor(blackColor)
        binding.searchText.setHintTextColor(blackColor)
        binding.topMenu.setTextColor(blackColor)
    }
    private fun loadBitmapFromView(v: View): Bitmap {
        val fullSizeBitmap = Bitmap.createBitmap(v.width, v.height, Bitmap.Config.ARGB_8888)
        val c = Canvas(fullSizeBitmap)
        v.layout(v.left, v.top, v.right, v.bottom)
        v.draw(c)

        val density=v.context.resources.displayMetrics.density
        val sampleBitmap = CommonUtil.getResizedBitmap(fullSizeBitmap,v.height/density,v.width/density)
        fullSizeBitmap?.recycle()
        return sampleBitmap
    }
    override fun start() {
        session.register(sessionObserver)
    }

    override fun stop() {
        session.unregister(sessionObserver)
    }
}
