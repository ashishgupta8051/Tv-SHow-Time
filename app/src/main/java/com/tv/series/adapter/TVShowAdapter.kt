package com.tv.series.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tv.series.R
import com.tv.series.model.TVShow
import com.tv.series.utils.ClickListener

class TVShowAdapter(private val tvShowList : ArrayList<TVShow>,private val listener:ClickListener) : RecyclerView.Adapter<TVShowAdapter.TVShowViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TVShowViewHolder {
        val viewHolder = TVShowViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.tv_show_list,parent,false))
        viewHolder.itemView.setOnClickListener {
            listener.onClickedTvShow(tvShowList[viewHolder.adapterPosition])
        }
        return viewHolder
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TVShowViewHolder, position: Int) {
        val tvShow = tvShowList[position]
        Picasso.get().load(tvShow.image_thumbnail_path).into(holder.imageView)

        holder.textName.text = tvShow.name
        holder.textNetwork.text = tvShow.network
        holder.textStarted.text = "Started on : "+tvShow.start_date
        holder.textStatus.text = "Status : "+tvShow.status
    }

    override fun getItemCount(): Int {
        return tvShowList.size
    }

    inner class TVShowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var imageView:ImageView = itemView.findViewById(R.id.showImage)
        var textName:TextView = itemView.findViewById(R.id.textName)
        var textNetwork:TextView = itemView.findViewById(R.id.textNetwork)
        var textStarted:TextView = itemView.findViewById(R.id.textStarted)
        var textStatus:TextView = itemView.findViewById(R.id.textStatus)
    }
}