package com.kanzankazu.kanzanutil

import android.content.Context
import android.text.Html
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.kanzankazu.databinding.ViewTextTyperBinding
import com.kanzankazu.kanzanutil.kanzanextension.type.formatHtml
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A text typer view that can animate typing and deleting text with multiple display modes.
 * Supports HTML formatting in the text.
 *
 * @property onTextClicked Callback when the text is clicked (only when not typing)
 */
class TextTyper @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), LifecycleObserver {

    private val binding = ViewTextTyperBinding.inflate(LayoutInflater.from(context), this, true)
    private val coroutineScope = CoroutineScope(Dispatchers.Main)
    private var typingJob: Job? = null
    
    // State
    private val _currentIndex = MutableStateFlow(0)
    private val _isTyping = MutableStateFlow(false)
    
    // Config
    private var texts: List<String> = emptyList()
    private var typingDelay: Long = 100 // ms per character
    private var deleteDelay: Long = 50 // ms per character when deleting
    private var pauseDuration: Long = 2000 // ms to pause between texts
    private var isLooping: Boolean = true
    private var isClickableWhenPaused: Boolean = true
    private var currentText: String = ""
    private var currentDisplayedText: String = ""
    
    // Display Mode
    enum class DisplayMode {
        SINGLE_LINE_LOOP,    // Teks berganti dalam satu baris (looping)
        MULTI_LINE,          // Setiap teks di baris baru (tidak looping)
        SINGLE_LINE_NO_LOOP  // Satu baris, tidak looping
    }
    
    private var displayMode: DisplayMode = DisplayMode.SINGLE_LINE_LOOP
    private var currentLineIndex = 0
    private val displayedLines = mutableListOf<String>()
    
    // Callback
    var onTextClicked: ((String) -> Unit)? = null
    
    init {
        setupClickListeners()
        setDisplayMode(DisplayMode.SINGLE_LINE_LOOP)
    }
    
    private fun setupClickListeners() {
        binding.root.setOnClickListener {
            if (!_isTyping.value && isClickableWhenPaused) {
                onTextClicked?.invoke(currentText)
            }
        }
    }
    
    /**
     * Set display mode for the text typer
     * @param mode The display mode to set
     */
    fun setDisplayMode(mode: DisplayMode) {
        this.displayMode = mode
        when (mode) {
            DisplayMode.MULTI_LINE -> {
                binding.tvText.maxLines = Int.MAX_VALUE
                binding.tvText.ellipsize = null
                isLooping = false
            }
            else -> {
                binding.tvText.maxLines = 1
                binding.tvText.ellipsize = TextUtils.TruncateAt.END
                isLooping = mode == DisplayMode.SINGLE_LINE_LOOP
            }
        }
        resetAndStart()
    }
    
    private fun resetAndStart() {
        stopTyping()
        currentLineIndex = 0
        displayedLines.clear()
        startTyping()
    }
    
    /**
     * Set list of texts to be displayed
     * @param texts List of strings that can contain HTML
     */
    fun setTexts(texts: List<String>) {
        this.texts = texts
        resetAndStart()
    }
    
    /**
     * Configure typing animation
     * @param typingDelay Delay between typing each character in milliseconds
     * @param deleteDelay Delay between deleting each character in milliseconds
     * @param pauseDuration Duration to pause between texts in milliseconds
     * @param isClickableWhenPaused Whether the view is clickable when typing is paused
     */
    fun configure(
        typingDelay: Long = 100,
        deleteDelay: Long = 50,
        pauseDuration: Long = 2000,
        isClickableWhenPaused: Boolean = true
    ) {
        this.typingDelay = typingDelay
        this.deleteDelay = deleteDelay
        this.pauseDuration = pauseDuration
        this.isClickableWhenPaused = isClickableWhenPaused
    }
    
    private fun startTyping() {
        if (texts.isEmpty()) return
        
        typingJob?.cancel()
        typingJob = coroutineScope.launch {
            when (displayMode) {
                DisplayMode.SINGLE_LINE_LOOP -> startSingleLineLoop()
                DisplayMode.SINGLE_LINE_NO_LOOP -> startSingleLineNoLoop()
                DisplayMode.MULTI_LINE -> startMultiLine()
            }
        }
    }
    
    private suspend fun startSingleLineLoop() {
        while (true) {
            for (text in texts) {
                currentText = text
                typeText(text)
                delay(pauseDuration)
                deleteText(text)
            }
        }
    }
    
    private suspend fun startSingleLineNoLoop() {
        for (text in texts) {
            currentText = text
            typeText(text)
            delay(pauseDuration)
            deleteText(text)
        }
    }
    
    private suspend fun startMultiLine() {
        for ((index, text) in texts.withIndex()) {
            currentText = text
            displayedLines.add("")
            currentLineIndex = index
            typeText(text)
            delay(pauseDuration)
            // Add new line except for the last item
            if (index < texts.size - 1) {
                displayedLines[index] = text
                updateMultiLineText()
            }
        }
    }
    
    private suspend fun typeText(text: String) {
        _isTyping.value = true
        currentDisplayedText = ""
        for (i in text.indices) {
            currentDisplayedText = text.take(i + 1)
            updateText(currentDisplayedText)
            delay(typingDelay)
        }
        _isTyping.value = false
    }
    
    private suspend fun deleteText(text: String) {
        _isTyping.value = true
        for (i in text.length downTo 0) {
            currentDisplayedText = text.take(i)
            updateText(currentDisplayedText)
            delay(deleteDelay)
        }
        _isTyping.value = false
    }
    
    private suspend fun updateText(text: String) {
        withContext(Dispatchers.Main) {
            when (displayMode) {
                DisplayMode.MULTI_LINE -> {
                    if (currentLineIndex < displayedLines.size) {
                        displayedLines[currentLineIndex] = text
                    }
                    updateMultiLineText()
                }
                else -> {
                    binding.tvText.text = text.formatHtml()
                }
            }
        }
    }
    
    private fun updateMultiLineText() {
        val htmlText = displayedLines.joinToString("<br>") { line ->
            if (line.isEmpty()) "<br>" else line
        }
        binding.tvText.text = htmlText.formatHtml()
    }
    
    /**
     * Stop the typing animation
     */
    fun stopTyping() {
        typingJob?.cancel()
        _isTyping.value = false
    }
    
    /**
     * Pause the typing animation at current state
     */
    fun pauseTyping() {
        typingJob?.cancel()
        _isTyping.value = false
    }
    
    /**
     * Resume typing animation from current state
     */
    fun resumeTyping() {
        if (!_isTyping.value) {
            startTyping()
        }
    }
    
    /**
     * Clear the current text and stop animation
     */
    fun clear() {
        stopTyping()
        binding.tvText.text = ""
        currentText = ""
        currentDisplayedText = ""
        displayedLines.clear()
    }
    
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        pauseTyping()
    }
    
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        if (currentText.isNotEmpty() || currentDisplayedText.isNotEmpty()) {
            resumeTyping()
        }
    }
    
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        stopTyping()
    }
}
