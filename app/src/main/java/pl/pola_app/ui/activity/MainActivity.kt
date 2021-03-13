package pl.pola_app.ui.activity

import android.app.FragmentManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.facebook.device.yearclass.YearClass
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.otto.Bus
import pl.pola_app.PolaApplication
import pl.pola_app.R
import pl.pola_app.helpers.*
import pl.pola_app.helpers.SessionId.Companion.create
import pl.pola_app.model.SearchResult
import pl.pola_app.ui.activity.MainPresenter
import pl.pola_app.ui.adapter.ProductList
import pl.pola_app.ui.adapter.ProductsAdapter
import pl.pola_app.ui.delegate.ProductDetailsFragmentDelegate
import pl.pola_app.ui.event.FlashActionListener
import pl.pola_app.ui.fragment.BarcodeListener
import pl.pola_app.ui.fragment.KeyboardFragment
import pl.pola_app.ui.fragment.ProductDetailsFragment
import pl.pola_app.ui.fragment.ProductDetailsFragment.Companion.newInstance
import pl.pola_app.ui.fragment.ScannerFragment
import pl.tajchert.nammu.Nammu
import javax.inject.Inject

class MainActivity : AppCompatActivity(), MainViewBinder, BarcodeListener,
    ProductDetailsFragmentDelegate {
    @JvmField
    @Inject
    var eventBus: Bus? = null

    @JvmField
    @Inject
    var settingsPreference: SettingsPreference? = null

    @JvmField
    @BindView(R.id.products_list)
    var productsListView: RecyclerView? = null

    @JvmField
    @BindView(R.id.open_keyboard_button)
    var openKeyboard: FloatingActionButton? = null

    @JvmField
    @BindView(R.id.support_pola_app)
    var supportPolaApp: Button? = null

    @JvmField
    @BindView(R.id.menu)
    var menu: ImageView? = null
    private var scannerFragment: ScannerFragment? = null
    private var mainPresenter: MainPresenter? = null
    private var logger: EventLogger? = null
    private lateinit var sessionId: SessionId
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this, this)
        PolaApplication.Companion.component(this)?.inject(this)
        Nammu.init(this)
        val productList: ProductList = ProductList.Companion.create(savedInstanceState)
        val productsAdapter = ProductsAdapter(this, productList)
        sessionId = create(this)
        mainPresenter = MainPresenter.Companion.create(
            applicationContext,
            this,
            productList,
            productsAdapter,
            sessionId,
            eventBus!!
        )
        logger = EventLogger(this)
        openKeyboard?.setOnClickListener { openKeyboard() }
        scannerFragment = fragmentManager.findFragmentById(R.id.scanner_fragment) as ScannerFragment
        productsListView?.layoutManager = ProductsListLinearLayoutManager(this)
        fragmentManager.addOnBackStackChangedListener {
            val isNotBackStackEmpty = fragmentManager.backStackEntryCount > 0
            mainPresenter?.onBackStackChange(isNotBackStackEmpty)
            if (isNotBackStackEmpty) {
                openKeyboard?.hide()
            } else {
                openKeyboard?.show()
            }
        }
        menu?.setOnClickListener { view: View? ->
            startActivity(
                Intent(
                    this,
                    MenuActivity::class.java
                )
            )
        }
    }

    @OnClick(R.id.flash_icon)
    fun onFlashIconClicked(view: View?) {
        val fragment = fragmentManager.findFragmentById(R.id.scanner_fragment)
        if (fragment != null && fragment is FlashActionListener) {
            val flashActionListener = fragment as FlashActionListener
            flashActionListener.onFlashAction()
            if (view != null && view is ImageView) {
                view.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        if (flashActionListener.isTorchOn) R.drawable.ic_flash_off_white_48dp else R.drawable.ic_flash_on_white_48dp
                    )
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mainPresenter?.register()
    }

    override fun onStop() {
        mainPresenter?.unregister()
        super.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mainPresenter?.onSaveState(outState)
    }

    override fun openProductDetails(searchResult: SearchResult) {
        val ft = fragmentManager.beginTransaction()
        ft.setCustomAnimations(R.animator.slide_in, 0, 0, R.animator.slide_out)
        val newFragment = newInstance(searchResult)
        ft.add(R.id.container, newFragment, ProductDetailsFragment::class.java.name)
        ft.addToBackStack(ProductDetailsFragment::class.java.name)
        ft.commitAllowingStateLoss()
        if (searchResult.askForSupport()) {
            supportPolaApp?.visibility = View.VISIBLE
            supportPolaApp?.text = searchResult.donate?.title
        } else {
            supportPolaApp?.visibility = View.GONE
        }
        mainPresenter?.setCurrentSearchResult(searchResult)
    }

    fun openKeyboard() {
        if (fragmentManager.backStackEntryCount > 0) {
            return  // prevent adding fragment twice
        }
        val ft = fragmentManager.beginTransaction()
        ft.setCustomAnimations(R.animator.fade_in, 0, 0, R.animator.fade_out)
        val newFragment = KeyboardFragment()
        ft.add(R.id.container, newFragment, KeyboardFragment::class.java.name)
        ft.addToBackStack(KeyboardFragment::class.java.name)
        ft.commitAllowingStateLoss()
    }

    override fun setAdapter(adapter: RecyclerView.Adapter<*>) {
        productsListView?.adapter = adapter
    }

    @OnClick(R.id.support_pola_app)
    fun onSupportPolaButtonClick() {
        mainPresenter?.onSupportPolaButtonClick()
    }

    override fun onsSeePolaFriendsAction() {
        val intent = Intent(this, ActivityWebView::class.java)
        intent.putExtra("url", URL_POLA_FRIENDS)
        startActivity(intent)
    }

    override fun setSupportPolaAppButtonVisibility(
        isVisible: Boolean,
        searchResult: SearchResult?
    ) {
        if (isVisible) {
            supportPolaApp?.visibility = View.VISIBLE
            supportPolaApp?.text = searchResult?.donate?.title
            return
        }
        supportPolaApp?.visibility = View.GONE
    }

    override fun openWww(searchResult: SearchResult?, url: String?) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(browserIntent)
    }

    override fun onBarcode(barcode: String, fromCamera: Boolean) {
        mainPresenter?.onBarcode(barcode, fromCamera)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK) {
            return
        }
        if (requestCode == DONATE_POLA) {
            mainPresenter?.onSupportPolaFinished()
        }
    }

    override val deviceYear: Int
        get() = YearClass.get(application)

    override fun resumeScanning() {
        scannerFragment?.resumeScanning()
    }

    override fun turnOffTorch() {
        scannerFragment?.setTorchOff()
    }

    override fun showNoConnectionMessage() {
        Toast.makeText(this, getString(R.string.toast_no_connection), Toast.LENGTH_SHORT).show()
    }

    override fun showErrorMessage(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun dismissProductDetailsView() {
        fragmentManager.popBackStack(
            ProductDetailsFragment::class.java.name,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle item selection
        return when (item.itemId) {
            R.id.action_menu -> {
                startActivity(Intent(this, MenuActivity::class.java))
                super.onOptionsItemSelected(item)
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private const val DONATE_POLA = 1000
    }
}