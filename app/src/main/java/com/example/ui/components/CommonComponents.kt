package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ADMIN_PHONE
import com.example.data.model.DesignCategory
import com.example.data.model.Product
import com.example.ui.theme.BrandAccent
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.BrandSuccess
import com.example.ui.viewmodel.Screen
import java.text.NumberFormat
import java.util.Locale

fun formatInr(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    return formatter.format(amount).replace("INR", "₹").trim()
}

fun launchPhoneDialer(context: Context, phoneNumber: String = ADMIN_PHONE) {
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:$phoneNumber")
    }
    context.startActivity(intent)
}

fun launchWhatsApp(context: Context, phoneNumber: String = ADMIN_PHONE, message: String = "Hello DesignBazaar Support! I have a question about digital designs & selling.") {
    try {
        val url = "https://api.whatsapp.com/send?phone=91$phoneNumber&text=${Uri.encode(message)}"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    } catch (_: Exception) {
        launchPhoneDialer(context, phoneNumber)
    }
}

@Composable
fun getProductDrawableId(resName: String): Int {
    return when (resName) {
        "preview_video" -> R.drawable.preview_video
        "preview_house" -> R.drawable.preview_house
        "preview_tshirt" -> R.drawable.preview_tshirt
        "preview_photo" -> R.drawable.preview_photo
        else -> R.drawable.hero_banner
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DesignBazaarTopBar(
    title: String,
    canNavigateBack: Boolean,
    onNavigateBack: () -> Unit,
    cartItemCount: Int,
    onOpenCart: () -> Unit,
    onOpenSupport: () -> Unit
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = BrandPrimary,
                    modifier = Modifier.size(36.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_designbazaar_logo),
                        contentDescription = "DesignBazaar Logo",
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Buy & Sell Designs",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.testTag("nav_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go Back"
                    )
                }
            }
        },
        actions = {
            IconButton(
                onClick = onOpenSupport,
                modifier = Modifier.testTag("top_support_button")
            ) {
                Icon(
                    imageVector = Icons.Default.SupportAgent,
                    contentDescription = "Customer Support Helpline",
                    tint = BrandPrimary
                )
            }
            IconButton(
                onClick = onOpenCart,
                modifier = Modifier.testTag("top_cart_button")
            ) {
                BadgedBox(
                    badge = {
                        if (cartItemCount > 0) {
                            Badge(
                                containerColor = BrandAccent,
                                contentColor = Color.Black
                            ) {
                                Text(
                                    text = "$cartItemCount",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Shopping Cart"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun DesignBazaarBottomNav(
    currentScreen: Screen,
    onSelectScreen: (Screen) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentScreen is Screen.Home,
            onClick = { onSelectScreen(Screen.Home) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Market") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BrandPrimary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.testTag("bottom_nav_home")
        )
        NavigationBarItem(
            selected = currentScreen is Screen.SellProduct,
            onClick = { onSelectScreen(Screen.SellProduct) },
            icon = { Icon(Icons.Default.AddCircle, contentDescription = "Sell Design", tint = BrandAccent) },
            label = { Text("Sell (+)", fontWeight = FontWeight.Bold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BrandAccent,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.testTag("bottom_nav_sell")
        )
        NavigationBarItem(
            selected = currentScreen is Screen.SellerDashboard,
            onClick = { onSelectScreen(Screen.SellerDashboard) },
            icon = { Icon(Icons.Default.MonetizationOn, contentDescription = "Seller Dashboard") },
            label = { Text("Earnings") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BrandSuccess,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.testTag("bottom_nav_dashboard")
        )
        NavigationBarItem(
            selected = currentScreen is Screen.MyPurchases,
            onClick = { onSelectScreen(Screen.MyPurchases) },
            icon = { Icon(Icons.Default.Download, contentDescription = "My Downloads") },
            label = { Text("Vault") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BrandPrimary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.testTag("bottom_nav_purchases")
        )
        NavigationBarItem(
            selected = currentScreen is Screen.CustomerService,
            onClick = { onSelectScreen(Screen.CustomerService) },
            icon = { Icon(Icons.Default.SupportAgent, contentDescription = "Customer Support") },
            label = { Text("Support") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BrandPrimary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.testTag("bottom_nav_support")
        )
    }
}

@Composable
fun CategoryBadge(
    category: DesignCategory,
    modifier: Modifier = Modifier
) {
    Surface(
        color = category.badgeColor.copy(alpha = 0.15f),
        shape = RoundedCornerShape(6.dp),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(category.badgeColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = category.displayName,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = category.badgeColor,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
fun CommissionNoticeBanner(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = BrandPrimary.copy(alpha = 0.08f)
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = null,
                    tint = BrandPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Official 20% Platform Commission Model",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = BrandPrimary
                    )
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "When any seller earns, 20% platform fee goes directly to Admin & maintenance (Help: +91 $ADMIN_PHONE). 80% is credited directly to the creator.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = BrandPrimary,
                    modifier = Modifier.clickable { launchPhoneDialer(context, ADMIN_PHONE) }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Call Support: $ADMIN_PHONE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    onProductClick: () -> Unit,
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onProductClick() }
            .testTag("product_card_${product.id}")
    ) {
        Column {
            // Thumbnail with Category overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                Image(
                    painter = painterResource(id = getProductDrawableId(product.drawableResName)),
                    contentDescription = product.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth()
                )

                // Category tag on top left
                CategoryBadge(
                    category = product.category,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                )

                // File type badge on bottom right
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color.Black.copy(alpha = 0.7f),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                ) {
                    Text(
                        text = product.fileSize,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Info details
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = BrandAccent,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${product.rating} (${product.reviewCount})",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }

                    Text(
                        text = "By ${product.sellerName}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = formatInr(product.price),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = BrandPrimary
                            )
                        )
                        Text(
                            text = "Commercial Use",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = BrandSuccess,
                                fontSize = 10.sp
                            )
                        )
                    }

                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier
                            .clickable { onAddToCart() }
                            .testTag("add_to_cart_btn_${product.id}")
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Add to Cart",
                                tint = BrandPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
