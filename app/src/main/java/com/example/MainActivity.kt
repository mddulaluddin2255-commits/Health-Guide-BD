package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.ui.components.HealthNavDrawerContent
import com.example.ui.components.HealthTopHeader
import com.example.ui.navigation.Screen
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.BookmarksScreen
import com.example.ui.screens.CategoryFeedScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PostDetailScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.StaticPagesScreen
import com.example.ui.theme.HealthGuideBDTheme
import com.example.ui.viewmodel.HealthViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            HealthGuideBDTheme {
                val viewModel: HealthViewModel = viewModel()
                HealthGuideApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun HealthGuideApp(viewModel: HealthViewModel) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route
    val bookmarkedPosts by viewModel.bookmarkedPosts.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            HealthNavDrawerContent(
                currentRoute = currentRoute,
                onSelectRoute = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                        if (route == Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                },
                onCloseDrawer = {
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        val isHomeScreen = currentRoute == Screen.Home.route

        Scaffold(
            topBar = {
                if (isHomeScreen) {
                    HealthTopHeader(
                        onMenuClick = {
                            scope.launch {
                                if (drawerState.isClosed) drawerState.open() else drawerState.close()
                            }
                        },
                        onSearchClick = {
                            navController.navigate(Screen.Search.route)
                        },
                        onBookmarksClick = {
                            navController.navigate(Screen.Bookmarks.route)
                        },
                        onAdminClick = {
                            navController.navigate(Screen.Admin.route)
                        },
                        bookmarkCount = bookmarkedPosts.size
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route
                ) {
                    // 1. Home Screen
                    composable(Screen.Home.route) {
                        HomeScreen(
                            viewModel = viewModel,
                            onPostClick = { slug ->
                                navController.navigate(Screen.PostDetail.createRoute(slug))
                            },
                            onCategoryClick = { categorySlug ->
                                navController.navigate(Screen.CategoryFeed.createRoute(categorySlug))
                            },
                            onNavigatePage = { pageKey ->
                                navController.navigate(Screen.StaticPage.createRoute(pageKey))
                            },
                            onSearchTrigger = {
                                navController.navigate(Screen.Search.route)
                            }
                        )
                    }

                    // 2. Post Detail Screen (with deep linking e.g. https://healthguidebd.com/health-tips/healthy-breakfast)
                    composable(
                        route = Screen.PostDetail.route,
                        arguments = listOf(navArgument("slug") { type = NavType.StringType }),
                        deepLinks = listOf(
                            navDeepLink { uriPattern = "https://healthguidebd.com/{categorySlug}/{slug}" },
                            navDeepLink { uriPattern = "https://healthguidebd.com/post/{slug}" }
                        )
                    ) { backStackEntry ->
                        val slug = backStackEntry.arguments?.getString("slug") ?: ""
                        PostDetailScreen(
                            slug = slug,
                            viewModel = viewModel,
                            onBackClick = { navController.popBackStack() },
                            onNavigatePost = { nextSlug ->
                                navController.navigate(Screen.PostDetail.createRoute(nextSlug))
                            },
                            onNavigatePage = { pageKey ->
                                navController.navigate(Screen.StaticPage.createRoute(pageKey))
                            },
                            onCategoryClick = { catSlug ->
                                navController.navigate(Screen.CategoryFeed.createRoute(catSlug))
                            }
                        )
                    }

                    // 3. Category Feed Screen
                    composable(
                        route = Screen.CategoryFeed.route,
                        arguments = listOf(navArgument("slug") { type = NavType.StringType }),
                        deepLinks = listOf(
                            navDeepLink { uriPattern = "https://healthguidebd.com/{slug}" }
                        )
                    ) { backStackEntry ->
                        val catSlug = backStackEntry.arguments?.getString("slug") ?: ""
                        CategoryFeedScreen(
                            categorySlug = catSlug,
                            viewModel = viewModel,
                            onBackClick = { navController.popBackStack() },
                            onPostClick = { slug ->
                                navController.navigate(Screen.PostDetail.createRoute(slug))
                            },
                            onNavigatePage = { pageKey ->
                                navController.navigate(Screen.StaticPage.createRoute(pageKey))
                            },
                            onCategoryClick = { targetSlug ->
                                navController.navigate(Screen.CategoryFeed.createRoute(targetSlug))
                            }
                        )
                    }

                    // 4. Search Screen
                    composable(Screen.Search.route) {
                        SearchScreen(
                            viewModel = viewModel,
                            onBackClick = { navController.popBackStack() },
                            onPostClick = { slug ->
                                navController.navigate(Screen.PostDetail.createRoute(slug))
                            }
                        )
                    }

                    // 5. Bookmarks Screen
                    composable(Screen.Bookmarks.route) {
                        BookmarksScreen(
                            viewModel = viewModel,
                            onBackClick = { navController.popBackStack() },
                            onPostClick = { slug ->
                                navController.navigate(Screen.PostDetail.createRoute(slug))
                            }
                        )
                    }

                    // 6. Admin CMS Dashboard Screen
                    composable(Screen.Admin.route) {
                        AdminScreen(
                            viewModel = viewModel,
                            onBackClick = { navController.popBackStack() },
                            onViewPostLive = { slug ->
                                navController.navigate(Screen.PostDetail.createRoute(slug))
                            }
                        )
                    }

                    // 7. Static Pages (About, Contact, Privacy, Disclaimer)
                    composable(
                        route = Screen.StaticPage.route,
                        arguments = listOf(navArgument("pageKey") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val pageKey = backStackEntry.arguments?.getString("pageKey") ?: "about"
                        StaticPagesScreen(
                            pageKey = pageKey,
                            onBackClick = { navController.popBackStack() },
                            onNavigatePage = { nextKey ->
                                navController.navigate(Screen.StaticPage.createRoute(nextKey))
                            },
                            onCategoryClick = { catSlug ->
                                navController.navigate(Screen.CategoryFeed.createRoute(catSlug))
                            }
                        )
                    }
                }
            }
        }
    }
}
