package com.github.hattamaulana.android.core.util

import android.view.LayoutInflater
import android.view.ViewGroup

typealias FragmentInflater<T> = (LayoutInflater, ViewGroup?, Boolean) -> T
typealias ActivityInflater<T> = (LayoutInflater) -> T