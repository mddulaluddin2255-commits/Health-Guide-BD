package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Health Guide BD", appName)
  }

  @Test
  fun `permanent URL formats correctly`() {
    val post = com.example.data.model.HealthPost(
        id = 1L,
        slug = "healthy-breakfast",
        title = "সকালের স্বাস্থ্যকর নাস্তা",
        shortDescription = "পুষ্টিকর সকালের নাস্তার ৫টি গুরুত্বপূর্ণ টিপস",
        categorySlug = "health-tips",
        categoryNameBn = "স্বাস্থ্য টিপস",
        content = "বিস্তারিত স্বাস্থ্য টিপস",
        featuredImageUrl = "https://example.com/image.jpg",
        author = "ডাঃ রাফিদ হাসান",
        publishedDate = "১১ সেপ্টেম্বর, ২০২৬",
        tags = "নাস্তা, স্বাস্থ্য"
    )
    assertEquals("https://healthguidebd.com/health-tips/healthy-breakfast", post.permanentUrl)
  }
}
