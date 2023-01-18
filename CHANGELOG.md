# Change Log


## Version 0.8.8
* Adding the `ReturnUrl` requirement back into the `CheckoutConfig`

## Version 0.8.7
* Upgraded the Cardinal SDK to version 2.2.7-2 which made updates to Device Data collection related to Google Play's User Data Policy

Note: If you are seeing the Play Store flag your APK after updating to this version, please try following these steps:
1. Go to your Play Console
2. Select the app
3. Go to App bundle explorer
4. Select the violating APK/app bundle's App version at the top right dropdown menu, and make a note of which releases they are under
5. Go to the track with the violation. It will be one of these 4 pages: Internal / Closed / Open testing or Production
6. Near the top right of the page, click Create new release. (You may need to click Manage track first)
If the release with the violating APK is in a draft state, discard the release
7. Add the new version of app bundles or APKs
    Make sure the non-compliant version of app bundles or APKs is under the Not included section of this release
8. To save any changes you make to your release, select Save
9. When you've finished preparing your release, select Review release, and then proceed to roll out the release to 100%.
10. If the violating APK is released to multiple tracks, repeat steps 5-9 in each track

## Version 0.8.6
* Added `CorrelationIds` to the `Approval` object returned from the approve order call

## Version 0.8.5
* Fixed a [web fallback issue](https://github.com/paypal/android-checkout-sdk/issues/153) causing an infinite spinner when coming back from web fallbacks flows.
* Fixed two issues related to auth native and legacy web auth flows.

## Version 0.8.4
* Added the `phone`, `birthDate` and `taxInfo`  fields to the `Payer`object that comes as part of the `OrderResponse` object in `getOrderDetails()`, `capture()` and `authorize()` order actions.

## Version 0.8.3
* Added `getOrderDetails()` to `OrderActions` in the onApprove callback for getting order information after an order has been approved

## Version 0.8.2
* Added the ability to accept a shipping change without having to patching the order
* Resolved crashes caused by Encrypted Shared Preferences
* Fixed an issue caused by a duplicate package name when minifying
* Accessibility fixes

## Version 0.8.1
* Removing the requirement for a returnUrl(If there are issues with returning back to the original app, try adding *nativexo://paypalpay* as another returnUrl on the developer portal)
* Adding the ability to add a card on US and EU
* Fixing memory leak issues

## Version 0.8.0
* Adding in support for native one time password
* Adding in support for checking out with crypto

## Version 0.7.3
* The original returnUrl was added back into this release and needs to be set in the merchant portal if it wasn't already set

## Version 0.7.2
* This version introduces a breaking change around removing the use for a returnUrl
* In order to get authenticatation working correctly, there needs to be a returnUrl of *nativexo://paypalpay* added to the clientId of the app
    * If this returnUrl is not added the app will not redirect correctly after auth

## Version 0.7.1
* Adding in native support for Billing Agreements
```    fun setBillingAgreementId(billingAgreementId: String) {
        CoroutineScope(coroutineContext).launch {
            val convertedEcToken = try {
                baTokenToEcTokenAction.execute(billingAgreementId)
            } catch (exception: Exception) {
                internalOnOrderCreated(
                    OrderCreateResult.Error(
                        PYPLException("exception with setting BA id: ${exception.message}")
                    )
                )
                null
            }
            convertedEcToken?.let {
                with(DebugConfigManager.getInstance()) {
                    checkoutToken = convertedEcToken
                    repo.isVaultFlow = false
                    applicationContext?.let { Cache.cacheIsVaultFlow(it, repo.isVaultFlow) }
                    internalOnOrderCreated(OrderCreateResult.Success(convertedEcToken))
                }
            }
        }
    }
```
## Version 0.7.0
* Extracting 3ds and cardinal into its own optional module controlled by build flavors
```
    productFlavors {
        external {
            dimension "clientType"
        }
    }
```
* Adding in native smart payment buttons
* Adding in support for Overcapture
* UI updates

## Version 0.6.3
* Updated paypal authentication SDK to 1.6.0 
* Resolved refresh token to access token exchange for subsequent logins use cases 

## Version 0.6.2
* Updated Proguard rules resolving a crash related to EncryptedSharedPreferences on minified builds
* OnShippingChange callback is now invoked when the checkout experience is launched
* Added ability to customize the cancellation dialog shown to users

```kotlin
val config = CheckoutConfig(
    ...
    uiConfig = UIConfig(
        showExitSurveyDialog = true
    )
)
```

## Version 0.6.1
* Added support for v2/vault web fallback
* Resolves a navigation issue specific to Android 6
* Animation updates

###  Required Proguard Rules for 0.6.1
```
-keepclassmembers class * extends com.google.crypto.tink.shaded.protobuf.GeneratedMessageLite {
  <fields>;
}
```
There's an issue with EncryptedSharedPreferences where the library is missing a required proguard rule. The temporary fix is to include the above rule in your app module's `proguard-rules.pro` file. Future versions of the SDK will automatically include this rule.

## Version 0.6.0
* SDK now targets Android 12 version (API 31) as well as Java 11.
* Resolved a crash that happens when the app gets destroyed and then re-created by Android OS due to a config change.
* Simplified Amount model returned in the OnApprove callback.

Note: There are some functions that were deprecated and some return types that were changed. Please refer to the upgrade guide when migrating from an earlier version: https://developer.paypal.com/sdk/in-app/android/upgrade/

## Version 0.5.4
* Resolved an issue with the shipping callback not getting invoked
* Resolved a crash caused by internal instrumentation
* Resolved a crash related gradients on Android 6
* Added a web checkout fallback for when there is a 3DS challenge

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
