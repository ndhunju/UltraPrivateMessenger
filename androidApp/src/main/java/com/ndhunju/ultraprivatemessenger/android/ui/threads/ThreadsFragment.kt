package com.ndhunju.ultraprivatemessenger.android.ui.threads

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.ndhunju.ultraprivatemessenger.android.R
import com.ndhunju.ultraprivatemessenger.android.ui.theme.MyApplicationTheme
import com.ndhunju.ultraprivatemessenger.data.MessageRepositoryImpl
import com.ndhunju.ultraprivatemessenger.service.AppStateBroadcastServiceImpl
import com.ndhunju.ultraprivatemessenger.ui.threads.ThreadsViewModel

class ThreadsFragment: Fragment() {

    private val viewModel: ThreadsViewModel by lazy { ThreadsViewModel(
        lifecycleScope,
        AppStateBroadcastServiceImpl(),
        MessageRepositoryImpl()
    ) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel.setTitle(getString(R.string.app_name))

        return ComposeView(requireContext()).apply {
            setContent {
                MyApplicationTheme {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        ThreadListContentWithNavDrawer(viewModel)
                    }
                }
            }
        }
    }

    companion object {

        private val TAG: String = ThreadsFragment::class.java.simpleName

        fun addToContent(fm: FragmentManager) {
            fm.beginTransaction()
                .add(android.R.id.content, ThreadsFragment(), TAG)
                .commit()
        }
    }
}