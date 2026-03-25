package com.kanzankazu.kanzanwidget.compose.widget

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kanzankazu.kanzanwidget.compose.ui.AppTextStyle
import com.kanzankazu.kanzanwidget.compose.ui.dp4
import com.kanzankazu.kanzanwidget.compose.ui.dp8
import com.kanzankazu.kanzanwidget.compose.ui.dp16
import com.kanzankazu.kanzanwidget.compose.ui.dp48
import com.kanzankazu.kanzanwidget.compose.ui.dp80
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest

// region ==================== Cache Manager ====================

/**
 * Simple image cache manager menggunakan SharedPreferences untuk tracking
 * dan file system untuk storage. Cache dihapus otomatis per hari.
 */
object KanzanImageCacheManager {
    private const val PREF_NAME = "kanzan_image_cache"
    private const val KEY_LAST_CLEAR = "last_clear_timestamp"
    private const val ONE_DAY_MS = 24 * 60 * 60 * 1000L

    private fun getPrefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    private fun getCacheDir(context: Context): File =
        File(context.cacheDir, "kanzan_images").also { if (!it.exists()) it.mkdirs() }

    /**
     * Cek dan hapus cache jika sudah lewat 1 hari.
     */
    fun clearIfExpired(context: Context) {
        val prefs = getPrefs(context)
        val lastClear = prefs.getLong(KEY_LAST_CLEAR, 0L)
        val now = System.currentTimeMillis()
        if (now - lastClear > ONE_DAY_MS) {
            clearCache(context)
            prefs.edit().putLong(KEY_LAST_CLEAR, now).apply()
        }
    }

    fun clearCache(context: Context) {
        getCacheDir(context).listFiles()?.forEach { it.delete() }
    }

    fun getCachedFile(context: Context, url: String): File? {
        val file = File(getCacheDir(context), urlToFileName(url))
        return if (file.exists()) file else null
    }

    fun saveToCacheFile(context: Context, url: String): File =
        File(getCacheDir(context), urlToFileName(url))

    private fun urlToFileName(url: String): String {
        val md = MessageDigest.getInstance("MD5")
        val hash = md.digest(url.toByteArray()).joinToString("") { "%02x".format(it) }
        val ext = url.substringAfterLast('.', "jpg").take(4)
        return "$hash.$ext"
    }
}
// endregion

// region ==================== KanzanImageLoader ====================

/**
 * Komponen generic untuk load gambar dari URL dengan local cache.
 * Cache disimpan di file system dan di-track via SharedPreferences.
 * Cache otomatis dihapus per hari.
 *
 * @param url URL gambar.
 * @param modifier Modifier.
 * @param contentDescription deskripsi gambar.
 * @param size ukuran gambar (null = sesuai modifier).
 * @param shape bentuk clip gambar.
 * @param contentScale skala konten gambar.
 * @param placeholder composable placeholder saat loading.
 * @param errorContent composable saat gagal load.
 * @param showShimmer tampilkan shimmer saat loading.
 * @param onClick callback klik gambar.
 * @param crossfade animasi crossfade saat gambar muncul.
 * @param enableCache aktifkan cache (default true).
 */
@Composable
fun KanzanImageLoader(
    url: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    size: Dp? = null,
    shape: Shape = RoundedCornerShape(dp8),
    contentScale: ContentScale = ContentScale.Crop,
    placeholder: @Composable (() -> Unit)? = null,
    errorContent: @Composable (() -> Unit)? = null,
    showShimmer: Boolean = true,
    onClick: (() -> Unit)? = null,
    crossfade: Boolean = true,
    enableCache: Boolean = true,
) {
    val context = LocalContext.current
    var bitmap by remember(url) { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember(url) { mutableStateOf(true) }
    var isError by remember(url) { mutableStateOf(false) }

    LaunchedEffect(url) {
        if (url.isBlank()) {
            isError = true
            isLoading = false
            return@LaunchedEffect
        }

        isLoading = true
        isError = false

        // Clear expired cache
        if (enableCache) KanzanImageCacheManager.clearIfExpired(context)

        withContext(Dispatchers.IO) {
            try {
                // Check cache first
                if (enableCache) {
                    val cached = KanzanImageCacheManager.getCachedFile(context, url)
                    if (cached != null) {
                        bitmap = BitmapFactory.decodeFile(cached.absolutePath)
                        if (bitmap != null) {
                            isLoading = false
                            return@withContext
                        }
                    }
                }

                // Download
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.connectTimeout = 10_000
                connection.readTimeout = 15_000
                connection.doInput = true
                connection.connect()

                val inputStream = connection.inputStream
                val downloadedBitmap = BitmapFactory.decodeStream(inputStream)
                inputStream.close()
                connection.disconnect()

                if (downloadedBitmap != null) {
                    // Save to cache
                    if (enableCache) {
                        val cacheFile = KanzanImageCacheManager.saveToCacheFile(context, url)
                        FileOutputStream(cacheFile).use { out ->
                            downloadedBitmap.compress(Bitmap.CompressFormat.PNG, 90, out)
                        }
                    }
                    bitmap = downloadedBitmap
                    isLoading = false
                } else {
                    isError = true
                    isLoading = false
                }
            } catch (e: Exception) {
                isError = true
                isLoading = false
            }
        }
    }

    val sizeModifier = if (size != null) modifier.size(size) else modifier
    val clickModifier = if (onClick != null) sizeModifier.clickable { onClick() } else sizeModifier
    val finalModifier = clickModifier.clip(shape)

    when {
        isLoading -> {
            if (placeholder != null) {
                placeholder()
            } else if (showShimmer) {
                KanzanImageShimmer(modifier = finalModifier, size = size)
            } else {
                Box(modifier = finalModifier.background(Color(0xFFE0E0E0)))
            }
        }
        isError -> {
            errorContent?.invoke() ?: Box(
                modifier = finalModifier.background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🖼️", style = AppTextStyle.nunito_regular_24)
            }
        }
        bitmap != null -> {
            Image(
                bitmap = bitmap!!.asImageBitmap(),
                contentDescription = contentDescription,
                modifier = finalModifier,
                contentScale = contentScale,
            )
        }
    }
}

@Composable
private fun KanzanImageShimmer(modifier: Modifier, size: Dp?) {
    val transition = rememberInfiniteTransition(label = "imgShimmer")
    val shimmerX by transition.animateFloat(
        initialValue = -300f, targetValue = 900f,
        animationSpec = infiniteRepeatable(tween(1200), RepeatMode.Restart),
        label = "shimmerX"
    )
    val brush = Brush.linearGradient(
        colors = listOf(Color(0xFFE0E0E0), Color(0xFFF5F5F5), Color(0xFFE0E0E0)),
        start = Offset(shimmerX, 0f),
        end = Offset(shimmerX + 300f, 0f)
    )
    Box(modifier = modifier.then(if (size != null) Modifier.size(size) else Modifier).background(brush))
}
// endregion

// region ==================== Preview ====================

@Preview(showBackground = true, name = "Image 1. Loading shimmer")
@Composable
private fun PreviewImageShimmer() {
    KanzanImageShimmer(
        modifier = Modifier.size(dp80).clip(RoundedCornerShape(dp8)),
        size = dp80
    )
}

@Preview(showBackground = true, name = "Image 2. Error state")
@Composable
private fun PreviewImageError() {
    Box(
        modifier = Modifier.size(dp80).clip(RoundedCornerShape(dp8)).background(Color(0xFFF5F5F5)),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "🖼️", style = AppTextStyle.nunito_regular_24)
    }
}

@Preview(showBackground = true, name = "Image 3. Circle avatar placeholder")
@Composable
private fun PreviewImageCircle() {
    KanzanImageShimmer(
        modifier = Modifier.size(dp48).clip(CircleShape),
        size = dp48
    )
}

@Preview(showBackground = true, name = "Image 4. Gallery row")
@Composable
private fun PreviewImageGallery() {
    Row(
        modifier = Modifier.padding(dp16),
        horizontalArrangement = Arrangement.spacedBy(dp8)
    ) {
        repeat(4) {
            KanzanImageShimmer(
                modifier = Modifier.size(dp80).clip(RoundedCornerShape(dp8)),
                size = dp80
            )
        }
    }
}

// endregion
