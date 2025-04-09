package com.mobile.barbershop.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.mobile.barbershop.adapter.ServicosAdapter
import com.mobile.barbershop.databinding.ActivityHomeBinding
import com.mobile.barbershop.models.Servicos
import com.mobile.barbershop.R

class Home : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var servicosAdapter: ServicosAdapter
    private val listaServicos: MutableList<Servicos> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val name = intent.extras?.getString("name")
        binding.username.text = "Bem-vindo(a), $name"

        val recyclerViewServicos = binding.recyclerViewServicos
        recyclerViewServicos.layoutManager = GridLayoutManager(this, 2)
        servicosAdapter = ServicosAdapter(this, listaServicos)
        recyclerViewServicos.setHasFixedSize(true)
        recyclerViewServicos.adapter = servicosAdapter

        getServicos()

        binding.btnAgendar.setOnClickListener {
            val intent = Intent(this, Agendamento::class.java)
            intent.putExtra("name", name)
            startActivity(intent)
        }
    }

    private fun getServicos() {
        val corteCabelo = Servicos(R.drawable.img1, "Corte de cabelo")
        listaServicos.add(corteCabelo)
        val corteBarba = Servicos(R.drawable.img2, "Corte de barba")
        listaServicos.add(corteBarba)
        val lavagemCabelo = Servicos(R.drawable.img3, "Lavagem cabelo")
        listaServicos.add(lavagemCabelo)
        val tratamentoCabelo = Servicos(R.drawable.img4, "Tratamento de cabelo")
        listaServicos.add(tratamentoCabelo)
    }
}