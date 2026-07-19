package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        androidx.core.view.WindowCompat.setDecorFitsSystemWindows(window, false)
        androidx.core.view.WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(androidx.core.view.WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = androidx.core.view.WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        setContent {
            MyApplicationTheme {
                val language by viewModel.language.collectAsState()
                val isRtl = language == "ar"
                val layoutDirection = if (isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr
                val t = if (isRtl) Translations.ar else Translations.en

                CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                    val powerState by viewModel.powerState.collectAsState()
                    if (powerState == "shutting_down") {
                        ShutdownScreen(t, viewModel)
                    } else {
                        Scaffold(
                            modifier = Modifier.fillMaxSize().background(BgDark),
                            containerColor = BgDark,
                            topBar = { TopHeader(t, language, viewModel) }
                        ) { innerPadding ->
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                            ) {
                                TabsRow(t, viewModel)
                                Divider(color = BorderColor)
                                MainContent(t, viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TopHeader(t: Translations.Translation, language: String, viewModel: AppViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BgHeader)
            .statusBarsPadding()
            .padding(horizontal = 18.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(t.appEyebrow.uppercase(), color = PrimaryLight, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.4.sp)
            Text(t.appTitle, color = TextPrimary, fontSize = 26.sp, fontWeight = FontWeight.Black)
            Text(t.appHint, color = NeutralText, fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier
                    .border(1.dp, PrimaryLight, RoundedCornerShape(50))
                    .clickable { viewModel.setLanguage(if (language == "en") "ar" else "en") }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(if (language == "en") "العربية" else "English", color = Color(0xFFE0F2FE), fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
            }
            Badge("API 21+", "success")
        }
    }
}

@Composable
fun TabsRow(t: Translations.Translation, viewModel: AppViewModel) {
    val activeTab by viewModel.activeTab.collectAsState()
    val scrollState = rememberScrollState()
    
    val tabs = listOf(
        "dashboard" to t.strings["tab_dashboard"],
        "check" to t.strings["tab_check"],
        "install" to t.strings["tab_install"],
        "history" to t.strings["tab_history"],
        "device" to t.strings["tab_device"],
        "packages" to t.strings["tab_packages"],
        "desktop" to t.strings["tab_desktop"],
        "settings" to t.strings["tab_settings"],
        "performance" to t.strings["tab_performance"],
        "root" to t.strings["tab_root"],
        "satellite" to t.strings["tab_satellite"]
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0F172A))
            .horizontalScroll(scrollState)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tabs.forEach { (id, label) ->
            val isActive = activeTab == id
            Box(
                modifier = Modifier
                    .background(if (isActive) Color(0xFF1D4ED8) else Color.Transparent, RoundedCornerShape(50))
                    .border(1.dp, if (isActive) Color(0xFF60A5FA) else Color(0xFF334155), RoundedCornerShape(50))
                    .clickable { viewModel.setActiveTab(id) }
                    .padding(horizontal = 14.dp, vertical = 9.dp)
            ) {
                Text(
                    text = label ?: "",
                    color = if (isActive) Color.White else NeutralText,
                    fontSize = 13.sp,
                    fontWeight = if (isActive) FontWeight.Black else FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun MainContent(t: Translations.Translation, viewModel: AppViewModel) {
    val activeTab by viewModel.activeTab.collectAsState()
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        when (activeTab) {
            "dashboard" -> DashboardContent(t, viewModel)
            "check" -> UpdateCheckContent(t, viewModel)
            "install" -> InstallContent(t, viewModel)
            "history" -> HistoryContent(t, viewModel)
            "device" -> DeviceInfoContent(t, viewModel)
            "packages" -> PackagesContent(t, viewModel)
            "desktop" -> DesktopContent(t, viewModel)
            "settings" -> SettingsContent(t, viewModel)
            "performance" -> PerformanceContent(t, viewModel)
            "root" -> RootSetupContent(t, viewModel)
            "satellite" -> SatelliteContent(t, viewModel)
            else -> DashboardContent(t, viewModel)
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun DashboardContent(t: Translations.Translation, viewModel: AppViewModel) {
    SectionTitle(t.dashboard.eyebrow, t.dashboard.title, t.dashboard.subtitle)
    
    Card(modifier = Modifier.background(Color(0xFF0F2A44), RoundedCornerShape(26.dp)).border(1.dp, Color(0xFF1D4ED8), RoundedCornerShape(26.dp))) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text(t.strings["current_device"] ?: "", color = Color(0xFF7DD3FC), fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                Text(AppData.deviceInfo.model, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(top = 4.dp))
                Text("${t.strings["build"]} ${AppData.deviceInfo.buildNumber} · ${AppData.deviceInfo.androidLabel}", color = NeutralText, fontSize = 14.sp, modifier = Modifier.padding(top = 6.dp))
            }
            Badge(t.strings["ready_to_check"] ?: "", "neutral")
        }
    }

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(modifier = Modifier.weight(1f)) {
            Text(t.strings["current_os"] ?: "", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(AppData.deviceInfo.currentVersion, color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Black)
            Badge(t.strings["ok"] ?: "", "success")
        }
        Card(modifier = Modifier.weight(1f)) {
            Text(t.strings["available_update"] ?: "", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(AppData.updatePackage.version, color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Black)
            Badge(t.strings["ok"] ?: "", "warning")
        }
    }
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(modifier = Modifier.weight(1f)) {
            Text(t.strings["storage"] ?: "", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(AppData.deviceInfo.storage, color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Black)
            Badge(t.strings["ok"] ?: "", "info")
        }
        Card(modifier = Modifier.weight(1f)) {
            Text(t.strings["root_bridge"] ?: "", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(t.strings["not_connected"] ?: "", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Black)
            Badge(t.strings["action_required"] ?: "", "danger")
        }
    }

    Card {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text(t.strings["latest_update_candidate"] ?: "", color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Black)
                Text(AppData.updatePackage.description, color = TextSecondary, fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
            }
            Badge(AppData.updatePackage.channel, "info")
        }
        Spacer(modifier = Modifier.height(14.dp))
        InfoRow(t.strings["version"] ?: "", AppData.updatePackage.version)
        InfoRow(t.strings["size"] ?: "", AppData.updatePackage.size)
        InfoRow(t.strings["build"] ?: "", AppData.updatePackage.build)
        
        Spacer(modifier = Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ActionButton(t.strings["check_for_updates"] ?: "", onClick = { viewModel.setActiveTab("check") })
            ActionButton(t.strings["download"] ?: "", onClick = { viewModel.setActiveTab("install") }, tone = "secondary")
        }
    }
}

@Composable
fun UpdateCheckContent(t: Translations.Translation, viewModel: AppViewModel) {
    val updateState by viewModel.updateState.collectAsState()
    val checkLogs by viewModel.checkLogs.collectAsState()

    SectionTitle(t.check.eyebrow, t.check.title, t.check.subtitle)
    
    if (updateState == "idle") {
        Card {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Badge(t.strings["ready_to_check"] ?: "", "neutral")
                Spacer(modifier = Modifier.height(14.dp))
                Text(t.strings["checking_for_updates"] ?: "Checking for updates...", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Black, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                Spacer(modifier = Modifier.height(14.dp))
                Text(t.strings["compat_msg"] ?: "", color = NeutralText, fontSize = 14.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }
            Spacer(modifier = Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ActionButton(t.strings["run_check"] ?: "", onClick = { viewModel.runUpdateCheck(t) })
            }
        }
    } else if (updateState == "checking") {
        Card {
            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(t.strings["checking_for_updates"] ?: "Checking for updates...", color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Black)
                    androidx.compose.material3.CircularProgressIndicator(color = Color(0xFF1D4ED8), modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                }
                Spacer(modifier = Modifier.height(14.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0B1B2E), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    checkLogs.forEach { log ->
                        Text(log, color = Color(0xFF60A5FA), fontSize = 13.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
    } else if (updateState == "available" || updateState == "downloading") {
        Card {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Badge(t.strings["update_available"] ?: "", "warning")
                Spacer(modifier = Modifier.height(14.dp))
                Text("${AppData.updatePackage.version} ${t.strings["update_msg"]}", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Black, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                Spacer(modifier = Modifier.height(14.dp))
                Text(t.strings["compat_msg"] ?: "", color = NeutralText, fontSize = 14.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }
            Spacer(modifier = Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ActionButton(t.strings["run_check"] ?: "", onClick = { viewModel.runUpdateCheck(t) }, tone = "secondary")
                ActionButton(t.strings["proceed_to_download"] ?: "", onClick = { viewModel.setActiveTab("install") })
            }
        }
    }
    
    Card {
        Text(t.strings["update_details"] ?: "", color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Black)
        Spacer(modifier = Modifier.height(14.dp))
        Text(AppData.updatePackage.description, color = NeutralText, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(14.dp))
        AppData.updatePackage.changelog.forEach { item ->
            Text("• $item", color = Color(0xFFDBEAFE), fontSize = 14.sp, modifier = Modifier.padding(bottom = 4.dp))
        }
    }
}

@Composable
fun InstallContent(t: Translations.Translation, viewModel: AppViewModel) {
    val downloadProgress by viewModel.downloadProgress.collectAsState()
    val downloadSpeed by viewModel.downloadSpeed.collectAsState()
    val eta by viewModel.eta.collectAsState()
    val updateState by viewModel.updateState.collectAsState()

    val installProgress by viewModel.installProgress.collectAsState()

    LaunchedEffect(updateState) {
        if (updateState == "downloading") {
            while (viewModel.downloadProgress.value < 100) {
                delay(900)
                val current = viewModel.downloadProgress.value
                val next = (current + 7).coerceAtMost(100)
                viewModel.setDownloadProgress(next)
                if (next >= 100) {
                    viewModel.setDownloadSpeed("11.9 MB/s")
                    viewModel.setEta("Complete")
                    viewModel.setUpdateState("downloaded")
                } else {
                    viewModel.setDownloadSpeed("${"%.1f".format(8 + next / 18.0)} MB/s")
                    val remaining = Math.ceil((100.0 - next) / 7.0).toInt()
                    viewModel.setEta("${maxOf(1, remaining)} min")
                }
            }
        } else if (updateState == "installing") {
            var currentProgress = 0f
            while (currentProgress < 1f) {
                delay(200)
                currentProgress += 0.05f
                viewModel.setInstallProgress(currentProgress.coerceAtMost(1f))
            }
            delay(1000)
            viewModel.setUpdateState("installed")
        }
    }

    SectionTitle(t.install.eyebrow, t.install.title, t.install.subtitle)
    Card {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text(AppData.updatePackage.version, color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Black)
                Text("${AppData.updatePackage.size} · ${AppData.updatePackage.checksum}", color = TextSecondary, fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
            }
            val badgeText = when (updateState) {
                "idle" -> t.strings["ready"] ?: "Ready"
                "downloading" -> t.strings["downloading"] ?: "Downloading"
                "downloaded" -> "Downloaded"
                "installing" -> "Installing"
                "installed" -> "Installed"
                else -> ""
            }
            val badgeTone = if (updateState == "idle") "neutral" else if (updateState == "installed") "success" else "info"
            Badge(badgeText, badgeTone)
        }
        
        Spacer(modifier = Modifier.height(14.dp))
        
        if (updateState == "installing" || updateState == "installed") {
            MinimalOsUpdateProgressBar(
                progress = installProgress,
                statusText = if (updateState == "installed") "Installation Complete" else "Applying OS Update (Optimized)..."
            )
        } else {
            ProgressBar(downloadProgress)
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("$downloadProgress% ${t.strings["complete"]}", color = NeutralText, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                Text("$downloadSpeed · ETA $eta", color = NeutralText, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        Column(
            modifier = Modifier
                .background(Color(0xFF0B1220), RoundedCornerShape(16.dp))
                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(t.strings["step1"] ?: "", color = Color(0xFFDBEAFE), fontSize = 13.sp)
            Text(t.strings["step2"] ?: "", color = Color(0xFFDBEAFE), fontSize = 13.sp)
            Text(t.strings["step3"] ?: "", color = Color(0xFFDBEAFE), fontSize = 13.sp)
            Text(t.strings["step4"] ?: "", color = Color(0xFFDBEAFE), fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            if (updateState == "downloaded") {
                ActionButton("Install Update", onClick = { 
                    viewModel.setUpdateState("installing")
                }, tone = "primary")
            } else {
                ActionButton(t.strings["download"] ?: "", onClick = { 
                    viewModel.setUpdateState("downloading")
                    viewModel.setDownloadSpeed("8.0 MB/s")
                    viewModel.setEta("17 min")
                }, disabled = updateState != "idle")
            }
            
            ActionButton(t.strings["reset"] ?: "", onClick = {
                viewModel.setUpdateState("idle")
                viewModel.setDownloadProgress(0)
                viewModel.setDownloadSpeed("0 MB/s")
                viewModel.setEta("—")
                viewModel.setInstallProgress(0f)
            }, tone = "ghost")
        }
    }
}

@Composable
fun HistoryContent(t: Translations.Translation, viewModel: AppViewModel) {
    val history by viewModel.history.collectAsState()
    SectionTitle(t.history.eyebrow, t.history.title, t.history.subtitle)
    
    history.forEach { item ->
        Card {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.title, color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Black)
                    Text(item.time, color = TextSecondary, fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
                }
                Badge(item.status, if (item.status == "Successful" || item.status == "Completed") "success" else "neutral")
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(item.detail, color = NeutralText, fontSize = 14.sp)
        }
    }
}

@Composable
fun DeviceInfoContent(t: Translations.Translation, viewModel: AppViewModel) {
    SectionTitle(t.device.eyebrow, t.device.title, t.device.subtitle)
    Card {
        InfoRow(t.strings["model"] ?: "", AppData.deviceInfo.model)
        InfoRow(t.strings["manufacturer"] ?: "", AppData.deviceInfo.manufacturer)
        InfoRow(t.strings["current_os"] ?: "", AppData.deviceInfo.currentVersion)
        InfoRow(t.strings["android_api"] ?: "", "API ${AppData.deviceInfo.apiLevel}")
        InfoRow(t.strings["build_number"] ?: "", AppData.deviceInfo.buildNumber)
        InfoRow(t.strings["network"] ?: "", AppData.deviceInfo.network)
        InfoRow(t.strings["power"] ?: "", AppData.deviceInfo.power)
        InfoRow(t.strings["storage"] ?: "", AppData.deviceInfo.storage)
        InfoRow(t.strings["root_status"] ?: "", t.strings["unknown_env"] ?: "")
    }
}

@Composable
fun PackagesContent(t: Translations.Translation, viewModel: AppViewModel) {
    SectionTitle(t.packages.eyebrow, t.packages.title, t.packages.subtitle)
    
    AppData.packageInventory.forEach { pkg ->
        Card {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(pkg.title, color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Black)
                    Text(pkg.name, color = Color(0xFF93C5FD), fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                }
                Badge(pkg.risk, if (pkg.risk == "Critical") "danger" else if (pkg.risk == "High") "warning" else "info")
            }
            Spacer(modifier = Modifier.height(14.dp))
            InfoRow(t.strings["type"] ?: "", pkg.type)
            InfoRow(t.strings["current"] ?: "", pkg.current)
            InfoRow(t.strings["replacement"] ?: "", pkg.proposed)
            InfoRow(t.strings["compatibility"] ?: "", pkg.compatibility)
            Spacer(modifier = Modifier.height(14.dp))
            Text(pkg.note, color = NeutralText, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ActionButton(t.strings["disable"] ?: "", onClick = { }, tone = "warning")
                ActionButton(t.strings["remove"] ?: "", onClick = { }, tone = "danger")
            }
        }
    }
    
    val usbState by viewModel.usbInstallState.collectAsState()
    Card {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(t.strings["install_from_usb"] ?: "Install from USB", color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.height(8.dp))
            Text(t.strings["usb_install_desc"] ?: "Open, update, and install system packages from an external USB storage drive.", color = NeutralText, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(14.dp))
            
            val usbInstallProgress by viewModel.usbInstallProgress.collectAsState()
            
            if (usbState == "idle") {
                ActionButton(
                    label = t.strings["install_from_usb"] ?: "Install from USB",
                    onClick = { viewModel.installFromUsb(t) },
                    tone = "primary"
                )
            } else if (usbState == "checking") {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    androidx.compose.material3.CircularProgressIndicator(color = Color(0xFF1D4ED8), modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(t.strings["usb_not_found"] ?: "Checking for USB...", color = NeutralText, fontSize = 14.sp)
                }
            } else if (usbState == "installing") {
                MinimalOsUpdateProgressBar(
                    progress = usbInstallProgress,
                    statusText = t.strings["installing_usb"] ?: "Installing from USB..."
                )
            } else if (usbState == "success") {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Badge(t.strings["usb_pkg_found"] ?: "Success", "success")
                }
            }
        }
    }
}

@Composable
fun DesktopContent(t: Translations.Translation, viewModel: AppViewModel) {
    SectionTitle(t.desktop.eyebrow, t.desktop.title, t.desktop.subtitle)
    Card {
        Text(t.strings["home_screen_layout"] ?: "", color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Black)
        Spacer(modifier = Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(t.strings["show_clock"] ?: "", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
            Switch(checked = true, onCheckedChange = {})
        }
        Spacer(modifier = Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(t.strings["show_weather"] ?: "", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
            Switch(checked = true, onCheckedChange = {})
        }
    }
    Card {
        Text(t.strings["media_apps_sources"] ?: "", color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Black)
        Spacer(modifier = Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(t.strings["satellite_channels"] ?: "", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
            Switch(checked = true, onCheckedChange = {})
        }
        Spacer(modifier = Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(t.strings["auto_start_digital"] ?: "", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
            Switch(checked = true, onCheckedChange = {})
        }
        Spacer(modifier = Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(t.strings["web_browser"] ?: "", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
            Switch(checked = true, onCheckedChange = {})
        }
    }
}

@Composable
fun SettingsContent(t: Translations.Translation, viewModel: AppViewModel) {
    SectionTitle(t.settings.eyebrow, t.settings.title, t.settings.subtitle)
    
    val startupMode by viewModel.startupMode.collectAsState()
    val autoDisplay by viewModel.autoDisplaySettings.collectAsState()
    val brightness by viewModel.brightness.collectAsState()
    val contrast by viewModel.contrast.collectAsState()
    val colorSaturation by viewModel.colorSaturation.collectAsState()
    val volumeLevel by viewModel.volumeLevel.collectAsState()

    Card {
        Text(t.strings["startup_mode"] ?: "Startup Mode", color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Black)
        Spacer(modifier = Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ActionButton(t.strings["startup_desktop"] ?: "Desktop", onClick = { viewModel.setStartupMode("desktop") }, tone = if (startupMode == "desktop") "primary" else "secondary")
            ActionButton(t.strings["startup_tv"] ?: "Live TV", onClick = { viewModel.setStartupMode("tv") }, tone = if (startupMode == "tv") "primary" else "secondary")
        }
    }

    Card {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(t.strings["display_settings"] ?: "Display Settings", color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Black)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(t.strings["auto_display"] ?: "Auto Adjust", color = NeutralText, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Switch(checked = autoDisplay, onCheckedChange = { viewModel.setAutoDisplaySettings(it) })
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        
        Text(t.strings["brightness"] ?: "Brightness", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        androidx.compose.material3.Slider(value = brightness, onValueChange = { viewModel.setBrightness(it) }, valueRange = 0f..100f, enabled = !autoDisplay)
        
        Text(t.strings["contrast"] ?: "Contrast", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        androidx.compose.material3.Slider(value = contrast, onValueChange = { viewModel.setContrast(it) }, valueRange = 0f..100f, enabled = !autoDisplay)
        
        Text(t.strings["color_saturation"] ?: "Color", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        androidx.compose.material3.Slider(value = colorSaturation, onValueChange = { viewModel.setColorSaturation(it) }, valueRange = 0f..100f, enabled = !autoDisplay)
    }

    Card {
        Text(t.strings["audio_settings"] ?: "Audio Settings", color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Black)
        Spacer(modifier = Modifier.height(14.dp))
        Text(t.strings["volume_level"] ?: "Volume Level", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        androidx.compose.material3.Slider(value = volumeLevel, onValueChange = { viewModel.setVolumeLevel(it) }, valueRange = 0f..100f)
    }

    Card {
        Text(t.strings["power_control"] ?: "", color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Black)
        Spacer(modifier = Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ActionButton(t.strings["restart"] ?: "", onClick = { }, tone = "warning")
            ActionButton(t.strings["shutdown"] ?: "", onClick = { viewModel.setPowerState("shutting_down") }, tone = "danger")
        }
        Spacer(modifier = Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ActionButton(t.strings["sleep"] ?: "", onClick = { }, tone = "secondary")
            ActionButton(t.strings["turn_on"] ?: "", onClick = { }, tone = "primary")
        }
    }

    Card {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(t.strings["usb_permissions"] ?: "", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
            Switch(checked = true, onCheckedChange = {})
        }
        Spacer(modifier = Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(t.strings["hdmi_permissions"] ?: "", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
            Switch(checked = true, onCheckedChange = {})
        }
        Spacer(modifier = Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(t.strings["wifi_control"] ?: "", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
            Switch(checked = true, onCheckedChange = {})
        }
        Spacer(modifier = Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(t.strings["bluetooth_control"] ?: "", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
            Switch(checked = true, onCheckedChange = {})
        }
        Spacer(modifier = Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(t.strings["remote_control"] ?: "", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
            Switch(checked = true, onCheckedChange = {})
        }
    }

    Card {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(t.strings["auto_update_checks"] ?: "", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
            Switch(checked = true, onCheckedChange = {})
        }
        Spacer(modifier = Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(t.strings["auto_downloads"] ?: "", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
            Switch(checked = false, onCheckedChange = {})
        }
        Spacer(modifier = Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(t.strings["wifi_only"] ?: "", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
            Switch(checked = true, onCheckedChange = {})
        }
        Spacer(modifier = Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(t.strings["update_notifications"] ?: "", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
            Switch(checked = true, onCheckedChange = {})
        }
    }

    val revertState by viewModel.revertState.collectAsState()
    Card {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(t.strings["revert_os"] ?: "Revert to Previous OS", color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.height(8.dp))
            Text(t.strings["revert_os_desc"] ?: "Restore the system to the previous working OS version if the current update is unstable.", color = NeutralText, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(14.dp))
            if (revertState == "reverting") {
                Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                    androidx.compose.material3.CircularProgressIndicator(color = Color(0xFF1D4ED8), modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                }
            } else {
                ActionButton(
                    label = t.strings["revert_os"] ?: "Revert to Previous OS",
                    onClick = { viewModel.revertOS(t) },
                    tone = if (revertState == "success") "success" else "danger"
                )
            }
        }
    }
}

@Composable
fun PerformanceContent(t: Translations.Translation, viewModel: AppViewModel) {
    val cpu by viewModel.cpuUsage.collectAsState()
    val ram by viewModel.ramUsage.collectAsState()
    val storage by viewModel.storageHealth.collectAsState()

    SectionTitle(t.strings["perf_dashboard"] ?: "Performance Dashboard", t.strings["perf_desc"] ?: "Real-time system resource monitoring.", null)

    Card {
        Text(t.strings["cpu_usage"] ?: "CPU Usage", color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Black)
        Spacer(modifier = Modifier.height(14.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("${cpu.toInt()}%", color = Color(0xFF38BDF8), fontSize = 24.sp, fontWeight = FontWeight.Black, modifier = Modifier.width(60.dp))
            Spacer(modifier = Modifier.width(16.dp))
            ProgressBar(cpu.toInt())
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
    Card {
        Text(t.strings["ram_allocation"] ?: "RAM Allocation", color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Black)
        Spacer(modifier = Modifier.height(14.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("${ram.toInt()}%", color = Color(0xFF34D399), fontSize = 24.sp, fontWeight = FontWeight.Black, modifier = Modifier.width(60.dp))
            Spacer(modifier = Modifier.width(16.dp))
            ProgressBar(ram.toInt())
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
    Card {
        Text(t.strings["storage_health"] ?: "Storage Health", color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Black)
        Spacer(modifier = Modifier.height(14.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("${storage.toInt()}%", color = Color(0xFFFBBF24), fontSize = 24.sp, fontWeight = FontWeight.Black, modifier = Modifier.width(60.dp))
            Spacer(modifier = Modifier.width(16.dp))
            ProgressBar(storage.toInt())
        }
    }
}

@Composable
fun SatelliteContent(t: Translations.Translation, viewModel: AppViewModel) {
    val selectedSource by viewModel.selectedChannelSource.collectAsState()
    
    SectionTitle(t.strings["sat_eyebrow"] ?: "Satellite Channels", t.strings["sat_desc"] ?: "Broadcast configuration.", null)
    
    Card {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(t.strings["sat_title"] ?: "Live Broadcasts", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Black)
                Badge("FTA", "success")
            }
            Text(t.strings["sat_subtitle"] ?: "Public / general mode", color = NeutralText, fontSize = 13.sp)
            
            Spacer(modifier = Modifier.height(24.dp))
            Text(t.strings["source"] ?: "Source", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            
            val scrollState = rememberScrollState()
            Row(
                modifier = Modifier.horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val sources = listOf(
                    "internal_dish" to (t.strings["int_recv_dish"] ?: "Internal (Dish)"),
                    "internal_web" to (t.strings["int_recv_web"] ?: "Internal (Web)"),
                    "digital_receiver" to (t.strings["digital_receiver"] ?: "Built-in Digital Receiver"),
                    "terrestrial" to (t.strings["terrestrial"] ?: "Terrestrial"),
                    "hdmi" to (t.strings["ext_recv"] ?: "External receiver"),
                    "usb" to (t.strings["usb_media"] ?: "USB media")
                )
                sources.forEach { (id, label) ->
                    val isSelected = selectedSource == id
                    Box(
                        modifier = Modifier
                            .background(if (isSelected) Color(0xFF1D4ED8) else Color.Transparent, RoundedCornerShape(8.dp))
                            .border(1.dp, if (isSelected) Color(0xFF1D4ED8) else Color(0xFF334155), RoundedCornerShape(8.dp))
                            .clickable { viewModel.setChannelSource(id) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(label, color = if (isSelected) Color.White else NeutralText, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            val autoScanRunning by viewModel.autoScanRunning.collectAsState()

            Spacer(modifier = Modifier.height(24.dp))
            
            if (selectedSource == "terrestrial" || selectedSource == "internal_dish" || selectedSource == "digital_receiver") {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(t.strings["auto_scan"] ?: "Auto Scan", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    ActionButton(
                        if (autoScanRunning) "Scanning..." else "Scan", 
                        onClick = { 
                            val tunerType = when (selectedSource) {
                                "terrestrial" -> TunerType.DVB_T
                                "digital_receiver" -> TunerType.DVB_C_T2
                                else -> TunerType.DVB_S
                            }
                            viewModel.setAutoScanRunning(!autoScanRunning, tunerType) 
                        },
                        tone = if (autoScanRunning) "warning" else "primary"
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (autoScanRunning) {
                    androidx.compose.material3.LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = Color(0xFF38BDF8))
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            val scannedChannels by viewModel.scannedChannels.collectAsState()

            // Channel List
            val channels = if (scannedChannels.isNotEmpty() && (selectedSource == "terrestrial" || selectedSource == "internal_dish" || selectedSource == "digital_receiver")) {
                scannedChannels.map { Triple(it.name, it.frequency, it.type) }
            } else if (selectedSource == "digital_receiver") {
                listOf(
                    Triple("National Digital 1", "DVB-T2 626MHz", "FHD • FTA"),
                    Triple("National Digital 2", "DVB-T2 626MHz", "HD • FTA"),
                    Triple("Sports Digital", "DVB-C 450MHz", "FHD • FTA"),
                    Triple("Kids Channel", "DVB-T2 642MHz", "SD • FTA")
                )
            } else if (selectedSource == "terrestrial") {
                listOf(
                    Triple("Local TV 1", "UHF 21", "HD • FTA"),
                    Triple("Local TV 2", "UHF 24", "HD • FTA"),
                    Triple("Local News", "VHF 10", "SD • FTA")
                )
            } else if (selectedSource == "internal_web") {
                 listOf(
                    Triple("Al Jazeera", "IPTV Stream 1", "NEWS"),
                    Triple("France 24", "IPTV Stream 2", "NEWS"),
                    Triple("Sports HD", "IPTV Stream 3", "SPORTS")
                )
            } else {
                listOf(
                    Triple("Al Jazeera", "Nilesat 201 • 7°W", "NEWS • FTA"),
                    Triple("France 24 English", "Nilesat 201 • 7°W", "NEWS • FTA"),
                    Triple("DW English", "Hotbird • 13°E", "NEWS • FTA"),
                    Triple("TRT World", "Turksat • 42°E", "NEWS • FTA"),
                    Triple("NHK World Japan", "Hotbird • 13°E", "NEWS • FTA"),
                    Triple("Sahel Sports (FTA)", "Eutelsat • 7°W", "SPORTS • FTA")
                )
            }
            
            channels.forEach { (name, freq, tag) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .background(Color(0xFF0F172A), RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(8.dp))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(name, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text(freq, color = NeutralText, fontSize = 12.sp)
                    }
                    Badge(tag, "success")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ActionButton(t.strings["open_channel"] ?: "Open Channel", onClick = { }, tone = "primary")
                ActionButton(t.strings["close"] ?: "Close", onClick = { }, tone = "secondary")
            }
        }
    }
}

@Composable
fun RootSetupContent(t: Translations.Translation, viewModel: AppViewModel) {
    val rootEnabled by viewModel.rootEnabled.collectAsState()
    val upgradePrepared by viewModel.upgradePrepared.collectAsState()
    val playStoreSigned by viewModel.playStoreSigned.collectAsState()
    val freeSpaceInstallCompleted by viewModel.freeSpaceInstallCompleted.collectAsState()

    SectionTitle(t.strings["root_upgrade_title"] ?: "Base Package Setup & Root", t.strings["root_upgrade_desc"] ?: "Base package setup.", null)

    Card {
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(t.strings["core_services"] ?: "Core services integrated.", color = Color(0xFF38BDF8), fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(t.strings["free_space_install"] ?: "Free Space & Install Features", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    if (freeSpaceInstallCompleted) {
                        Text(t.strings["free_space_install_desc"] ?: "Installed full features", color = Color(0xFF34D399), fontSize = 13.sp)
                    } else {
                        Text(t.strings["free_space_install_desc"] ?: "Replace old system", color = NeutralText, fontSize = 12.sp)
                    }
                }
                if (!freeSpaceInstallCompleted) {
                    ActionButton("Install", onClick = { viewModel.completeFreeSpaceInstall() }, tone = "primary")
                } else {
                    Badge("OK", "success")
                }
            }
            
            HorizontalDivider(color = BorderColor)
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(t.strings["enable_root"] ?: "Enable Root", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    if (rootEnabled) {
                        Text(t.strings["root_enabled"] ?: "Root enabled", color = Color(0xFF34D399), fontSize = 13.sp)
                    }
                }
                if (!rootEnabled) {
                    ActionButton("Run", onClick = { viewModel.enableRoot() }, tone = "danger")
                } else {
                    Badge("OK", "success")
                }
            }
            
            HorizontalDivider(color = BorderColor)
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(t.strings["prepare_upgrade"] ?: "Prepare Upgrade", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    if (upgradePrepared) {
                        Text(t.strings["upgrade_prepared"] ?: "Ready", color = Color(0xFF34D399), fontSize = 13.sp)
                    }
                }
                if (!upgradePrepared) {
                    ActionButton("Start", onClick = { viewModel.prepareUpgrade() }, tone = "primary")
                } else {
                    Badge("OK", "success")
                }
            }
            
            HorizontalDivider(color = BorderColor)
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(t.strings["sign_play_store"] ?: "Sign with Google Play Store", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    if (playStoreSigned) {
                        Text(t.strings["signed_play_store"] ?: "Signed", color = Color(0xFF34D399), fontSize = 13.sp)
                    }
                }
                if (!playStoreSigned) {
                    ActionButton("Sign", onClick = { viewModel.signPlayStore() }, tone = "warning")
                } else {
                    Badge("OK", "success")
                }
            }
        }
    }
}

@Composable
fun ShutdownScreen(t: Translations.Translation, viewModel: AppViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B1220))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("MAZENGALAB", color = Color.White, fontSize = 42.sp, fontWeight = FontWeight.Black, letterSpacing = 6.sp)
        Text("BESTON ANDROID", color = Color(0xFF38BDF8), fontSize = 28.sp, fontWeight = FontWeight.Black, letterSpacing = 4.sp)
        
        HorizontalDivider(color = Color(0xFF1D4ED8), thickness = 4.dp, modifier = Modifier.padding(vertical = 24.dp).width(200.dp))
        
        Text(t.strings["shutting_down"] ?: "Shutting down...", color = Color.White, fontSize = 20.sp, modifier = Modifier.padding(bottom = 32.dp))
        
        androidx.compose.material3.CircularProgressIndicator(color = Color(0xFF1D4ED8), modifier = Modifier.size(48.dp), strokeWidth = 4.dp)
        
        Spacer(modifier = Modifier.height(64.dp))
        
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = { /* confirm shut down demo */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(48.dp)
            ) {
                Text(t.strings["confirm_shut_down"] ?: "Confirm Shut Down", color = Color.White, fontWeight = FontWeight.Bold)
            }
            OutlinedButton(
                onClick = { viewModel.setPowerState("on") },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(48.dp)
            ) {
                Text(t.strings["cancel"] ?: "Cancel", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(t.strings["shutdown_demo"] ?: "Demo only — managed Expo cannot power off or reboot Android. Connect an OEM/native power control to complete the action.", color = NeutralText, fontSize = 13.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.padding(horizontal = 24.dp))
    }
}
