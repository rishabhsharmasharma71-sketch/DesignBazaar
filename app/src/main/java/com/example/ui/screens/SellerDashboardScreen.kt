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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import com.example.data.model.Product
import com.example.data.model.SellerStats
import com.example.ui.components.CategoryBadge
import com.example.ui.components.formatInr
import com.example.ui.components.getProductDrawableId
import com.example.ui.theme.BrandAccent
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSuccess

@Composable
fun SellerDashboardScreen(
    sellerStats: SellerStats,
    myListings: List<Product>,
    onAddNewListing: () -> Unit,
    onDeleteListing: (Long) -> Unit
) {
    var showWithdrawModal by remember { mutableStateOf(false) }
    var withdrawalUpi by remember { mutableStateOf("") }
    var withdrawalSuccess by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .testTag("seller_dashboard_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Earnings Overview Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = BrandPrimary
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = "Seller Wallet Net Balance",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = formatInr(sellerStats.netEarnings),
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = 0.2f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = BrandSuccess,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "80% Payout Share",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Breakdown row: Gross vs 20% Platform Cut
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = 0.12f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Gross Sales",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                )
                                Text(
                                    text = formatInr(sellerStats.grossSales),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                                Text(
                                    text = "${sellerStats.totalSalesUnits} Orders Sold",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White.copy(alpha = 0.7f),
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = 0.12f),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "20% Platform Fee",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                )
                                Text(
                                    text = formatInr(sellerStats.totalPlatformFees),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = BrandAccent
                                    )
                                )
                                Text(
                                    text = "Admin: $ADMIN_PHONE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White.copy(alpha = 0.7f),
                                        fontSize = 11.sp
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Withdraw Button
                    Button(
                        onClick = { showWithdrawModal = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandSuccess,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("withdraw_payout_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalance,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Withdraw to UPI / Bank Account",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Action header: My Listings
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "My Uploaded Designs",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "${myListings.size} active listings in marketplace",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                Button(
                    onClick = onAddNewListing,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("add_new_listing_btn")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add New")
                }
            }
        }

        if (myListings.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = null,
                            tint = BrandPrimary,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No listings yet",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "You haven't listed any designs yet. Upload your Videos, Photos, House Blueprints, or T-Shirt graphics now to start earning 80% per sale!",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onAddNewListing,
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Create First Listing")
                        }
                    }
                }
            }
        } else {
            items(myListings, key = { it.id }) { listing ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = getProductDrawableId(listing.drawableResName)),
                            contentDescription = listing.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(70.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            CategoryBadge(category = listing.category)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = listing.title,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Price: ${formatInr(listing.price)} • Net: ${formatInr(listing.sellerNetEarnings)}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = BrandSuccess,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }

                        IconButton(
                            onClick = { onDeleteListing(listing.id) },
                            modifier = Modifier.testTag("delete_listing_${listing.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Listing",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }

    // Withdrawal Modal
    if (showWithdrawModal) {
        AlertDialog(
            onDismissRequest = { showWithdrawModal = false },
            title = {
                Text(
                    text = "Withdraw Payout",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column {
                    Text(
                        text = "Transfer your earnings (${formatInr(sellerStats.netEarnings)}) instantly to your bank account via UPI or NEFT.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = withdrawalUpi,
                        onValueChange = { withdrawalUpi = it },
                        label = { Text("UPI ID (e.g. mobile@paytm or name@okaxis)") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (withdrawalSuccess) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Withdrawal request submitted! Payout processed in 15 mins.",
                            color = BrandSuccess,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (withdrawalUpi.isNotBlank()) {
                            withdrawalSuccess = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandSuccess)
                ) {
                    Text("Confirm Payout")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showWithdrawModal = false
                    withdrawalSuccess = false
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}
