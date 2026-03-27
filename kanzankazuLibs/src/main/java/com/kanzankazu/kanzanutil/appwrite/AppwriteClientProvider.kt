package com.kanzankazu.kanzanutil.appwrite

import android.content.Context
import io.appwrite.Client
import io.appwrite.services.Account
import io.appwrite.services.Databases
import io.appwrite.services.Functions
import io.appwrite.services.Realtime
import io.appwrite.services.Storage

object AppwriteClientProvider {
    private var client: Client? = null
    private var account: Account? = null
    private var databases: Databases? = null
    private var storage: Storage? = null
    private var realtime: Realtime? = null
    private var functions: Functions? = null

    fun initialize(context: Context, config: AppwriteConfig) {
        require(config.endpoint.isNotBlank()) { "Appwrite endpoint must not be blank" }
        require(config.projectId.isNotBlank()) { "Appwrite project ID must not be blank" }

        val appwriteClient = Client(context)
            .setEndpoint(config.endpoint)
            .setProject(config.projectId)

        client = appwriteClient
        account = Account(appwriteClient)
        databases = Databases(appwriteClient)
        storage = Storage(appwriteClient)
        realtime = Realtime(appwriteClient)
        functions = Functions(appwriteClient)
    }

    fun getClient(): Client {
        return client ?: throw IllegalStateException(
            "AppwriteClient has not been initialized. Call AppwriteClientProvider.initialize() first."
        )
    }

    fun getAccount(): Account {
        return account ?: throw IllegalStateException(
            "AppwriteClient has not been initialized. Call AppwriteClientProvider.initialize() first."
        )
    }

    fun getDatabases(): Databases {
        return databases ?: throw IllegalStateException(
            "AppwriteClient has not been initialized. Call AppwriteClientProvider.initialize() first."
        )
    }

    fun getStorage(): Storage {
        return storage ?: throw IllegalStateException(
            "AppwriteClient has not been initialized. Call AppwriteClientProvider.initialize() first."
        )
    }

    fun getRealtime(): Realtime {
        return realtime ?: throw IllegalStateException(
            "AppwriteClient has not been initialized. Call AppwriteClientProvider.initialize() first."
        )
    }

    fun getFunctions(): Functions {
        return functions ?: throw IllegalStateException(
            "AppwriteClient has not been initialized. Call AppwriteClientProvider.initialize() first."
        )
    }

    fun isInitialized(): Boolean = client != null
}
