package com.kanzankazu.kanzanutil.kanzanextension.type

import com.google.gson.Gson

/**
 * Creates a lazy-initialized delegate that uses the `LazyThreadSafetyMode.NONE` mode, meaning it does not provide any thread safety guarantees.
 * The initializer function is invoked lazily the first time the value is accessed.
 *
 * @param initializer A lambda function that defines how the value should be initialized when accessed for the first time.
 * @return A `Lazy` instance that initializes the value using the provided initializer when accessed in an unsafe thread mode.
 *
 * Example:
 * ```kotlin
 * val lazyValue = lazyNone { computeExpensiveValue() }
 * println(lazyValue.value) // Calls computeExpensiveValue() once, then caches the result
 * ```
 */
fun <T> lazyNone(initializer: () -> T): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE, initializer)
}

/**
 * Converts a given object of a specified class to its JSON string representation using the Gson library.
 *
 * @param objectClass The class of the object to be converted to JSON. May be null.
 * @return A JSON string representation of the object. If the input class is null, it returns an empty JSON object "{}".
 *
 * Example:
 * ```kotlin
 * val jsonString = object2Json(YourClass::class.java) // Result: {"property1":"value1","property2":value2...}
 * ```
 */
fun <T> object2Json(objectClass: Class<T>?): String {
    val gson = Gson()
    return gson.toJson(objectClass)
}

/**
 * Converts a JSON string representation into an object of the specified type.
 *
 * @param T The type of the object to deserialize the JSON string into.
 * @param objectClass The class of the type `T` that the JSON string should be converted to.
 * @return An object of type `T` created from the JSON string.
 *
 * Example:
 * ```kotlin
 * val jsonString = """{"name": "John", "age": 30}"""
 * val person = jsonString.json2Object(Person::class.java)
 * println(person.name) // Output: John
 * ```
 */
fun <T> String.json2Object(objectClass: Class<T>): T {
    val gson = Gson()
    return gson.fromJson(this, objectClass)
}

/**
 * Applies the given listener function to the object instance it is called upon.
 * This function allows invoking a block of code on the object instance, providing
 * it within the scope of the lambda, and ensuring clean readability.
 *
 * @param listener A lambda function (receiver) that is executed with the object
 *                 as its receiver. The function utilizes `T.() -> Unit` to interact
 *                 with the current instance inside the block.
 *
 * Usage Example:
 * ```kotlin
 * val number = 10
 * number.use {
 *     println("The number is $this") // Output: The number is 10
 * }
 *
 * val list = mutableListOf(1, 2, 3)
 * list.use {
 *     add(4)
 *     println("Updated list: $this") // Output: Updated list: [1, 2, 3, 4]
 * }
 * ```
 */
fun <T> T.use(listener: T.() -> Unit) = listener.invoke(this)

/**
 * Executes the given lambda `listener` using the current object (`this`) as the receiver
 * and returns the result produced by the lambda function.
 *
 * @param listener A lambda function to be executed with the current object as its receiver.
 *                 It should take the current object as a receiver context (using `this`)
 *                 and return a value of type R.
 * @return The result of executing the given lambda function.
 *
 * Example:
 * ```kotlin
 * val result = "Hello".useReturn { this + ", World!" }
 * println(result) // Output: Hello, World!
 *
 * val result2 = 10.useReturn { this * 2 }
 * println(result2) // Output: 20
 * ```
 */
fun <T, R> T.useReturn(listener: T.() -> R) = listener.invoke(this)

//fun Any?.isDataClass(): Boolean {
//    return this?.let {
//        it.javaClass.declaredAnnotations.any { annotation ->
//            annotation.annotationClass.simpleName == "Data"
//        } || it.javaClass.kotlin.isData
//    } ?: false
//}
