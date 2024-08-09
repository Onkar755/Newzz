package com.example.newzz.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity("articles")
data class Article(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val author: String?,
    val content: String?,
    val description: String?,
    val publishedAt: String?,
    var source: Source?,
    val title: String?,
    val url: String?,
    val urlToImage: String?,
    val isSaved: Boolean = false,
    val category: String?
) : Parcelable