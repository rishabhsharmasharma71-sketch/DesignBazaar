package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.DesignBazaarBottomNav
import com.example.ui.components.DesignBazaarTopBar
import com.example.ui.screens.CartScreen
import com.example.ui.screens.CustomerServiceScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MyPurchasesScreen
import com.example.ui.screens.PaymentCheckoutScreen
import com.example.ui.screens.ProductDetailScreen
import com.example.ui.screens.SellProductScreen
import com.example.ui.screens.SellerDashboardScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MarketplaceViewModel
import com.example.ui.viewmodel.Screen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                DesignBazaarApp()
            }
        }
    }
}

@Composable
fun DesignBazaarApp(
    viewModel: MarketplaceViewModel = viewModel()
) {
    val activeScreen by viewModel.activeScreen.collectAsStateWithLifecycle()
    val filteredProducts by viewModel.filteredProducts.collectAsStateWithLifecycle()
    val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
    val orders by viewModel.orders.collectAsStateWithLifecycle()
    val myListings by viewModel.myListings.collectAsStateWithLifecycle()
    val supportMessages by viewModel.supportMessages.collectAsStateWithLifecycle()
    val sellerStats by viewModel.sellerStats.collectAsStateWithLifecycle()
    val selectedProduct by viewModel.selectedProduct.collectAsStateWithLifecycle()
    val paymentState by viewModel.paymentState.collectAsStateWithLifecycle()
    val userNotification by viewModel.userNotification.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategoryFilter.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userNotification) {
        userNotification?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearNotification()
        }
    }

    // System Back Press handling
    BackHandler(enabled = activeScreen !is Screen.Home) {
        val didPop = viewModel.navigateBack()
        if (!didPop) {
            viewModel.navigateTo(Screen.Home)
        }
    }

    val topBarTitle = when (activeScreen) {
        is Screen.Home -> "DesignBazaar"
        is Screen.ProductDetail -> "Design Details"
        is Screen.SellProduct -> "Sell Digital Design"
        is Screen.Cart -> "My Shopping Cart"
        is Screen.Checkout -> "Online Payment"
        is Screen.MyPurchases -> "Downloads & Vault"
        is Screen.SellerDashboard -> "Seller Dashboard"
        is Screen.CustomerService -> "Customer Support"
    }

    val showBottomNav = activeScreen !is Screen.ProductDetail && activeScreen !is Screen.Checkout

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            DesignBazaarTopBar(
                title = topBarTitle,
                canNavigateBack = activeScreen !is Screen.Home,
                onNavigateBack = {
                    val didPop = viewModel.navigateBack()
                    if (!didPop) viewModel.navigateTo(Screen.Home)
                },
                cartItemCount = cartItems.size,
                onOpenCart = { viewModel.navigateTo(Screen.Cart) },
                onOpenSupport = { viewModel.navigateTo(Screen.CustomerService) }
            )
        },
        bottomBar = {
            if (showBottomNav) {
                DesignBazaarBottomNav(
                    currentScreen = activeScreen,
                    onSelectScreen = { screen -> viewModel.navigateTo(screen) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val screen = activeScreen) {
                is Screen.Home -> {
                    HomeScreen(
                        products = filteredProducts,
                        searchQuery = searchQuery,
                        selectedCategory = selectedCategory,
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        onCategorySelect = { viewModel.setCategoryFilter(it) },
                        onProductClick = { productId -> viewModel.selectProduct(productId) },
                        onAddToCart = { productId -> viewModel.addToCart(productId) },
                        onSellClick = { viewModel.navigateTo(Screen.SellProduct) }
                    )
                }

                is Screen.ProductDetail -> {
                    ProductDetailScreen(
                        product = selectedProduct,
                        onAddToCart = { productId -> viewModel.addToCart(productId) },
                        onBuyNow = { product ->
                            viewModel.startCheckout(listOf(product))
                        }
                    )
                }

                is Screen.SellProduct -> {
                    SellProductScreen(
                        onPublishListing = { title, category, price, desc, fileType, fileSize, preview, tags ->
                            viewModel.createListing(
                                title = title,
                                category = category,
                                price = price,
                                description = desc,
                                fileType = fileType,
                                fileSize = fileSize,
                                drawableResName = preview,
                                tags = tags
                            )
                        }
                    )
                }

                is Screen.SellerDashboard -> {
                    SellerDashboardScreen(
                        sellerStats = sellerStats,
                        myListings = myListings,
                        onAddNewListing = { viewModel.navigateTo(Screen.SellProduct) },
                        onDeleteListing = { productId -> viewModel.deleteListing(productId) }
                    )
                }

                is Screen.Cart -> {
                    CartScreen(
                        cartItems = cartItems,
                        onRemoveFromCart = { productId -> viewModel.removeFromCart(productId) },
                        onProceedToCheckout = { products ->
                            viewModel.startCheckout(products)
                        },
                        onExploreDesigns = { viewModel.navigateTo(Screen.Home) }
                    )
                }

                is Screen.Checkout -> {
                    PaymentCheckoutScreen(
                        checkoutProducts = screen.checkoutProducts,
                        paymentState = paymentState,
                        onProcessPayment = { products, method ->
                            viewModel.processPayment(products, method)
                        },
                        onViewPurchases = {
                            viewModel.resetPaymentState()
                            viewModel.navigateTo(Screen.MyPurchases)
                        },
                        onReturnToHome = {
                            viewModel.resetPaymentState()
                            viewModel.navigateTo(Screen.Home)
                        }
                    )
                }

                is Screen.MyPurchases -> {
                    MyPurchasesScreen(
                        orders = orders,
                        onDownloadAsset = { orderId -> viewModel.markDownloaded(orderId) },
                        onExploreDesigns = { viewModel.navigateTo(Screen.Home) }
                    )
                }

                is Screen.CustomerService -> {
                    CustomerServiceScreen(
                        messages = supportMessages,
                        onSendMessage = { query -> viewModel.sendSupportMessage(query) }
                    )
                }
            }
        }
    }
}
