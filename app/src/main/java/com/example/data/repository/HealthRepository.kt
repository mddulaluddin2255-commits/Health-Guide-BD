package com.example.data.repository

import com.example.data.dao.CategoryDao
import com.example.data.dao.PostDao
import com.example.data.dao.SubscriberDao
import com.example.data.database.InitialDataProvider
import com.example.data.model.HealthCategory
import com.example.data.model.HealthPost
import com.example.data.model.Subscriber
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class HealthRepository(
    private val postDao: PostDao,
    private val categoryDao: CategoryDao,
    private val subscriberDao: SubscriberDao
) {
    val allPosts: Flow<List<HealthPost>> = postDao.getAllPosts()
    val publishedPosts: Flow<List<HealthPost>> = postDao.getPublishedPosts()
    val featuredPosts: Flow<List<HealthPost>> = postDao.getFeaturedPosts()
    val popularPosts: Flow<List<HealthPost>> = postDao.getPopularPosts(6)
    val bookmarkedPosts: Flow<List<HealthPost>> = postDao.getBookmarkedPosts()
    val categories: Flow<List<HealthCategory>> = categoryDao.getAllCategories()
    val totalPostCount: Flow<Int> = postDao.getTotalPostCount()
    val publishedPostCount: Flow<Int> = postDao.getPublishedPostCount()
    val draftPostCount: Flow<Int> = postDao.getDraftPostCount()
    val totalViews: Flow<Int?> = postDao.getTotalViewsCount()

    fun getPostsByCategory(categorySlug: String): Flow<List<HealthPost>> {
        return postDao.getPostsByCategory(categorySlug)
    }

    fun getPostBySlug(slug: String): Flow<HealthPost?> {
        return postDao.getPostBySlug(slug)
    }

    fun getPostById(id: Long): Flow<HealthPost?> {
        return postDao.getPostById(id)
    }

    fun searchPosts(query: String): Flow<List<HealthPost>> {
        return postDao.searchPosts(query)
    }

    fun getRelatedPosts(categorySlug: String, currentPostId: Long): Flow<List<HealthPost>> {
        return postDao.getRelatedPosts(categorySlug, currentPostId)
    }

    suspend fun insertPost(post: HealthPost): Long {
        return postDao.insertPost(post)
    }

    suspend fun updatePost(post: HealthPost) {
        postDao.updatePost(post)
    }

    suspend fun deletePost(post: HealthPost) {
        postDao.deletePost(post)
    }

    suspend fun incrementViews(id: Long) {
        postDao.incrementViews(id)
    }

    suspend fun toggleBookmark(id: Long) {
        postDao.toggleBookmark(id)
    }

    suspend fun setPublishStatus(id: Long, isPublished: Boolean) {
        postDao.setPublishStatus(id, isPublished)
    }

    suspend fun insertCategory(category: HealthCategory): Long {
        return categoryDao.insertCategory(category)
    }

    suspend fun subscribeNewsletter(email: String): Long {
        val subscriber = Subscriber(
            email = email,
            subscribedDate = "আজ"
        )
        return subscriberDao.insertSubscriber(subscriber)
    }

    suspend fun checkAndSeedInitialData() {
        val existingCategories = categoryDao.getAllCategories().first()
        if (existingCategories.isEmpty()) {
            categoryDao.insertAll(InitialDataProvider.initialCategories)
        }
        val existingPosts = postDao.getAllPosts().first()
        if (existingPosts.isEmpty()) {
            for (post in InitialDataProvider.samplePosts) {
                postDao.insertPost(post)
            }
        }
    }

    suspend fun resetToSampleData() {
        for (post in InitialDataProvider.samplePosts) {
            postDao.insertPost(post)
        }
    }
}
