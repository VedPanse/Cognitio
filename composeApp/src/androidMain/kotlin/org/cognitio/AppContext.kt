package org.cognitio

import android.content.Context

object AppContextProvider {
    private var appContext: Context? = null

    fun initialize(context: Context) {
        if (appContext == null) {
            appContext = context.applicationContext
        }
    }

    fun getContext(): Context {
        return appContext ?: throw IllegalStateException("AppContextProvider is not initialized")
    }
}
