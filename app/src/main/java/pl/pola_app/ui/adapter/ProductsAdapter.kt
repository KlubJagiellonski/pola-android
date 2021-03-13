package pl.pola_app.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import pl.pola_app.R
import pl.pola_app.model.SearchResult
import timber.log.Timber

class ProductsAdapter(
    private val context: Context,
    private val searchResults: ProductList
) : RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {
    interface ProductClickListener {
        fun itemClicked(searchResult: SearchResult)
    }

    private var productClickListener: ProductClickListener? = null
    fun setOnProductClickListener(productClickListener: (SearchResult) -> Unit) {
        this.productClickListener = object : ProductClickListener{
            override fun itemClicked(searchResult: SearchResult) {
                productClickListener.invoke(searchResult)
            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.view_product_item, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val p = searchResults[i]
        viewHolder.bind(p)
    }

    override fun getItemCount(): Int {
        return searchResults.size()
    }

    private fun onItemClicked(position: Int) {
        if (productClickListener == null) {
            return
        }
        if (position > searchResults.size()) { //TODO: is it even possible to reach this state?
            Timber.w(
                IndexOutOfBoundsException(),
                "Position: %d, list size: %d",
                position,
                searchResults.size()
            )
            return
        }
        val searchResult = searchResults[position]
        searchResult?.run {
            productClickListener?.itemClicked(this)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        @kotlin.jvm.JvmField
        @BindView(R.id.company_name)
        var companyName: TextView? = null

        @kotlin.jvm.JvmField
        @BindView(R.id.score_bar)
        var plScore: ProgressBar? = null

        @kotlin.jvm.JvmField
        @BindView(R.id.view_product_item)
        var productCard: CardView? = null

        @kotlin.jvm.JvmField
        @BindView(R.id.progressBar)
        var progress: ProgressBar? = null

        @kotlin.jvm.JvmField
        @BindView(R.id.heart_image)
        var heartIcon: ImageView? = null
        fun bind(searchResult: SearchResult?) {
            if (searchResult == null) {
                progress?.visibility = View.VISIBLE
                companyName?.text = ""
                plScore?.progress = 0
                applyStyle(context.getString(R.string.type_white))
                return
            }
            progress?.visibility = View.GONE
            applyStyle(searchResult.card_type)
            companyName?.text = searchResult.name
            plScore?.progress = searchResult.plScore ?: 0
            if (searchResult?.is_friend == true) {
                heartIcon?.visibility = View.VISIBLE
            } else {
                heartIcon?.visibility = View.GONE
            }
        }

        private fun applyStyle(style: String) {
            val resources = context.resources
            if (style == resources.getString(R.string.type_grey)) {
                productCard?.setCardBackgroundColor(resources.getColor(R.color.card_type_grey_bk))
                plScore?.setBackgroundColor(resources.getColor(R.color.card_type_grey_score_bk))
            } else {
                productCard?.setCardBackgroundColor(resources.getColor(R.color.card_type_white_bk))
                plScore?.setBackgroundColor(resources.getColor(R.color.card_type_white_score_bk))
            }
        }

        override fun onClick(view: View) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onItemClicked(position)
            }
        }

        init {
            ButterKnife.bind(this, itemView)
            itemView.setOnClickListener(this)
        }
    }
}