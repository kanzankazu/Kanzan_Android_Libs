import android.content.Context
import android.widget.Toast
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus

/**
 * A utility class for loading, checking, and unloading dynamic feature modules
 * in an Android Application using the Play Core Library.
 * It manages module downloading, installing, checking installation status,
 * and uninstalling modules dynamically as needed.
 */
class DynamicFeatureLoader(private val context: Context) {

    private lateinit var splitInstallManager: SplitInstallManager
    private var listener: SplitInstallStateUpdatedListener? = null

    /**
     * Load a dynamic feature module.
     * Downloads and installs the module if not already installed.
     */
    fun loadDynamicFeatureModule(
        moduleName: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        // Initialize the SplitInstallManager
        splitInstallManager = SplitInstallManagerFactory.create(context)

        // Build the request for the module
        val request = SplitInstallRequest.newBuilder()
            .addModule(moduleName)
            .build()

        // Listener to monitor the module download/install progress
        listener = SplitInstallStateUpdatedListener { state ->
            when (state.status()) {
                SplitInstallSessionStatus.PENDING ->
                    Toast.makeText(context, "Download pending...", Toast.LENGTH_SHORT).show()
                SplitInstallSessionStatus.FAILED ->
                    onFailure(Exception("Failed to download module: ${state.errorCode()}"))
                SplitInstallSessionStatus.CANCELED ->
                    Toast.makeText(context, "Download canceled", Toast.LENGTH_SHORT).show()
                SplitInstallSessionStatus.DOWNLOADING -> {
                    val totalBytes = state.totalBytesToDownload()
                    val progress = state.bytesDownloaded()
                    Toast.makeText(context, "Downloading: $progress/$totalBytes bytes", Toast.LENGTH_SHORT).show()
                }
                SplitInstallSessionStatus.INSTALLED -> {
                    Toast.makeText(context, "Module installed", Toast.LENGTH_SHORT).show()
                    onSuccess()
                }

                SplitInstallSessionStatus.CANCELING -> Toast.makeText(context, "Download canceling...", Toast.LENGTH_SHORT).show()
                SplitInstallSessionStatus.DOWNLOADED -> Toast.makeText(context, "Download completed", Toast.LENGTH_SHORT).show()
                SplitInstallSessionStatus.INSTALLING -> Toast.makeText(context, "Installing module...", Toast.LENGTH_SHORT).show()
                SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> Toast.makeText(context, "User confirmation required...", Toast.LENGTH_SHORT).show()
                SplitInstallSessionStatus.UNKNOWN -> Toast.makeText(context, "Unknown status", Toast.LENGTH_SHORT).show()
            }
        }

        // Register the listener
        splitInstallManager.registerListener(listener!!)

        // Request the module installation/download
        splitInstallManager.startInstall(request)
            .addOnSuccessListener {
                Toast.makeText(context, "Download started successfully.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to start download: ${it.message}", Toast.LENGTH_SHORT).show()
                onFailure(it)
            }
    }

    /**
     * Check if a dynamic feature module has already been downloaded.
     *
     * @param moduleName Name of the dynamic module to check.
     * @return True if the module is installed, False otherwise.
     */
    fun isModuleDownloaded(moduleName: String): Boolean {
        // Initialize the SplitInstallManager
        splitInstallManager = SplitInstallManagerFactory.create(context)

        // Get the list of installed modules
        val installedModules = splitInstallManager.installedModules

        // Return true if the module is in the installed modules list
        return installedModules.contains(moduleName)
    }

    /**
     * Uninstall (disable/remove) a dynamic feature module if it is installed.
     *
     * @param moduleName Name of the module to be uninstalled.
     * @param onSuccess Callback when the module is successfully uninstalled.
     * @param onFailure Callback when uninstall attempt fails.
     */
    fun uninstallModule(
        moduleName: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        // Initialize the SplitInstallManager
        splitInstallManager = SplitInstallManagerFactory.create(context)

        // Uninstall the module
        splitInstallManager.deferredUninstall(listOf(moduleName))
            .addOnSuccessListener {
                Toast.makeText(context, "Module $moduleName successfully uninstalled.", Toast.LENGTH_SHORT).show()
                onSuccess()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Failed to uninstall module: ${exception.message}", Toast.LENGTH_SHORT).show()
                onFailure(exception)
            }
    }

    /**
     * Clean up the listener to avoid memory leaks.
     */
    fun cleanUp() {
        listener?.let {
            splitInstallManager.unregisterListener(it)
        }
    }
}