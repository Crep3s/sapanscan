package com.example.warehousechecker.ui

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.warehousechecker.databinding.ActivityChecklistBinding
import com.example.warehousechecker.network.Product

class ChecklistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChecklistBinding
    private lateinit var adapter: ProductChecklistAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChecklistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val products = getProductsFromIntent()

        if (products != null) {
            setupRecyclerView(products)
            updateSummary()
        } else {
            // Обработка случая, если данные не пришли
            binding.summaryTextView.text = "Ошибка: не удалось загрузить список товаров."
        }
    }

    private fun getProductsFromIntent(): List<Product>? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra("products_list", Product::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableArrayListExtra("products_list")
        }
    }

    private fun setupRecyclerView(products: List<Product>) {
        val checklistItems = products.map { ChecklistItem(it) }.toMutableList()
        adapter = ProductChecklistAdapter(checklistItems)
        binding.productsRecyclerView.adapter = adapter
    }

    private fun updateSummary() {
        val checkedCount = adapter.getCheckedCount()
        val totalCount = adapter.itemCount
        binding.summaryTextView.text = "Собрано $checkedCount из $totalCount"
    }

    // Эту функцию мы будем вызывать после сканирования штрих-кода
    private fun onBarcodeScanned(barcode: String) {
        adapter.checkItemByBarcode(barcode)
        updateSummary()
    }
}