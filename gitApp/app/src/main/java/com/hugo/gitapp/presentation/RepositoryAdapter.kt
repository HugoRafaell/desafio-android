package com.hugo.gitapp.presentation

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.hugo.gitapp.R
import com.hugo.gitapp.data.entities.ResponseRepository
import kotlinx.android.synthetic.main.item_repository.view.*
import kotlin.properties.Delegates

class RepositoryAdapter: RecyclerView.Adapter<RepositoryAdapter.ValueViewHolder>(){

    private var itens: MutableList<ResponseRepository.Itens> by Delegates
        .observable(mutableListOf()) { _, _, _ ->
            notifyDataSetChanged()
        }

    private var mItemListener: ItemListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ValueViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_repository, parent, false)
        val holder = ValueViewHolder(view)

        holder.itemView.setOnClickListener {
            if (holder.adapterPosition != RecyclerView.NO_POSITION) {

            }
        }
        return holder
    }

    override fun getItemCount(): Int = itens.size

    override fun onBindViewHolder(holder: ValueViewHolder, position: Int) {
        if (position != RecyclerView.NO_POSITION) {
            val value: ResponseRepository.Itens = itens[position]
            holder.bind(value)
        }
    }

    fun updateData(newItens: MutableList<ResponseRepository.Itens>) {
        itens = newItens
    }

    fun addData(newItens: MutableList<ResponseRepository.Itens>) {
        val ids = itens.map { it.id }
        val result = newItens.filter { it.id !in ids }

        result.forEach {
            itens.add(it)
        }
        notifyDataSetChanged()
    }

    class ValueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bind(itens: ResponseRepository.Itens) {
            itemView.title.text = itens.name
            itemView.description.text = itens.description
            Glide.with(itemView.context)
                .load(itens.owner?.avatar_url)
                .placeholder(R.drawable.ic_person)
                .into(itemView.avatar)
            itemView.username.text = itens.owner?.login
            itemView.surname.text = itens.owner?.login
            itemView.countFork.text = itens.forks_count.toString()
            itemView.countStar.text = itens.stargazers_count.toString()
        }
    }

    fun setItemListener(itemListener: ItemListener) {
        mItemListener = itemListener
    }

    interface ItemListener {
        fun onItemClick(item: ResponseRepository.Itens)
    }
}