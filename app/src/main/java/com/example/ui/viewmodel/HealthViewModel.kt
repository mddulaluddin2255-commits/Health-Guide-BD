package com.example.ui.viewmodel

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.HealthCategory
import com.example.data.model.HealthPost
import com.example.data.repository.HealthRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class HealthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HealthRepository

    init {
        val db = AppDatabase.getDatabase(application, viewModelScope)
        repository = HealthRepository(db.postDao(), db.categoryDao(), db.subscriberDao())
        viewModelScope.launch {
            repository.checkAndSeedInitialData()
        }
    }

    val categories: StateFlow<List<HealthCategory>> = repository.categories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val publishedPosts: StateFlow<List<HealthPost>> = repository.publishedPosts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val featuredPosts: StateFlow<List<HealthPost>> = repository.featuredPosts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val popularPosts: StateFlow<List<HealthPost>> = repository.popularPosts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookmarkedPosts: StateFlow<List<HealthPost>> = repository.bookmarkedPosts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPostsAdmin: StateFlow<List<HealthPost>> = repository.allPosts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalPostCount: StateFlow<Int> = repository.totalPostCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val publishedPostCount: StateFlow<Int> = repository.publishedPostCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val draftPostCount: StateFlow<Int> = repository.draftPostCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalViews: StateFlow<Int?> = repository.totalViews
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val searchResults: StateFlow<List<HealthPost>> = _searchQuery
        .flatMapLatest { query ->
            if (query.trim().isEmpty()) flowOf(emptyList())
            else repository.searchPosts(query.trim())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Category Screen selection
    private val _selectedCategorySlug = MutableStateFlow<String?>(null)
    val selectedCategorySlug: StateFlow<String?> = _selectedCategorySlug.asStateFlow()

    val categoryPosts: StateFlow<List<HealthPost>> = _selectedCategorySlug
        .flatMapLatest { slug ->
            if (slug == null) repository.publishedPosts
            else repository.getPostsByCategory(slug)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Post Detail
    private val _activePostSlug = MutableStateFlow<String?>(null)
    val activePostSlug: StateFlow<String?> = _activePostSlug.asStateFlow()

    val currentPost: StateFlow<HealthPost?> = _activePostSlug
        .flatMapLatest { slug ->
            if (slug == null) flowOf(null)
            else repository.getPostBySlug(slug)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Related posts for current detail post
    val relatedPosts: StateFlow<List<HealthPost>> = currentPost
        .flatMapLatest { post ->
            if (post == null) flowOf(emptyList())
            else repository.getRelatedPosts(post.categorySlug, post.id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin Auth
    private val _isAdminLoggedIn = MutableStateFlow(false)
    val isAdminLoggedIn: StateFlow<Boolean> = _isAdminLoggedIn.asStateFlow()

    private val _adminLoginError = MutableStateFlow<String?>(null)
    val adminLoginError: StateFlow<String?> = _adminLoginError.asStateFlow()

    // Newsletter feedback
    private val _newsletterMessage = MutableStateFlow<String?>(null)
    val newsletterMessage: StateFlow<String?> = _newsletterMessage.asStateFlow()

    // Action handlers
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(slug: String?) {
        _selectedCategorySlug.value = slug
    }

    fun selectPostBySlug(slug: String) {
        _activePostSlug.value = slug
        viewModelScope.launch {
            // Find post and increment view count
            val post = repository.getPostBySlug(slug).first()
            post?.let { repository.incrementViews(it.id) }
        }
    }

    fun toggleBookmark(id: Long) {
        viewModelScope.launch {
            repository.toggleBookmark(id)
        }
    }

    fun subscribeNewsletter(email: String) {
        val trimmed = email.trim()
        if (trimmed.isEmpty() || !trimmed.contains("@")) {
            _newsletterMessage.value = "অনুগ্রহ করে একটি সঠিক ইমেইল অ্যাড্রেস লিখুন"
            return
        }
        viewModelScope.launch {
            repository.subscribeNewsletter(trimmed)
            _newsletterMessage.value = "ধন্যবাদ! হেলথ গাইড বিডি নিউজল্যাটারে সফলভাবে সাবস্ক্রাইব হয়েছে।"
        }
    }

    fun clearNewsletterMessage() {
        _newsletterMessage.value = null
    }

    fun loginAdmin(username: String, pin: String): Boolean {
        if ((username.trim() == "admin" && pin.trim() == "admin") ||
            (username.trim() == "admin" && pin.trim() == "admin123") ||
            (username.trim() == "doctor" && pin.trim() == "doctor")
        ) {
            _isAdminLoggedIn.value = true
            _adminLoginError.value = null
            return true
        } else {
            _adminLoginError.value = "ইউজারনেম অথবা পাসওয়ার্ড সঠিক নয় (ব্যবহার করুন: admin / admin)"
            return false
        }
    }

    fun logoutAdmin() {
        _isAdminLoggedIn.value = false
    }

    fun savePost(post: HealthPost, onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (post.id == 0L) {
                repository.insertPost(post)
            } else {
                repository.updatePost(post)
            }
            onSuccess()
        }
    }

    fun deletePost(post: HealthPost) {
        viewModelScope.launch {
            repository.deletePost(post)
        }
    }

    fun togglePublishStatus(post: HealthPost) {
        viewModelScope.launch {
            repository.setPublishStatus(post.id, !post.isPublished)
        }
    }

    fun copyPostUrl(context: Context, post: HealthPost) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Health Guide BD URL", post.permanentUrl)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "পোস্ট লিংক কপি করা হয়েছে:\n${post.permanentUrl}", Toast.LENGTH_LONG).show()
    }

    fun sharePost(context: Context, post: HealthPost, platform: String? = null) {
        val shareText = """
            ${post.title}
            
            ${post.shortDescription}
            
            বিস্তারিত পড়ুন হেলথ গাইড বিডি-তে:
            ${post.permanentUrl}
        """.trimIndent()

        when (platform) {
            "facebook" -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/sharer/sharer.php?u=${Uri.encode(post.permanentUrl)}"))
                context.startActivity(Intent.createChooser(intent, "Share on Facebook"))
            }
            "whatsapp" -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("whatsapp://send?text=${Uri.encode(shareText)}"))
                try {
                    context.startActivity(intent)
                } catch (e: Exception) {
                    shareGeneral(context, shareText, post.title)
                }
            }
            "telegram" -> {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/share/url?url=${Uri.encode(post.permanentUrl)}&text=${Uri.encode(post.title)}"))
                try {
                    context.startActivity(intent)
                } catch (e: Exception) {
                    shareGeneral(context, shareText, post.title)
                }
            }
            "messenger" -> {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, "${post.title} - ${post.permanentUrl}")
                    setPackage("com.facebook.orca")
                }
                try {
                    context.startActivity(intent)
                } catch (e: Exception) {
                    shareGeneral(context, shareText, post.title)
                }
            }
            else -> {
                shareGeneral(context, shareText, post.title)
            }
        }
    }

    private fun shareGeneral(context: Context, text: String, title: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            putExtra(Intent.EXTRA_TITLE, title)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "স্বাস্থ্য টিপস শেয়ার করুন")
        context.startActivity(shareIntent)
    }

    fun restoreSampleData() {
        viewModelScope.launch {
            repository.resetToSampleData()
        }
    }
}
