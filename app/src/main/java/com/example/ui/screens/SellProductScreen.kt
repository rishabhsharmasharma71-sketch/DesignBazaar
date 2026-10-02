package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ADMIN_PHONE
import com.example.data.model.DesignCategory
import com.example.data.model.PLATFORM_COMMISSION_RATE
import com.example.data.model.SELLER_PAYOUT_RATE
import com.example.ui.components.formatInr
import com.example.ui.theme.BrandAccent
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandSecondary
import com.example.ui.theme.BrandSuccess

@Composable
fun SellProductScreen(
    onPublishListing: (
        title: String,
        category: DesignCategory,
        price: Double,
        description: String,
        fileType: String,
        fileSize: String,
        drawableResName: String,
        tags: List<String>
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(DesignCategory.VIDEO) }
    var priceText by remember { mutableStateOf("499") }
    var description by remember { mutableStateOf("") }
    var fileFormat by remember { mutableStateOf(selectedCategory.defaultFormat) }
    var fileSize by remember { mutableStateOf("150 MB") }
    var tagsText by remember { mutableStateOf("New, High Quality, Commercial") }

    // Previews options mapped to categories
    val previewOptions = listOf(
        "preview_video" to "Video Template Preview",
        "preview_house" to "House Design Blueprint Preview",
        "preview_tshirt" to "T-Shirt Mockup Preview",
        "preview_photo" to "High-Res Photo Preview"
    )
    var selectedPreview by remember {
        mutableStateOf(
            when (selectedCategory) {
                DesignCategory.VIDEO -> "preview_video"
                DesignCategory.HOUSE_DESIGN -> "preview_house"
                DesignCategory.TSHIRT_DESIGN -> "preview_tshirt"
                DesignCategory.PHOTO -> "preview_photo"
            }
        )
    }

    val priceVal = priceText.toDoubleOrNull() ?: 0.0
    val platformFee = priceVal * PLATFORM_COMMISSION_RATE // 20%
    val netPayout = priceVal * SELLER_PAYOUT_RATE         // 80%

    var validationError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("sell_product_screen")
    ) {
        // Header
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = BrandAccent.copy(alpha = 0.15f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CloudUpload,
                    contentDescription = null,
                    tint = BrandAccent,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Sell Your Digital Creation",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Upload Video, Photo, House Design, or T-Shirt Design. Anyone can sell!",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Step 1: Select Category
        Text(
            text = "1. Choose Design Category",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DesignCategory.entries.forEach { cat ->
                val isSelected = selectedCategory == cat
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) cat.badgeColor else MaterialTheme.colorScheme.surface,
                    border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant) else null,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            selectedCategory = cat
                            fileFormat = cat.defaultFormat
                            selectedPreview = when (cat) {
                                DesignCategory.VIDEO -> "preview_video"
                                DesignCategory.HOUSE_DESIGN -> "preview_house"
                                DesignCategory.TSHIRT_DESIGN -> "preview_tshirt"
                                DesignCategory.PHOTO -> "preview_photo"
                            }
                        }
                        .testTag("sell_cat_${cat.id}")
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp)
                    ) {
                        Text(
                            text = cat.displayName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            ),
                            maxLines = 1
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Step 2: Title
        Text(
            text = "2. Title of Your Design",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = title,
            onValueChange = {
                title = it
                validationError = null
            },
            placeholder = {
                Text(
                    when (selectedCategory) {
                        DesignCategory.VIDEO -> "e.g. 4K Cinematic YouTube Intro Template"
                        DesignCategory.PHOTO -> "e.g. Ladakh Mountain Sunrise 8K Wallpaper"
                        DesignCategory.HOUSE_DESIGN -> "e.g. 30x40 Ft 3BHK Modern Villa 3D Elevation"
                        DesignCategory.TSHIRT_DESIGN -> "e.g. Cyberpunk Neon Dragon Streetwear Tee"
                    }
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("sell_title_input")
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Step 3: Price & Live 20% Commission Calculator
        Text(
            text = "3. Selling Price & Earnings Calculator",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = priceText,
            onValueChange = { priceText = it.filter { char -> char.isDigit() } },
            label = { Text("Price in Indian Rupees (₹)") },
            prefix = { Text("₹ ", fontWeight = FontWeight.Bold) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("sell_price_input")
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Live Commission Breakdown Card
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PieChart,
                            contentDescription = null,
                            tint = BrandPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Revenue Split on Every Sale:",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    Text(
                        text = "Total: ${formatInr(priceVal)}",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                // 80% Seller Share
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "Your Net Earnings (80%)",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = BrandSuccess
                            )
                        )
                        Text(
                            text = "Direct payout to your UPI/Bank Wallet",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                    Text(
                        text = formatInr(netPayout),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = BrandSuccess
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 20% Platform Share
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "DesignBazaar Platform Fee (20%)",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = BrandPrimary
                            )
                        )
                        Text(
                            text = "Goes to Owner/Admin (+91 $ADMIN_PHONE) for server & escrow",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                    Text(
                        text = formatInr(platformFee),
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = BrandPrimary
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Step 4: Description
        Text(
            text = "4. Detailed Description",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            placeholder = { Text("Describe the design layers, color schemes, dimensions, and included files...") },
            minLines = 3,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("sell_desc_input")
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Step 5: Format & Size
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column(modifier = Modifier.weight(1.2f)) {
                Text(
                    text = "File Format",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = fileFormat,
                    onValueChange = { fileFormat = it },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Column(modifier = Modifier.weight(0.8f)) {
                Text(
                    text = "File Size",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = fileSize,
                    onValueChange = { fileSize = it },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Step 6: Preview Image Selection
        Text(
            text = "6. Choose Thumbnail Showcase",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            previewOptions.forEach { (resName, label) ->
                val isSelected = selectedPreview == resName
                val drawableId = when (resName) {
                    "preview_video" -> R.drawable.preview_video
                    "preview_house" -> R.drawable.preview_house
                    "preview_tshirt" -> R.drawable.preview_tshirt
                    "preview_photo" -> R.drawable.preview_photo
                    else -> R.drawable.hero_banner
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(70.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            width = if (isSelected) 3.dp else 1.dp,
                            color = if (isSelected) BrandPrimary else MaterialTheme.colorScheme.outlineVariant,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { selectedPreview = resName }
                ) {
                    Image(
                        painter = painterResource(id = drawableId),
                        contentDescription = label,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    if (isSelected) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = BrandPrimary,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Step 7: Tags
        Text(
            text = "7. Search Tags (Comma separated)",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = tagsText,
            onValueChange = { tagsText = it },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        // Error banner if any
        if (validationError != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = validationError ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Publish Button
        Button(
            onClick = {
                if (title.isBlank()) {
                    validationError = "Please enter a title for your design"
                    return@Button
                }
                if (priceVal <= 0.0) {
                    validationError = "Please enter a valid price (greater than ₹0)"
                    return@Button
                }
                val tagsList = tagsText.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                onPublishListing(
                    title,
                    selectedCategory,
                    priceVal,
                    if (description.isBlank()) "High quality ${selectedCategory.displayName} digital asset ready for commercial use." else description,
                    fileFormat,
                    fileSize,
                    selectedPreview,
                    tagsList
                )
            },
            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("publish_listing_button")
        ) {
            Icon(
                imageVector = Icons.Default.CloudUpload,
                contentDescription = null,
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Publish Design & Start Earning",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
