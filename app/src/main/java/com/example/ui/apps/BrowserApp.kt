package com.example.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun BrowserApp(
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var urlText by remember { mutableStateOf("https://google.com") }
    var currentWebPage by remember { mutableStateOf<WebPage>(WebPage.Home) }
    var isLoading by remember { mutableStateOf(false) }

    fun loadUrl(url: String) {
        urlText = url
        isLoading = true
    }

    LaunchedEffect(isLoading) {
        if (isLoading) {
            delay(400)
            currentWebPage = when {
                urlText.contains("google", ignoreCase = true) -> WebPage.SearchEngine(urlText)
                urlText.contains("wiki", ignoreCase = true) -> WebPage.Wikipedia
                urlText.contains("news", ignoreCase = true) -> WebPage.TechNews
                urlText.contains("ai", ignoreCase = true) -> WebPage.AiPortal
                else -> WebPage.GenericWeb(urlText)
            }
            isLoading = false
        }
    }

    Surface(
        color = Color(0xFF0F172A),
        contentColor = Color.White,
        modifier = modifier
            .fillMaxSize()
            .testTag("browser_app")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Browser Top Bar
            Surface(
                color = Color(0xFF1E293B),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { loadUrl("https://google.com") },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.Home, contentDescription = "Home", tint = Color.White)
                        }

                        // URL input field
                        OutlinedTextField(
                            value = urlText,
                            onValueChange = { urlText = it },
                            singleLine = true,
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Language,
                                    contentDescription = null,
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = { loadUrl(urlText) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = "Go",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF38BDF8),
                                unfocusedBorderColor = Color(0xFF475569),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Color(0xFF0F172A),
                                unfocusedContainerColor = Color(0xFF0F172A)
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("browser_url_input")
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        IconButton(
                            onClick = { loadUrl(urlText) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Reload", tint = Color.White)
                        }

                        IconButton(
                            onClick = onClose,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                        }
                    }

                    // Bookmarks row
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item { BookmarkChip("Google", "https://google.com") { loadUrl(it) } }
                        item { BookmarkChip("TechNews", "https://technews.io") { loadUrl(it) } }
                        item { BookmarkChip("Wikipedia", "https://wikipedia.org") { loadUrl(it) } }
                        item { BookmarkChip("Cloud AI Hub", "https://ai-hub.cloud") { loadUrl(it) } }
                    }
                }
            }

            if (isLoading) {
                LinearProgressIndicator(
                    color = Color(0xFF38BDF8),
                    trackColor = Color(0xFF1E293B),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Web Page Content
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFF111827))
            ) {
                when (val page = currentWebPage) {
                    is WebPage.Home -> WebHomePage(onSearch = { loadUrl(it) })
                    is WebPage.SearchEngine -> SearchResultsPage(query = page.query, onOpen = { loadUrl(it) })
                    is WebPage.TechNews -> TechNewsPage()
                    is WebPage.Wikipedia -> WikipediaPage()
                    is WebPage.AiPortal -> AiPortalPage()
                    is WebPage.GenericWeb -> GenericPage(page.url)
                }
            }
        }
    }
}

@Composable
private fun BookmarkChip(
    title: String,
    url: String,
    onClick: (String) -> Unit
) {
    Surface(
        color = Color(0xFF334155),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.clickable { onClick(url) }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Bookmark, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = title, fontSize = 11.sp, color = Color.White)
        }
    }
}

private sealed class WebPage {
    object Home : WebPage()
    data class SearchEngine(val query: String) : WebPage()
    object TechNews : WebPage()
    object Wikipedia : WebPage()
    object AiPortal : WebPage()
    data class GenericWeb(val url: String) : WebPage()
}

@Composable
private fun WebHomePage(onSearch: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Cloud Browser",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF38BDF8)
        )
        Text(
            text = "متصفح الويب السريع داخل هاتفك السحابي الوهمي",
            fontSize = 13.sp,
            color = Color.LightGray,
            modifier = Modifier.padding(top = 6.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickWebCard(
                title = "أخبار التقنية",
                desc = "آخر التطورات والذكاء الاصطناعي",
                color = Color(0xFF0284C7),
                modifier = Modifier.weight(1f),
                onClick = { onSearch("https://technews.io") }
            )
            QuickWebCard(
                title = "موسوعة المعرفة",
                desc = "استكشف المقالات والمعلومات",
                color = Color(0xFF7C3AED),
                modifier = Modifier.weight(1f),
                onClick = { onSearch("https://wikipedia.org") }
            )
        }
    }
}

@Composable
private fun QuickWebCard(
    title: String,
    desc: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = desc, fontSize = 11.sp, color = Color(0xFF94A3B8))
        }
    }
}

@Composable
private fun SearchResultsPage(query: String, onOpen: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "نتائج البحث عن: $query",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF38BDF8)
            )
        }

        items(listOf(
            "تقنية الهواتف السحابية الافتراضية 2026" to "https://technews.io/cloud-phones",
            "كيف يعمل محاكي الأندرويد السحابي بأقل زمن استجابة؟" to "https://wikipedia.org/cloud-android",
            "أحدث ميزات نظام Android 15 و Jetpack Compose" to "https://android.dev/compose-m3",
            "حلول الخصوصية والأمان في البيئات الافتراضية" to "https://security.cloud/sandbox"
        )) { (title, link) ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpen(link) }
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = link, fontSize = 10.sp, color = Color(0xFF38BDF8))
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "تجربة متكاملة لتصفح المحتوى والبيانات داخل بيئة سحابية آمنة دون التأثير على النظام الأساسي.",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }
    }
}

@Composable
private fun TechNewsPage() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "⚡ TechNews Today",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF59E0B)
            )
        }

        items(listOf(
            "ثورة الهواتف السحابية: لماذا تنتقل التطبيقات للافتراضية؟" to "قبل 15 دقيقة • تقارير تقنية",
            "معالجات الجيل القادم تدعم محاكاة فائقة السرعة على الهواتف" to "قبل 1 ساعة • أجهزة وعتاد",
            "إطلاق واجهات برمجية جديدة تتيح تشغيل أنظمة متعددة بنقرة واحدة" to "قبل 3 ساعات • مطورو البرمجيات"
        )) { (headline, meta) ->
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = headline, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = meta, fontSize = 10.sp, color = Color(0xFF94A3B8))
                }
            }
        }
    }
}

@Composable
private fun WikipediaPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "موسوعة المعرفة الحرة", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "المحاكاة الافتراضية (Virtualization): هي عملية إنشاء بيئة برمجية تعزل المهام والتطبيقات داخل جهاز وهمي، مما يسمح بتشغيل عدة بيئات عمل مختلفة دون تداخل مع نظام التشغيل المضيف.",
            fontSize = 13.sp,
            color = Color(0xFFE2E8F0),
            lineHeight = 20.sp
        )
    }
}

@Composable
private fun AiPortalPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Cloud AI Gateway", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "نظام الذكاء السحابي متصل ويعمل بسرعة فائقة.", color = Color.LightGray, fontSize = 13.sp)
    }
}

@Composable
private fun GenericPage(url: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.Language, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(48.dp))
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "تم تحميل الصفحة: $url", color = Color.White, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = "تم فتح الموقع بأمان في رمال الحماية السحابية الخاصة بهاتفك الوهمي.", color = Color.Gray, fontSize = 12.sp)
    }
}
