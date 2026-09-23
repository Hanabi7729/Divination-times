package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CrystalCatalog
import com.example.model.CrystalSkin
import com.example.model.FortuneStyle
import com.example.model.OrbAnimation
import com.example.ui.components.CrystalBallView
import com.example.viewmodel.OracleViewModel

@Composable
fun ShopScreen(
    viewModel: OracleViewModel,
    modifier: Modifier = Modifier
) {
    val strings by viewModel.strings.collectAsState()
    val stardust by viewModel.stardust.collectAsState()
    val isPremium by viewModel.isPremium.collectAsState()
    val selectedSkinId by viewModel.selectedSkinId.collectAsState()
    val selectedAnimationId by viewModel.selectedAnimationId.collectAsState()
    val selectedStyleId by viewModel.selectedStyleId.collectAsState()
    val unlockedSkins by viewModel.unlockedSkinIds.collectAsState()
    val unlockedAnims by viewModel.unlockedAnimationIds.collectAsState()
    val unlockedStyles by viewModel.unlockedStyleIds.collectAsState()

    var activeSubTab by remember { mutableIntStateOf(0) } // 0: Skins, 1: Auras, 2: Styles
    var previewSkinId by remember { mutableStateOf(selectedSkinId) }
    var previewAnimId by remember { mutableStateOf(selectedAnimationId) }

    val previewSkin = CrystalCatalog.getSkin(previewSkinId)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Stardust & Daily Meditation Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Stardust Counter Badge
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF1E1B4B))
                        .border(1.dp, Color(0xFF6366F1), RoundedCornerShape(16.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFDE047),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$stardust",
                        color = Color(0xFFFDE047),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = strings.stardustLabel,
                        color = Color(0xFFCBD5E1),
                        fontSize = 12.sp
                    )
                }

                // Daily Meditation Action
                Button(
                    onClick = { viewModel.claimDailyMeditation() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF312E81),
                        contentColor = Color(0xFFE0E7FF)
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.testTag("daily_meditation_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SelfImprovement,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "+100 Bonus",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Live Interactive Preview of Crystal Ball
            CrystalBallView(
                skin = previewSkin,
                animationId = previewAnimId,
                isDivining = false,
                size = 210.dp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Sub-tabs (Skins, Animations, Styles)
            TabRow(
                selectedTabIndex = activeSubTab,
                containerColor = Color(0xFF101229),
                contentColor = Color.White,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[activeSubTab]),
                        color = previewSkin.glowColor
                    )
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, Color(0xFF282B54), RoundedCornerShape(14.dp))
            ) {
                Tab(
                    selected = activeSubTab == 0,
                    onClick = { activeSubTab = 0 },
                    text = { Text(strings.shopTabSkins, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = activeSubTab == 1,
                    onClick = { activeSubTab = 1 },
                    text = { Text(strings.shopTabAnimations, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = activeSubTab == 2,
                    onClick = { activeSubTab = 2 },
                    text = { Text(strings.shopTabStyles, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // SubTab 0: Skins
        if (activeSubTab == 0) {
            items(CrystalCatalog.SKINS) { skin ->
                val isEquipped = selectedSkinId == skin.id
                val isUnlocked = unlockedSkins.contains(skin.id) || (isPremium && skin.isPremiumOnly)
                SkinCard(
                    skin = skin,
                    isEquipped = isEquipped,
                    isUnlocked = isUnlocked,
                    isPremiumUser = isPremium,
                    strings = strings,
                    onSelect = {
                        previewSkinId = skin.id
                        if (isUnlocked) {
                            viewModel.equipSkin(skin.id)
                        } else if (skin.isPremiumOnly) {
                            viewModel.showPremium()
                        } else {
                            viewModel.unlockSkin(skin.id)
                        }
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        // SubTab 1: Auras / Animations
        if (activeSubTab == 1) {
            items(CrystalCatalog.ANIMATIONS) { anim ->
                val isEquipped = selectedAnimationId == anim.id
                val isUnlocked = unlockedAnims.contains(anim.id)
                AnimationCard(
                    anim = anim,
                    isEquipped = isEquipped,
                    isUnlocked = isUnlocked,
                    strings = strings,
                    onSelect = {
                        previewAnimId = anim.id
                        if (isUnlocked) {
                            viewModel.equipAnimation(anim.id)
                        } else {
                            viewModel.unlockAnimation(anim.id)
                        }
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        // SubTab 2: Fortune Styles
        if (activeSubTab == 2) {
            items(CrystalCatalog.STYLES) { style ->
                val isEquipped = selectedStyleId == style.id
                val isUnlocked = isPremium || unlockedStyles.contains(style.id)
                StyleCard(
                    style = style,
                    isEquipped = isEquipped,
                    isUnlocked = isUnlocked,
                    strings = strings,
                    onSelect = {
                        if (isUnlocked) {
                            viewModel.equipStyle(style.id)
                        } else {
                            viewModel.unlockStyle(style.id)
                        }
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        // Archmage Premium Upgrade Banner at Bottom
        item {
            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(
                        1.dp,
                        Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFF6366F1))),
                        RoundedCornerShape(20.dp)
                    )
                    .clickable { viewModel.showPremium() }
                    .testTag("shop_premium_banner"),
                color = Color(0xFF131433)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                Brush.radialGradient(listOf(Color(0xFFFDE047), Color(0xFFD97706))),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFF1E1B4B),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isPremium) "Archmage Grimoire (Active)" else "Archmage Ascension ($2.00)",
                            color = Color.White,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isPremium) "All ads removed, offline mode & Archmage skin equipped." else "Remove all ads forever, unlock offline mode & Archmage Eternity skin.",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.5.sp,
                            lineHeight = 16.sp
                        )
                    }

                    if (!isPremium) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFF59E0B))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "$2.00",
                                color = Color(0xFF1E1B4B),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun SkinCard(
    skin: CrystalSkin,
    isEquipped: Boolean,
    isUnlocked: Boolean,
    isPremiumUser: Boolean,
    strings: com.example.engine.LocalizedStrings,
    onSelect: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = if (isEquipped) 2.dp else 1.dp,
                color = if (isEquipped) skin.glowColor else Color(0xFF282B54),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onSelect() }
            .testTag("skin_item_${skin.id}"),
        color = Color(0xFF11132D)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Orb Color Swatch
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(skin.accentColor, skin.secondaryColor, skin.primaryColor)
                        )
                    )
                    .border(2.dp, skin.glowColor, CircleShape)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = skin.name,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (skin.isPremiumOnly) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF78350F))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "PREMIUM",
                                color = Color(0xFFFDE047),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = skin.description,
                    color = Color(0xFF94A3B8),
                    fontSize = 11.5.sp,
                    lineHeight = 15.sp
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Action Button / Badge
            if (isEquipped) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(skin.glowColor.copy(alpha = 0.2f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = strings.equippedLabel,
                        color = skin.accentColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp
                    )
                }
            } else if (isUnlocked) {
                Button(
                    onClick = onSelect,
                    colors = ButtonDefaults.buttonColors(containerColor = skin.glowColor),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(strings.equipAction, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }
            } else if (skin.isPremiumOnly) {
                Button(
                    onClick = onSelect,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF59E0B),
                        contentColor = Color(0xFF1E1B4B)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("$2.00", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = onSelect,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF312E81)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFDE047),
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${skin.priceStardust}", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun AnimationCard(
    anim: OrbAnimation,
    isEquipped: Boolean,
    isUnlocked: Boolean,
    strings: com.example.engine.LocalizedStrings,
    onSelect: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = if (isEquipped) 2.dp else 1.dp,
                color = if (isEquipped) Color(0xFF818CF8) else Color(0xFF282B54),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onSelect() }
            .testTag("anim_item_${anim.id}"),
        color = Color(0xFF11132D)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(Color(0xFF1E1B4B), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color(0xFF818CF8),
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = anim.name,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = anim.description,
                    color = Color(0xFF94A3B8),
                    fontSize = 11.5.sp
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            if (isEquipped) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF312E81))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = strings.equippedLabel,
                        color = Color(0xFFA5B4FC),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp
                    )
                }
            } else if (isUnlocked) {
                Button(
                    onClick = onSelect,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4338CA)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(strings.equipAction, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = onSelect,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1B4B)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFDE047),
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${anim.priceStardust}", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun StyleCard(
    style: FortuneStyle,
    isEquipped: Boolean,
    isUnlocked: Boolean,
    strings: com.example.engine.LocalizedStrings,
    onSelect: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = if (isEquipped) 2.dp else 1.dp,
                color = if (isEquipped) Color(0xFFEC4899) else Color(0xFF282B54),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onSelect() }
            .testTag("style_item_${style.id}"),
        color = Color(0xFF11132D)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = style.name,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF38153A))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = style.toneTag,
                            color = Color(0xFFF472B6),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = style.description,
                    color = Color(0xFF94A3B8),
                    fontSize = 11.5.sp
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            if (isEquipped) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF500724))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = strings.equippedLabel,
                        color = Color(0xFFF472B6),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp
                    )
                }
            } else if (isUnlocked) {
                Button(
                    onClick = onSelect,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBE185D)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(strings.equipAction, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = onSelect,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1B4B)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFDE047),
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${style.priceStardust}", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
