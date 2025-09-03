package com.example.warehousechecker

// ИМПОРТЫ, КОТОРЫХ НЕ ХВАТАЛО:
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.warehousechecker.ui.MainViewModel // <- Вот он
import com.example.warehousechecker.ui.OrderState    // <- И этот
import com.google.gson.Gson // <- И этот
import kotlinx.coroutines.launch
import com.example.warehousechecker.ui.ChecklistActivity
class MainActivity : AppCompatActivity() {

    // Теперь студия знает, что такое MainViewModel
    private val viewModel: MainViewModel by viewModels()

    private lateinit var orderIdEditText: EditText
    private lateinit var loadOrderButton: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        orderIdEditText = findViewById(R.id.orderIdEditText)
        loadOrderButton = findViewById(R.id.loadOrderButton)
        progressBar = findViewById(R.id.progressBar)

        loadOrderButton.setOnClickListener {
            val orderId = orderIdEditText.text.toString().trim()
            if (orderId.isNotEmpty()) {
                // Теперь студия знает, что такое loadOrder
                viewModel.loadOrder(orderId)
            } else {
                Toast.makeText(this, "Введите номер заказа", Toast.LENGTH_SHORT).show()
            }
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            // Теперь студия знает, что такое orderState
            viewModel.orderState.collect { state ->
                when (state) {
                    // И что такое OrderState
                    is OrderState.Idle -> {
                        progressBar.visibility = View.GONE
                        loadOrderButton.isEnabled = true
                    }
                    is OrderState.Loading -> {
                        progressBar.visibility = View.VISIBLE
                        loadOrderButton.isEnabled = false
                    }
                    is OrderState.Success -> {
                        progressBar.visibility = View.GONE
                        loadOrderButton.isEnabled = true
                        Toast.makeText(this@MainActivity, "Заказ загружен", Toast.LENGTH_SHORT).show()

                        // Переход на следующий экран
                        // !! Важно: ChecklistActivity еще не создан, поэтому эта строка будет красной
                        // до тех пор, пока вы не создадите этот файл.
                        val intent = Intent(this@MainActivity, ChecklistActivity::class.java)
                        // Передаем список товаров в виде JSON строки
                        intent.putExtra("PRODUCTS_JSON", Gson().toJson(state.products))
                        startActivity(intent)
                    }
                    is OrderState.Error -> {
                        progressBar.visibility = View.GONE
                        loadOrderButton.isEnabled = true
                        Toast.makeText(this@MainActivity, "Ошибка: ${state.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}