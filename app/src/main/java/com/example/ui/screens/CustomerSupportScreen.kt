package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SupportMessage
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CustomerSupportScreen(
    messages: List<SupportMessage>,
    onSendMessage: (String) -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("customer_support_screen")
    ) {
        // Header
        Surface(
            color = DarkSurfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(EmeraldGreen.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🎧", fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "ELISHA BET 24/7 SUPPORT",
                        color = GoldPrimary,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(EmeraldGreen)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Specialist Online • Instant Replies",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Quick Query Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val chips = listOf(
                "How to deposit with M-Pesa?",
                "How does Acca Boost work?",
                "Withdrawal processing time",
                "KYC Verification documents",
                "How to Cash Out live?"
            )
            items(chips) { query ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = CardBackground,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
                    modifier = Modifier.clip(RoundedCornerShape(14.dp))
                ) {
                    TextButton(
                        onClick = { onSendMessage(query) },
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text(text = query, color = GoldLight, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        // Chat Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 10.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                ChatBubble(message = msg)
            }
        }

        // Bottom Input Row
        Surface(
            color = DarkSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder),
            modifier = Modifier.fillMaxWidth().padding(bottom = 70.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Ask a question about your bets or account...", fontSize = 12.sp, color = TextMuted) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("support_message_input"),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = CardBorder,
                        focusedContainerColor = CardBackground,
                        unfocusedContainerColor = CardBackground,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            onSendMessage(inputText)
                            inputText = ""
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(GoldPrimary)
                        .testTag("send_support_message_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = LuxuryBlack,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: SupportMessage) {
    val isUser = message.isFromUser
    val timeStr = remember(message.timestamp) {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        sdf.format(Date(message.timestamp))
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Text(
            text = message.sender,
            color = if (isUser) TextMuted else GoldPrimary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        )

        Surface(
            color = if (isUser) Color(0xFF1E3A2B) else DarkSurfaceVariant,
            shape = RoundedCornerShape(
                topStart = 12.dp,
                topEnd = 12.dp,
                bottomStart = if (isUser) 12.dp else 2.dp,
                bottomEnd = if (isUser) 2.dp else 12.dp
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isUser) EmeraldGreen.copy(alpha = 0.5f) else CardBorder
            ),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = message.text,
                    color = TextPrimary,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = timeStr,
                    color = TextMuted,
                    fontSize = 9.sp,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}
