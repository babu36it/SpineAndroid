package com.wiesoftware.spine.ui.home.menus.spine.featuredpost

import android.view.View
import androidx.lifecycle.ViewModel
import com.wiesoftware.spine.ui.home.menus.spine.featuredpost.thankyou_featured.ThankYouFeaturedEventListener

class PaymentFeaturedViewModel():ViewModel (){
    var paymentFeaturedEventListner: PaymentFeaturedEventListner? = null

    fun onClose(view: View){
        paymentFeaturedEventListner?.onClose()
    }

    fun onNext(view: View){
        paymentFeaturedEventListner?.onNext()
    }
}