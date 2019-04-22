package com.dev.orangebrowser.bloc.tabs.integration

import com.dev.base.extension.DEFAULT_INTERPOLATOR
import com.dev.base.extension.FAST_ANIMATION
import com.dev.base.extension.NORMAL_ANIMATION
import com.dev.base.extension.onGlobalLayoutComplete
import com.dev.orangebrowser.databinding.FragmentTabBinding

class TitleIntegration(var binding: FragmentTabBinding) {
    init {

    }

    fun hide() {
        binding.title.animate().setDuration(NORMAL_ANIMATION).setInterpolator(DEFAULT_INTERPOLATOR).alpha(0f).start()
    }

    fun show() {
        binding.title.onGlobalLayoutComplete {
            it.animate().setDuration(NORMAL_ANIMATION).setInterpolator(DEFAULT_INTERPOLATOR).alpha(1f).start()
        }
    }
}