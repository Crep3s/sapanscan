package com.example.warehousechecker.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.warehousechecker.databinding.ItemProductChecklistBinding
import com.example.warehousechecker.network.Product

// Вспомогательный класс для хранения состояния
data class ChecklistItem(
    val product: Product,
    var isChecked: Boolean = false
)

class ProductChecklistAdapter(
    private val items: MutableList<ChecklistItem>
) : RecyclerView.Adapter<ProductChecklistAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductChecklistBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun getCheckedCount(): Int = items.count { it.isChecked }

    // Функция для отметки товара по штрих-коду
    // Возвращает true, если товар был найден и отмечен, иначе false
    fun checkItemByBarcode(barcode: String): Boolean {
        // Ищем ПЕРВЫЙ НЕОТМЕЧЕННЫЙ товар с таким штрих-кодом
        val index = items.indexOfFirst { it.product.barCode == barcode && !it.isChecked }
        return if (index != -1) {
            items[index].isChecked = true
            notifyItemChanged(index)
            true // Успех
        } else {
            false // Неудача (товар не найден или все уже отмечены)
        }
    }

    class ProductViewHolder(private val binding: ItemProductChecklistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ChecklistItem) {
            binding.productNameTextView.text = item.product.name
            binding.productSkuTextView.text = "Артикул: ${item.product.sku ?: "нет"}"
            // Количество теперь всегда "x1", так как список "плоский"
            binding.productQuantityTextView.text = "x1"
            binding.productCheckBox.isChecked = item.isChecked

            // Меняем цвет фона для отмеченных позиций для наглядности
            if (item.isChecked) {
                itemView.setBackgroundColor(Color.parseColor("#E0F7FA")) // Светло-голубой
            } else {
                itemView.setBackgroundColor(Color.TRANSPARENT)
            }
        }
    }
}