package com.dev.orangebrowser.bloc.setting.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.dev.base.BaseFragment
import com.dev.base.extension.hide
import com.dev.base.extension.show
import com.dev.base.support.BackHandler
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.host.MainViewModel
import com.dev.orangebrowser.databinding.FragmentAccountSettingBinding
import com.dev.orangebrowser.extension.*
import com.dev.view.extension.loadRemoteImage
import es.dmoral.toasty.Toasty

class AccountFragment : BaseFragment(), BackHandler {


    companion object {
        const val Tag = "AccountFragment"
        fun newInstance() = AccountFragment()
    }

    lateinit var activityViewModel: MainViewModel
    lateinit var binding: FragmentAccountSettingBinding
    override fun onBackPressed(): Boolean {
        RouterActivity?.loadSettingFragment(enterAnimationId = R.anim.slide_right_in,exitAnimationId = R.anim.holder)
        return true
    }


    override fun useDataBinding(): Boolean {
        return true
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //注入
        appComponent.inject(this)
    }

    override fun getLayoutResId(): Int {
        return R.layout.fragment_account_setting
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentAccountSettingBinding.bind(super.onCreateView(inflater, container, savedInstanceState))
        binding.lifecycleOwner=this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        activityViewModel = ViewModelProviders.of(activity!!, factory).get(MainViewModel::class.java)
        binding.activityViewModel = activityViewModel
        binding.backHandler = this
        binding.fragment=this
        super.onActivityCreated(savedInstanceState)
    }


    override fun initViewWithDataBinding(savedInstanceState: Bundle?) {
        val isLogin = getSpBool(R.string.pref_setting_is_login, false)
        if (isLogin){
            binding.registerOrLogin.hide()
            binding.userInfo.show()
        }else{
            binding.registerOrLogin.show()
            binding.userInfo.hide()
            initUserInfoView()
        }
    }

    fun loginOrRegister(){
        val email = binding.email.text.toString()
        val password = binding.password.toString()
        when {
            email.isBlank() -> Toasty.warning(requireContext(), R.string.tip_email_not_empty).show()
            password.isBlank() -> Toasty.warning(requireContext(), R.string.tip_password_not_empty).show()
            else -> {
                //TODO:注册或者登录
            }
        }
    }
    fun findPassword(){

    }
    private fun initUserInfoView() {
        val avatar = getSpString(R.string.pref_user_avatar, "")
        if (avatar.isNotBlank()) {
            binding.avatar.loadRemoteImage(avatar)
        }
        binding.syncBookmarkBtn.isChecked=getSpBool(R.string.pref_setting_sync_bookmark,false)
        binding.syncBookmarkBtn.setOnCheckedChangeListener { _, isChecked ->
            setSpBool(R.string.pref_setting_sync_bookmark,isChecked)
        }
        binding.userAccount.text=getSpString(R.string.pref_user_email)
        binding.quitLogin.setOnClickListener {
            setSpString(R.string.pref_user_email,"")
            setSpString(R.string.pref_user_avatar,"")
            fragmentManager?.popBackStack()
        }
    }

    override fun initData(savedInstanceState: Bundle?) {

    }

}
