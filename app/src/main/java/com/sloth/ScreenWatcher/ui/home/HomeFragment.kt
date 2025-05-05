package com.example.ScreenWatcher.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.sloth.ScreenWatcher.R
import com.sloth.ScreenWatcher.databinding.FragmentHomeBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        homeViewModel.statusTela.observe(viewLifecycleOwner) {
            updateScreenStatus(it)
        }

        //homeViewModel.checkScreenStatuses("usuario_atual", "yuri")

        return root
    }

    private fun updateScreenStatus(isScreenOn: Boolean) {
        if (!isAdded || _binding == null) return

        if (isScreenOn) {
            binding.statusIcon.setImageResource(R.drawable.ic_screen_on)
            binding.statusText.text = "Tela Ligada"
            binding.statusText.setTextColor(Color.GREEN)
            binding.statusDetails.text = "Ativa agora"
        } else {
            binding.statusIcon.setImageResource(R.drawable.ic_screen_off)
            binding.statusText.text = "Tela Desligada"
            binding.statusText.setTextColor(Color.RED)

            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            binding.statusDetails.text = "Inativa desde ${timeFormat.format(Date())}"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}