package pl.pola_app.feature.common

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView

@BindingAdapter(value = ["adapter", "linearLayoutManager"])
fun RecyclerView.setRecyclerView(
    adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
    linearLayoutManager: RecyclerView.LayoutManager
) {
    this.adapter = adapter
    this.layoutManager = linearLayoutManager
}