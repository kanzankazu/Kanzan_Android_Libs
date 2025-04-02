package com.kanzankazu.kanzanutil

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.kanzankazu.kanzanutil.kanzanextension.getLaunch
import kotlinx.coroutines.GlobalScope.coroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterIsInstance

/**
 * Provides utility functions for publishing and subscribing to events using `MutableSharedFlow`.
 * This helper allows handling events with different replay configurations and ensures proper
 * event delivery in various scenarios, such as nested fragments.
 *
 * `EventBusHelper` includes support for three types of event flows:
 * - `events`: Standard event flow with no replay.
 * - `eventsReplay`: Event flow with a replay buffer size of 1, ensuring new subscribers receive recent events.
 * - `eventsUnlimited`: Event flow with no replay limit.
 *
 * All functions are suspendable, so they should be called within a coroutine context.
 */
object EventBusHelper {
    //prevent event subscribe not call in nested fragment (init replay value, default = 0)
    //https://stackoverflow.com/questions/70963343/why-is-the-collect-of-a-flow-in-a-nested-fragment-viewmodel-not-called
    private val _eventsReplay = MutableSharedFlow<Any>(replay = 1)
    val eventsReplay = _eventsReplay.asSharedFlow()

    private val _events = MutableSharedFlow<Any>()
    val events = _events.asSharedFlow()

    private val _eventsUnlimited = MutableSharedFlow<Any>()
    val eventsUnlimited = _eventsUnlimited.asSharedFlow()

    /**
     * Publishes the given event to all subscribers by emitting it to the internal event stream.
     *
     * @param event The event object to be published. This can be any type of object representing the event to be dispatched.
     *
     * Example:
     * ```kotlin
     * publish("New Event") // Publishes a new string event.
     * publish(CustomEventObject()) // Publishes a custom event object.
     * ```
     */
    suspend fun publish(event: Any) {
        _events.emit(event)
    }

    /**
     * Subscribes to a specific type of events within a flow of events.
     * Filters events to only include instances of the specified type and processes them using the provided callback.
     *
     * @param onEvent A lambda function to handle the filtered events of type T.
     *                This callback will be triggered whenever an event of type T is emitted.
     *
     * Example:
     * ```kotlin
     * subscribe<MyEvent> { event ->
     *     // Handle the event here
     *     println("Received event: $event")
     * }
     * ```
     */
    suspend inline fun <reified T> subscribe(crossinline onEvent: (T) -> Unit) {
        events.filterIsInstance<T>()
            .collectLatest { event ->
                coroutineContext.ensureActive()
                onEvent(event)
            }
    }

    /**
     * Publishes the given event to a shared replay channel, making it available for subscribers.
     *
     * @param event The event to be emitted into the replay channel. Typically used to notify subscribers of a new update or action.
     *
     * Example:
     * ```kotlin
     * val myEvent = "UserLoggedIn"
     * publishEventWithReplay(myEvent)
     * ```
     */
    suspend fun publishEventWithReplay(event: Any) {
        _eventsReplay.emit(event)
    }

    /**
     * Subscribes to a stream of events of a specific type and processes them using the provided event handler.
     * This method uses a replay mechanism to collect the most recent relevant events.
     *
     * @param onEvent A lambda function that handles events of type `T`. It is invoked for each collected event
     *                of the specified type.
     *
     * Example:
     * ```kotlin
     * subscribeWithReplay<MyEventType> { event ->
     *     // Handle the event here
     *     println("Event received: $event")
     * }
     * ```
     */
    suspend inline fun <reified T> subscribeWithReplay(crossinline onEvent: (T) -> Unit) {
        eventsReplay.filterIsInstance<T>()
            .collectLatest { event ->
                coroutineContext.ensureActive()
                onEvent(event)
            }
    }

    /**
     * Publishes an event with no limit on the number of emissions. The provided event is emitted
     * to an underlying flow for further processing or observation.
     *
     * @param event The event to be published. Can be of any type (`Any`).
     *
     * Example:
     * ```kotlin
     * val myEvent = "New Data Available"
     * publishUnlimitedEvents(myEvent)
     * ```
     */
    suspend fun publishUnlimitedEvents(event: Any) {
        _eventsUnlimited.emit(event)
    }

    /**
     * Subscribes to an unlimited stream of events of a specific type `T`. The method uses a coroutine-based flow
     * to filter events of type `T` from an existing event stream and triggers the provided callback for each emitted event.
     * This method ensures that the event handling coroutine remains active and processes events until explicitly cancelled.
     *
     * @param onEvent A lambda function that gets invoked with each event of type `T`.
     *                It processes the filtered events from the stream.
     *
     * Example:
     * ```kotlin
     * // Example of subscribing to events of type String
     * subscribeUnlimitedEvents<String> { event ->
     *     println("Received event: $event")
     * }
     * ```
     */
    suspend inline fun <reified T> subscribeUnlimitedEvents(crossinline onEvent: (T) -> Unit) {
        eventsUnlimited.filterIsInstance<T>()
            .collect { event ->
                coroutineContext.ensureActive()
                onEvent(event)
            }
    }
}

/**
 * Subscribes to an event bus for events of type `T` within the lifecycle of a FragmentActivity,
 * ensuring the subscription only occurs while the activity is in the `CREATED` state or higher.
 * The provided lambda function `onEvent` will be triggered whenever an event of the specified type is emitted.
 *
 * @param onEvent A lambda function to handle the events of type `T`.
 *                This function will be called each time an event of type `T` is received.
 *
 * Example:
 * ```kotlin
 * class MyActivity : FragmentActivity() {
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         super.onCreate(savedInstanceState)
 *
 *         subscribeEventBusHelper<MyEvent> { event ->
 *             // Handle the event
 *             println("Received: $event")
 *         }
 *     }
 * }
 * ```
 */
inline fun <reified T> FragmentActivity.subscribeEventBusHelper(crossinline onEvent: (T) -> Unit) {
    getLaunch {
        repeatOnLifecycle(Lifecycle.State.CREATED) {
            EventBusHelper.subscribe(onEvent)
        }
    }
}

/**
 * Subscribes to events of type T using the EventBusHelper within the lifecycle of the Fragment, starting from the CREATED state.
 * The provided callback function is executed whenever an event of type T is received.
 *
 * @param onEvent A lambda function that handles the emitted events of type T.
 *                This callback will be triggered when the EventBus emits an event of the specified type.
 *
 * Example:
 * ```kotlin
 * class MyFragment : Fragment() {
 *     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
 *         super.onViewCreated(view, savedInstanceState)
 *
 *         subscribeEventBusHelper<MyEvent> { event ->
 *             // Handle the received event
 *             println("Received Event: $event")
 *         }
 *     }
 * }
 * ```
 */
inline fun <reified T> Fragment.subscribeEventBusHelper(crossinline onEvent: (T) -> Unit) {
    getLaunch {
        repeatOnLifecycle(Lifecycle.State.CREATED) {
            EventBusHelper.subscribe(onEvent)
        }
    }
}
