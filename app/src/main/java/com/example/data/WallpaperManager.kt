package com.example.data

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.R

data class WallpaperTheme(
    val id: Int,
    val name: String,
    val arabicName: String,
    val drawableResId: Int? = null,
    val gradientBrush: Brush? = null,
    val previewColor: Color
)

object WallpaperManager {
    fun getWallpapers(): List<WallpaperTheme> {
        return listOf(
            WallpaperTheme(
                id = 0,
                name = "Aurora Cyber",
                arabicName = "شفق سايبر",
                drawableResId = R.drawable.phone_wallpaper_aurora_1791434984339,
                previewColor = Color(0xFF6366F1)
            ),
            WallpaperTheme(
                id = 1,
                name = "Cosmic Marble",
                arabicName = "رخام كوني",
                drawableResId = R.drawable.phone_art_abstract_1791434997382,
                previewColor = Color(0xFF06B6D4)
            ),
            WallpaperTheme(
                id = 2,
                name = "Midnight Nebula",
                arabicName = "سديم منتصف الليل",
                gradientBrush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E1B4B),
                        Color(0xFF311042),
                        Color(0xFF090D16)
                    )
                ),
                previewColor = Color(0xFF311042)
            ),
            WallpaperTheme(
                id = 3,
                name = "Cyberpunk Emerald",
                arabicName = "زمرد سيبراني",
                gradientBrush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF022C22),
                        Color(0xFF064E3B),
                        Color(0xFF065F46),
                        Color(0xFF041813)
                    )
                ),
                previewColor = Color(0xFF10B981)
            ),
            WallpaperTheme(
                id = 4,
                name = "Sunset Horizon",
                arabicName = "أفق الغروب",
                gradientBrush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF431407),
                        Color(0xFF7C2D12),
                        Color(0xFF9A3412),
                        Color(0xFF1E0A05)
                    )
                ),
                previewColor = Color(0xFFEA580C)
            ),
            WallpaperTheme(
                id = 5,
                name = "Deep AMOLED Black",
                arabicName = "أسود فائق العمق",
                gradientBrush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A0A0C),
                        Color(0xFF11141B),
                        Color(0xFF050508)
                    )
                ),
                previewColor = Color(0xFF1E293B)
            )
        )
    }

    fun getWallpaper(id: Int): WallpaperTheme {
        val list = getWallpapers()
        return list.find { it.id == id } ?: list.first()
    }
}
