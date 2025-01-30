package com.example.blogapp.Adapter
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.blogapp.Model.PostModel
import com.example.blogapp.R

class PostAdapter(private val context: Context, private val postModelList: List<PostModel>) : RecyclerView.Adapter<PostAdapter.MyHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.home_post, parent, false)
        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val postModel = postModelList[position]
        holder.postTitle.text = postModel.pTitle
        holder.postDescription.text = postModel.pDescription
        Glide.with(context).load(postModel.pImage).into(holder.postImage)
    }

    override fun getItemCount(): Int {
        return postModelList.size
    }

    inner class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val postImage: ImageView = itemView.findViewById(R.id.postImage)
        val postTitle: TextView = itemView.findViewById(R.id.postTitle)
        val postDescription: TextView = itemView.findViewById(R.id.postDescription)
    }
}