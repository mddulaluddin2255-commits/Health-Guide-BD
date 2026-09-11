package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.HealthCategory
import com.example.data.model.HealthPost
import com.example.data.model.Subscriber
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    @Query("SELECT * FROM health_posts ORDER BY timestamp DESC")
    fun getAllPosts(): Flow<List<HealthPost>>

    @Query("SELECT * FROM health_posts WHERE isPublished = 1 ORDER BY timestamp DESC")
    fun getPublishedPosts(): Flow<List<HealthPost>>

    @Query("SELECT * FROM health_posts WHERE isPublished = 1 AND isFeatured = 1 ORDER BY timestamp DESC")
    fun getFeaturedPosts(): Flow<List<HealthPost>>

    @Query("SELECT * FROM health_posts WHERE isPublished = 1 ORDER BY viewsCount DESC LIMIT :limit")
    fun getPopularPosts(limit: Int = 5): Flow<List<HealthPost>>

    @Query("SELECT * FROM health_posts WHERE isPublished = 1 AND categorySlug = :categorySlug ORDER BY timestamp DESC")
    fun getPostsByCategory(categorySlug: String): Flow<List<HealthPost>>

    @Query("SELECT * FROM health_posts WHERE isPublished = 1 AND isBookmarked = 1 ORDER BY timestamp DESC")
    fun getBookmarkedPosts(): Flow<List<HealthPost>>

    @Query("SELECT * FROM health_posts WHERE slug = :slug LIMIT 1")
    fun getPostBySlug(slug: String): Flow<HealthPost?>

    @Query("SELECT * FROM health_posts WHERE id = :id LIMIT 1")
    fun getPostById(id: Long): Flow<HealthPost?>

    @Query("SELECT * FROM health_posts WHERE isPublished = 1 AND (title LIKE '%' || :query || '%' OR shortDescription LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%' OR categoryNameBn LIKE '%' || :query || '%') ORDER BY timestamp DESC")
    fun searchPosts(query: String): Flow<List<HealthPost>>

    @Query("SELECT * FROM health_posts WHERE isPublished = 1 AND categorySlug = :categorySlug AND id != :currentPostId LIMIT 4")
    fun getRelatedPosts(categorySlug: String, currentPostId: Long): Flow<List<HealthPost>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: HealthPost): Long

    @Update
    suspend fun updatePost(post: HealthPost)

    @Delete
    suspend fun deletePost(post: HealthPost)

    @Query("UPDATE health_posts SET viewsCount = viewsCount + 1 WHERE id = :id")
    suspend fun incrementViews(id: Long)

    @Query("UPDATE health_posts SET isBookmarked = NOT isBookmarked WHERE id = :id")
    suspend fun toggleBookmark(id: Long)

    @Query("UPDATE health_posts SET isPublished = :isPublished WHERE id = :id")
    suspend fun setPublishStatus(id: Long, isPublished: Boolean)

    @Query("SELECT COUNT(*) FROM health_posts")
    fun getTotalPostCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM health_posts WHERE isPublished = 1")
    fun getPublishedPostCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM health_posts WHERE isPublished = 0")
    fun getDraftPostCount(): Flow<Int>

    @Query("SELECT SUM(viewsCount) FROM health_posts")
    fun getTotalViewsCount(): Flow<Int?>
}

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY id ASC")
    fun getAllCategories(): Flow<List<HealthCategory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: HealthCategory): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<HealthCategory>)

    @Update
    suspend fun updateCategory(category: HealthCategory)

    @Delete
    suspend fun deleteCategory(category: HealthCategory)
}

@Dao
interface SubscriberDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscriber(subscriber: Subscriber): Long

    @Query("SELECT * FROM subscribers ORDER BY timestamp DESC")
    fun getAllSubscribers(): Flow<List<Subscriber>>

    @Query("SELECT COUNT(*) FROM subscribers")
    fun getSubscriberCount(): Flow<Int>
}
