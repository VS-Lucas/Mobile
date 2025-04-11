package com.mobile.barbershop.view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.mobile.barbershop.databinding.ActivityRegistrarBinding

class Registrar : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrarBinding
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegistrarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding?.registerButton?.setOnClickListener {
            val email: String = binding?.emailEditText?.text.toString()
            val password: String = binding?.passwordEditText?.text.toString()
            val confirmPassword: String = binding?.confirmPasswordEditText?.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (password == confirmPassword) {
                    createUser(email, password)
                } else {
                    showSnackbar(it, "As senha não são compatíveis")
                }
            } else {
                showSnackbar(it, "Por favor, preencha os campos!")
            }

        }

    }

    private fun showSnackbar(view: View, message: String) {
        val snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        snackbar.setBackgroundTint(Color.parseColor("#FF0000"))
        snackbar.setTextColor(Color.WHITE)
        snackbar.show()
    }

    private fun createUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("DEBUG", "Usuário criado com sucesso")
                val intent = Intent(this@Registrar, Home::class.java)
                startActivity(intent)
            } else {
                Log.d("DEBUG", "Erro ao criar usuário", task.exception)
            }
        }
    }

}