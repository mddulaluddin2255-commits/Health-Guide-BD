package com.example.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object PostDetail : Screen("post/{slug}") {
        fun createRoute(slug: String) = "post/$slug"
    }
    object CategoryFeed : Screen("category/{slug}") {
        fun createRoute(slug: String) = "category/$slug"
    }
    object Search : Screen("search")
    object Bookmarks : Screen("bookmarks")
    object Admin : Screen("admin")
    object StaticPage : Screen("page/{pageKey}") {
        fun createRoute(pageKey: String) = "page/$pageKey"
    }
}
