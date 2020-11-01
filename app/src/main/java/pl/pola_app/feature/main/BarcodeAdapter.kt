package pl.pola_app.feature.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import pl.pola_app.databinding.ItemBarcodeBinding
import pl.pola_app.feature.common.ClickListener

class BarcodeAdapter(
    var list: List<String>,
    var clickListener: ClickListener<String>
) : RecyclerView.Adapter<BarcodeAdapter.ViewHolder>() {

    override fun getItemCount() = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemBarcodeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        (holder.binding as? ItemBarcodeBinding)?.apply {
            this.barcode = list[position]
            this.listener = clickListener
        }
    }

    open class ViewHolder(var binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root)
}