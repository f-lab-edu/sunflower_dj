package com.djyoo.sunflower.common.common.base

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

abstract class BaseFragment<T : ViewDataBinding>(@LayoutRes private val layoutResId: Int) : Fragment() {
    protected lateinit var mBinding: T
    protected lateinit var mActivity: Activity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, layoutResId, container, false)
        mActivity = requireActivity()

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.lifecycleOwner = viewLifecycleOwner

        init()
    }

    abstract fun init()
}