package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ADMIN_PHONE
import com.example.data.model.SupportMessage
import com.example.ui.components.launchPhoneDialer
import com.example.ui.components.launchWhatsApp
import com.example.ui.theme.BrandAccent
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSuccess
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CustomerServiceScreen(
    messages: List<SupportMessage>,
    onSendMessage: (String) -> Unit
) {
    val context = LocalContext.current
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Auto-scroll on new messages
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    var expandedFaqIndex by remember { mutableStateOf<Int?>(null) }

    val quickQuestions = listOf(
        "How does the 20% commission work?",
        "How do I sell my Video or House design?",
        "How to download purchased files?",
        "Can I talk to Admin at 9241185235?",
        "What payment methods are supported?"
    )

    val faqs = listOf(
        "Why is there a 20% platform commission?" to "For every sale on DesignBazaar, 20% platform fee is deducted to ensure secure online payment processing, digital escrow buyer protection, continuous cloud hosting, and support operations managed by Admin ($ADMIN_PHONE). The creator receives 80% directly into their wallet.",
        "How do sellers receive payments?" to "Once a customer buys your digital design, 80% net amount is instantly credited to your DesignBazaar Seller Wallet. You can withdraw anytime to your bank account or UPI ID with zero withdrawal delay.",
        "Which digital designs can be bought and sold?" to "DesignBazaar currently supports 4 major categories: 1) Video templates & motion graphics, 2) High-resolution photos & wallpapers, 3) 2D/3D House designs & CAD blueprints, and 4) T-shirt graphics & vector merchandise designs.",
        "How do buyers receive files after online payment?" to "As soon as your online payment is verified, you are redirected to 'My Downloads' where full resolution source files (e.g. .MP4, .RAW, .DWG, .AI) can be downloaded anytime.",
        "Direct Contact & Helpline:" to "You can call or WhatsApp our official platform hotline directly at +91 $ADMIN_PHONE anytime for priority assistance."
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("customer_service_screen")
    ) {
        // Support Header Banner
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = BrandPrimary.copy(alpha = 0.15f),
                            modifier = Modifier.size(46.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.SupportAgent,
                                    contentDescription = null,
                                    tint = BrandPrimary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "DesignBazaar Help Desk",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(BrandSuccess)
                                )
                            }
                            Text(
                                text = "Admin Hotline: +91 $ADMIN_PHONE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }

                    // Direct Call button
                    Button(
                        onClick = { launchPhoneDialer(context, ADMIN_PHONE) },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("support_direct_call_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Call", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // WhatsApp action chip
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = BrandSuccess.copy(alpha = 0.15f),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { launchWhatsApp(context, ADMIN_PHONE) }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = null,
                                tint = BrandSuccess,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "WhatsApp: $ADMIN_PHONE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = BrandSuccess,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = BrandPrimary.copy(alpha = 0.1f),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = BrandPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "20% Escrow Protected",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = BrandPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }

        // Live Chat Message List + FAQs
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // FAQ Section Header
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.HelpOutline,
                                contentDescription = null,
                                tint = BrandPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Frequently Asked Questions",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        faqs.forEachIndexed { index, (question, answer) ->
                            val isExpanded = expandedFaqIndex == index
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        expandedFaqIndex = if (isExpanded) null else index
                                    }
                                    .padding(vertical = 6.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = question,
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                        modifier = Modifier.weight(1f)
                                    )
                                    Icon(
                                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                if (isExpanded) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = answer,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            lineHeight = 18.sp
                                        ),
                                        modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                                    )
                                }
                            }
                            if (index < faqs.size - 1) {
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            }
                        }
                    }
                }
            }

            // Chat divider
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f))
                    Text(
                        text = " Live Conversation ",
                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f))
                }
            }

            // Message Bubbles
            items(messages, key = { it.id }) { msg ->
                val isUser = msg.sender == "USER"
                val timeStr = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(msg.timestamp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    Surface(
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isUser) 16.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 16.dp
                        ),
                        color = if (isUser) BrandPrimary else MaterialTheme.colorScheme.surface,
                        shadowElevation = 1.dp,
                        modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = if (isUser) "You" else "DesignBazaar Support",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isUser) Color.White.copy(alpha = 0.9f) else BrandPrimary
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = msg.message,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 20.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = timeStr,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (isUser) Color.White.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.align(Alignment.End)
                            )
                        }
                    }
                }
            }
        }

        // Quick Suggestion Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(quickQuestions) { q ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.clickable { onSendMessage(q) }
                ) {
                    Text(
                        text = q,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Input Bar
        Surface(
            shadowElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
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
                    placeholder = { Text("Ask about buying, selling, 20% cut...") },
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("support_input_field")
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
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(BrandPrimary)
                        .testTag("support_send_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send Message",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
