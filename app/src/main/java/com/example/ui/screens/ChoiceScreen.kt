package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CrystalCatalog
import com.example.ui.components.CrystalBallView
import com.example.viewmodel.OracleViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChoiceScreen(
    viewModel: OracleViewModel,
    modifier: Modifier = Modifier
) {
    val strings by viewModel.strings.collectAsState()
    val options by viewModel.choiceOptions.collectAsState()
    val isDivining by viewModel.isDiviningChoice.collectAsState()
    val result by viewModel.currentChoiceResult.collectAsState()
    val selectedSkinId by viewModel.selectedSkinId.collectAsState()
    val selectedAnimationId by viewModel.selectedAnimationId.collectAsState()
    val activeSkin = CrystalCatalog.getSkin(selectedSkinId)

    var newOptionText by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Quick Template Presets
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PresetChip(
                label = strings.presetFood,
                selected = false,
                onClick = {
                    viewModel.setChoicePreset(listOf("Pizza", "Sushi", "Tacos", "Thai Curry", "Homemade Pasta"))
                }
            )
            PresetChip(
                label = strings.presetWeekend,
                selected = false,
                onClick = {
                    viewModel.setChoicePreset(listOf("Hiking Trail", "Gaming Marathon", "Cinema Night", "Cozy Reading", "Road Trip"))
                }
            )
            PresetChip(
                label = strings.presetFocus,
                selected = false,
                onClick = {
                    viewModel.setChoicePreset(listOf("Deep Study 90m", "Workout & Gym", "Creative Art", "Meditate & Rest"))
                }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Add Option Input Field
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newOptionText,
                onValueChange = { newOptionText = it },
                placeholder = {
                    Text(
                        text = strings.addOption,
                        color = Color(0xFF64748B),
                        fontSize = 13.5.sp
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("new_option_input"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF131533),
                    unfocusedContainerColor = Color(0xFF101229),
                    focusedBorderColor = activeSkin.glowColor,
                    unfocusedBorderColor = Color(0xFF282B54),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    if (newOptionText.isNotBlank()) {
                        viewModel.addChoiceOption(newOptionText)
                        newOptionText = ""
                    }
                },
                modifier = Modifier
                    .height(54.dp)
                    .testTag("add_option_button"),
                colors = ButtonDefaults.buttonColors(containerColor = activeSkin.glowColor),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Active Options Chips
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("options_chips_row"),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { opt ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E214A),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF383D7C))
                ) {
                    Row(
                        modifier = Modifier.padding(start = 12.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = opt,
                            color = Color(0xFFF1F5F9),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        IconButton(
                            onClick = { viewModel.removeChoiceOption(opt) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            }
        }

        if (options.size < 2) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = strings.needOptionsHint,
                color = Color(0xFFFBBF24),
                fontSize = 11.5.sp
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Animated Crystal Ball
        CrystalBallView(
            skin = activeSkin,
            animationId = selectedAnimationId,
            isDivining = isDivining,
            size = 250.dp,
            onRubbed = {
                if (!isDivining && options.size >= 2) {
                    viewModel.divineChoice()
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.TouchApp,
                contentDescription = null,
                tint = Color(0xFF94A3B8),
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = strings.touchToCommune,
                color = Color(0xFF94A3B8),
                fontSize = 11.5.sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Divine My Choice Button
        Button(
            onClick = { viewModel.divineChoice() },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("divine_choice_button"),
            enabled = !isDivining && options.size >= 2,
            colors = ButtonDefaults.buttonColors(
                containerColor = activeSkin.glowColor,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            if (isDivining) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = strings.diviningPrompt,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            } else {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = strings.chooseForMeButton,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Revealed Choice Card
        AnimatedVisibility(
            visible = result != null,
            enter = fadeIn() + slideInVertically(initialOffsetY = { 40 })
        ) {
            result?.let { res ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.5.dp, Color(0xFFF59E0B), RoundedCornerShape(20.dp))
                        .testTag("choice_result_card"),
                    color = Color(0xFF131533)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(Color(0xFF78350F), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = Color(0xFFFDE047),
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "The Sphere Has Chosen:",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = res.selectedOption,
                            color = Color(0xFFFDE047),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = res.propheticReason,
                            color = Color(0xFFE2E8F0),
                            fontSize = 13.5.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 19.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = strings.alignmentLabel,
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp
                            )
                            Text(
                                text = "${res.cosmicHarmony}%",
                                color = Color(0xFF10B981),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        LinearProgressIndicator(
                            progress = { res.cosmicHarmony / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = Color(0xFF10B981),
                            trackColor = Color(0xFF1E2248)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF1A1D40))
                                .clickable { viewModel.divineChoice() }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Replay,
                                contentDescription = null,
                                tint = Color(0xFFA5B4FC),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = strings.askAgain,
                                color = Color(0xFFA5B4FC),
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun PresetChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) Color(0xFF4338CA) else Color(0xFF161838))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 7.dp)
    ) {
        Text(
            text = label,
            color = if (selected) Color.White else Color(0xFFCBD5E1),
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
