package com.example

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.ScreenShare
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material.icons.outlined.ScreenShare
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.ActivityLogScreen
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.GeofenceScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PairingScreen
import com.example.ui.screens.RemoteAssistScreen
import com.example.ui.theme.GuardCardDark
import com.example.ui.theme.GuardNavyDark
import com.example.ui.theme.GuardPrimaryCyan
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.FamilyGuardViewModel

enum class MainNavTab(
    val title: String,
    val iconFilled: androidx.compose.ui.graphics.vector.ImageVector,
    val iconOutlined: androidx.compose.ui.graphics.vector.ImageVector
) {
    RADAR("Peta", Icons.Default.LocationOn, Icons.Outlined.LocationOn),
    ASSIST("Bantuan", Icons.Default.ScreenShare, Icons.Outlined.ScreenShare),
    GEOFENCE("Zona", Icons.Default.Security, Icons.Outlined.Security),
    ANALYTICS("Analisis", Icons.Default.Analytics, Icons.Outlined.Analytics),
    PAIRING("Pairing", Icons.Default.QrCode, Icons.Outlined.QrCode),
    LOGS("Riwayat", Icons.Default.History, Icons.Outlined.History)
}

class MainActivity : ComponentActivity() {

    private val viewModel: FamilyGuardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme(darkTheme = true) {
                // Request normal location and notification permissions gracefully
                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions()
                ) { _ -> }

                LaunchedEffect(Unit) {
                    val perms = mutableListOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        perms.add(Manifest.permission.POST_NOTIFICATIONS)
                    }
                    permissionLauncher.launch(perms.toTypedArray())
                }

                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: FamilyGuardViewModel) {
    var currentTab by remember { mutableStateOf(MainNavTab.RADAR) }

    // Handle Back Press on sub-screens to return to Radar
    if (currentTab != MainNavTab.RADAR) {
        BackHandler {
            currentTab = MainNavTab.RADAR
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = GuardNavyDark,
        bottomBar = {
            NavigationBar(
                modifier = Modifier.testTag("main_bottom_nav"),
                containerColor = GuardCardDark,
                contentColor = Color.White,
                tonalElevation = 6.dp
            ) {
                MainNavTab.values().forEach { tab ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        alwaysShowLabel = true,
                        icon = {
                            Icon(
                                imageVector = if (isSelected) tab.iconFilled else tab.iconOutlined,
                                contentDescription = tab.title,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 10.sp,
                                maxLines = 1,
                                softWrap = false
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = GuardNavyDark,
                            selectedTextColor = GuardPrimaryCyan,
                            indicatorColor = GuardPrimaryCyan,
                            unselectedIconColor = Color(0xFF8B949E),
                            unselectedTextColor = Color(0xFF8B949E)
                        ),
                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        val screenModifier = Modifier.padding(innerPadding)
        when (currentTab) {
            MainNavTab.RADAR -> HomeScreen(
                viewModel = viewModel,
                onNavigateToPairing = { currentTab = MainNavTab.PAIRING },
                onNavigateToAssist = { currentTab = MainNavTab.ASSIST },
                modifier = screenModifier
            )
            MainNavTab.ASSIST -> RemoteAssistScreen(
                viewModel = viewModel,
                modifier = screenModifier
            )
            MainNavTab.ANALYTICS -> AnalyticsScreen(
                viewModel = viewModel,
                modifier = screenModifier
            )
            MainNavTab.GEOFENCE -> GeofenceScreen(
                viewModel = viewModel,
                modifier = screenModifier
            )
            MainNavTab.LOGS -> ActivityLogScreen(
                viewModel = viewModel,
                modifier = screenModifier
            )
            MainNavTab.PAIRING -> PairingScreen(
                viewModel = viewModel,
                modifier = screenModifier
            )
        }
    }
}
