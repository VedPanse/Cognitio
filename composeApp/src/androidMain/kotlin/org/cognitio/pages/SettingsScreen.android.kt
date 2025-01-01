package org.cognitio.pages

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import org.cognitio.AppContextProvider

actual fun openUrlInBrowser(url: String) {
    val context: Context = AppContextProvider.getContext()
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // If it's called outside of an activity
    context.startActivity(intent)
}
