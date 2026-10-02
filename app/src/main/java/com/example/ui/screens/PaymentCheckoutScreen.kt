package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.data.model.ADMIN_PHONE
import com.example.data.model.Order
import com.example.data.model.PLATFORM_COMMISSION_RATE
import com.example.data.model.PaymentMethod
import com.example.data.model.Product
import com.example.data.model.SELLER_PAYOUT_RATE
import com.example.ui.components.formatInr
import com.example.ui.theme.BrandAccent
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.BrandSuccess
import com.example.ui.viewmodel.PaymentUiState

@Composable
fun PaymentCheckoutScreen(
    checkoutProducts: List<Product>,
    paymentState: PaymentUiState,
    onProcessPayment: (List<Product>, PaymentMethod) -> Unit,
    onViewPurchases: () -> Unit,
    onReturnToHome: () -> Unit
) {
    var selectedMethod by remember { mutableStateOf(PaymentMethod.UPI_GPAY) }
    var upiIdInput by remember { mutableStateOf("buyer@okhdfcbank") }
    var cardNumberInput by remember { mutableStateOf("4532 •••• •••• 8912") }

    val totalAmount = checkoutProducts.sumOf { it.price }
    val platformFee = totalAmount * PLATFORM_COMMISSION_RATE
    val sellerPayout = totalAmount * SELLER_PAYOUT_RATE

    when (paymentState) {
        is PaymentUiState.Processing -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .testTag("payment_processing_state"),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    modifier = Modifier.padding(32.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        CircularProgressIndicator(
                            color = BrandPrimary,
                            strokeWidth = 4.dp,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "Securing Online Payment...",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Processing ${formatInr(totalAmount)} via NPCI / RBI Secure Gateway",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "20% Platform Commission (${formatInr(platformFee)}) safely routed to Admin: $ADMIN_PHONE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = BrandPrimary,
                                fontWeight = FontWeight.SemiBold
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        is PaymentUiState.Success -> {
            val orders = paymentState.orders
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
                    .testTag("payment_success_state"),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                Surface(
                    shape = CircleShape,
                    color = BrandSuccess.copy(alpha = 0.15f),
                    modifier = Modifier.size(80.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Payment Successful",
                            tint = BrandSuccess,
                            modifier = Modifier.size(54.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Payment Verified & Successful! 🎉",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = BrandSuccess
                    ),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Amount Paid: ${formatInr(totalAmount)}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Invoice Receipt Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "DesignBazaar Tax Invoice",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = BrandSuccess.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "PAID",
                                    color = BrandSuccess,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                        orders.forEach { order ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = order.productTitle,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "Order: ${order.orderNumber} • ${order.fileType}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    )
                                }
                                Text(
                                    text = formatInr(order.totalAmount),
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Creator Earnings (80%):", style = MaterialTheme.typography.bodySmall)
                            Text(text = formatInr(sellerPayout), style = MaterialTheme.typography.bodySmall.copy(color = BrandSuccess, fontWeight = FontWeight.Bold))
                        }
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Platform Commission (20% to Admin $ADMIN_PHONE):", style = MaterialTheme.typography.bodySmall)
                            Text(text = formatInr(platformFee), style = MaterialTheme.typography.bodySmall.copy(color = BrandPrimary, fontWeight = FontWeight.Bold))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onViewPurchases,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("download_purchased_designs_btn")
                ) {
                    Icon(imageVector = Icons.Default.Download, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Go to Downloads Vault",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onReturnToHome,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Continue Shopping")
                }
            }
        }

        else -> {
            // Idle or Error: Payment selection
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
                    .testTag("payment_checkout_screen")
            ) {
                // Header
                Text(
                    text = "Select Online Payment Method",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Instant 100% encrypted digital escrow payment",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Amount to pay card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = BrandPrimary.copy(alpha = 0.08f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Total Payable Amount",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                            Text(
                                text = formatInr(totalAmount),
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = BrandPrimary
                                )
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = BrandPrimary
                        ) {
                            Text(
                                text = "${checkoutProducts.size} Items",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Choose Payment Option",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Payment Methods List
                PaymentMethod.entries.forEach { method ->
                    val isSelected = selectedMethod == method
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) BrandPrimary.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surface
                        ),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, BrandPrimary) else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { selectedMethod = method }
                            .testTag("pay_method_${method.name}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedMethod = method },
                                colors = RadioButtonDefaults.colors(selectedColor = BrandPrimary)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Icon(
                                imageVector = when (method) {
                                    PaymentMethod.UPI_GPAY, PaymentMethod.UPI_CUSTOM -> Icons.Default.QrCode2
                                    PaymentMethod.DEBIT_CREDIT_CARD -> Icons.Default.CreditCard
                                    PaymentMethod.NET_BANKING -> Icons.Default.AccountBalance
                                    PaymentMethod.WALLET -> Icons.Default.AccountBalanceWallet
                                },
                                contentDescription = null,
                                tint = BrandPrimary,
                                modifier = Modifier.size(24.dp)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = method.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = method.description,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Security & Commission Notice
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = BrandSuccess,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "20% Platform Fee & Escrow Protection Active",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BrandSuccess
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Admin Hotline: +91 $ADMIN_PHONE. Files are unlocked instantly upon payment confirmation.",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Pay Button
                Button(
                    onClick = {
                        onProcessPayment(checkoutProducts, selectedMethod)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("confirm_and_pay_button")
                ) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Pay ${formatInr(totalAmount)} Securely",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
