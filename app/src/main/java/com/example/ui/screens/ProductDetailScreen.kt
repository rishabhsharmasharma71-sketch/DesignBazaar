package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ADMIN_PHONE
import com.example.data.model.Product
import com.example.ui.components.CategoryBadge
import com.example.ui.components.formatInr
import com.example.ui.components.getProductDrawableId
import com.example.ui.components.launchPhoneDialer
import com.example.ui.theme.BrandAccent
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSuccess

@Composable
fun ProductDetailScreen(
    product: Product?,
    onAddToCart: (Long) -> Unit,
    onBuyNow: (Product) -> Unit
) {
    if (product == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Product details unavailable.")
        }
        return
    }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Scrollable content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            // Hero Preview Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            ) {
                Image(
                    painter = painterResource(id = getProductDrawableId(product.drawableResName)),
                    contentDescription = product.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Category tag overlay
                CategoryBadge(
                    category = product.category,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                )

                // Commercial badge overlay
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.75f),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = BrandSuccess,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Commercial License",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            // Product Header Info
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = BrandAccent.copy(alpha = 0.2f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = BrandAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${product.rating}",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = BrandAccent
                                    )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${product.reviewCount} customer reviews • ${product.downloadCount}+ downloads",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Price display
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = BrandPrimary.copy(alpha = 0.06f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    text = "One-Time Purchase Price",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                                Text(
                                    text = formatInr(product.price),
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        color = BrandPrimary
                                    )
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = BrandSuccess.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "Instant File Access",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = BrandSuccess,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )

                        // 20% platform commission breakdown disclosure
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = BrandPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Transparent Pricing Breakdown:",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BrandPrimary
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• 80% to Creator (${formatInr(product.sellerNetEarnings)})\n• 20% Platform & Escrow Fee (${formatInr(product.platformFee)}) - Admin (+91 $ADMIN_PHONE)",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 18.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // File Specifications
                Text(
                    text = "File Specifications & Assets Included",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FolderZip,
                                contentDescription = null,
                                tint = BrandPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "File Format: ",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = product.fileType,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = null,
                                tint = BrandPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Download Size: ",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = product.fileSize,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = BrandSuccess,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "License: ",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = product.licenseType,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Description
                Text(
                    text = "Description & Details",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 22.sp
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Creator & Support Section
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Column {
                            Text(
                                text = "Seller: ${product.sellerName}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "Support Hotline: +91 $ADMIN_PHONE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }

                        Button(
                            onClick = { launchPhoneDialer(context, ADMIN_PHONE) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandPrimary.copy(alpha = 0.15f),
                                contentColor = BrandPrimary
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Call Admin")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Bottom Fixed Action Bar
        Surface(
            shadowElevation = 16.dp,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { onAddToCart(product.id) },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("detail_add_to_cart_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add to Cart")
                }

                Button(
                    onClick = { onBuyNow(product) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandPrimary
                    ),
                    modifier = Modifier
                        .weight(1.3f)
                        .height(50.dp)
                        .testTag("detail_buy_now_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Buy Now (${formatInr(product.price)})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}
