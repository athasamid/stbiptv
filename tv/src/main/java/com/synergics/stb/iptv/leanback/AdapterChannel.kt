package com.synergics.stb.iptv.leanback

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.synergics.stb.iptv.leanback.etc.TextDrawable
import com.synergics.stb.iptv.leanback.models.TVItems

class AdapterChannel: RecyclerView.Adapter<AdapterChannel.ChannelViewHolder>() {
    var items: MutableList<TVItems> ? = null
        get() = field
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_card_channel, parent, false)
        return ChannelViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
        holder.tvNama.text = "${items?.get(position)?.id} ${items?.get(position)?.nama}"
        holder.tvGroup.text = "${items?.get(position)?.group}"

        if (items?.get(position)?.icon == null)
            holder.icon.setImageDrawable(TextDrawable("${items?.get(position)?.nama}"))
        else
            Glide.with(holder.icon.context).load(items?.get(position)?.icon).into(holder.icon)
    }

    override fun getItemCount(): Int = items?.size ?: 0

    class ChannelViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var tvNama = itemView.findViewById<TextView>(R.id.nama)
        var tvGroup = itemView.findViewById<TextView>(R.id.group)
        var icon = itemView.findViewById<ImageView>(R.id.icon)
    }
}