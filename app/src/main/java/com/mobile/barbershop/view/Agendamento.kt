package com.mobile.barbershop.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.mobile.barbershop.databinding.ActivityAgendamentoBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.mobile.barbershop.view.Registrar

class Agendamento : AppCompatActivity() {

    private lateinit var binding: ActivityAgendamentoBinding
    val db = Firebase.firestore

    private var selectedDate: String = ""
    private var selectedTime: String = ""
    private var selectedCalendarDate: Calendar = Calendar.getInstance()
    private var selectedService: String = ""

    private val services = listOf(
        "Corte de cabelo social",
        "Corte de cabelo degradê",
        "Barba completa",
        "Limpeza"
    )

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgendamentoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupServiceSpinner()
        setupTimeSpinner()
        setupCalendarView()
        setupSaveButton()
    }

    private fun setupServiceSpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, services)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerService.adapter = adapter


        binding.spinnerService.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                selectedService = services[position]
                updateSelectedDateTimeText()
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {

            }
        }
    }

    private fun setupTimeSpinner() {
        val timeSlots = ArrayList<String>()
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 8)
        calendar.set(Calendar.MINUTE, 0)

        val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())

        while (calendar.get(Calendar.HOUR_OF_DAY) < 18 ||
            (calendar.get(Calendar.HOUR_OF_DAY) == 18 && calendar.get(Calendar.MINUTE) == 0)) {
            timeSlots.add(formatter.format(calendar.time))
            calendar.add(Calendar.MINUTE, 30)
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, timeSlots)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTime.adapter = adapter

        binding.spinnerTime.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                selectedTime = timeSlots[position]
                updateSelectedDateTimeText()
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {
            }
        }
    }

    private fun setupCalendarView() {

        binding.calendarView.minDate = Calendar.getInstance().timeInMillis


        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedCalendarDate = Calendar.getInstance()
            selectedCalendarDate.set(year, month, dayOfMonth)

            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            selectedDate = dateFormat.format(selectedCalendarDate.time)
            updateSelectedDateTimeText()
        }


        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        selectedDate = dateFormat.format(Calendar.getInstance().time)
    }

    private fun updateSelectedDateTimeText() {
        binding.tvSelectedDateTime.text = "Data e hora selecionadas: $selectedDate às $selectedTime"
    }

    private fun setupSaveButton() {
        binding.btnSaveAppointment.setOnClickListener {
            if (selectedDate.isNotEmpty() && selectedTime.isNotEmpty()) {
                saveAppointmentToFirebase()
            } else {
                mensagem(it, "Selecione data e hora para agendar", "#FF0000")
            }
        }
    }

    private fun saveAppointmentToFirebase() {
        // Show loading state
        binding.btnSaveAppointment.isEnabled = false
        binding.btnSaveAppointment.text = "Salvando..."

        // Create appointment data
        val appointment = hashMapOf(
            "service" to selectedService,
            "date" to selectedDate,
            "time" to selectedTime,
            "timestamp" to Calendar.getInstance().timeInMillis,
            "userId" to getCurrentUserId()
        )

        // Save to Firebase
        db.collection("appointments")
            .add(appointment)
            .addOnSuccessListener { documentReference ->
                mensagem(binding.root, "Agendamento realizado com sucesso", "#FF03DAC")
                binding.btnSaveAppointment.isEnabled = true
                binding.btnSaveAppointment.text = "Agendar"
                var intent = Intent(this, Home::class.java)
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                mensagem(binding.root, "Ocorreu um erro ao agendar", "#FF0000")
                binding.btnSaveAppointment.isEnabled = true
                binding.btnSaveAppointment.text = "Agendar"
            }
    }

    private fun getCurrentUserId(): String {
        // AJUSTAR PARA PEGAR O USER ID

        return "user123"
    }

    private fun mensagem(view: View, mensagem: String, cor: String) {
        "#FF0000"
        "#FF03DAC"
        val snackbar = Snackbar.make(view, mensagem, Snackbar.LENGTH_SHORT)
        snackbar.setBackgroundTint(android.graphics.Color.parseColor(cor))
        snackbar.setTextColor(android.graphics.Color.parseColor("#FFFFFF"))
        snackbar.show()
    }
}