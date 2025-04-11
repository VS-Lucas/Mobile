package com.mobile.barbershop.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobile.barbershop.databinding.ServicosItemBinding
import com.mobile.barbershop.models.Servicos
import com.mobile.barbershop.R

class ServicosAdapter(
    private var servicos: List<Servicos>,
    private val onItemClick: (Servicos) -> Unit):
    RecyclerView.Adapter<ServicosAdapter.ServicosViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServicosViewHolder {
        val binding = ServicosItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ServicosViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ServicosViewHolder, position: Int) {
        holder.bind(servicos[position])
    }

    override fun getItemCount() = servicos.size

    fun updateAppointments(newAppointments: List<Servicos>) {
        servicos = newAppointments
        notifyDataSetChanged()
    }

    inner class ServicosViewHolder(val binding: ServicosItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(appointment: Servicos) {
            binding.apply {
                tvAppointmentDate.text = appointment.date
                tvAppointmentTime.text = appointment.time
                tvService.text = appointment.service

                root.setOnClickListener { onItemClick(appointment) }
            }
        }
    }

}