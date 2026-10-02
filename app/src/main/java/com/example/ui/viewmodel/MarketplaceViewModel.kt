package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.CartItem
import com.example.data.model.DesignCategory
import com.example.data.model.Order
import com.example.data.model.PaymentMethod
import com.example.data.model.Product
import com.example.data.model.SellerStats
import com.example.data.model.SupportMessage
import com.example.data.repository.DesignBazaarRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class Screen {
    data object Home : Screen()
    data class ProductDetail(val productId: Long) : Screen()
    data object SellProduct : Screen()
    data object Cart : Screen()
    data class Checkout(val checkoutProducts: List<Product>) : Screen()
    data object MyPurchases : Screen()
    data object SellerDashboard : Screen()
    data object CustomerService : Screen()
}

sealed class PaymentUiState {
    data object Idle : PaymentUiState()
    data object Processing : PaymentUiState()
    data class Success(val orders: List<Order>) : PaymentUiState()
    data class Error(val message: String) : PaymentUiState()
}

class MarketplaceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DesignBazaarRepository

    // Screen navigation stack
    private val _screenStack = MutableStateFlow<List<Screen>>(listOf(Screen.Home))
    val currentScreen: StateFlow<Screen> = MutableStateFlow<Screen>(Screen.Home)

    private val _currentScreen = MutableStateFlow<Screen>(Screen.Home)
    val activeScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Filters and search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryFilter = MutableStateFlow<DesignCategory?>(null)
    val selectedCategoryFilter: StateFlow<DesignCategory?> = _selectedCategoryFilter.asStateFlow()

    // Products from Room
    val allProducts: StateFlow<List<Product>>
    val filteredProducts: StateFlow<List<Product>>

    // Cart
    val cartItems: StateFlow<List<CartItem>>

    // Orders
    val orders: StateFlow<List<Order>>

    // My Listings
    val myListings: StateFlow<List<Product>>

    // Support Chat
    val supportMessages: StateFlow<List<SupportMessage>>

    // Seller Stats
    private val _sellerStats = MutableStateFlow(
        SellerStats(0, 0, 0.0, 0.0, 0.0)
    )
    val sellerStats: StateFlow<SellerStats> = _sellerStats.asStateFlow()

    // Selected product for detail
    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()

    // Payment state
    private val _paymentState = MutableStateFlow<PaymentUiState>(PaymentUiState.Idle)
    val paymentState: StateFlow<PaymentUiState> = _paymentState.asStateFlow()

    // Global snackbar / toast message
    private val _userNotification = MutableStateFlow<String?>(null)
    val userNotification: StateFlow<String?> = _userNotification.asStateFlow()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = DesignBazaarRepository(db.designBazaarDao())

        // Preload sample catalogue
        viewModelScope.launch {
            repository.initializeSeedDataIfNeeded()
            refreshSellerStats()
        }

        allProducts = repository.allProducts.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        filteredProducts = combine(
            allProducts,
            _searchQuery,
            _selectedCategoryFilter
        ) { products, query, catFilter ->
            products.filter { prod ->
                val matchesQuery = query.isBlank() ||
                        prod.title.contains(query, ignoreCase = true) ||
                        prod.description.contains(query, ignoreCase = true) ||
                        prod.tags.any { it.contains(query, ignoreCase = true) } ||
                        prod.category.displayName.contains(query, ignoreCase = true)

                val matchesCat = catFilter == null || prod.category == catFilter
                matchesQuery && matchesCat
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        cartItems = repository.cartItems.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        orders = repository.allOrders.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        myListings = repository.myListings.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        supportMessages = repository.supportMessages.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun navigateTo(screen: Screen) {
        val currentStack = _screenStack.value.toMutableList()
        currentStack.add(screen)
        _screenStack.value = currentStack
        _currentScreen.value = screen
    }

    fun navigateBack(): Boolean {
        val currentStack = _screenStack.value.toMutableList()
        return if (currentStack.size > 1) {
            currentStack.removeAt(currentStack.lastIndex)
            _screenStack.value = currentStack
            _currentScreen.value = currentStack.last()
            true
        } else {
            false
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategoryFilter(category: DesignCategory?) {
        _selectedCategoryFilter.value = category
    }

    fun selectProduct(productId: Long) {
        viewModelScope.launch {
            val prod = repository.getProductById(productId)
            _selectedProduct.value = prod
            navigateTo(Screen.ProductDetail(productId))
        }
    }

    fun addToCart(productId: Long) {
        viewModelScope.launch {
            repository.addToCart(productId)
            _userNotification.value = "Added to Cart! 🛍️"
        }
    }

    fun removeFromCart(productId: Long) {
        viewModelScope.launch {
            repository.removeFromCart(productId)
            _userNotification.value = "Item removed from cart"
        }
    }

    fun clearNotification() {
        _userNotification.value = null
    }

    fun createListing(
        title: String,
        category: DesignCategory,
        price: Double,
        description: String,
        fileType: String,
        fileSize: String,
        drawableResName: String,
        tags: List<String>
    ) {
        viewModelScope.launch {
            repository.createProductListing(
                title = title,
                category = category,
                price = price,
                description = description,
                fileType = fileType,
                fileSize = fileSize,
                drawableResName = drawableResName,
                tags = tags
            )
            refreshSellerStats()
            _userNotification.value = "Your design is live on DesignBazaar! 🎉"
            navigateTo(Screen.SellerDashboard)
        }
    }

    fun deleteListing(productId: Long) {
        viewModelScope.launch {
            repository.deleteListing(productId)
            refreshSellerStats()
            _userNotification.value = "Listing removed successfully"
        }
    }

    fun startCheckout(products: List<Product>) {
        _paymentState.value = PaymentUiState.Idle
        navigateTo(Screen.Checkout(products))
    }

    fun processPayment(
        checkoutProducts: List<Product>,
        paymentMethod: PaymentMethod
    ) {
        viewModelScope.launch {
            _paymentState.value = PaymentUiState.Processing
            // Realistic payment verification simulation
            delay(1500)
            try {
                val createdOrders = repository.processCheckout(
                    products = checkoutProducts,
                    paymentMethodName = paymentMethod.title
                )
                _paymentState.value = PaymentUiState.Success(createdOrders)
                refreshSellerStats()
                _userNotification.value = "Payment Verified! ₹${checkoutProducts.sumOf { it.price }} received. Downloads ready! ⚡"
            } catch (e: Exception) {
                _paymentState.value = PaymentUiState.Error("Payment failed: ${e.localizedMessage}")
            }
        }
    }

    fun resetPaymentState() {
        _paymentState.value = PaymentUiState.Idle
    }

    fun markDownloaded(orderId: Long) {
        viewModelScope.launch {
            repository.markOrderDownloaded(orderId)
            _userNotification.value = "Design file downloaded to storage! 💾"
        }
    }

    fun sendSupportMessage(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.sendUserSupportMessage(text)
        }
    }

    fun refreshSellerStats() {
        viewModelScope.launch {
            _sellerStats.value = repository.getSellerStats()
        }
    }
}
