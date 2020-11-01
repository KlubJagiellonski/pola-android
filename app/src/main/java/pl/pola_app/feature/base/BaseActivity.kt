package pl.pola_app.feature.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

abstract class BaseActivity<BINDING : ViewDataBinding>(
    @LayoutRes val layoutResId: Int
) : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    lateinit var binding: BINDING

    open val viewModel: BaseViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutResId)
        binding.lifecycleOwner = this

        initView()
        initBaseData()
        initObservers()
        initAnimations()
    }

    override fun onResume() {
        super.onResume()

        initListeners()
    }

    abstract fun initView()

    abstract fun initAnimations()

    abstract fun initListeners()

    abstract fun initBaseData()

    abstract fun initObservers()

    override fun onPause() {
        super.onPause()
        viewModel?.disposable?.clear()
    }
}