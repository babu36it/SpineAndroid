package com.wiesoftware.spine.ui.home.menus.profile.setting.currency

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.wiesoftware.spine.R
import com.wiesoftware.spine.RuntimeLocaleChanger
import com.wiesoftware.spine.data.adapter.CurrencyAdapter
import com.wiesoftware.spine.data.net.reponses.CurrencyData
import com.wiesoftware.spine.data.repo.HomeRepositry
import com.wiesoftware.spine.databinding.ActivityCurrencyBinding
import com.wiesoftware.spine.util.ApiException
import com.wiesoftware.spine.util.NoInternetException
import com.wiesoftware.spine.util.Prefs
import com.wiesoftware.spine.util.putAny
import kotlinx.coroutines.launch
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class CurrencyActivity : AppCompatActivity(),KodeinAware, CurrencyEventListener,
    CurrencyAdapter.CurrencyEventListener {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.let { RuntimeLocaleChanger.wrapContext(it) })
    }

    override val kodein by kodein()
    val factory : CurrencyViewmodelFactory by instance()
    val homeRepositry: HomeRepositry by instance()
    lateinit var binding: ActivityCurrencyBinding
    lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_currency)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_currency)
        val viewmodel=ViewModelProvider(this,factory).get(CurreencyViewmodel::class.java)
        binding.viewmodel=viewmodel
        viewmodel.currencyEventListener=this
        viewmodel.getLoggedInUser().observe(this, Observer { user->
            userId=user.users_id!!
        })

        getCurrency()
    }

    private fun getCurrency() {
        lifecycleScope.launch {
            try {
                val  res=homeRepositry.getCurrency()
                if (res.status){
                    val currencyList=res.data
                    binding.rvCurrency.also {
                        it.layoutManager=LinearLayoutManager(this@CurrencyActivity,RecyclerView.VERTICAL,false)
                        it.setHasFixedSize(true)
                        it.adapter=CurrencyAdapter(currencyList,this@CurrencyActivity)
                    }
                }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }

    override fun onBack() {
        onBackPressed()
    }

    override fun onCurencySelected(currencyData: CurrencyData) {
        Prefs.putAny(CURRENCY_ID,currencyData.id)
        Prefs.putAny(CURRENCY_SYMBOL,currencyData.symbol)
        Prefs.putAny(CURRENCY_NAME,currencyData.currency)
        onBackPressed()
    }

    companion object{
        val CURRENCY_ID="currencyId"
        val CURRENCY_SYMBOL="currencySymbol"
        val CURRENCY_NAME="currencyName"
    }
}