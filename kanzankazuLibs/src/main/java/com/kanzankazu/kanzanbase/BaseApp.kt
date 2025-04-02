package com.kanzankazu.kanzanbase

import androidx.multidex.MultiDexApplication

/**
 * BaseApp: Kelas utama yang bertindak sebagai basis untuk aplikasi.
 *
 * Kelas ini diturunkan oleh aplikasi lain dalam proyek dan berfungsi untuk,
 * misalnya, pengaturan global, third-party initialization, maupun lifecycle manager di tingkat aplikasi.
 *
 * @author Faisal Bahri
 * @created 2020-02-11
 */
abstract class BaseApp : MultiDexApplication()
