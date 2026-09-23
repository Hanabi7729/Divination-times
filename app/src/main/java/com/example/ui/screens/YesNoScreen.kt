package com.example.ui.screens

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CrystalCatalog
import com.example.model.VerdictType
import com.example.ui.components.CrystalBallView
import com.example.viewmodel.OracleViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun YesNoScreen(
    viewModel: OracleViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val strings by viewModel.strings.collectAsState()
    val question by viewModel.yesNoQuestion.collectAsState()
    val isDivining by viewModel.isDiviningYesNo.collectAsState()
    val answer by viewModel.currentYesNoAnswer.collectAsState()
    val selectedSkinId by viewModel.selectedSkinId.collectAsState()
    val selectedAnimationId by viewModel.selectedAnimationId.collectAsState()
    val activeSkin = CrystalCatalog.getSkin(selectedSkinId)
    val activeStyle by viewModel.selectedStyleId.collectAsState()

    // Speech Recognizer setup
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!matches.isNullOrEmpty()) {
                viewModel.setYesNoQuestion(matches[0])
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            try {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_PROMPT, strings.voiceListening)
                }
                speechLauncher.launch(intent)
            } catch (_: Exception) {}
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Question Input Field with Mic & Clear
        OutlinedTextField(
            value = question,
            onValueChange = { viewModel.setYesNoQuestion(it) },
            placeholder = {
                Text(
                    text = strings.askQuestionHint,
                    color = Color(0xFF64748B),
                    fontSize = 14.sp
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("yes_no_question_input"),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF131533),
                unfocusedContainerColor = Color(0xFF101229),
                focusedBorderColor = activeSkin.glowColor,
                unfocusedBorderColor = Color(0xFF282B54),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            trailingIcon = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (question.isNotBlank()) {
                        IconButton(onClick = { viewModel.setYesNoQuestion("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = Color(0xFF94A3B8)
                            )
                        }
                    }
                    IconButton(
                        onClick = {
                            permissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
                        },
                        modifier = Modifier.testTag("microphone_button")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(activeSkin.glowColor.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = strings.speakQuestion,
                                tint = activeSkin.accentColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            },
            singleLine = false,
            maxLines = 3
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Quick Preset Suggestions
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            strings.presetYesNoQuestions.take(3).forEach { suggestion ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF191B3E))
                        .clickable { viewModel.setYesNoQuestion(suggestion) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = suggestion,
                        color = Color(0xFFCBD5E1),
                        fontSize = 11.5.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Interactive Crystal Ball View
        CrystalBallView(
            skin = activeSkin,
            animationId = selectedAnimationId,
            isDivining = isDivining,
            size = 250.dp,
            onRubbed = {
                if (!isDivining) {
                    viewModel.divineYesNo()
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Interaction Hint
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

        // Primary Divine Button
        Button(
            onClick = { viewModel.divineYesNo() },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("divine_yes_no_button"),
            enabled = !isDivining,
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
                    text = strings.divineButton,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Revealed Prophecy Card
        AnimatedVisibility(
            visible = answer != null,
            enter = fadeIn() + slideInVertically(initialOffsetY = { 40 })
        ) {
            answer?.let { ans ->
                val isAffirmative = ans.verdict == VerdictType.YES
                val verdictColor = if (isAffirmative) Color(0xFF10B981) else Color(0xFFF43F5E)
                val badgeText = if (isAffirmative) "YES" else "NO"

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, verdictColor.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                        .testTag("yes_no_result_card"),
                    color = Color(0xFF11132D)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Prophecy Index out of 1,200+
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Prophecy #${ans.idNumber} of 1,200+",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(verdictColor.copy(alpha = 0.2f))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = badgeText,
                                    color = verdictColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Short Answer Headline
                        Text(
                            text = ans.shortAnswer,
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Cosmic Wisdom Insight
                        Text(
                            text = ans.cosmicWisdom,
                            color = Color(0xFFE2E8F0),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Alignment Percentage
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
                                text = "${ans.alignmentPercentage}%",
                                color = activeSkin.accentColor,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        LinearProgressIndicator(
                            progress = { ans.alignmentPercentage / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = activeSkin.accentColor,
                            trackColor = Color(0xFF1E2248)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Replay consultation button
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF1A1D40))
                                .clickable { viewModel.divineYesNo() }
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
