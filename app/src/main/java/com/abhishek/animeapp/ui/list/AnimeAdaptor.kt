package com.abhishek.animeapp.ui.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.abhishek.animeapp.R
import com.abhishek.animeapp.data.local.AnimeEntity
import com.bumptech.glide.Glide
import java.util.Locale

class AnimeAdapter(
    private val onClick: (AnimeEntity) -> Unit
) : RecyclerView.Adapter<AnimeAdapter.AnimeViewHolder>(), Filterable {

    private val fullList = mutableListOf<AnimeEntity>()
    private val filteredList = mutableListOf<AnimeEntity>()

    private var isSearching = false

    fun updateList(newList: List<AnimeEntity>) {
        fullList.clear()
        fullList.addAll(newList)

        if (!isSearching) {
            filteredList.clear()
            filteredList.addAll(newList)
            notifyDataSetChanged()
        }
    }

    fun isSearchActive(): Boolean = isSearching

    override fun getFilter(): Filter {
        return object : Filter() {

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint?.toString()?.trim()?.lowercase(Locale.getDefault())

                val resultList = if (query.isNullOrEmpty()) {
                    isSearching = false
                    fullList
                } else {
                    isSearching = true
                    fullList.filter {
                        it.title.lowercase(Locale.getDefault()).contains(query)
                    }
                }

                return FilterResults().apply {
                    values = resultList
                }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList.clear()
                filteredList.addAll(results?.values as List<AnimeEntity>)
                notifyDataSetChanged()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_anime, parent, false)
        return AnimeViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnimeViewHolder, position: Int) {
        val anime = filteredList[position]
        holder.bind(anime)
        holder.itemView.setOnClickListener { onClick(anime) }
    }

    override fun getItemCount(): Int = filteredList.size

    class AnimeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val poster: ImageView = itemView.findViewById(R.id.imgPoster)
        private val title: TextView = itemView.findViewById(R.id.txtTitle)
        private val episodes: TextView = itemView.findViewById(R.id.txtEpisodes)
        private val rating: TextView = itemView.findViewById(R.id.txtRating)

        fun bind(anime: AnimeEntity) {
            title.text = anime.title
            episodes.text = "Episodes: ${anime.episodes ?: "N/A"}"
            rating.text = "Rating: ${anime.rating ?: "N/A"}"

            if (anime.imageUrl != null) {
                poster.visibility = View.VISIBLE
                Glide.with(itemView)
                    .load(anime.imageUrl)
                    .into(poster)
            } else {
                poster.visibility = View.GONE
            }
        }
    }
}
