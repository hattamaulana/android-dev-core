package com.github.hattamaulana.android.core.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.github.hattamaulana.android.core.util.Inflate

abstract class BaseFragment<T : ViewBinding>(
    private val inflate: Inflate<T>
) : Fragment() {

    protected var binding: T? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = inflate.invoke(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.let { initView(it) }

        initData()
    }

    override fun onDestroy() {
        super.onDestroy()

        binding = null
    }

    abstract fun initView(binding: T)


    abstract fun initData()
}
