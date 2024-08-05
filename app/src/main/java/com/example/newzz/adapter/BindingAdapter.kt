package com.example.newzz.adapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.newzz.R


@BindingAdapter("image")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        Glide.with(imgView.context)
            .load(it)
            .into(imgView)
    } ?: run {
        imgView.setImageResource(R.drawable.no_image_found)
    }
}