# Change Log
## Version 0.5.2
* Improvements to logging
* Resolved a crash in the address selection screen
* Updated to AndroidX libraries - Jetifier is no longer needed.

## Version 0.5.1
* Resolved a crash in the buyer authentication process

## Version 0.5.0
* Added OnShippingChanged callback
* Resolved a bug where adding a new shipping address would break the checkout experience
* Added additional error information in the OnError callback

## Version 0.4.5
* Fix bug for disappearing payment button
* Added payee info to order

## Version 0.4.4
* Resolved a crash caused by registering network callbacks on Android 11

## Version 0.4.3
* Resolved a crash caused by null checkout session
* Resolved conflicting attribute names
* Added buyer's name, phone and address to Approval

## Version 0.4.2
* Resolved a crash caused by the funding eligibility call

## Version 0.4.1
* Resolved a bug where rapid, multiple clicks of the payment button would stop the checkout flow
* Resolved a bug where setting the config in the Application class would render payment buttons to be ineligible

## Version 0.4.0
* Invoking the SDK now requires API level 23 and up (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
* Added `paymentButtonIntent` to `CheckoutConfig`
* `Cart` and `Buyer` are now returned in `OnApproves`'s `ApprovalData`
* `ProcessingInstruction` can be set when creating an order client side.
* Resolved a bug where the SDK would crash with `kotlin.UninitializedPropertyAccessException: lateinit property accessToken has not been initialized`

## Version 0.3.1

* Resolved a bug where the SDK would crash when the buyer tries to authenticate with PayPal

## Version 0.3.0

* Added the ability to pass in a Billing Agreement token through `CreateOrderActions`
* Added the ability to patch an order through `OrderActions` in the `OnApprove` callback
* Resolved a bug where the SDK would invoke the `OnError` callback on a background thread

## Version 0.2.0
* Added Cardinal to support 3DS, this will require adding a private maven repository in order to import the SDK.

```groovy
    url  "https://cardinalcommerceprod.jfrog.io/artifactory/android"
    credentials {
        // Be sure to add these non-sensitive credentials in order to retrieve dependencies related to the Cardinal SDK.
        username 'paypal_sgerritz'
        password 'AKCp8jQ8tAahqpT5JjZ4FRP2mW7GMoFZ674kGqHmupTesKeAY2G8NcmPKLuTxTGkKjDLRzDUQ'
    }
```

* Added the ability to cancel checkout through `CreateOrderActions`, this is useful if an error occurs while generating an Order ID from a server-side integration.
* Resolved a bug where the SDK would crash if a buyer clicked the "Cancel checkout and Return" text while authenticating.
* Resolved a bug where the SDK would occasionally get stuck after the buyer approved an order.

## Version 0.1.0
Initial release. Please see [official documentation](https://developer.paypal.com/docs/business/native-checkout/android/) for full integration steps.

* Added `PayPalCheckout` as one of the main interfaces for launching the pay sheet.
* Added `PaymentButton` along with `PayPalButton`, `PayLaterButton`, and `PayPalCreditButton` which can also be used to launch the pay sheet (this is the preferred option as well).
* Added `CreateOrder` interface which allows for orders to be created with both client-side and server-side integrations.
* Added `OnApprove` interface which notifies the client when a buyer approves an order, at this point the client application can either `Capture` or `Authorize` an order.
* Added `OnError` interface which notifies the client when a terminal error occurred in the experience, in these situations the pay sheet will be dismissed.
* Added `OnCancel` interface which notifies the client application that a buyer cancelled out of the experience.
