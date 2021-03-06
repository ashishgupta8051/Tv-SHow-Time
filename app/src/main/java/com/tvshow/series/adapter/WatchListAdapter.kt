package com.tvshow.series.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tvshow.series.R
import com.tvshow.series.model.TVShow
import com.tvshow.series.utils.WatchListListener

class WatchListAdapter(private val watchListListener: WatchListListener)
    : RecyclerView.Adapter<WatchListAdapter.WatchListHolder>() {

    private val watchList:ArrayList<TVShow> = arrayListOf()

    inner class WatchListHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var imageView: ImageView = itemView.findViewById(R.id.showImage)
        var deleteWatchList: ImageView = itemView.findViewById(R.id.deleteWatchList)
        var textName: TextView = itemView.findViewById(R.id.textName)
        var textNetwork: TextView = itemView.findViewById(R.id.textNetwork)
        var textStarted: TextView = itemView.findViewById(R.id.textStarted)
        var textStatus: TextView = itemView.findViewById(R.id.textStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WatchListHolder {
        val viewHolder = WatchListHolder(LayoutInflater.from(parent.context).inflate(R.layout.tv_show_list,parent,false))
        viewHolder.itemView.setOnClickListener {
            watchListListener.onTvShowClicked(watchList[viewHolder.adapterPosition])
        }

        viewHolder.deleteWatchList.setOnClickListener {
            watchListListener.removeTvShow(watchList[viewHolder.adapterPosition])
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: WatchListHolder, position: Int) {
        val tvShow = watchList[position]

        holder.deleteWatchList.visibility = View.VISIBLE
        Picasso.get().load(tvShow.image_thumbnail_path).into(holder.imageView)

        holder.textName.text = tvShow.name
        holder.textNetwork.text = tvShow.network
        holder.textStarted.text = "Started on : "+tvShow.start_date
        holder.textStatus.text = "Status : "+tvShow.status
    }

    @SuppressLint("NotifyDataSetChanged")
    fun getWatchList(list : List<TVShow>){
        watchList.clear()
        watchList.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return watchList.size
    }
}