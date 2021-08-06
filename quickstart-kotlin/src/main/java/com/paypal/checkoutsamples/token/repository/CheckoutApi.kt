package com.paypal.checkoutsamples.token.repository

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.paypal.checkoutsamples.PAYPAL_CLIENT_ID
import com.paypal.checkoutsamples.PAYPAL_SECRET
import com.paypal.checkoutsamples.token.repository.request.OrderRequest
import com.paypal.checkoutsamples.token.repository.response.OAuthTokenResponse
import com.paypal.checkoutsamples.token.repository.response.OrderResponse
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Credentials
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * CheckoutApi includes the necessary endpoints for creating an order and receiving an EC token that
 * can be used for launching the Checkout SDK pay sheet with a token.
 *
 * Note: when implementing your own integration, you should avoid creating the OAuth Token within your
 * Android app as the creating an OAuth Token in these examples require passing your app secret.
 */
interface CheckoutApi {

    /**
     * Creates a new OAuth Token that can be used for subsequent API requests.
     *
     * @see payPalAuthorization
     */
    @Headers("Accept: application/json", "Accept-Language: en_US")
    @FormUrlEncoded
    @POST("/v1/oauth2/token")
    suspend fun postOAuthToken(
        @Header("Authorization") authorization: String = payPalAuthorization,
        @Field("grant_type") grantType: String = "client_credentials"
    ): OAuthTokenResponse

    /**
     * Creates a new Order, the subsequent id ([OrderResponse.id]) can then be used to start a new
     * pay sheet instance.
     */
    @Headers("Accept: application/json")
    @POST("/v2/checkout/orders")
    suspend fun postCheckoutOrder(
        @Header("Authorization") authorization: String,
        @Body orderRequest: OrderRequest
    ): OrderResponse

    companion object {

        /**
         * Valid credentials for creating a server <-> server auth token require a valid client id
         * as the user and a corresponding secret for the password. This should be encoded in Base64.
         *
         * The proper format prior to encoding is as follows: clientId:secret
         *
         * OkHttp provides a convenient "Basic" function which handles the heavy lifting for us.
         */
        val payPalAuthorization: String = Credentials.basic(PAYPAL_CLIENT_ID, PAYPAL_SECRET)

        /**
         * Provides an easy way to instantiate a [CheckoutApi] as you normally would if this were a
         * class instead of an interface.
         *
         * Example: val checkoutApi = CheckoutApi()
         */
        @ExperimentalSerializationApi
        operator fun invoke(): CheckoutApi {
            val json = Json {
                ignoreUnknownKeys = true
            }
            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.sandbox.paypal.com/")
                .addConverterFactory(
                    json.asConverterFactory("application/json".toMediaType())
                )
                .build()

            return retrofit.create(CheckoutApi::class.java)
        }
    }
}

/**
 * Updates a [String] to be a valid Bearer token (assuming the String itself is a valid OAuth Token).
 */
val String.asBearer: String
    get() = "Bearer $this"
