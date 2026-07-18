package com.example

object AppData {
    val updatePackage = UpdatePackage(
        version = "5.2.1-TV",
        build = "SSO-2026.07.15-r2",
        size = "684 MB",
        channel = "Stable OTA",
        checksum = "SHA-256 verified after download",
        description = "Security patches, Android TV WebView compatibility fixes, launcher stability improvements, and OTA client hardening for Android 5+ smart screens.",
        changelog = listOf(
            "Updated security patch baseline for Android Smart Screen devices.",
            "Improved OTA verification and interrupted-download recovery flow.",
            "Added compatibility checks for Android TV launcher and media packages.",
            "Fixed standby wake-up behavior during scheduled update checks."
        )
    )

    val deviceInfo = DeviceInfo(
        model = "BSN32100S",
        manufacturer = "BESTON",
        currentVersion = "5.1.0-TV",
        androidLabel = "Android 5.0.1+ compatible",
        buildNumber = "SSO-2026.05.03-r8",
        apiLevel = android.os.Build.VERSION.SDK_INT,
        storage = "3.8 GB available",
        power = "AC power connected",
        network = "Wi‑Fi online"
    )

    val managedLimitations = listOf(
        "Managed Expo JavaScript cannot execute su/root commands.",
        "System package removal, replacement, or OTA flashing requires an OEM updater, Device Owner/MDM policy, native Android module, or privileged companion service.",
        "This screen prepares auditable dry-runs and compatibility checklists only; it does not modify the system partition."
    )

    val packageInventory = listOf(
        PackageItem("launcher", "com.smarttv.launcher", "Smart TV Launcher", "privileged", "3.4.8", "3.5.2", "Critical", "Leanback UI and Android 5+ verified", "Never remove without a fallback launcher and remote-control path."),
        PackageItem("ota", "com.smarttv.ota", "OTA Update Client", "system", "2.9.1", "3.0.0", "High", "Requires OEM signature and recovery package validation", "Replacing this package can break future firmware delivery."),
        PackageItem("media", "com.smarttv.media.service", "Media Service", "system", "7.12.0", "7.13.4", "Medium", "Codec profile and API 21 compatibility confirmed", "Validate HDMI, DRM, and standby playback before rollout."),
        PackageItem("webview", "com.android.webview", "Android System WebView", "system", "95.0.4638", "103.0.5060", "Medium", "Min SDK supports Android 5, signature must match policy", "Use vendor-approved builds for Android TV firmware images."),
        PackageItem("input", "com.smarttv.remote.input", "Remote Input Service", "privileged", "1.8.6", "1.9.0", "Critical", "Remote key map and accessibility service checked", "Disabling can lock administrators out of the device.")
    )

    val ftaChannels = listOf(
        FtaChannel("ajz", "Al Jazeera", "Nilesat 201 · 7°W", "News", true),
        FtaChannel("f24", "France 24 English", "Nilesat 201 · 7°W", "News", true),
        FtaChannel("dw", "DW English", "Hotbird · 13°E", "News", true),
        FtaChannel("trt", "TRT World", "Turksat · 42°E", "News", true),
        FtaChannel("nhk", "NHK World Japan", "Hotbird · 13°E", "News", true),
        FtaChannel("makkah", "Makkah TV", "Badr · 26°E", "General", true),
        FtaChannel("sahel", "Sahel Sports (FTA)", "Eutelsat · 7°W", "Sports", true)
    )

    val initialHistory = listOf(
        HistoryItem("seed-1", "2026-07-01 09:42", "Firmware 5.1.0-TV installed", "Successful", "Security and launcher stability release completed by OEM updater."),
        HistoryItem("seed-2", "2026-07-10 18:15", "Package compatibility audit", "Completed", "Checked Android TV package versions against Android 5+ minimum support.")
    )
}

data class UpdatePackage(val version: String, val build: String, val size: String, val channel: String, val checksum: String, val description: String, val changelog: List<String>)
data class DeviceInfo(val model: String, val manufacturer: String, val currentVersion: String, val androidLabel: String, val buildNumber: String, val apiLevel: Int, val storage: String, val power: String, val network: String)
data class PackageItem(val id: String, val name: String, val title: String, val type: String, val current: String, val proposed: String, val risk: String, val compatibility: String, val note: String)
data class FtaChannel(val id: String, val name: String, val sat: String, val cat: String, val fta: Boolean)
data class HistoryItem(val id: String, val time: String, val title: String, val status: String, val detail: String)
