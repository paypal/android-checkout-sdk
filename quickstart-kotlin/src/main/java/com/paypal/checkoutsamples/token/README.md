# startCheckoutWithToken

If you want the Native Checkout SDK to only provide a pay sheet using a pre-existing order ID (referred 
to as a token or EC Token) then using `checkoutSdk.startCheckoutWithToken` is the perfect solution.

```kotlin
val orderToken: String = //... refer to the full walk through for how tokens are created in the next section.
checkoutSdk.startCheckoutWithToken(
    context = this, // Activity Context
    token = orderToken,
    checkoutConfig = checkoutConfig
)
```

If you are looking to step through the implementation yourself then starting with `TokenQuickStartActivity` is a great place to begin. Below you'll find a walk through of the critical parts of this sample.

## Creating An Order Token

**Disclaimer:** _To keep this sample simple we are making the appropriate API calls to create an order token on device.
However these API calls require using a combination of a client ID and secret, and since secrets are
essentially passwords you should avoid implementing the following code within a client. It's recommended
that this is handled by a server side service._

### Required API Calls

In order to create an order there are two API calls which are invoked by the sample app. The first is a call to [generate an OAuth token](https://developer.paypal.com/docs/api/get-an-access-token-postman/) and the second is one for [creating the order](https://developer.paypal.com/docs/api/orders/v2/#orders_create).

With this sample we opted to make use of [Retrofit](https://square.github.io/retrofit/) to handle our network requests and [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization) for serialization and deserialization of requests and responses.

#### Generating An OAuth Token

The process to generate an OAuth Token is relatively simple since `Retrofit`, `kotlinx.serialization`, and `Credentials` (from OkHttp) take care of the heavy lifting for us.

First we setup our `payPalAuthorization` String which will be passed in as  the `Authorization` header to the following url: https://api.sandbox.paypal.com/v1/oauth2/token. The required structure is as follows `Bearer ${Base64(clientId:secret)}` and it is accomplished with the following snippet:

```kotlin
interface CheckoutApi {
    // ...
    companion object {
        // ...
        val payPalAuthorization: String = Credentials.basic(PAYPAL_CLIENT_ID, PAYPAL_SECRET)
        // ...
    }
    // ...
}
```

Here we make use use of the `Credentials.basic` function which takes a user name (client ID) and password (secret) and joins the two together `"$clientId:$secret"` followed by Base64 encoding. The final string returned by the `basic` function will be prepended with `Basic` and is ready to be passed into the `Authorization` header.

Our definition for `postOAuthToken` provides the necessary annotations for this network call to succeed providing that a valid `PAYPAL_CLIENT_ID` and `PAYPAL_SECRET` were included in the step above (simply update those values in `QuickStartConstants.kt` if you haven't already).

```kotlin
interface CheckoutApi {
    // ...
    @Headers("Accept: application/json", "Accept-Language: en_US")
    @FormUrlEncoded
    @POST("/v1/oauth2/token")
    suspend fun postOAuthToken(
        @Header("Authorization") authorization: String = payPalAuthorization,
        @Field("grant_type") grantType: String = "client_credentials"
    ): OAuthTokenResponse
    // ...
}
```

Notice that we default to `client_credentials` for the `grant_type` field, default to `payPalAuthorization` for the authorization parameter, and are  using the `@FormUrlEncoded` annotation. The final part to go over is the `OAuthTokenResponse` that is being returned.

```kotlin
// OAuthTokenResponse.kt
@Serializable
data class OAuthTokenResponse(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("expires_in")
    val expiresIn: Long
)
```

You'll notice that we are only deserializing two fields from the response. The first is the `access_token` field which is required for making authorized requests for other API endpoints. The second is the `expires_in` field which allows us to add some caching around the token to minimize the number of network requests we need to make.

In the end we are left with the following snippet spanning two classes and files:

```kotlin
// CheckoutApi.kt
interface CheckoutApi {
    // ...
    @Headers("Accept: application/json", "Accept-Language: en_US")
    @FormUrlEncoded
    @POST("/v1/oauth2/token")
    suspend fun postOAuthToken(
        @Header("Authorization") authorization: String = payPalAuthorization,
        @Field("grant_type") grantType: String = "client_credentials"
    ): OAuthTokenResponse
    // ...

    companion object {
        // ...
        val payPalAuthorization: String = Credentials.basic(PAYPAL_CLIENT_ID, PAYPAL_SECRET)
        // ...
    }
}

// OAuthTokenResponse.kt
@Serializable
data class OAuthTokenResponse(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("expires_in")
    val expiresIn: Long
)
```

#### Creating An Order

With this sample we are going to create a new `Order` using the [/v2/checkout/orders](https://developer.paypal.com/docs/api/orders/v2/#orders_create) API endpoint.

Creating a new `Order` starts with our function definition within the `CheckoutApi` interface. For this sample we are making requests to our sandbox environment so the full url is: https://api.sandbox.paypal.com/v2/checkout/orders.

```kotlin
interface CheckoutApi {
    // ...
    @Headers("Accept: application/json")
    @POST("/v2/checkout/orders")
    suspend fun postCheckoutOrder(
        @Header("Authorization") authorization: String,
        @Body orderRequest: OrderRequest
    ): OrderResponse
    // ...
}
```

Some parts to take note of with the above snippet include the `Authorization` header as well as the `OrderRequest` and `OrderResponse`. We will go over the request and response in a moment, so for now let's focus on `Authorization`.

The `Authorization` header will be a different value from the one we provided to `postOAuthToken`, instead of creating a Basic token we will use the `accessToken` provided by the `OAuthTokenResponse`. We'll go over this in more depth once we discuss the `OrderRepository`.

Assuming a valid `accessToken` is provided the next part we need to provide is an `OrderRequest`. For a complete list of possible parameters you can reference the [create order](https://developer.paypal.com/docs/api/orders/v2/#orders_create) API documentation. The values we are providing for this sample are a simplified version that let us retrieve a valid order ID (EC Token) and also demonstrate the pay sheet customization.

```kotlin
// OrderRequest.kt
// ...
@Serializable
data class OrderRequest(
    val intent: String = OrderIntent.CAPTURE.name,
    @SerialName("application_context")
    val applicationContext: ApplicationContextRequest = ApplicationContextRequest(),
    @SerialName("purchase_units")
    val purchaseUnits: List<PurchaseUnitRequest> = listOf(PurchaseUnitRequest())
)
// ...
```

The `OrderRequest` object is annotated with `@Serializable`, this allows `kotlinx.serialization` to do the heavy lifting for us while we focus on the business requirements.

The three properties we are concerned with are the `intent`, `applicationContext`, and `purchaseUnits`.

1. `intent` allows us to tell PayPal how the order will be handled. We are defaulting to `CAPTURE` in this case as it tells PayPal we intend to capture the payment imemdiately after the customer makes a payment. `AUTHORIZE` is another option if you want to authorize the payment to put the funds on hold but wish to complete the payment at a later date. You can learn more about `intent` with our [reference documentation](https://developer.paypal.com/docs/api/orders/v2/#definition-checkout_payment_intent).
2. `applicationContext` lets us encapsulate details specific to our application. It can include things like `brand_name`, `locale`, `shipping_preference`, and `user_action` to name a few. In our case we are only concerned with `user_action` and default to `PAY_NOW` which will display the pay sheet with the total amount a customer will be billed. `CONTINUE` is another option when you want to display the total on your redirect screen. You can learn more about `applicationContext` with our [reference documentation](https://developer.paypal.com/docs/api/orders/v2/#definition-order_application_context).
3. `purchaseUnits` are the final property we are concerned with this for this example. A purchase unit lets us provide additional details about the order to PayPal, most importantly, it is where we include the total amount that a customer will be billed. You can learn more about `purcahseUnits` with our [reference documentation](https://developer.paypal.com/docs/api/orders/v2/#definition-purchase_unit_request).

Since `intent` is only a String value there is nothing more we need to do with that. However `applicationContext` and `purchaseUnits` are treated as their own objects and thus need to do a bit more work for those parts of the request.

```kotlin
// OrderRequest.kt
// ...
@Serializable
data class ApplicationContextRequest(
    @SerialName("user_action")
    val userAction: String = UserAction.PAY_NOW.name
)
// ...
```

For the `ApplicationContextRequest` our only concern is providing a `userAction` and ensuring it is serialized using snake case instead of camel case.

```kotlin
// OrderRequest.kt
// ...
@Serializable
data class PurchaseUnitRequest(
    val amount: AmountRequest = AmountRequest()
)
// ...
```

For the `PurchaseUnitRequest` our only concern is providing the total `amount` of the order. However since `amount` is itself an object we have one more class to define, `AmountRequest`.

```kotlin
// OrderRequest.kt
// ...
@Serializable
data class AmountRequest(
    @SerialName("currency_code")
    val currencyCode: String = CurrencyCode.USD.name,
    val value: String = "100.00"
)
// ...
```

An `amount` consists of a `value` String which should be in the format of `#.##` along with a correspdoning `currencyCode`. In our example we default to `100.00 USD` if no value is provided. One thing to note is we only use `SerialName` for `currencyCode`, since `value` is a single word it's both valid for camel case and snake case and it's not necessary to override the name.

Once we have a valid `OrderRequest` we can expect that `postCheckoutOrder` will return an `OrderResponse`. To close out creating an `Order` lets go over what that response looks like.

```kotlin
// OrderResponse.kt
@Serializable
data class OrderResponse(
    val id: String
)
```

You may notice that this response is quite a bit smaller than the one outlined in our [reference documentation](https://developer.paypal.com/docs/api/orders/v2/#orders-create-response). For the purpose of this sample we are only concerned with the `id` of the `Order` as that will allow us to invoke the pay sheet using the `startCheckoutWithToken` function, passing the `id` into the `token` parameter.

In the end we are left with the following snippet spanning six classes and three files:

```kotlin
// CheckoutApi.kt
interface CheckoutApi {
    // ...
    @Headers("Accept: application/json")
    @POST("/v2/checkout/orders")
    suspend fun postCheckoutOrder(
        @Header("Authorization") authorization: String,
        @Body orderRequest: OrderRequest
    ): OrderResponse
    // ...
}

// OrderRequest.kt
@Serializable
data class OrderRequest(
    val intent: String = OrderIntent.CAPTURE.name,
    @SerialName("application_context")
    val applicationContext: ApplicationContextRequest = ApplicationContextRequest(),
    @SerialName("purchase_units")
    val purchaseUnits: List<PurchaseUnitRequest> = listOf(PurchaseUnitRequest())
)

@Serializable
data class ApplicationContextRequest(
    @SerialName("user_action")
    val userAction: String = UserAction.PAY_NOW.name
)

@Serializable
data class PurchaseUnitRequest(
    val amount: AmountRequest = AmountRequest()
)

@Serializable
data class AmountRequest(
    @SerialName("currency_code")
    val currencyCode: String = CurrencyCode.USD.name,
    val value: String = "100.00"
)

// OrderResponse.kt
@Serializable
data class OrderResponse(
    val id: String
)
```

#### Encapsulating The Order Creation Process

Using the above code samples we can further refine our solution by creating two simple repositories, `AuthTokenRepository` and `OrderRepository`. We won't get too in-depth on those within this document but you can find fully working samples within the `com.paypal.checkoutsamples.token.repository` package. The main idea though is we allow those repositories manage how they retrieve and create their objects. With the `AuthTokenRepository` it only allows you to retrieve one, if one doesn't exist or it has expired then a new one is generated and returned. The `OrderRepository` lets you provide some details about the `Order` and then it takes care of constructing the API request and returning a valid `CreatedOrder` (or throw an exception).

## Invoking The SDK With A Token

Once you have a valid EC Token (Order ID) can invoke the SDK to launch a pay sheet.

```kotlin
checkoutSdk.startCheckoutWithToken(
    context = this,
    token = orderToken, // <-- the id of the order that was just created
    checkoutConfig = checkoutConfig
)
```
