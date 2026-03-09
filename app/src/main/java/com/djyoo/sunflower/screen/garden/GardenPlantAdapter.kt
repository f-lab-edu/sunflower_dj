package com.djyoo.sunflower.screen.garden

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.djyoo.sunflower.databinding.ItemGardenListBinding
import com.djyoo.sunflower.screen.plant.data.model.Plant

class GardenPlantAdapter(
    private val onItemClick: (Plant) -> Unit,
) : ListAdapter<Plant, GardenPlantAdapter.GardenPlantViewHolder>(GardenPlantDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GardenPlantViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemGardenListBinding.inflate(inflater, parent, false)
        return GardenPlantViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: GardenPlantViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class GardenPlantViewHolder(
        private val binding: ItemGardenListBinding,
        private val onItemClick: (Plant) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(plant: Plant) {
            val dateText = GardenUiFormatter.formatTodayDate()

            binding.gardenPlantTitle.text = plant.name
            binding.gardenPlantedDate.text = dateText
            binding.gardenLastWateredDate.text = dateText
            binding.gardenWateringMessage.text =
                GardenUiFormatter.formatWateringMessage(binding.root.context, plant.wateringInterval)

            Glide.with(binding.gardenPlantImage.context)
                .load(plant.imageUrl)
                .into(binding.gardenPlantImage)

            binding.root.setOnClickListener {
                onItemClick(plant)
            }

            binding.executePendingBindings()
        }
    }

    private class GardenPlantDiffCallback : DiffUtil.ItemCallback<Plant>() {
        override fun areItemsTheSame(oldItem: Plant, newItem: Plant): Boolean =
            oldItem.plantId == newItem.plantId

        override fun areContentsTheSame(oldItem: Plant, newItem: Plant): Boolean =
            oldItem == newItem
    }
}
