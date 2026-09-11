package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "health_posts",
    indices = [Index(value = ["slug"], unique = true)]
)
data class HealthPost(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val slug: String,
    val title: String,
    val shortDescription: String,
    val categorySlug: String,
    val categoryNameBn: String,
    val content: String,
    val featuredImageUrl: String,
    val internalImages: String = "", // comma-separated or json
    val author: String = "হেলথ গাইড বিডি টিম",
    val publishedDate: String,
    val timestamp: Long = System.currentTimeMillis(),
    val tags: String = "",
    val viewsCount: Int = 0,
    val isPublished: Boolean = true,
    val isFeatured: Boolean = false,
    val isBookmarked: Boolean = false,
    val metaTitle: String = "",
    val metaDescription: String = ""
) {
    val permanentUrl: String
        get() = "https://healthguidebd.com/$categorySlug/$slug"

    val tagList: List<String>
        get() = tags.split(",").map { it.trim() }.filter { it.isNotEmpty() }
}

@Entity(
    tableName = "categories",
    indices = [Index(value = ["slug"], unique = true)]
)
data class HealthCategory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val slug: String,
    val nameBn: String,
    val nameEn: String,
    val iconName: String,
    val descriptionBn: String
)

@Entity(tableName = "subscribers")
data class Subscriber(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val email: String,
    val subscribedDate: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
