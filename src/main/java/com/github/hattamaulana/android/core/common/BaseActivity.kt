package com.github.hattamaulana.android.core.common

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.github.hattamaulana.android.core.util.ActivityInflater

abstract class BaseActivity<T: ViewBinding>(
    private val fragmentInflater: ActivityInflater<T>
): AppCompatActivity() {
    protected var binding: T? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = fragmentInflater.invoke(layoutInflater)

        setContentView(binding?.root)

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