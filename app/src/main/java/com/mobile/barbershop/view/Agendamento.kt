package com.mobile.barbershop.view

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import com.mobile.barbershop.databinding.ActivityAgendamentoBinding
import java.util.Calendar

class Agendamento : AppCompatActivity() {

    private lateinit var binding: ActivityAgendamentoBinding
    private val calendar: Calendar = Calendar.getInstance()
    private var data: String = ""
    private var hora: String = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgendamentoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
//        setContentView(R.layout.activity_agendamento)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        supportActionBar?.hide()
        val nome = intent.extras?.getString("nome").toString()

        val datePicker = binding.datePicker
        datePicker.setOnDateChangedListener {_, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            var dia = dayOfMonth.toString()
            val mes: String

            if (dayOfMonth < 10) {
                dia = "0$dayOfMonth"
            }

            if (monthOfYear < 10) {
                mes = "" + (monthOfYear + 1)
            } else {
                mes = (monthOfYear + 1).toString()
            }

            data = "$dia / $monthOfYear / $year"
        }
    
        binding.timePicker.setOnTimeChangedListener { _, hourOfDay, minute ->
            val minuto: String

            if (minute < 10) {
                minuto = "0$minute"
            } else {
                minuto = minute.toString()
            }

            hora = "$hourOfDay:$minuto"
        }

        binding.timePicker.setIs24HourView(true)

        binding.btAgendar.setOnClickListener {
            val barbeiro1 = binding.barbeiro1
            val barbeiro2 = binding.barbeiro2
            val barbeiro3 = binding.barbeiro3

            when {
                hora.isEmpty() -> {
                    mensagem(it, "Preencha o horário!", "#FF0000")
                }
                hora < "8:00" && hora > "19:00" -> {
                    mensagem(it, "Barber Shop está fechado -  hoário de atendimento das 08:00 às 19:00!", "#FF0000")
                }
                data.isEmpty() -> {
                    mensagem(it, "Coloque uma data!", "#FF0000")
                }
                barbeiro1.isChecked && data.isNotEmpty() && hora.isNotEmpty() -> {
                    mensagem(it, "Agendamento realizado com sucesso!", "#FF03DAC")
                }
                barbeiro2.isChecked && data.isNotEmpty() && hora.isNotEmpty() -> {
                    mensagem(it, "Agendamento realizado com sucesso!", "#FF03DAC")
                }
                barbeiro3.isChecked && data.isNotEmpty() && hora.isNotEmpty() -> {
                    mensagem(it, "Agendamento realizado com sucesso!", "#FF03DAC")
                }
                else -> {
                    mensagem(it, "Escolha um barbeiro!", "#FF0000")
                }
            }
        }
    }

    private fun mensagem(view: View, mensagem: String, cor: String) {
        val snackbar = Snackbar.make(view, mensagem, Snackbar.LENGTH_SHORT)
        snackbar.setBackgroundTint(android.graphics.Color.parseColor(cor))
        snackbar.setTextColor(android.graphics.Color.parseColor("#FFFFFF"))
        snackbar.show()
    }
}