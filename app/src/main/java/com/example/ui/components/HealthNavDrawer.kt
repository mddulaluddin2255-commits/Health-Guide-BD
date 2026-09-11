package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Dining
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HealthCategory

@Composable
fun HealthNavDrawerContent(
    currentRoute: String,
    onSelectRoute: (String) -> Unit,
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        modifier = modifier
            .width(310.dp)
            .fillMaxHeight(),
        drawerContainerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.LocalHospital,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Health Guide BD",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "হেলথ গাইড বিডি",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "সুস্থ জীবনের জন্য নির্ভরযোগ্য স্বাস্থ্য তথ্য ও পুষ্টি নির্দেশিকা",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main Menu Item: Home
            DrawerItem(
                label = "হোম (Home)",
                icon = Icons.Default.Home,
                selected = currentRoute == "home",
                onClick = {
                    onSelectRoute("home")
                    onCloseDrawer()
                }
            )

            DrawerItem(
                label = "সংরক্ষিত পোস্ট (Saved)",
                icon = Icons.Default.Bookmark,
                selected = currentRoute == "bookmarks",
                onClick = {
                    onSelectRoute("bookmarks")
                    onCloseDrawer()
                }
            )

            Spacer(modifier = Modifier.height(6.dp))
            SectionDivider(title = "স্বাস্থ্য ক্যাটাগরিসমূহ")

            // Categories
            DrawerCategoryItem("স্বাস্থ্য টিপস (Health Tips)", Icons.Default.HealthAndSafety, "health-tips", currentRoute, onSelectRoute, onCloseDrawer)
            DrawerCategoryItem("পুষ্টি ও খাদ্য (Nutrition)", Icons.Default.Restaurant, "nutrition", currentRoute, onSelectRoute, onCloseDrawer)
            DrawerCategoryItem("ফিটনেস ও ব্যায়াম (Fitness)", Icons.Default.DirectionsRun, "fitness", currentRoute, onSelectRoute, onCloseDrawer)
            DrawerCategoryItem("সুস্থ জীবনযাপন (Lifestyle)", Icons.Default.SelfImprovement, "healthy-lifestyle", currentRoute, onSelectRoute, onCloseDrawer)
            DrawerCategoryItem("নারী স্বাস্থ্য (Women's Health)", Icons.Default.Female, "womens-health", currentRoute, onSelectRoute, onCloseDrawer)
            DrawerCategoryItem("পুরুষ স্বাস্থ্য (Men's Health)", Icons.Default.Male, "mens-health", currentRoute, onSelectRoute, onCloseDrawer)
            DrawerCategoryItem("শিশু স্বাস্থ্য (Children's Health)", Icons.Default.ChildCare, "childrens-health", currentRoute, onSelectRoute, onCloseDrawer)
            DrawerCategoryItem("মানসিক স্বাস্থ্য (Mental Wellness)", Icons.Default.Psychology, "mental-wellness", currentRoute, onSelectRoute, onCloseDrawer)

            Spacer(modifier = Modifier.height(6.dp))
            SectionDivider(title = "তথ্য ও নীতি")

            DrawerItem(
                label = "আমাদের সম্পর্কে (About Us)",
                icon = Icons.Default.Info,
                selected = currentRoute == "page/about",
                onClick = {
                    onSelectRoute("page/about")
                    onCloseDrawer()
                }
            )

            DrawerItem(
                label = "যোগাযোগ (Contact)",
                icon = Icons.Default.Mail,
                selected = currentRoute == "page/contact",
                onClick = {
                    onSelectRoute("page/contact")
                    onCloseDrawer()
                }
            )

            DrawerItem(
                label = "গোপনীয়তা নীতি (Privacy)",
                icon = Icons.Default.Policy,
                selected = currentRoute == "page/privacy",
                onClick = {
                    onSelectRoute("page/privacy")
                    onCloseDrawer()
                }
            )

            DrawerItem(
                label = "মেডিকেল দাবিত্যাগ (Disclaimer)",
                icon = Icons.Default.WarningAmber,
                selected = currentRoute == "page/disclaimer",
                onClick = {
                    onSelectRoute("page/disclaimer")
                    onCloseDrawer()
                }
            )

            Spacer(modifier = Modifier.height(6.dp))
            SectionDivider(title = "অ্যাডমিন পোর্টাল")

            DrawerItem(
                label = "অ্যাডমিন প্যানেল (CMS)",
                icon = Icons.Default.AdminPanelSettings,
                selected = currentRoute == "admin",
                onClick = {
                    onSelectRoute("admin")
                    onCloseDrawer()
                }
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun DrawerItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        label = { Text(label, style = MaterialTheme.typography.bodyMedium, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) },
        icon = { Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(20.dp)) },
        selected = selected,
        onClick = onClick,
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
            selectedIconColor = MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
    )
}

@Composable
fun DrawerCategoryItem(
    label: String,
    icon: ImageVector,
    categorySlug: String,
    currentRoute: String,
    onSelectRoute: (String) -> Unit,
    onCloseDrawer: () -> Unit
) {
    val route = "category/$categorySlug"
    DrawerItem(
        label = label,
        icon = icon,
        selected = currentRoute == route,
        onClick = {
            onSelectRoute(route)
            onCloseDrawer()
        }
    )
}

@Composable
fun SectionDivider(title: String) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    }
}
