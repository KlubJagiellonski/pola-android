package pl.pola_app.ui.fragment

import android.app.DialogFragment
import android.content.Context
import pl.pola_app.helpers.SessionId.Companion.create
import butterknife.BindView
import pl.pola_app.R
import androidx.cardview.widget.CardView
import javax.inject.Inject
import com.squareup.otto.Bus
import pl.pola_app.ui.delegate.ProductDetailsFragmentDelegate
import pl.pola_app.helpers.EventLogger
import android.os.Bundle
import org.parceler.Parcels
import android.view.LayoutInflater
import android.view.ViewGroup
import pl.pola_app.PolaApplication
import butterknife.ButterKnife
import butterknife.OnClick
import android.content.Intent
import android.content.res.Resources
import android.view.View
import android.widget.*
import pl.pola_app.helpers.SessionId
import pl.pola_app.helpers.URL_POLA_FRIENDS
import pl.pola_app.model.SearchResult
import pl.pola_app.ui.activity.ActivityWebView
import pl.pola_app.ui.event.ProductDetailsFragmentDismissedEvent
import java.lang.IllegalArgumentException

class ProductDetailsFragment : DialogFragment() {
    @JvmField
    @BindView(R.id.product_info_card)
    var productInfoCard: CardView? = null
    @JvmField
    @BindView(R.id.company_name)
    var tv_companyName: TextView? = null
    @JvmField
    @BindView(R.id.plscore_details_progressbar)
    var plScoreBar: ProgressBar? = null
    @JvmField
    @BindView(R.id.plscore_details_text)
    var plScoreText: TextView? = null
    @JvmField
    @BindView(R.id.plcapital_details_progressbar)
    var plCapitalBar: ProgressBar? = null
    @JvmField
    @BindView(R.id.plcapital_details_text)
    var plCapitalText: TextView? = null
    @JvmField
    @BindView(R.id.buttonWorkers)
    var buttonWorkers: ImageButton? = null
    @JvmField
    @BindView(R.id.buttonGlobent)
    var buttonGlobent: ImageButton? = null
    @JvmField
    @BindView(R.id.buttonRegistered)
    var buttonRegistered: ImageButton? = null
    @JvmField
    @BindView(R.id.buttonRnd)
    var buttonRnd: ImageButton? = null
    @JvmField
    @BindView(R.id.seePolaFriends)
    var seePolaFriendsButton: Button? = null
    @JvmField
    @BindView(R.id.tv_altText)
    var altText: TextView? = null
    @JvmField
    @BindView(R.id.tv_description)
    var description: TextView? = null
    @JvmField
    @BindView(R.id.pl_data_layout)
    var plDataLayout: LinearLayout? = null
    @JvmField
    @BindView(R.id.isFriendLayout)
    var isFriendLayout: LinearLayout? = null
    @JvmField
    @BindView(R.id.isFriendText)
    var isFriendText: TextView? = null
    @JvmField
    @Inject
    var eventBus: Bus? = null

    @JvmField
    @Inject
    var res: Resources? = null

    private lateinit var searchResult: SearchResult
    private var delegate: ProductDetailsFragmentDelegate? = null
    private lateinit var logger: EventLogger
    private lateinit var sessionId: SessionId

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ProductDetailsFragmentDelegate) {
            delegate = context
            return
        }
        throw IllegalArgumentException("Context that uses this fragment should implements ProductDetailsFragmentDelegate class")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            searchResult = Parcels.unwrap(
                it.getParcelable(
                    SearchResult::class.java.name
                )
            )
        }
        sessionId = create(activity)
        logger = EventLogger(activity)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_product_details, container, false)
        PolaApplication.component(activity)?.inject(this)
        ButterKnife.bind(this, view)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        applyStyle(searchResult.card_type, searchResult.report_button_type)
        tv_companyName?.text = searchResult.name
        if (searchResult.plScore != null) {
            plScoreBar?.progress = searchResult.plScore ?: 0
            plScoreText?.text = searchResult.plScore.toString() + " pkt"
        } else {
            plScoreBar?.progress = 0
            plScoreText?.text = "?"
        }

        if (searchResult.plCapital != null) {
            plCapitalBar?.progress = searchResult.plCapital ?: 0
            plCapitalText?.text = searchResult.plCapital.toString() + "%"
        } else {
            plCapitalBar?.progress = 0
            plCapitalText?.text = "?"
        }

        if (searchResult.altText != null) {
            plDataLayout?.visibility = View.GONE
            altText?.visibility = View.VISIBLE
            altText?.text = searchResult.altText
        } else {
            altText?.visibility = View.GONE
            plDataLayout?.visibility = View.VISIBLE
            if (searchResult.plWorkers != null && searchResult.plWorkers != 0) {
                buttonWorkers?.isSelected = true
            } else if (searchResult.plWorkers == null) {
                buttonWorkers?.isEnabled = false
            }
            if (searchResult.plRnD != null && searchResult.plRnD != 0) {
                buttonRnd?.isSelected = true
            } else if (searchResult.plRnD == null) {
                buttonRnd?.isEnabled = false
            }
            if (searchResult.plRegistered != null && searchResult.plRegistered != 0) {
                buttonRegistered?.isSelected = true
            } else if (searchResult.plRegistered == null) {
                buttonRegistered?.isEnabled = false
            }
            if (searchResult.plNotGlobEnt != null && searchResult.plNotGlobEnt != 0) {
                buttonGlobent?.isSelected = true
            } else if (searchResult.plNotGlobEnt == null) {
                buttonGlobent?.isEnabled = false
            }
            if (searchResult.description != null) {
                description?.visibility = View.VISIBLE
                description?.text = searchResult.description
            } else {
                description?.visibility = View.GONE
            }
        }

        if (searchResult.askForSupport()) {
            seePolaFriendsButton?.visibility = View.VISIBLE
            seePolaFriendsButton?.setOnClickListener { view: View? -> if (delegate != null) delegate?.onsSeePolaFriendsAction() }
        } else {
            seePolaFriendsButton?.visibility = View.GONE
        }
        searchResult.is_friend?.run {
            if (this && searchResult.friend_text != null) {
                isFriendLayout?.visibility = View.VISIBLE
                isFriendText?.text = searchResult.friend_text
            }
        }
        productInfoCard?.setOnClickListener { eventBus?.post(ProductDetailsFragmentDismissedEvent()) }
    }

    override fun onDestroy() {
        super.onDestroy()
        delegate = null
    }

    private fun applyStyle(cardType: String, reportType: String) {
        res?.run {
            if (cardType == this.getString(R.string.type_grey)) {
                productInfoCard?.setCardBackgroundColor(this.getColor(R.color.card_type_grey_bk))
            } else {
                productInfoCard?.setCardBackgroundColor(this.getColor(R.color.card_type_white_bk))
            }
        }
    }

    @OnClick(R.id.isFriendLayout)
    fun onFriendsClick() {
        val bundle = Bundle()
        bundle.putString("item", "Przyjaciele Poli")
        bundle.putString("device_id", sessionId.get())
        logger.logCustom("product_details_friend", bundle)
        val intent = Intent(activity, ActivityWebView::class.java)
        intent.putExtra("url", URL_POLA_FRIENDS)
        startActivity(intent)
    }

    companion object {
        fun newInstance(searchResult: SearchResult): ProductDetailsFragment {
            val fragment = ProductDetailsFragment()
            val args = Bundle()
            args.putParcelable(SearchResult::class.java.name, Parcels.wrap(searchResult))
            fragment.arguments = args
            return fragment
        }
    }
}