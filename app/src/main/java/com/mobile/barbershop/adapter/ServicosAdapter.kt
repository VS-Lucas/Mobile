package com.mobile.barbershop.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobile.barbershop.databinding.ServicosItemBinding
import com.mobile.barbershop.models.Servicos

class ServicosAdapter(private val context: Context, private val listaServices: MutableList<Servicos>):
    RecyclerView.Adapter<ServicosAdapter.ServicosViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ServicosViewHolder {
        val item = ServicosItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return ServicosViewHolder(item)
    }

    override fun onBindViewHolder(
        holder: ServicosViewHolder,
        position: Int
    ) {
        holder.imgServico.setImageResource(listaServices[position].img!!)
        holder.txtServico.text = listaServices[position].nome
    }

    override fun getItemCount() = listaServices.size

    inner class ServicosViewHolder(binding: ServicosItemBinding): RecyclerView.ViewHolder(binding.root) {
        val imgServico = binding.imgServicos
        val txtServico = binding.txtServico
    }

}