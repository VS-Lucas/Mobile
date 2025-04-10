package com.mobile.barbershop

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.mobile.barbershop.databinding.ActivityMainBinding
import com.mobile.barbershop.ui.theme.BarberShopTheme
import com.mobile.barbershop.view.Home
import com.mobile.barbershop.view.Registrar

class MainActivity : ComponentActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding?.btLogin?.setOnClickListener {
            val email = binding.editNome.text.toString()
            val password = binding.editSenha.text.toString()

            when {
                email.isEmpty() -> {
                    Log.d("DEBUG", "Login:: Campo do nome é obrigatório")
                    showSnackbar(it, "O campo nome é obrigatório.")
                } password.isEmpty() -> {
                    showSnackbar(it, "O campo senha é obrigatório.")
                } else -> {
                    signIn(email, password)
                }
            }
        }

        binding?.createAccount?.setOnClickListener {
            val intent = Intent(this@MainActivity, Registrar::class.java)
            startActivity(intent)
        }
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("DEBUG", "Login feito com sucesso")
                val user = auth.currentUser
                goToHome("Nome do cara")
            } else {
                Log.d("DEBUG", "Erro ao fazer login", task.exception)
                showSnackbar(binding.btLogin, "Email ou senha incorreto!")
            }
        }
    }

    private fun showSnackbar(view: View, message: String) {
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        snackbar.setBackgroundTint(Color.parseColor("#FF0000"))
        snackbar.setTextColor(Color.WHITE)
        snackbar.show()
    }

    private fun goToHome(name: String) {
        val intent = Intent(this, Home::class.java)
        intent.putExtra("name", name)
        startActivity(intent)
    }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BarberShopTheme {
        Greeting("Android")
    }
}