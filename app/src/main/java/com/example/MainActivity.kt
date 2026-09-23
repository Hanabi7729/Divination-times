package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AltRoute
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AdInterstitialDialog
import com.example.ui.components.LanguagePickerDialog
import com.example.ui.components.OfflineGateDialog
import com.example.ui.components.PremiumUpgradeDialog
import com.example.ui.screens.ChoiceScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.ShopScreen
import com.example.ui.screens.YesNoScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.OracleViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: OracleViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val currentTab by viewModel.currentTab.collectAsState()
                val strings by viewModel.strings.collectAsState()
                val isPremium by viewModel.isPremium.collectAsState()
                val stardust by viewModel.stardust.collectAsState()
                val simulateOffline by viewModel.simulateOffline.collectAsState()
                val currentLanguage by viewModel.languageCode.collectAsState()

                val showAdDialog by viewModel.showAdDialog.collectAsState()
                val showPremiumDialog by viewModel.showPremiumDialog.collectAsState()
                val showOfflineGate by viewModel.showOfflineGateDialog.collectAsState()
                val showLanguageDialog by viewModel.showLanguageDialog.collectAsState()
                val statusMessage by viewModel.statusMessage.collectAsState()

                val snackbarHostState = remember { SnackbarHostState() }

                LaunchedEffect(statusMessage) {
                    statusMessage?.let {
                        snackbarHostState.showSnackbar(it)
                        viewModel.clearStatusMessage()
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets.systemBars,
                    topBar = {
                        TopAppBar(
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .background(
                                                Brush.radialGradient(
                                                    listOf(Color(0xFFC084FC), Color(0xFF6B21A8))
                                                ),
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = strings.appTitle,
                                        color = Color.White,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            },
                            actions = {
                                // Offline Mode Simulation Toggle
                                IconButton(
                                    onClick = { viewModel.toggleSimulateOffline() },
                                    modifier = Modifier.testTag("offline_toggle_button")
                                ) {
                                    Icon(
                                        imageVector = if (simulateOffline) Icons.Default.CloudOff else Icons.Default.CloudDone,
                                        contentDescription = "Toggle Offline Mode",
                                        tint = if (simulateOffline) Color(0xFFF43F5E) else Color(0xFF10B981),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                // Language Switcher
                                IconButton(
                                    onClick = { viewModel.showLanguage() },
                                    modifier = Modifier.testTag("language_selector_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Language,
                                        contentDescription = strings.languageTitle,
                                        tint = Color(0xFFA5B4FC),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                // Stardust Counter Badge
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF161838))
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Color(0xFFFDE047),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "$stardust",
                                        color = Color(0xFFFDE047),
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                // Premium Badge / Action
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            if (isPremium) Color(0xFF78350F) else Color(0xFF1E1B4B)
                                        )
                                        .border(
                                            1.dp,
                                            if (isPremium) Color(0xFFF59E0B) else Color(0xFF6366F1),
                                            RoundedCornerShape(12.dp)
                                        )
                                        .clickable { viewModel.showPremium() }
                                        .padding(horizontal = 9.dp, vertical = 5.dp)
                                        .testTag("top_premium_badge")
                                ) {
                                    Text(
                                        text = if (isPremium) "ARCHMAGE ⭐" else "PRO $2",
                                        color = if (isPremium) Color(0xFFFEF08A) else Color(0xFFA5B4FC),
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color(0xFF090A18)
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = Color(0xFF0D0F24),
                            contentColor = Color.White
                        ) {
                            NavigationBarItem(
                                selected = currentTab == 0,
                                onClick = { viewModel.setTab(0) },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.HelpOutline,
                                        contentDescription = strings.tabYesNo
                                    )
                                },
                                label = { Text(strings.tabYesNo, fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color(0xFFFDE047),
                                    selectedTextColor = Color(0xFFFDE047),
                                    indicatorColor = Color(0xFF312E81),
                                    unselectedIconColor = Color(0xFF64748B),
                                    unselectedTextColor = Color(0xFF64748B)
                                ),
                                modifier = Modifier.testTag("tab_yes_no")
                            )

                            NavigationBarItem(
                                selected = currentTab == 1,
                                onClick = { viewModel.setTab(1) },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.AltRoute,
                                        contentDescription = strings.tabChoice
                                    )
                                },
                                label = { Text(strings.tabChoice, fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color(0xFFFDE047),
                                    selectedTextColor = Color(0xFFFDE047),
                                    indicatorColor = Color(0xFF312E81),
                                    unselectedIconColor = Color(0xFF64748B),
                                    unselectedTextColor = Color(0xFF64748B)
                                ),
                                modifier = Modifier.testTag("tab_choice")
                            )

                            NavigationBarItem(
                                selected = currentTab == 2,
                                onClick = { viewModel.setTab(2) },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.ShoppingBag,
                                        contentDescription = strings.tabShop
                                    )
                                },
                                label = { Text(strings.tabShop, fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color(0xFFFDE047),
                                    selectedTextColor = Color(0xFFFDE047),
                                    indicatorColor = Color(0xFF312E81),
                                    unselectedIconColor = Color(0xFF64748B),
                                    unselectedTextColor = Color(0xFF64748B)
                                ),
                                modifier = Modifier.testTag("tab_shop")
                            )

                            NavigationBarItem(
                                selected = currentTab == 3,
                                onClick = { viewModel.setTab(3) },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.History,
                                        contentDescription = strings.tabHistory
                                    )
                                },
                                label = { Text(strings.tabHistory, fontSize = 11.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color(0xFFFDE047),
                                    selectedTextColor = Color(0xFFFDE047),
                                    indicatorColor = Color(0xFF312E81),
                                    unselectedIconColor = Color(0xFF64748B),
                                    unselectedTextColor = Color(0xFF64748B)
                                ),
                                modifier = Modifier.testTag("tab_history")
                            )
                        }
                    },
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    containerColor = Color(0xFF090A18)
                ) { innerPadding ->
                    // Top offline banner alert if simulated offline
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        if (simulateOffline) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = if (isPremium) Color(0xFF064E3B) else Color(0xFF881337)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = if (isPremium) Icons.Default.CloudDone else Icons.Default.CloudOff,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isPremium) strings.offlineActiveSuccess else strings.simulatedOfflineActive,
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        // Tab Content
                        Box(modifier = Modifier.fillMaxSize()) {
                            AnimatedContent(
                                targetState = currentTab,
                                transitionSpec = { fadeIn() togetherWith fadeOut() },
                                label = "tab_transition"
                            ) { tab ->
                                when (tab) {
                                    0 -> YesNoScreen(viewModel = viewModel)
                                    1 -> ChoiceScreen(viewModel = viewModel)
                                    2 -> ShopScreen(viewModel = viewModel)
                                    3 -> HistoryScreen(viewModel = viewModel)
                                }
                            }
                        }
                    }

                    // Modals
                    if (showAdDialog) {
                        AdInterstitialDialog(
                            strings = strings,
                            onDismiss = { viewModel.dismissAdDialog() },
                            onUpgradeClicked = {
                                viewModel.dismissAdDialog()
                                viewModel.showPremium()
                            }
                        )
                    }

                    if (showPremiumDialog) {
                        PremiumUpgradeDialog(
                            strings = strings,
                            isAlreadyPremium = isPremium,
                            onDismiss = { viewModel.dismissPremium() },
                            onPurchaseConfirmed = { viewModel.upgradeToPremium() }
                        )
                    }

                    if (showOfflineGate) {
                        OfflineGateDialog(
                            strings = strings,
                            onDismiss = { viewModel.dismissOfflineGate() },
                            onUpgradeClicked = {
                                viewModel.dismissOfflineGate()
                                viewModel.showPremium()
                            }
                        )
                    }

                    if (showLanguageDialog) {
                        LanguagePickerDialog(
                            strings = strings,
                            currentCode = currentLanguage,
                            onLanguageSelected = { viewModel.setLanguage(it) },
                            onDismiss = { viewModel.dismissLanguage() }
                        )
                    }
                }
            }
        }
    }
}
