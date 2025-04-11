package com.mobile.barbershop.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.mobile.barbershop.adapter.ServicosAdapter
import com.mobile.barbershop.databinding.ActivityHomeBinding
import com.mobile.barbershop.models.Servicos
import android.os.Build
import android.view.WindowManager

class Home : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var servicosAdapter: ServicosAdapter
    private val listaServicos: MutableList<Servicos> = mutableListOf()
    val db = Firebase.firestore
    private lateinit var userId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getStringExtra("userId") ?: ""

        setupUI()
        setupRecyclerView()
        loadAppointments()
    }

    private fun setupUI() {
        binding.tvWelcome.text = "Bem-vindo"

        binding.btnSchedule.setOnClickListener {
            val intent = Intent(this, Agendamento::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        servicosAdapter = ServicosAdapter(listaServicos) { appointment ->
            mensagem(binding.root, "Agendamento: ${appointment.service} em ${appointment.date}", "#FF03DAC")
        }

        binding.rvAppointments.apply {
            layoutManager = LinearLayoutManager(this@Home)
            adapter = servicosAdapter
        }
    }

    private fun loadAppointments() {
        showLoading(true)
        Log.d("DEBUG_USERID", "UserID: $userId")
        db.collection("appointments")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                listaServicos.clear()

                if (documents.isEmpty) {
                    showEmptyState(true)
                } else {
                    for (document in documents) {
                        Log.d("DEBUG_RAW", document.data.toString())

                        val servico = document.toObject(Servicos::class.java).copy(id = document.id)
                        Log.d("DEBUG", servico.toString())
                        listaServicos.add(servico)
                    }
                    showEmptyState(false)
                }

                servicosAdapter.updateAppointments(listaServicos)
                showLoading(false)
            }
            .addOnFailureListener { e ->
                Log.d("DEBUG_BUSCAR", "DEU ERRO P BUSCAR")
                showLoading(false)
                showEmptyState(true)
            }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.rvAppointments.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showEmptyState(isEmpty: Boolean) {
        binding.tvEmptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.rvAppointments.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        loadAppointments()
    }

    private fun mensagem(view: View, mensagem: String, cor: String) {
        val snackbar = Snackbar.make(view, mensagem, Snackbar.LENGTH_SHORT)
        snackbar.setBackgroundTint(android.graphics.Color.parseColor(cor))
        snackbar.setTextColor(android.graphics.Color.parseColor("#FFFFFF"))
        snackbar.show()
    }

}