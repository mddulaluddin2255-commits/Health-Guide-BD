package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.HealthFooter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaticPagesScreen(
    pageKey: String,
    onBackClick: () -> Unit,
    onNavigatePage: (String) -> Unit,
    onCategoryClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val (pageTitle, pageIcon) = when (pageKey) {
        "about" -> Pair("আমাদের সম্পর্কে (About Us)", Icons.Default.Info)
        "contact" -> Pair("যোগাযোগ (Contact)", Icons.Default.Email)
        "privacy" -> Pair("গোপনীয়তা নীতি (Privacy Policy)", Icons.Default.Policy)
        "disclaimer" -> Pair("মেডিকেল দাবিত্যাগ (Disclaimer)", Icons.Default.WarningAmber)
        else -> Pair("হেলথ গাইড বিডি", Icons.Default.Info)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = pageTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("static_page_back")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "ফিরে যান"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = pageIcon,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Health Guide BD",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = pageTitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    when (pageKey) {
                        "about" -> AboutUsContent()
                        "contact" -> ContactContent(onSendMessage = {
                            Toast.makeText(context, "ধন্যবাদ! আপনার বার্তাটি হেলথ গাইড বিডি টিমের কাছে পৌঁছেছে।", Toast.LENGTH_LONG).show()
                        })
                        "privacy" -> PrivacyPolicyContent()
                        "disclaimer" -> DisclaimerContent()
                        else -> AboutUsContent()
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
                HealthFooter(
                    onNavigatePage = onNavigatePage,
                    onCategoryClick = onCategoryClick
                )
            }
        }
    }
}

@Composable
fun AboutUsContent() {
    Column {
        Text(
            text = "আমাদের লক্ষ্য ও উদ্দেশ্য",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Health Guide BD বাংলাদেশের সর্বস্তরের মানুষের জন্য নির্ভরযোগ্য, বিজ্ঞানভিত্তিক এবং সহজবোধ্য স্বাস্থ্য তথ্য ও পুষ্টি পরামর্শের একটি বিশ্বস্ত উন্মুক্ত ডিজিটাল প্ল্যাটফর্ম। আমাদের মূল লক্ষ্য হল সাধারণ মানুষকে রোগ প্রতিরোধে সচেতন করা, পুষ্টিকর খাদ্যাভ্যাস গড়ে তুলতে সাহায্য করা এবং একটি সুস্থ ও দীর্ঘায়ু জীবনযাপনের অনুপ্রেরণা যোগানো।",
            style = MaterialTheme.typography.bodyLarge,
            lineHeight = 26.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "আমাদের মূল বৈশিষ্ট্যসমূহ:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        BulletItem(text = "১০০% খাঁটি ও চিকিৎসাবিজ্ঞানের স্বীকৃত গবেষণার আলোকে তথ্য উপস্থাপন")
        BulletItem(text = "সহজ বাংলায় প্রতিদিনের ঘরোয়া ডায়েট ও পুষ্টি তালিকা")
        BulletItem(text = "নারী, পুরুষ, শিশু ও বয়োজ্যেষ্ঠদের জন্য স্বতন্ত্র ক্যাটাগরি")
        BulletItem(text = "মানসিক স্বাস্থ্য ও স্ট্রেস রিলিজের বৈজ্ঞানিক গাইডলাইন")
        BulletItem(text = "কোনো প্রকার অবৈজ্ঞানিক বা চটকদার ভূয়া স্বাস্থ্য দাবি থেকে শতভাগ মুক্ত")

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "সম্পাদকীয় ও মেডিকেল রিভিউ প্যানেল",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "আমাদের প্রতিটি নিবন্ধ নিবন্ধিত চিকিৎসক (MBBS), পুষ্টিবিদ এবং ফিটনেস বিশেষজ্ঞদের তথ্যসূত্র যাচাই করে প্রস্তুত করা হয়।",
            style = MaterialTheme.typography.bodyMedium,
            lineHeight = 24.sp
        )
    }
}

@Composable
fun ContactContent(onSendMessage: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Column {
        Text(
            text = "আমাদের সাথে যোগাযোগ করুন",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "যে কোনো স্বাস্থ্য সংক্রান্ত প্রশ্ন, মতামত, সম্পাদকীয় পরামর্শ বা সহযোগিতার জন্য আমাদের সাথে সরাসরি যোগাযোগ করুন।",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Contact info cards
        ContactInfoRow(icon = Icons.Default.Email, title = "ইমেইল অ্যাড্রেস", info = "contact@healthguidebd.com")
        ContactInfoRow(icon = Icons.Default.Phone, title = "হেল্পলাইন", info = "+880 1700-000000 (সকাল ৯টা - বিকাল ৫টা)")
        ContactInfoRow(icon = Icons.Default.LocationOn, title = "ঠিকানা", info = "ঢাকা, বাংলাদেশ")

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "সরাসরি বার্তা পাঠান:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("আপনার পূর্ণ নাম") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("ইমেইল অ্যাড্রেস") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = subject,
            onValueChange = { subject = it },
            label = { Text("বার্তার বিষয়") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("আপনার বার্তা লিখুন...") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            minLines = 4
        )
        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = {
                if (name.isNotBlank() && email.isNotBlank() && message.isNotBlank()) {
                    onSendMessage()
                    name = ""
                    email = ""
                    subject = ""
                    message = ""
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("বার্তা প্রেরণ করুন")
        }
    }
}

@Composable
fun ContactInfoRow(icon: ImageVector, title: String, info: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = info, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun PrivacyPolicyContent() {
    Column {
        Text(
            text = "গোপনীয়তা নীতি (Privacy Policy)",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Health Guide BD ব্যবহারকারীদের ব্যক্তিগত তথ্যের গোপনীয়তা রক্ষায় সর্বোচ্চ শ্রদ্ধাশীল ও প্রতিজ্ঞাবদ্ধ।",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(14.dp))

        PolicySection(
            title = "১. তথ্যের সংগ্রহ ও ব্যবহার",
            body = "আমাদের নিউজল্যাটার বা যোগাযোগ ফর্ম পূরণ ছাড়া আমরা ব্যবহারকারীর ব্যক্তিগত নাম বা ইমেইল সংগ্রহ করি না। সংগৃহীত ইমেইল শুধুমাত্র স্বাস্থ্য টিপস আপডেট পাঠানোর কাজে ব্যবহৃত হয় এবং কোনো তৃতীয় পক্ষের কাছে বিক্রি বা হস্তান্তর করা হয় না।"
        )
        PolicySection(
            title = "২. কুকিজ (Cookies) ও অ্যানালিটিক্স",
            body = "ওয়েবসাইটের গতিশীলতা ও ব্যবহারকারীর পাঠাভ্যাস বোঝার জন্য সাধারণ ব্রাউজার কুকিজ ব্যবহৃত হতে পারে।"
        )
        PolicySection(
            title = "৩. থার্ড পার্টি বিজ্ঞাপন (Google AdSense)",
            body = "আমাদের ওয়েবসাইটে গুগল অ্যাডসেন্স বিজ্ঞাপন প্রদর্শন করা হতে পারে। গুগল ব্যবহারকারীর আগ্রহ অনুসারে বিজ্ঞাপন পরিবেশনের জন্য কুকি ব্যবহার করতে পারে।"
        )
    }
}

@Composable
fun PolicySection(title: String, body: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = body, style = MaterialTheme.typography.bodyMedium, lineHeight = 22.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun DisclaimerContent() {
    Column {
        Text(
            text = "আইনি ও মেডিকেল দাবিত্যাগ (Medical Disclaimer)",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFB45309)
        )
        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            color = Color(0xFFFEF3C7),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0xFFFDE68A)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "গুরুত্বপূর্ণ ঘোষণা:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF92400E)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Health Guide BD একটি সাধারণ স্বাস্থ্য সচেতনতা ও শিক্ষামূলক পোর্টাল। এখানে প্রকাশিত কোনো নিবন্ধ, টিপস বা মন্তব্য কখনোই সরাসরি চিকিৎসা, রোগ নির্ণয় বা ক্লিনিক্যাল প্রেসক্রিপশনের বিকল্প নয়।",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF78350F),
                    lineHeight = 22.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        BulletItem(text = "কোনো শারীরিক লক্ষণ বা অসুস্থতায় নিজ উদ্যোগে ওষুধ গ্রহণ না করে দ্রুত এমবিবিএস বা বিশেষজ্ঞ ডাক্তারের পরামর্শ নিন।")
        BulletItem(text = "আমরা কোনো প্রকার অলৌকিক চিকিৎসা বা শতভাগ নিরাময়ের ভূয়া গ্যারান্টি প্রদান করি না।")
        BulletItem(text = "জরুরি অবস্থায় ন্যাশনাল হেল্পলাইন ৯৯৯ বা নিকটস্থ জরুরি বিভাগে যোগাযোগ করুন।")
    }
}

@Composable
fun BulletItem(text: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = "✔ ", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 22.sp
        )
    }
}
