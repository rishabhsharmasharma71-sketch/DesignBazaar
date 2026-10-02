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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.data.model.CartItem
import com.example.data.model.PLATFORM_COMMISSION_RATE
import com.example.data.model.Product
import com.example.data.model.SELLER_PAYOUT_RATE
import com.example.ui.components.CategoryBadge
import com.example.ui.components.formatInr
import com.example.ui.components.getProductDrawableId
import com.example.ui.theme.BrandAccent
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSuccess

@Composable
fun CartScreen(
    cartItems: List<CartItem>,
    onRemoveFromCart: (Long) -> Unit,
    onProceedToCheckout: (List<Product>) -> Unit,
    onExploreDesigns: () -> Unit
) {
    if (cartItems.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp)
                .testTag("empty_cart_screen"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.size(90.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            tint = BrandPrimary,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Your Cart is Empty",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Explore 4K Videos, Photos, House Plans, and T-Shirt Designs from creators across India.",
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
                    Text("Explore Marketplace")
                }
            }
        }
        return
    }

    val totalAmount = cartItems.sumOf { it.product.price }
    val platformCut = totalAmount * PLATFORM_COMMISSION_RATE
    val creatorEarnings = totalAmount * SELLER_PAYOUT_RATE

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("cart_screen")
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Review Cart (${cartItems.size} items)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            items(cartItems, key = { it.id }) { item ->
                val prod = item.product
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
                            painter = painterResource(id = getProductDrawableId(prod.drawableResName)),
                            contentDescription = prod.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(75.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            CategoryBadge(category = prod.category)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = prod.title,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                maxLines = 1
                            )
                            Text(
                                text = prod.fileType,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = formatInr(prod.price),
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = BrandPrimary
                                )
                            )
                        }

                        IconButton(
                            onClick = { onRemoveFromCart(prod.id) },
                            modifier = Modifier.testTag("remove_cart_item_${prod.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Remove",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            // Price Breakdown Summary
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Payment & Commission Summary",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Item Subtotal",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = formatInr(totalAmount),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "• Creators' Payout (80%)",
                                style = MaterialTheme.typography.bodySmall.copy(color = BrandSuccess)
                            )
                            Text(
                                text = formatInr(creatorEarnings),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = BrandSuccess,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "• Platform Escrow & Support (20%)",
                                style = MaterialTheme.typography.bodySmall.copy(color = BrandPrimary)
                            )
                            Text(
                                text = formatInr(platformCut),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = BrandPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Total Due (Instant Access)",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = formatInr(totalAmount),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = BrandPrimary
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = BrandSuccess,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Protected by DesignBazaar 100% Escrow Guarantee. Support: $ADMIN_PHONE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }
        }

        // Bottom Checkout Bar
        Surface(
            shadowElevation = 16.dp,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Total Payable",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Text(
                        text = formatInr(totalAmount),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = BrandPrimary
                        )
                    )
                }

                Button(
                    onClick = {
                        val productsToCheckout = cartItems.map { it.product }
                        onProceedToCheckout(productsToCheckout)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .height(50.dp)
                        .testTag("cart_proceed_to_checkout_btn")
                ) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Checkout & Pay",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}
