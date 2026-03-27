package com.kanzankazu.kanzandatabase.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.functions.Functions
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage

object SupabaseClientProvider {
    private var client: SupabaseClient? = null

    fun initialize(config: SupabaseConfig) {
        require(config.supabaseUrl.isNotBlank()) { "Supabase URL must not be blank" }
        require(config.supabaseAnonKey.isNotBlank()) { "Supabase Anon Key must not be blank" }

        client = createSupabaseClient(
            supabaseUrl = config.supabaseUrl,
            supabaseKey = config.supabaseAnonKey
        ) {
            install(Postgrest)
            install(Auth)
            install(Storage)
            install(Realtime)
            install(Functions)
        }
    }

    fun getClient(): SupabaseClient {
        return client ?: throw IllegalStateException(
            "SupabaseClient has not been initialized. Call SupabaseClientProvider.initialize() first."
        )
    }

    fun isInitialized(): Boolean = client != null
}
