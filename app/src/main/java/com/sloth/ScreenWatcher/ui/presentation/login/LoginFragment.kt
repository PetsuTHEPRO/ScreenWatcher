package com.example.ScreenWatcher.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.sloth.ScreenWatcher.R
import com.sloth.ScreenWatcher.auth.data.datasource.AuthRemoteDataSource
import com.sloth.ScreenWatcher.auth.data.repository.AuthRepositoryImpl
import com.sloth.ScreenWatcher.databinding.FragmentMainLoginBinding
import com.sloth.ScreenWatcher.ui.presentation.AuthActivity
import com.sloth.ScreenWatcher.ui.presentation.login.LoginViewModelFactory
import com.sloth.ScreenWatcher.ui.presentation.register.RegisterFragment

class LoginFragment : Fragment() {

    private var _binding: FragmentMainLoginBinding? = null
    private val binding get() = _binding!! // View Binding seguro (não nulo entre onCreateView e onDestroyView)

    private lateinit var loginViewModel: LoginViewModel // ViewModel para gerenciar a lógica

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Infla o layout usando View Binding
        _binding = FragmentMainLoginBinding.inflate(inflater, container, false)
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
        val factory = LoginViewModelFactory(authRepository)

        // Instancie o ViewModel
        loginViewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]

        // Observa o estado do login
        loginViewModel.loginResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                if (it.isSuccess) {
                    Toast.makeText(requireContext(), "Login bem-sucedido!", Toast.LENGTH_SHORT).show()
                    // Navegar para próxima tela
                } else {
                    Toast.makeText(requireContext(), "Erro: ${it.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Configura clique do botão
        binding.loginButton.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            loginViewModel.login(email, password)
        }

        // Configura o clique no texto de registro
        binding.registerLink.setOnClickListener {
            // Substitui o LoginFragment pelo RegisterFragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.container, RegisterFragment())
                .addToBackStack(null) // Opcional: adiciona à pilha para voltar com o botão back
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Limpa o binding para evitar vazamentos de memória
    }
}