package com.example.storysubmissionapp.view.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.example.storysubmissionapp.data.loadImage
import com.example.storysubmissionapp.data.model.Story
import com.example.storysubmissionapp.databinding.ListItemBinding
import com.example.storysubmissionapp.view.detail.DetailActivity

class StoryAdapter(
    private val dataset: List<Story>,
) : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    inner class StoryViewHolder(
        val binding: ListItemBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ListItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return StoryViewHolder(binding)
    }

    override fun getItemCount(): Int = dataset.size

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = dataset[position]

        holder.binding.apply {
            tvItemName.text = story.name
            tvItemDescription.text = story.description
            ivAvatar.loadImage(holder.itemView.context, story.photoUrl)
        }

        holder.apply {
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailActivity::class.java)
                intent.putExtra(DetailActivity.ID_KEY, story.id)

                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(binding.tvItemName as View, "name"),
                        Pair(binding.tvItemDescription as View, "description"),
                        Pair(binding.ivAvatar as View, "photo")
                    )

                itemView.context.startActivity(intent, optionsCompat.toBundle())
            }
        }
    }
}