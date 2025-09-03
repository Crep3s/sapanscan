package com.example.warehousechecker.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.warehousechecker.databinding.ActivityChecklistBinding
import com.example.warehousechecker.network.Product

class ChecklistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChecklistBinding
    private lateinit var adapter: ProductChecklistAdapter
    private var totalItems = 0

    // 1. Регистрируем обработчик для получения результата от сканера
    private val barcodeScannerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val barcode = result.data?.getStringExtra("scanned_barcode")
            if (!barcode.isNullOrEmpty()) {
                onBarcodeScanned(barcode)
            }
        }
    }

    // 2. Регистрируем обработчик для запроса разрешений
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            openScanner()
        } else {
            Toast.makeText(this, "Для сканирования необходимо разрешение на использование камеры", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChecklistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val products = getProductsFromIntent()

        if (products != null) {
            totalItems = products.sumOf { it.quantity } // Считаем общее количество единиц товара
            setupRecyclerView(products)
            updateSummary()
        } else {
            binding.summaryTextView.text = "Ошибка: не удалось загрузить список товаров."
        }

        binding.scanChecklistButton.setOnClickListener {
            checkCameraPermissionAndOpenScanner()
        }
    }

    private fun checkCameraPermissionAndOpenScanner() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                openScanner()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun openScanner() {
        val intent = Intent(this, BarcodeScannerActivity::class.java)
        barcodeScannerLauncher.launch(intent)
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
        // Создаем "плоский" список, где каждый товар повторяется quantity раз
        val flatList = products.flatMap { product -> List(product.quantity) { product } }
        val checklistItems = flatList.map { ChecklistItem(it) }.toMutableList()

        adapter = ProductChecklistAdapter(checklistItems)
        binding.productsRecyclerView.adapter = adapter
    }

    private fun updateSummary() {
        val checkedCount = adapter.getCheckedCount()
        binding.summaryTextView.text = "Собрано $checkedCount из $totalItems"

        // Если всё собрано, можно показать сообщение
        if (checkedCount == totalItems) {
            Toast.makeText(this, "Заказ собран полностью!", Toast.LENGTH_LONG).show()
            binding.scanChecklistButton.isEnabled = false // Отключаем кнопку
        }
    }

    private fun onBarcodeScanned(barcode: String) {
        val success = adapter.checkItemByBarcode(barcode)
        if (!success) {
            Toast.makeText(this, "Товар с таким штрих-кодом не найден или уже собран", Toast.LENGTH_SHORT).show()
        }
        updateSummary()
    }
}