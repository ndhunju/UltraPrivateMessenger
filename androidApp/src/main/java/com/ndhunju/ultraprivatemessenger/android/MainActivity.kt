package com.ndhunju.ultraprivatemessenger.android

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.ndhunju.ultraprivatemessenger.android.ui.threads.ThreadsFragment

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThreadsFragment.addToContent(supportFragmentManager)
    }
}

