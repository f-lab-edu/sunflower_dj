package com.djyoo.sunflower.screen.planet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.djyoo.sunflower.common.base.BaseAdapter
import com.djyoo.sunflower.databinding.ItemPlanetListBinding
import com.djyoo.sunflower.screen.planet.data.model.Plant

class PlantListAdapter(
    private val onItemClick: (Plant) -> Unit,
) : BaseAdapter<Plant, PlantListAdapter.PlantViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPlanetListBinding.inflate(inflater, parent, false)
        return PlantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick)
    }

    class PlantViewHolder(
        private val binding: ItemPlanetListBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(plant: Plant, onItemClick: (Plant) -> Unit) {
            binding.plantName.text = plant.name
            Glide.with(binding.plantImage.context)
                .load(plant.imageUrl)
                .into(binding.plantImage)

            binding.root.setOnClickListener {
                onItemClick(plant)
            }

            binding.executePendingBindings()
        }
    }
}

