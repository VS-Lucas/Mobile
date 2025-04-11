package com.mobile.barbershop.view

import AppointmentNotificationWorker
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.*
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.work.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.mobile.barbershop.databinding.ActivityAgendamentoBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class Agendamento : AppCompatActivity() {

    private lateinit var binding: ActivityAgendamentoBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var userId: String

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        binding = ActivityAgendamentoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()
        userId = intent.getStringExtra("userId") ?: ""

        requestNotificationPermission()

        binding.btnBack.setOnClickListener { onBackPressed() }

        setupServiceSpinner()
        setupTimeSpinner()
        setupCalendarView()
        setupSaveButton()
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }
    }

    private fun setupServiceSpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, services)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerService.adapter = adapter

        binding.spinnerService.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedService = services[position]
                updateSelectedDateTimeText()
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }
    }

    private fun setupTimeSpinner() {
        val timeSlots = ArrayList<String>()
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 8)
        calendar.set(Calendar.MINUTE, 0)

        val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())

        while (calendar.get(Calendar.HOUR_OF_DAY) < 18 || (calendar.get(Calendar.HOUR_OF_DAY) == 18 && calendar.get(Calendar.MINUTE) == 0)) {
            timeSlots.add(formatter.format(calendar.time))
            calendar.add(Calendar.MINUTE, 30)
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, timeSlots)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTime.adapter = adapter

        binding.spinnerTime.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedTime = timeSlots[position]
                updateSelectedDateTimeText()
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
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
        binding.btnSaveAppointment.isEnabled = false
        binding.btnSaveAppointment.text = "Salvando..."

        db.collection("appointments")
            .whereEqualTo("date", selectedDate)
            .whereEqualTo("time", selectedTime)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    saveNewAppointment()
                } else {
                    mensagem(binding.root, "Horário já agendado. Escolha outro horário.", "#FF0000")
                    binding.btnSaveAppointment.isEnabled = true
                    binding.btnSaveAppointment.text = "Agendar"
                }
            }
            .addOnFailureListener {
                mensagem(binding.root, "Erro ao verificar disponibilidade", "#FF0000")
                binding.btnSaveAppointment.isEnabled = true
                binding.btnSaveAppointment.text = "Agendar"
            }
    }

    private fun saveNewAppointment() {
        val appointment = hashMapOf(
            "service" to selectedService,
            "date" to selectedDate,
            "time" to selectedTime,
            "timestamp" to Calendar.getInstance().timeInMillis,
            "userId" to getCurrentUserId()
        )

        db.collection("appointments")
            .add(appointment)
            .addOnSuccessListener {
                mensagem(binding.root, "Agendamento realizado com sucesso", "#FF03DAC5")

                scheduleAppointmentNotification()

                Handler(Looper.getMainLooper()).postDelayed({
                    val intent = Intent(this, Home::class.java)
                    intent.putExtra("userId", getCurrentUserId())
                    startActivity(intent)
                }, 1500)

                binding.btnSaveAppointment.isEnabled = true
                binding.btnSaveAppointment.text = "Agendar"
            }
            .addOnFailureListener {
                mensagem(binding.root, "Erro ao agendar", "#FF0000")
                binding.btnSaveAppointment.isEnabled = true
                binding.btnSaveAppointment.text = "Agendar"
            }
    }

    private fun scheduleAppointmentNotification() {
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val appointmentDateTime = formatter.parse("$selectedDate $selectedTime")

        appointmentDateTime?.let {
            val currentTime = System.currentTimeMillis()
            val notificationTime = it.time - TimeUnit.MINUTES.toMillis(10) // 10 minutos antes
            val delay = notificationTime - currentTime

            if (delay > 0) {
                val workRequest = OneTimeWorkRequestBuilder<AppointmentNotificationWorker>()
                    .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                    .build()

                WorkManager.getInstance(applicationContext).enqueue(workRequest)
            }
        }
    }

    private fun getCurrentUserId(): String {
        return userId
    }

    private fun mensagem(view: View, mensagem: String, cor: String) {
        val snackbar = Snackbar.make(view, mensagem, Snackbar.LENGTH_SHORT)
        snackbar.setBackgroundTint(Color.parseColor(cor))
        snackbar.setTextColor(Color.WHITE)
        snackbar.show()
    }
}
