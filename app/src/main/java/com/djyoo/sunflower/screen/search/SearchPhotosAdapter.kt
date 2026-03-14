package com.djyoo.sunflower.screen.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.djyoo.sunflower.databinding.ItemSearchPhotoBinding
import com.djyoo.sunflower.screen.search.data.model.UnsplashPhoto

class SearchPhotosAdapter(
    private val onPhotoClick: (UnsplashPhoto) -> Unit,
) : ListAdapter<UnsplashPhoto, SearchPhotosAdapter.SearchPhotoViewHolder>(SearchPhotoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchPhotoViewHolder {
        val binding = ItemSearchPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchPhotoViewHolder(binding, onPhotoClick)
    }

    override fun onBindViewHolder(holder: SearchPhotoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class SearchPhotoViewHolder(
        private val binding: ItemSearchPhotoBinding,
        private val onPhotoClick: (UnsplashPhoto) -> Unit,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(photo: UnsplashPhoto) {
            binding.searchPhotoUserName.text = photo.user.name
            Glide.with(binding.searchPhotoImage.context)
                .load(photo.urls.small)
                .centerCrop()
                .into(binding.searchPhotoImage)
            binding.root.setOnClickListener { onPhotoClick(photo) }
            binding.executePendingBindings()
        }
    }

    private class SearchPhotoDiffCallback : DiffUtil.ItemCallback<UnsplashPhoto>() {
        override fun areItemsTheSame(oldItem: UnsplashPhoto, newItem: UnsplashPhoto): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: UnsplashPhoto, newItem: UnsplashPhoto): Boolean =
            oldItem == newItem
    }
}
