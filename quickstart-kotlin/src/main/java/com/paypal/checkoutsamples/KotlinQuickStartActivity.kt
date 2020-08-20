package com.paypal.checkoutsamples

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.paypal.checkoutsamples.order.OrdersQuickStartActivity
import com.paypal.checkoutsamples.token.TokenQuickStartActivity
import kotlinx.android.synthetic.main.activity_kotlin_quick_start.*

class KotlinQuickStartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin_quick_start)

        buyWithOrder.setOnClickListener {
            startActivity(OrdersQuickStartActivity.startIntent(this))
        }

        buyWithOrderToken.setOnClickListener {
            startActivity(TokenQuickStartActivity.startIntent(this))
        }
    }
}
