package com.hugo.gitapp.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.hugo.gitapp.R
import com.hugo.gitapp.data.entities.ResponsePull
import kotlinx.android.synthetic.main.item_pull.view.*
import kotlin.properties.Delegates

class PullAdapter: RecyclerView.Adapter<PullAdapter.ValueViewHolder>(){

    private var pulls: MutableList<ResponsePull> by Delegates
        .observable(mutableListOf()) { _, _, _ ->
            notifyDataSetChanged()
        }

    private var mItemListener: ItemListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ValueViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_pull, parent, false)
        return ValueViewHolder(
            view
        )
    }

    override fun getItemCount(): Int = pulls.size

    override fun onBindViewHolder(holder: ValueViewHolder, position: Int) {
        if (position != RecyclerView.NO_POSITION) {
            val value: ResponsePull = pulls[position]

            holder.itemView.setOnClickListener {
                if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                    mItemListener?.onItemClick(value)
                }
            }
            holder.bind(value)
        }
    }

    fun updateData(newPulls: MutableList<ResponsePull>) {
        pulls = newPulls
    }

    fun addData(newPulls: MutableList<ResponsePull>) {
        val ids = pulls.map { it.id }
        val result = newPulls.filter { it.id !in ids }

        result.forEach {
            pulls.add(it)
        }
        notifyDataSetChanged()
    }

    class ValueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bind(pull: ResponsePull) {
            itemView.title.text = pull.title
            itemView.description.text = pull.body
            Glide.with(itemView.context)
                .load(pull.user?.avatar_url)
                .placeholder(R.drawable.ic_person)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(itemView.avatar)
            itemView.username.text = pull.user?.login
            itemView.surname.text = pull.user?.login
            itemView.state.text = "#"+pull.state
            if (pull.state?.toUpperCase() == "OPEN") {
                itemView.state.setTextColor(
                    ContextCompat
                    .getColor(itemView.context, R.color.orange))
            } else
                itemView.state.setTextColor(
                    ContextCompat
                        .getColor(itemView.context, android.R.color.black))
        }
    }

    fun setItemListener(itemListener: ItemListener) {
        mItemListener = itemListener
    }

    interface ItemListener {
        fun onItemClick(pull: ResponsePull)
    }
}