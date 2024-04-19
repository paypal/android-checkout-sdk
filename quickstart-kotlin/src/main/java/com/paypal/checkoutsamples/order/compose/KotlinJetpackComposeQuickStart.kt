package com.paypal.checkoutsamples.order.compose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.paypal.checkout.PayPalCheckout
import com.paypal.checkout.approve.OnApprove
import com.paypal.checkout.cancel.OnCancel
import com.paypal.checkout.createorder.CreateOrder
import com.paypal.checkout.createorder.CurrencyCode
import com.paypal.checkout.createorder.OrderIntent
import com.paypal.checkout.createorder.ShippingPreference
import com.paypal.checkout.createorder.UserAction
import com.paypal.checkout.error.OnError
import com.paypal.checkout.order.CaptureOrderResult
import com.paypal.checkoutsamples.order.CreatedItem
import com.paypal.checkoutsamples.order.compose.checkoutstate.Loading
import com.paypal.checkoutsamples.order.compose.checkoutstate.OrderCapturingFailed
import com.paypal.checkoutsamples.order.compose.checkoutstate.OrderPaidForSuccessfully
import com.paypal.checkoutsamples.order.compose.checkoutstate.OrderPaymentCancelled
import com.paypal.checkoutsamples.order.compose.checkoutstate.OrderPaymentFailedWithAnError
import com.paypal.checkoutsamples.order.compose.checkoutstate.PaypalCheckoutState
import com.paypal.checkoutsamples.order.usecase.CreateOrderRequest
import com.paypal.checkoutsamples.order.usecase.CreateOrderUseCase
import com.paypal.checkoutsamples.ui.theme.AndroidNativeCheckoutSamplesTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class KotlinJetpackComposeQuickStart : ComponentActivity() {
    private val checkoutState = MutableStateFlow<PaypalCheckoutState>(Loading)
    private val checkOutSdk:PayPalCheckout
        get() =  PayPalCheckout
    private val createdItems = mutableListOf<CreatedItem>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerPaypalCheckoutCallback(checkOutSdk)
        val createOrderUseCase by lazy { CreateOrderUseCase() }

        setContent {
            AndroidNativeCheckoutSamplesTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    PaymentScreen(paypalSdkInstance = checkOutSdk, orderUseCase = createOrderUseCase,
                        checkoutState = checkoutState,
                        createdItems = createdItems)
                }
            }
        }
    }



    fun registerPaypalCheckoutCallback(checkoutSdk: PayPalCheckout) {
        checkoutSdk.registerCallbacks(onApprove = OnApprove { approval ->
            approval.orderActions.capture { result ->
                val message = when (result) {
                    is CaptureOrderResult.Success -> {
                        checkoutState.update { OrderPaidForSuccessfully }
                        "payment successful. Thank you for shopping with us"
                    }

                    is CaptureOrderResult.Error -> {
                        checkoutState.update { OrderCapturingFailed }
                        "failed to capture your order"
                    }
                }
                Log.i("ComposeActivityTag",message)
            }
        }, onCancel = OnCancel {
            checkoutState.update { OrderPaymentCancelled }
            Log.i("ComposeActivityTag","payment cancelled")
        }, onError = OnError { errorInfo ->
            checkoutState.update { OrderPaymentFailedWithAnError(errorInfo.reason) }
            Log.i("ComposeActivityTag","the following error occurred while paying ${errorInfo.reason}")
        })
    }
    @Composable
    fun PaymentScreen(modifier: Modifier=Modifier,
                      paypalSdkInstance:PayPalCheckout,
                      orderUseCase: CreateOrderUseCase,
                      checkoutState:StateFlow<PaypalCheckoutState>,
                      createdItems: List<CreatedItem>){
        var observeCheckoutState by remember { mutableStateOf(false) }
        var checkoutStateMsg by remember { mutableStateOf("") }
        val internalCheckoutState by checkoutState.collectAsState(Loading)
        when(observeCheckoutState){
            true->{
                CircularProgressIndicator(modifier = Modifier.size(200.dp))
                ObservePaypalCheckoutState(internalCheckoutState){
                    observeState,msg->
                    observeCheckoutState = observeState
                    checkoutStateMsg=msg
                }
            }
            else->{
                Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                    Column(modifier = Modifier, verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally){
                        Spacer(Modifier.height(20.dp))
                        AnimatedVisibility(visible = checkoutStateMsg.isNotEmpty()){
                            Text(checkoutStateMsg, textAlign = TextAlign.Center)
                        }
                        Spacer(Modifier.height(20.dp))
                        Button(modifier=Modifier.fillMaxWidth()
                            .padding(horizontal = 20.dp),
                            onClick = {
                                // initiate payment
                                observeCheckoutState=true
                                initiatePaymentMethod(paypalSdkInstance, orderUseCase, createdItems)
                            }){
                            Text("Complete Purchase")
                        }
                    }
                }
            }
        }
    }
    /* Observes the different check-out states and updates the screen with the correct message
    * Can be customized according to the user-needs */
    @Composable
    private fun ObservePaypalCheckoutState(checkoutState: PaypalCheckoutState,
                                           updateObserveCheckoutState:(Boolean,String)->Unit){
        LaunchedEffect(checkoutState){
            when(checkoutState){
                Loading -> Log.d("ComposeActivityTag","Initiating order capture..")
                OrderCapturingFailed -> {
                    // stop observing and show the default view to enable the user to initiate payment again
                    updateObserveCheckoutState(false,"Order capturing failed..")
                    Log.d("ComposeActivityTag","Order capturing failed..")
                }
                OrderPaidForSuccessfully ->{
                    Log.d("ComposeActivityTag","Order capturing success")
                    // stop observing and show the default view to enable the user to initiate payment again
                    updateObserveCheckoutState(false, "Order capturing success")
                }
                OrderPaymentCancelled ->{
                    Log.d("ComposeActivityTag","Order payment cancelled!")
                    updateObserveCheckoutState(false,"Order payment cancelled!")
                }
                is OrderPaymentFailedWithAnError ->{
                    updateObserveCheckoutState(false,"Order payment failed with the following error ${checkoutState.errorMsg}")
                    Log.d("ComposeActivityTag","Order payment failed with the following error ${checkoutState.errorMsg}")
                }
            }
        }
    }
    /* Creates the order item needed by paypal checkout sdk */
    private fun initiatePaymentMethod(
        paypalSdkInstance: PayPalCheckout,
        orderUseCase: CreateOrderUseCase,
        createdItems: List<CreatedItem>
    ) {
        // create your order request
        val createOrderRequest = CreateOrderRequest(
            orderIntent = OrderIntent.CAPTURE,
            userAction = UserAction.PAY_NOW, shippingPreference = ShippingPreference.NO_SHIPPING,
            currencyCode = CurrencyCode.USD, createdItems = createdItems
        )
        val orderObject = orderUseCase.execute(createOrderRequest)
        // start check out process
        paypalSdkInstance.startCheckout(createOrder = CreateOrder.invoke { actions ->
            actions.create(orderObject) { orderId ->
                Log.d("ComposeActivityTag", "Order id $orderId")
            }
        })
    }
}
