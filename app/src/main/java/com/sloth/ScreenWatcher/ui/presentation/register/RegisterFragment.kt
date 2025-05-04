package com.sloth.ScreenWatcher.ui.presentation.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.sloth.ScreenWatcher.auth.data.datasource.AuthRemoteDataSource
import com.sloth.ScreenWatcher.auth.data.repository.AuthRepositoryImpl
import com.sloth.ScreenWatcher.databinding.FragmentMainRegisterBinding

class RegisterFragment : Fragment() {

    companion object {
        fun newInstance() = RegisterFragment()
    }

    private var _binding: FragmentMainRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicialize o FirebaseAuth e FirebaseFirestore
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val realtimeDb = Firebase.database.reference // Adicione esta linha

        // Crie a instância do AuthRemoteDataSource manualmente
        val authRemoteDataSource = AuthRemoteDataSource(auth, db, realtimeDb)

        // Crie a instância do AuthRepository manualmente
        val authRepository = AuthRepositoryImpl(authRemoteDataSource)

        // Crie a instância da fábrica do ViewModel
        val factory = RegisterViewModelFactory(authRepository)

        // Instancie o ViewModel
        registerViewModel = ViewModelProvider(this, factory)[RegisterViewModel::class.java]

        // Observa o estado do registro
        registerViewModel.registerResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                if (it.isSuccess) {
                    Toast.makeText(requireContext(), "Registro bem-sucedido!", Toast.LENGTH_SHORT).show()
                    // Navegar para a tela de login ou a próxima tela
                } else {
                    Toast.makeText(requireContext(), "Erro: ${it.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Configura clique do botão de registro
        binding.registerButton.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            val username = binding.username.text.toString()
            registerViewModel.register(email, password, username)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Limpa o binding para evitar vazamentos de memória
    }
}