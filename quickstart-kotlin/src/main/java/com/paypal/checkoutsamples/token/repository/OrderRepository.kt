package com.paypal.checkoutsamples.token.repository

import android.util.Log
import com.paypal.checkoutsamples.token.repository.request.OrderRequest
import com.paypal.checkoutsamples.token.repository.response.OrderResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

/**
 * OrderRepository provides an entry point to create new orders. The main purpose with regards to
 * this sample app is being able to get an Order ID (EC Token) which can then be provided to the
 * Checkout SDK to launch a pay sheet.
 */
class OrderRepository(
    private val checkoutApi: CheckoutApi,
    private val authTokenRepository: AuthTokenRepository = AuthTokenRepository(checkoutApi),
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val tag = javaClass.simpleName

    /**
     * Creates a new Order ([CreatedOrder]) given a [CreateOrderRequest].
     */
    suspend fun create(request: CreateOrderRequest): CreatedOrder {
        Log.d(tag, "Create order request: $request")
        val token = authTokenRepository.retrieve()
        return try {
            withContext(dispatcher) {
                checkoutApi.postCheckoutOrder(
                    authorization = token.accessToken.asBearer,
                    orderRequest = request
                )
            }
        } catch (ex: HttpException) {
            Log.w(tag, "Unable to create order with token: $token")
            Log.e(tag, "Could not create order: $ex")
            throw ex
        }
    }
}

typealias CreateOrderRequest = OrderRequest
typealias CreatedOrder = OrderResponse
