@file:Suppress("MemberVisibilityCanBePrivate")

package com.kanzankazu.kanzanwidget.qrcode.widget

import android.app.Activity
import androidx.fragment.app.FragmentActivity
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.google.zxing.Result
import com.kanzankazu.kanzanutil.kanzanextension.PermissionEnumArray
import com.kanzankazu.kanzanutil.kanzanextension.PermissionState
import com.kanzankazu.kanzanutil.kanzanextension.getPermissionState

/**
 * A wrapper class for the 'com.budiyev.android:code-scanner:2.1.0' library, simplifying QR code scanning.
 *
 * Requires the CAMERA permission. Uses `PermissionUtil` (from `com.sample.core.permission`) for permission handling.
 *  **Ensure the necessary permissions and the `PermissionUtil` are set up in your project.**
 *
 * **Example Usage:**
 *
 * ```kotlin
 * // In your FragmentActivity or Fragment:
 *
 * private lateinit var qrScanner: KanzanQrScanner
 * private lateinit var scannerView: CodeScannerView
 *
 * override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
 *     // ... inflate layout ...
 *     scannerView = view.findViewById(R.id.scanner_view)
 *     qrScanner = KanzanQrScanner(requireActivity(), scannerView, object : KanzanQrScanner.Listener {
 *         override fun successScan(result: Result) {
 *             // Handle successful scan, e.g., display the result.text
 *             Toast.makeText(requireContext(), "Scanned: ${result.text}", Toast.LENGTH_SHORT).show()
 *         }
 *
 *         override fun failedScan(e: Exception) {
 *             // Handle scan failure, e.g., display an error message.
 *             Toast.makeText(requireContext(), "Scan failed: ${e.message}", Toast.LENGTH_SHORT).show()
 *         }
 *     })
 *     return view
 * }
 *
 * override fun onResume() {
 *     super.onResume()
 *     qrScanner.startScan()
 * }
 *
 */
class KanzanQrScanner(
    private val activity: FragmentActivity,
    private val scannerView: CodeScannerView,
    private val listener: Listener,
) {
    private var mCodeScanner: CodeScanner? = null

    init {
        startScan()
        qrScannerInit(activity, scannerView)
    }

    /**
     * place in onStart or onResume
     * */
    fun startScan() {
        if (activity.getPermissionState(PermissionEnumArray.CAMERA_FILE_ACCESS) == PermissionState.GRANTED) {
            mCodeScanner?.startPreview()
        }
    }

    /**
     * place in onPause or onDestroy
     * */
    fun stopScan() {
        mCodeScanner?.stopPreview()
        mCodeScanner?.releaseResources()
    }

    private fun qrScannerInit(activity: Activity, scannerView: CodeScannerView) {
        mCodeScanner = CodeScanner(activity, scannerView)
        mCodeScanner?.setDecodeCallback { result ->
            activity.runOnUiThread {
                try {
                    listener.successScan(result)
                } catch (e: Exception) {
                    mCodeScanner?.startPreview()
                    listener.failedScan(e)
                }
            }
        }
    }

    interface Listener {
        fun successScan(result: Result)
        fun failedScan(e: Exception)
    }
}