package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.FiberNew
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.HealthPost
import com.example.ui.components.AdSenseBannerCard
import com.example.ui.components.CategoryScrollBar
import com.example.ui.components.HealthFooter
import com.example.ui.components.HealthSearchBar
import com.example.ui.components.HeroSection
import com.example.ui.components.MedicalDisclaimerCard
import com.example.ui.components.NewsletterSection
import com.example.ui.components.PostCard
import com.example.ui.viewmodel.HealthViewModel

@Composable
fun HomeScreen(
    viewModel: HealthViewModel,
    onPostClick: (String) -> Unit,
    onCategoryClick: (String) -> Unit,
    onNavigatePage: (String) -> Unit,
    onSearchTrigger: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val categories by viewModel.categories.collectAsState()
    val selectedCategorySlug by viewModel.selectedCategorySlug.collectAsState()
    val publishedPosts by viewModel.publishedPosts.collectAsState()
    val featuredPosts by viewModel.featuredPosts.collectAsState()
    val popularPosts by viewModel.popularPosts.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val newsletterMsg by viewModel.newsletterMessage.collectAsState()

    // Filter posts if category selected on homepage
    val displayedPosts = if (selectedCategorySlug == null) {
        publishedPosts
    } else {
        publishedPosts.filter { it.categorySlug == selectedCategorySlug }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_feed"),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // 1. Search Bar
        item {
            HealthSearchBar(
                query = searchQuery,
                onQueryChange = {
                    viewModel.setSearchQuery(it)
                    if (it.isNotEmpty()) onSearchTrigger()
                },
                placeholder = "স্বাস্থ্য টিপস খুঁজুন (যেমন: সকালের নাস্তা, ইমিউনিটি)..."
            )
        }

        // 2. Hero Section
        item {
            HeroSection(
                onExploreClick = { onCategoryClick("health-tips") }
            )
        }

        // 3. Category Filter Chips
        item {
            CategoryScrollBar(
                categories = categories,
                selectedCategorySlug = selectedCategorySlug,
                onSelectCategory = { slug ->
                    viewModel.selectCategory(slug)
                }
            )
        }

        // 4. Featured Health Tips Carousel (shown when no specific category is filtered)
        if (selectedCategorySlug == null && featuredPosts.isNotEmpty()) {
            item {
                SectionHeader(
                    title = "নির্বাচিত স্বাস্থ্য টিপস",
                    icon = Icons.Default.Star,
                    iconTint = Color(0xFFF59E0B)
                )
            }

            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.padding(vertical = 6.dp)
                ) {
                    items(featuredPosts, key = { "featured_${it.id}" }) { post ->
                        FeaturedPostItem(
                            post = post,
                            onClick = { onPostClick(post.slug) }
                        )
                    }
                }
            }
        }

        // 5. AdSense Monetization Space 1
        item {
            AdSenseBannerCard()
        }

        // 6. Main Feed Header (Latest Posts or Category Filtered)
        item {
            val title = if (selectedCategorySlug == null) {
                "সাম্প্রতিক স্বাস্থ্য পরামর্শ"
            } else {
                val cat = categories.find { it.slug == selectedCategorySlug }
                "${cat?.nameBn ?: "ক্যাটাগরি"} সম্পর্কিত টিপস"
            }
            SectionHeader(
                title = title,
                icon = Icons.Default.FiberNew,
                iconTint = MaterialTheme.colorScheme.primary,
                trailingText = if (selectedCategorySlug != null) "সবগুলো দেখুন" else null,
                onTrailingClick = { viewModel.selectCategory(null) }
            )
        }

        // 7. Posts List
        if (displayedPosts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "কোনো পোস্ট পাওয়া যায়নি",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(displayedPosts, key = { it.id }) { post ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    PostCard(
                        post = post,
                        onClick = { onPostClick(post.slug) },
                        onBookmarkToggle = { viewModel.toggleBookmark(post.id) },
                        onShareClick = { viewModel.sharePost(context, post) }
                    )
                }
            }
        }

        // 8. Popular Posts Section
        if (selectedCategorySlug == null && popularPosts.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                SectionHeader(
                    title = "জনপ্রিয় স্বাস্থ্য বিষয়",
                    icon = Icons.AutoMirrored.Filled.TrendingUp,
                    iconTint = Color(0xFFEF4444)
                )
            }

            items(popularPosts.take(3), key = { "pop_${it.id}" }) { post ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    PopularPostCompactCard(
                        post = post,
                        onClick = { onPostClick(post.slug) }
                    )
                }
            }
        }

        // 9. Medical Disclaimer
        item {
            Spacer(modifier = Modifier.height(8.dp))
            MedicalDisclaimerCard()
        }

        // 10. Newsletter Subscription
        item {
            NewsletterSection(
                onSubscribe = { email -> viewModel.subscribeNewsletter(email) },
                feedbackMessage = newsletterMsg
            )
        }

        // 11. Footer
        item {
            Spacer(modifier = Modifier.height(8.dp))
            HealthFooter(
                onNavigatePage = onNavigatePage,
                onCategoryClick = onCategoryClick
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    trailingText: String? = null,
    onTrailingClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = iconTint.copy(alpha = 0.15f),
                modifier = Modifier.size(28.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (trailingText != null && onTrailingClick != null) {
            TextButton(onClick = onTrailingClick) {
                Text(
                    text = trailingText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun FeaturedPostItem(
    post: HealthPost,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .width(280.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("featured_post_${post.slug}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(post.featuredImageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = post.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )

                Surface(
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.TopStart),
                    color = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = post.categoryNameBn,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${post.author} • ${post.publishedDate}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun PopularPostCompactCard(
    post: HealthPost,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(post.featuredImageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = post.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = post.categoryNameBn,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${post.viewsCount} পাঠক পড়েছেন",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }
        }
    }
}
