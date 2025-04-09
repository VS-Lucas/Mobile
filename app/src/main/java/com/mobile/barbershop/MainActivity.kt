package com.mobile.barbershop

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
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
import com.mobile.barbershop.databinding.ActivityMainBinding
import com.mobile.barbershop.ui.theme.BarberShopTheme
import com.mobile.barbershop.view.Home

class MainActivity : ComponentActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btLogin.setOnClickListener {
            val name = binding.editNome.text.toString()
            val password = binding.editSenha.text.toString()

            when {
                name.isEmpty() -> {
                    showSnackbar(it, "O campo nome é obrigatório.")
                } password.isEmpty() -> {
                    showSnackbar(it, "O campo senha é obrigatório.")
                } else -> {
                    // Caso estivesse tudo válido e ele iria para a tela de home
                    // Aqui vai fazer a lógica do login imagino
                    goToHome(name)
                }
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