package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileDownloadDone
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ADMIN_PHONE
import com.example.data.model.Order
import com.example.ui.components.CategoryBadge
import com.example.ui.components.formatInr
import com.example.ui.components.getProductDrawableId
import com.example.ui.theme.BrandAccent
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSuccess
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MyPurchasesScreen(
    orders: List<Order>,
    onDownloadAsset: (Long) -> Unit,
    onExploreDesigns: () -> Unit
) {
    var selectedOrderForInvoice by remember { mutableStateOf<Order?>(null) }

    if (orders.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp)
                .testTag("empty_purchases_screen"),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.size(80.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            tint = BrandPrimary,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No Purchases Yet",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Designs you buy will be stored permanently here with unlimited instant downloads and commercial licenses.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onExploreDesigns,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Explore Designs")
                }
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .testTag("my_purchases_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "My Digital Vault & Downloads (${orders.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "Download source files anytime with full commercial rights",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }

        items(orders, key = { it.id }) { order ->
            val isDownloaded = order.downloadStatus == "DOWNLOADED"
            val dateFormatted = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(order.purchaseTimestamp))

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = getProductDrawableId(order.drawableResName)),
                            contentDescription = order.productTitle,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(75.dp)
                                .clip(RoundedCornerShape(10.dp))
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            CategoryBadge(category = order.productCategory)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = order.productTitle,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                maxLines = 1
                            )
                            Text(
                                text = "${order.fileType} • ${order.fileSize}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Paid: ${formatInr(order.totalAmount)} (${order.paymentMethod})",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = BrandPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = dateFormatted,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { selectedOrderForInvoice = order },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ReceiptLong,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Invoice", fontSize = 12.sp)
                            }

                            Button(
                                onClick = { onDownloadAsset(order.id) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isDownloaded) BrandSuccess else BrandPrimary
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("download_asset_btn_${order.id}")
                            ) {
                                Icon(
                                    imageVector = if (isDownloaded) Icons.Default.FileDownloadDone else Icons.Default.Download,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isDownloaded) "Downloaded" else "Download File",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Tax Invoice Modal
    selectedOrderForInvoice?.let { order ->
        AlertDialog(
            onDismissRequest = { selectedOrderForInvoice = null },
            title = {
                Text(
                    text = "Tax Invoice & License",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column {
                    Text(
                        text = "DesignBazaar Digital Invoice: ${order.invoiceUrl}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = BrandPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Product: ${order.productTitle}")
                    Text("Transaction ID: ${order.transactionId}")
                    Text("Payment Method: ${order.paymentMethod}")
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Total Price: ${formatInr(order.totalAmount)}", fontWeight = FontWeight.Bold)
                    Text("• Creator Payout (80%): ${formatInr(order.sellerPayout)}", color = BrandSuccess)
                    Text("• Platform Commission (20%): ${formatInr(order.platformCommission)}", color = BrandPrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Official Admin Contact: +91 $ADMIN_PHONE",
                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            },
            confirmButton = {
                Button(onClick = { selectedOrderForInvoice = null }) {
                    Text("Done")
                }
            }
        )
    }
}
