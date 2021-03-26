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

Please reference our [developer documentation](https://developer.paypal.com/docs/business/native-checkout/android)
overview to learn about how to create a new PayPal application as well as how to find those details. **At this time, the SDK is in limited release so please be sure to follow all of the steps outlined.**
Once you have the credentials available you will want to add them to `QuickStartConstants.kt`.

```kotlin
// QuickStartConstants.kt
const val PAYPAL_CLIENT_ID = "YOUR-CLIENT-ID-HERE"
const val PAYPAL_SECRET = "ONLY-FOR-QUICKSTART-DO-NOT-INCLUDE-SECRET-IN-CLIENT-SIDE-APPLICATIONS"
```

### Setting a Return URL

A return URL is required for redirecting users back to the sample app after authenticating. For more details on setting a return URL please see our [developer documentation](https://developer.paypal.com/docs/business/native-checkout/android),
however instead of setting the Live App Settings you want to ensure you are setting your Sandbox App Settings. The return URL you should use is `com.paypal.checkoutsamples://paypalpay`.
