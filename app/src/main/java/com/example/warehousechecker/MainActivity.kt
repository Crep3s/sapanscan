package com.example.warehousechecker

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.warehousechecker.databinding.ActivityMainBinding
import com.example.warehousechecker.network.Product
import com.example.warehousechecker.ui.BarcodeScannerActivity
import com.example.warehousechecker.ui.ChecklistActivity
import com.example.warehousechecker.ui.MainViewModel
import com.example.warehousechecker.ui.OrderState
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    // 1. Регистрируем обработчик для получения результата от сканера
    private val barcodeScannerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val barcode = result.data?.getStringExtra("scanned_barcode")
            if (!barcode.isNullOrEmpty()) {
                binding.orderIdEditText.setText(barcode)
                viewModel.loadOrder(barcode) // Сразу загружаем заказ
            }
        }
    }

    // 2. Регистрируем обработчик для запроса разрешений
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            openScanner() // Если разрешение дали, открываем сканер
        } else {
            Toast.makeText(this, "Для сканирования необходимо разрешение на использование камеры", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.loadOrderButton.setOnClickListener {
            loadOrderFromInput()
        }

        binding.scanButton.setOnClickListener {
            checkCameraPermissionAndOpenScanner()
        }
    }

    private fun loadOrderFromInput() {
        val orderId = binding.orderIdEditText.text.toString().trim()
        if (orderId.isNotEmpty()) {
            viewModel.loadOrder(orderId)
        } else {
            Toast.makeText(this, "Пожалуйста, введите номер заказа", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkCameraPermissionAndOpenScanner() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                openScanner() // Разрешение уже есть
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA) // Запрашиваем разрешение
            }
        }
    }

    private fun openScanner() {
        val intent = Intent(this, BarcodeScannerActivity::class.java)
        barcodeScannerLauncher.launch(intent)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.orderState.collect { state ->
                when (state) {
                    is OrderState.Idle -> {
                        binding.progressBar.visibility = View.GONE
                        binding.errorTextView.visibility = View.GONE
                        binding.loadOrderButton.isEnabled = true
                    }
                    is OrderState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.errorTextView.visibility = View.GONE
                        binding.loadOrderButton.isEnabled = false
                    }
                    is OrderState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.loadOrderButton.isEnabled = true
                        openChecklistActivity(state.products)
                    }
                    is OrderState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.loadOrderButton.isEnabled = true
                        binding.errorTextView.visibility = View.VISIBLE
                        binding.errorTextView.text = state.message
                    }
                }
            }
        }
    }

    private fun openChecklistActivity(products: List<Product>) {
        val intent = Intent(this, ChecklistActivity::class.java).apply {
            putParcelableArrayListExtra("products_list", ArrayList(products))
        }
        startActivity(intent)
    }
}