package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.HealthCategory
import com.example.data.model.HealthPost
import com.example.ui.viewmodel.HealthViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    viewModel: HealthViewModel,
    onBackClick: () -> Unit,
    onViewPostLive: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isLoggedIn by viewModel.isAdminLoggedIn.collectAsState()
    val allPosts by viewModel.allPostsAdmin.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val totalCount by viewModel.totalPostCount.collectAsState()
    val publishedCount by viewModel.publishedPostCount.collectAsState()
    val draftCount by viewModel.draftPostCount.collectAsState()
    val totalViews by viewModel.totalViews.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Overview & Posts, 1: Add/Edit Post, 2: Categories

    // Editing state
    var editingPost by remember { mutableStateOf<HealthPost?>(null) }
    var postToDelete by remember { mutableStateOf<HealthPost?>(null) }
    var previewPost by remember { mutableStateOf<HealthPost?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isLoggedIn) "হেলথ গাইড সিএমএস" else "অ্যাডমিন লগইন",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("admin_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "ফিরে যান"
                        )
                    }
                },
                actions = {
                    if (isLoggedIn) {
                        IconButton(
                            onClick = {
                                viewModel.logoutAdmin()
                                Toast.makeText(context, "লগআউট সম্পন্ন হয়েছে", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.testTag("admin_logout_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = "লগআউট"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        if (!isLoggedIn) {
            AdminLoginView(
                onLogin = { user, pass ->
                    val ok = viewModel.loginAdmin(user, pass)
                    if (ok) {
                        Toast.makeText(context, "অ্যাডমিন প্যানেলে স্বাগতম!", Toast.LENGTH_SHORT).show()
                    }
                },
                errorMessage = viewModel.adminLoginError.collectAsState().value,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("পোস্ট তালিকা", fontWeight = FontWeight.SemiBold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = {
                            editingPost = null // fresh post
                            selectedTab = 1
                        },
                        text = {
                            Text(
                                text = if (editingPost != null) "পোস্ট এডিট" else "নতুন পোস্ট",
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("ক্যাটাগরি", fontWeight = FontWeight.SemiBold) }
                    )
                }

                when (selectedTab) {
                    0 -> AdminPostListView(
                        posts = allPosts,
                        totalCount = totalCount,
                        publishedCount = publishedCount,
                        draftCount = draftCount,
                        totalViews = totalViews ?: 0,
                        onAddNew = {
                            editingPost = null
                            selectedTab = 1
                        },
                        onEdit = { post ->
                            editingPost = post
                            selectedTab = 1
                        },
                        onDelete = { post -> postToDelete = post },
                        onTogglePublish = { post -> viewModel.togglePublishStatus(post) },
                        onCopyUrl = { post -> viewModel.copyPostUrl(context, post) },
                        onViewLive = { slug -> onViewPostLive(slug) },
                        onResetSampleData = {
                            viewModel.restoreSampleData()
                            Toast.makeText(context, "স্যাম্পল পোস্ট রিস্টোর করা হয়েছে", Toast.LENGTH_SHORT).show()
                        }
                    )
                    1 -> AdminPostFormView(
                        existingPost = editingPost,
                        categories = categories,
                        onCancel = {
                            editingPost = null
                            selectedTab = 0
                        },
                        onSave = { post ->
                            viewModel.savePost(post) {
                                Toast.makeText(context, "পোস্ট সফলভাবে সংরক্ষিত হয়েছে!", Toast.LENGTH_SHORT).show()
                                editingPost = null
                                selectedTab = 0
                            }
                        },
                        onPreview = { post -> previewPost = post }
                    )
                    2 -> AdminCategoryListView(
                        categories = categories,
                        posts = allPosts
                    )
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (postToDelete != null) {
        AlertDialog(
            onDismissRequest = { postToDelete = null },
            title = { Text("পোস্ট ডিলিট নিশ্চিত করুন") },
            text = { Text("আপনি কি নিশ্চিত যে \"${postToDelete!!.title}\" পোস্টটি মুছে ফেলতে চান? এটি আর ফিরিয়ে আনা যাবে না।") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deletePost(postToDelete!!)
                        Toast.makeText(context, "পোস্ট মুছে ফেলা হয়েছে", Toast.LENGTH_SHORT).show()
                        postToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("মুছে ফেলুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { postToDelete = null }) {
                    Text("বাতিল")
                }
            }
        )
    }

    // Live Post Preview Dialog
    if (previewPost != null) {
        AlertDialog(
            onDismissRequest = { previewPost = null },
            title = { Text("পোস্ট প্রিভিউ (Live Preview)") },
            text = {
                LazyColumn(modifier = Modifier.height(400.dp)) {
                    item {
                        Text(
                            text = previewPost!!.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "URL: ${previewPost!!.permanentUrl}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        AsyncImage(
                            model = previewPost!!.featuredImageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = previewPost!!.shortDescription,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = previewPost!!.content,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = { previewPost = null }) {
                    Text("বন্ধ করুন")
                }
            }
        )
    }
}

@Composable
fun AdminLoginView(
    onLogin: (String, String) -> Unit,
    errorMessage: String?,
    modifier: Modifier = Modifier
) {
    var username by remember { mutableStateOf("admin") }
    var password by remember { mutableStateOf("admin") }

    Column(
        modifier = modifier
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(72.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Health Guide BD অ্যাডমিন পোর্টাল",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "স্বাস্থ্য টিপস ও আর্টিকেল তৈরি, সম্পাদনা ও প্রকাশ করুন",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("ইউজারনেম (Username)") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("admin_username_input"),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("পাসওয়ার্ড (Password)") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("admin_password_input"),
            shape = RoundedCornerShape(12.dp)
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage,
                color = Color(0xFFEF4444),
                style = MaterialTheme.typography.labelSmall
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { onLogin(username, password) },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("admin_login_submit_btn"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("লগইন করুন", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Quick Demo Login helper for non-technical users
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.clickable {
                onLogin("admin", "admin")
            }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "দ্রুত ডেমো লগইন (ডিফল্ট: admin / admin)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun AdminPostListView(
    posts: List<HealthPost>,
    totalCount: Int,
    publishedCount: Int,
    draftCount: Int,
    totalViews: Int,
    onAddNew: () -> Unit,
    onEdit: (HealthPost) -> Unit,
    onDelete: (HealthPost) -> Unit,
    onTogglePublish: (HealthPost) -> Unit,
    onCopyUrl: (HealthPost) -> Unit,
    onViewLive: (String) -> Unit,
    onResetSampleData: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var filterStatus by remember { mutableStateOf("all") } // all, published, draft

    val filtered = posts.filter { post ->
        val matchesQuery = post.title.contains(searchQuery, ignoreCase = true) ||
                post.slug.contains(searchQuery, ignoreCase = true) ||
                post.categoryNameBn.contains(searchQuery, ignoreCase = true)
        val matchesStatus = when (filterStatus) {
            "published" -> post.isPublished
            "draft" -> !post.isPublished
            else -> true
        }
        matchesQuery && matchesStatus
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("admin_post_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Statistics Cards Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AdminStatCard(title = "মোট পোস্ট", count = "$totalCount", color = Color(0xFF0284C7), modifier = Modifier.weight(1f))
                AdminStatCard(title = "প্রকাশিত", count = "$publishedCount", color = Color(0xFF16A34A), modifier = Modifier.weight(1f))
                AdminStatCard(title = "ড্রাফট", count = "$draftCount", color = Color(0xFFEAB308), modifier = Modifier.weight(1f))
                AdminStatCard(title = "মোট ভিউ", count = "$totalViews", color = Color(0xFF8B5CF6), modifier = Modifier.weight(1f))
            }
        }

        // Action Buttons Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onAddNew,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("admin_add_new_post_btn")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("নতুন পোস্ট লিখুন", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onResetSampleData,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("ডেমো ডেটা রিস্টোর", style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        // Search & Filter
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("পোস্ট খুঁজুন (শিরোনাম বা স্লাগ)...") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = filterStatus == "all",
                    onClick = { filterStatus = "all" },
                    label = { Text("সকল ($totalCount)") }
                )
                FilterChip(
                    selected = filterStatus == "published",
                    onClick = { filterStatus = "published" },
                    label = { Text("প্রকাশিত ($publishedCount)") }
                )
                FilterChip(
                    selected = filterStatus == "draft",
                    onClick = { filterStatus = "draft" },
                    label = { Text("ড্রাফট ($draftCount)") }
                )
            }
        }

        // Posts List
        items(filtered, key = { "admin_post_${it.id}" }) { post ->
            AdminPostRow(
                post = post,
                onEdit = { onEdit(post) },
                onDelete = { onDelete(post) },
                onTogglePublish = { onTogglePublish(post) },
                onCopyUrl = { onCopyUrl(post) },
                onViewLive = { onViewLive(post.slug) }
            )
        }
    }
}

@Composable
fun AdminStatCard(
    title: String,
    count: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = count, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = color)
            Text(text = title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
        }
    }
}

@Composable
fun AdminPostRow(
    post: HealthPost,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onTogglePublish: () -> Unit,
    onCopyUrl: () -> Unit,
    onViewLive: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = post.featuredImageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (post.isPublished) Color(0xFFDCFCE7) else Color(0xFFFEF9C3)
                        ) {
                            Text(
                                text = if (post.isPublished) "প্রকাশিত" else "ড্রাফট",
                                color = if (post.isPublished) Color(0xFF15803D) else Color(0xFFA16207),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Text(
                            text = "${post.viewsCount} ভিউ",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = post.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "স্লাগ: /${post.categorySlug}/${post.slug}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(6.dp))

            // Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Copy URL Button
                TextButton(onClick = onCopyUrl) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("লিংক কপি", style = MaterialTheme.typography.labelSmall)
                }

                // Live View Button
                TextButton(onClick = onViewLive) {
                    Icon(imageVector = Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("দেখুন", style = MaterialTheme.typography.labelSmall)
                }

                // Edit Button
                IconButton(onClick = onEdit) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "এডিট", tint = MaterialTheme.colorScheme.primary)
                }

                // Delete Button
                IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "মুছে ফেলুন", tint = Color(0xFFEF4444))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPostFormView(
    existingPost: HealthPost?,
    categories: List<HealthCategory>,
    onCancel: () -> Unit,
    onSave: (HealthPost) -> Unit,
    onPreview: (HealthPost) -> Unit
) {
    var title by remember { mutableStateOf(existingPost?.title ?: "") }
    var slug by remember { mutableStateOf(existingPost?.slug ?: "") }
    var selectedCategorySlug by remember { mutableStateOf(existingPost?.categorySlug ?: (categories.firstOrNull()?.slug ?: "health-tips")) }
    var shortDescription by remember { mutableStateOf(existingPost?.shortDescription ?: "") }
    var content by remember { mutableStateOf(existingPost?.content ?: "") }
    var author by remember { mutableStateOf(existingPost?.author ?: "ডাঃ রাফিদ হাসান") }
    var tags by remember { mutableStateOf(existingPost?.tags ?: "স্বাস্থ্য, পুষ্টি, সুস্থতা") }
    var featuredImageUrl by remember { mutableStateOf(existingPost?.featuredImageUrl ?: "https://images.unsplash.com/photo-1505751172876-fa1923c5c528?w=800&q=80") }
    var isPublished by remember { mutableStateOf(existingPost?.isPublished ?: true) }
    var isFeatured by remember { mutableStateOf(existingPost?.isFeatured ?: false) }

    // Photo picker for selecting images from gallery
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            featuredImageUrl = uri.toString()
        }
    }

    // Curated high quality medical/nutrition images library for quick 1-tap select
    val curatedImages = listOf(
        "https://images.unsplash.com/photo-1505751172876-fa1923c5c528?w=800&q=80",
        "https://images.unsplash.com/photo-1540420773420-3366772f4999?w=800&q=80",
        "https://images.unsplash.com/photo-1525351484163-7529414344d8?w=800&q=80",
        "https://images.unsplash.com/photo-1576091160399-112ba8d25d1d?w=800&q=80",
        "https://images.unsplash.com/photo-1506126613408-eca07ce68773?w=800&q=80",
        "https://images.unsplash.com/photo-1517838277536-f5f99be501cd?w=800&q=80",
        "https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?w=800&q=80"
    )

    // Category dropdown state
    var expandedCategory by remember { mutableStateOf(false) }
    val currentCategory = categories.find { it.slug == selectedCategorySlug } ?: categories.firstOrNull()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("admin_post_form"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = if (existingPost == null) "নতুন স্বাস্থ্য পোস্ট তৈরি করুন" else "পোস্ট সম্পাদনা করুন",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Title Input
        item {
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    if (existingPost == null && slug.isEmpty()) {
                        // Auto-generate basic slug from English transliteration or timestamp
                        slug = "post-${System.currentTimeMillis() % 100000}"
                    }
                },
                label = { Text("পোস্টের শিরোনাম (বাংলায়) *") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("form_title_input"),
                shape = RoundedCornerShape(10.dp)
            )
        }

        // Custom Slug Input
        item {
            Column {
                OutlinedTextField(
                    value = slug,
                    onValueChange = {
                        // Format slug to lowercase-hyphen
                        slug = it.lowercase().replace(" ", "-").replace("[^a-z0-9-]".toRegex(), "")
                    },
                    label = { Text("স্থায়ী SEO স্লাগ (e.g. healthy-breakfast) *") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("form_slug_input"),
                    shape = RoundedCornerShape(10.dp)
                )
                Text(
                    text = "স্থায়ী URL হবে: https://healthguidebd.com/$selectedCategorySlug/${slug.ifEmpty { "your-slug" }}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                )
            }
        }

        // Category Picker Dropdown
        item {
            ExposedDropdownMenuBox(
                expanded = expandedCategory,
                onExpandedChange = { expandedCategory = !expandedCategory }
            ) {
                OutlinedTextField(
                    value = currentCategory?.nameBn ?: "ক্যাটাগরি নির্বাচন করুন",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("ক্যাটাগরি *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(10.dp)
                )
                ExposedDropdownMenu(
                    expanded = expandedCategory,
                    onDismissRequest = { expandedCategory = false }
                ) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.nameBn) },
                            onClick = {
                                selectedCategorySlug = cat.slug
                                expandedCategory = false
                            }
                        )
                    }
                }
            }
        }

        // Short Description
        item {
            OutlinedTextField(
                value = shortDescription,
                onValueChange = { shortDescription = it },
                label = { Text("সংক্ষিপ্ত বিবরণ (Short Description) *") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                minLines = 2
            )
        }

        // Author Name
        item {
            OutlinedTextField(
                value = author,
                onValueChange = { author = it },
                label = { Text("লেখকের নাম ও পদবী (Author) *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )
        }

        // Featured Image Picker & Preview
        item {
            Column {
                Text(
                    text = "ফিচার্ড ইমেজ (Featured Image) *",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = featuredImageUrl,
                        onValueChange = { featuredImageUrl = it },
                        label = { Text("ছবির লিঙ্ক (URL)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Button(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Image, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("গ্যালারি")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "অথবা দ্রুত নির্বাচন করুন:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Curated thumbnail selector
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(curatedImages) { imgUrl ->
                        AsyncImage(
                            model = imgUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { featuredImageUrl = imgUrl }
                                .borderOnSelected(featuredImageUrl == imgUrl, MaterialTheme.colorScheme.primary),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        }

        // Article Bengali Content
        item {
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("আর্টিকেলের বিস্তারিত বিষয়বস্তু (বাংলা কনটেন্ট) *") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("form_content_input"),
                shape = RoundedCornerShape(10.dp),
                minLines = 8,
                placeholder = {
                    Text("# মূল শিরোনাম\n\nপ্যারাগ্রাফ লিখুন...\n\n### উপ-শিরোনাম\n- পয়েন্ট ১\n- পয়েন্ট ২\n\n> জরুরি পরামর্শ")
                }
            )
        }

        // Tags
        item {
            OutlinedTextField(
                value = tags,
                onValueChange = { tags = it },
                label = { Text("ট্যাগসমূহ (কমা দিয়ে আলাদা করুন)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            )
        }

        // Publish Toggle
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isPublished) "অবস্থা: প্রকাশিত (Published)" else "অবস্থা: ড্রাফট (Draft)",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isPublished) "সবার জন্য উন্মুক্ত থাকবে" else "শুধুমাত্র অ্যাডমিন প্যানেলে ড্রাফট হিসেবে থাকবে",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Switch(
                    checked = isPublished,
                    onCheckedChange = { isPublished = it }
                )
            }
        }

        // Action Buttons: Cancel, Preview, Save
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("বাতিল")
                }

                OutlinedButton(
                    onClick = {
                        val buildPost = buildPostObject(
                            existing = existingPost,
                            title = title,
                            slug = slug,
                            category = currentCategory,
                            shortDescription = shortDescription,
                            content = content,
                            author = author,
                            tags = tags,
                            imageUrl = featuredImageUrl,
                            isPublished = isPublished,
                            isFeatured = isFeatured
                        )
                        onPreview(buildPost)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Preview, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("প্রিভিউ")
                }

                Button(
                    onClick = {
                        if (title.isBlank() || slug.isBlank() || shortDescription.isBlank()) {
                            return@Button
                        }
                        val buildPost = buildPostObject(
                            existing = existingPost,
                            title = title,
                            slug = slug,
                            category = currentCategory,
                            shortDescription = shortDescription,
                            content = content,
                            author = author,
                            tags = tags,
                            imageUrl = featuredImageUrl,
                            isPublished = isPublished,
                            isFeatured = isFeatured
                        )
                        onSave(buildPost)
                    },
                    modifier = Modifier
                        .weight(1.2f)
                        .testTag("form_save_post_btn"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(imageVector = Icons.Default.Publish, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isPublished) "প্রকাশ করুন" else "ড্রাফট রাখুন", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private fun buildPostObject(
    existing: HealthPost?,
    title: String,
    slug: String,
    category: HealthCategory?,
    shortDescription: String,
    content: String,
    author: String,
    tags: String,
    imageUrl: String,
    isPublished: Boolean,
    isFeatured: Boolean
): HealthPost {
    val dateStr = existing?.publishedDate ?: SimpleDateFormat("dd MMMM, yyyy", Locale("bn", "BD")).format(Date())
    return HealthPost(
        id = existing?.id ?: 0L,
        slug = slug.ifEmpty { "post-${System.currentTimeMillis()}" },
        title = title.ifEmpty { "শিরোনামহীন স্বাস্থ্য টিপস" },
        shortDescription = shortDescription,
        categorySlug = category?.slug ?: "health-tips",
        categoryNameBn = category?.nameBn ?: "স্বাস্থ্য টিপস",
        content = content,
        featuredImageUrl = imageUrl,
        author = author,
        publishedDate = dateStr,
        tags = tags,
        isPublished = isPublished,
        isFeatured = isFeatured,
        metaTitle = "$title | Health Guide BD",
        metaDescription = shortDescription
    )
}

@Composable
fun AdminCategoryListView(
    categories: List<HealthCategory>,
    posts: List<HealthPost>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "স্বাস্থ্য ক্যাটাগরি তালিকা (${categories.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        items(categories, key = { "cat_${it.id}" }) { cat ->
            val count = posts.count { it.categorySlug == cat.slug }
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = cat.nameBn, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text(text = "Slug: ${cat.slug} • ${cat.descriptionBn}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "$count পোস্ট",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun Modifier.borderOnSelected(isSelected: Boolean, color: Color): Modifier {
    return if (isSelected) {
        this.then(Modifier.background(color = color.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp)))
    } else {
        this
    }
}
