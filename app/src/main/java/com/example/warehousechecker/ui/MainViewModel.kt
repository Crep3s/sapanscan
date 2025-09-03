package com.example.warehousechecker.ui
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.warehousechecker.data.WarehouseRepository
import com.example.warehousechecker.network.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Состояния UI
sealed class OrderState {
    object Idle : OrderState()
    object Loading : OrderState()
    data class Success(val products: List<Product>) : OrderState()
    data class Error(val message: String) : OrderState()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = WarehouseRepository(application)

    private val _orderState = MutableStateFlow<OrderState>(OrderState.Idle)
    val orderState: StateFlow<OrderState> = _orderState

    fun loadOrder(orderId: String) {
        viewModelScope.launch {
            _orderState.value = OrderState.Loading
            val result = repository.getOrderProducts(orderId)
            result.onSuccess { products ->
                _orderState.value = OrderState.Success(products)
            }.onFailure { error ->
                _orderState.value = OrderState.Error(error.message ?: "Неизвестная ошибка")
            }
        }
    }
}