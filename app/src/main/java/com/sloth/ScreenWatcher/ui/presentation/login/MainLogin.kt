package com.sloth.ScreenWatcher.ui.presentation.login

import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sloth.ScreenWatcher.R
import com.sloth.ScreenWatcher.ui.presentation.AuthActivity

class MainLogin : Fragment() {

    companion object {
        fun newInstance() = MainLogin()
    }

    private val viewModel: MainLoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_main_login, container, false)
    }

}