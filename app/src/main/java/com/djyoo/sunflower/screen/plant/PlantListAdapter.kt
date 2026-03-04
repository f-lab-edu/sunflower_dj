package com.djyoo.sunflower.screen.plant

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.djyoo.sunflower.databinding.ItemPlantListBinding
import com.djyoo.sunflower.screen.plant.data.model.Plant

class PlantListAdapter(
    private val onItemClick: (Plant) -> Unit,
) : ListAdapter<Plant, PlantListAdapter.PlantViewHolder>(PlantDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPlantListBinding.inflate(inflater, parent, false)
        return PlantViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PlantViewHolder(
        private val binding: ItemPlantListBinding,
        private val onItemClick: (Plant) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(plant: Plant) {
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

    private class PlantDiffCallback : DiffUtil.ItemCallback<Plant>() {
        override fun areItemsTheSame(oldItem: Plant, newItem: Plant): Boolean =
            oldItem.plantId == newItem.plantId

        override fun areContentsTheSame(oldItem: Plant, newItem: Plant): Boolean =
            oldItem == newItem
    }
}

