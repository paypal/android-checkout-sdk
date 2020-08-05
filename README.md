# PayPal Checkout Samples for Android

This repository contains various sample applications for the PayPal Checkout SDK for Android. If you experience issues with the sample app or have general questions please create a new [issue](https://github.com/paypal/paypalcheckout-samples-android/issues) if one related to your question does not already exist.

## Sample App Preparation

The sample project is intended to be as hands off as possible. With that in mind, there are only two
values which are required to this sample app and they include:

1. An app client ID. This is used by the CheckoutConfig and ensures your application can authorize
customers to place orders.
2. A corresponding app secret. This is required for generating payment tokens. This is not required
for your own implementation of the PayPal Checkout SDK and is only used to illustrate how you could
generate tokens for customer orders with a backend system.
3. Setting a return URL.

Please reference our [PayPal Native Checkout Initial Setup](https://developer.paypal.com/docs/limited-release/native-checkout/setup/#)
overview to learn about how to create a new PayPal application as well as how to find those details. **At this time, the SDK is in limited release so please be sure to follow all of the steps outlined.**
Once you have the credentials available you will want to add them to `QuickStartConstants.kt`.

```kotlin
// QuickStartConstants.kt
const val PAYPAL_CLIENT_ID = "YOUR-CLIENT-ID-HERE"
const val PAYPAL_SECRET = "ONLY-FOR-QUICKSTART-DO-NOT-INCLUDE-SECRET-IN-CLIENT-SIDE-APPLICATIONS"
```

### Setting a Return URL

A return URL is required for redirecting users back to the sample app after authenticating. Setting the return URL is referenced in step 4 of the [PayPal Native Checkout Initial Setup](https://developer.paypal.com/docs/limited-release/native-checkout/setup/#), however instead of setting the Live App Settings you want to ensure you are setting your Sandbox App Settings. The return URL you should use is `com.paypal.checkoutsamples://paypalpay`.

## Ways To Invoke The SDK

There are several different ways that the SDK can be invoked, regardless of which method though, all
of them will require a proper `CheckoutConfig` instance which is configured for your specific Client ID.

### Setting Up Your CheckoutConfig

A valid CheckoutConfig requires setting several different properties which include:
* `checkoutConfig.clientId` which is the Client ID related for your application that will be integrating
with the Native SDK. You can find more information about creating a new Client ID or finding an existing
one [here](https://developer.paypal.com/docs/api/overview/?mark=client%20id#get-credentials).
* `checkoutConfig.setMerchantRedirectScheme` which is the return URL that will be used by the Checkout
SDK and must be added to your PayPal App Settings via the [Developer Dashboard](https://developer.paypal.com/developer/applications).
The SDK will automatically append `://paypalpay` to the end of this URL, so if the return URL was the
application ID for `com.example.app` then you would need to provide `com.example.app://paypalpay` from
within the PayPal App Settings.
* `checkoutConfig.setPayPalEnvironment` sets the app environment that should be used. PayPal has two
different environment for every app, Staging (`RunTimeEnvironment.SANDBOX`) and Production (`RunTimeEnvironment.LIVE`).
* `checkoutConfig.payPalCheckoutCompleteListener` which is invoked when the pay sheet is dismissed.
If the pay sheet was dismissed without the customer successfully checking out then `onCheckoutCancelled`
will be invoked. If it was successful then `onCheckoutComplete` will be invoked, the parameters provided
can then be used to finalize the checkout experience.

Here is an example of a full `CheckoutConfig`:

```kotlin
val checkoutConfig by lazy {
    CheckoutConfig.getInstance()
        /*
         * Please see [QuickStartConstants], be sure to update the value of this constant prior
         * to running this sample.
         */
        .apply { clientId = PAYPAL_CLIENT_ID }
        /*
         * Note: this will append ://paypalpay which will be used as the Return URL. In our case
         * for this sample we set the following return url within the admin console on PayPal
         * Developers: com.paypal.checkoutsamples://paypalpay
         */
        .apply { setMerchantRedirectUrlScheme(BuildConfig.APPLICATION_ID) }
        /*
         * For the purpose of this sample we are working within the Sandbox environment. Once
         * you are ready to do a production release this should instead point to
         * [RunTimeEnvironment.LIVE].
         */
        .apply { setPayPalEnvironment(RunTimeEnvironment.SANDBOX) }
        /*
         * [PayPalCheckoutCompleteListener] is required and will be invoked after the pay sheet
         * is closed.
         */
        .apply {
            payPalCheckoutCompleteListener = object : PayPalCheckoutCompleteListener {
                override fun onCheckoutCancelled(
                    cancelReason: CheckoutCancelReason,
                    reason: String
                ) {
                    Log.i("CheckoutCancelled", reason)
                    showCheckoutComplete("Checkout Cancelled", "Reason: $reason")
                }

                override fun onCheckoutComplete(params: HashMap<String, String>) {
                    val formattedParameters = params.map { "${it.key} : ${it.value}" }
                        .joinToString(separator = "\n\n")
                    Log.i("CheckoutComplete", formattedParameters)
                    showCheckoutComplete("Checkout Complete", formattedParameters)
                }

            }
        }
}
```

### startCheckoutWithOrders

If you want the Native Checkout SDK to create a new `Order` on your behalf then using `checkoutSdk.startCheckoutWithOrders`
is the perfect solution. A full walk through for how to create a valid `Order` can be found [here](quickstart-kotlin/src/main/java/com/paypal/checkoutsamples/order/README.md).

```kotlin
val order: Order = //... refer to the full walk through for creating a valid order above ☝️
checkoutSdk.startCheckoutWithOrders(
    context = this, // Activity Context
    order = order,
    orderCallbacks = object : OrderCallbacks {
        override fun onOrderCreateFailed(exception: PYPLException) {
            // The order could not be created, please consult Logcat for the reason why.
            Log.w("OrderCreateFailed", exception)
        }

        override fun onOrderCreated(id: String) {
            // The order was created, at this point the pay sheet should be displaying the order total
            // along with additional details.
            Log.i("OrderCreated", "id: $id")
        }
    },
    checkoutConfig = checkoutConfig
)
```

### startCheckoutWithToken

If you want the Native Checkout SDK to only provide a pay sheet using a pre-existing order ID (referred 
to as a token or EC token) then using `checkoutSdk.startCheckoutWithToken` is the perfect solution.
A full walk through for how to create a valid token can be found [here](quickstart-kotlin/src/main/java/com/paypal/checkoutsamples/token/README.md).

```kotlin
val orderToken: String = //... refer to the full walk through for how tokens are created above ☝️
checkoutSdk.startCheckoutWithToken(
    context = this, // Activity Context
    token = orderToken,
    checkoutConfig = checkoutConfig
)
```
