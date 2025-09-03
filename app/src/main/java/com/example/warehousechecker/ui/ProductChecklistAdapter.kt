package com.example.warehousechecker.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.warehousechecker.databinding.ItemProductChecklistBinding
import com.example.warehousechecker.network.Product

// Вспомогательный класс для хранения состояния (отмечен/не отмечен)
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
    fun checkItemByBarcode(barcode: String) {
        val index = items.indexOfFirst { it.product.barCode == barcode && !it.isChecked }
        if (index != -1) {
            items[index].isChecked = true
            notifyItemChanged(index)
        }
    }

    class ProductViewHolder(private val binding: ItemProductChecklistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ChecklistItem) {
            binding.productNameTextView.text = item.product.name
            binding.productSkuTextView.text = "Артикул: ${item.product.sku ?: "нет"}"
            binding.productQuantityTextView.text = "x${item.product.quantity}"
            binding.productCheckBox.isChecked = item.isChecked
        }
    }
}