# PayPal Checkout Samples for Android

![Maven Central](https://img.shields.io/maven-central/v/com.paypal.checkout/android-sdk?style=for-the-badge) ![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/com.paypal.checkout/android-sdk?server=https%3A%2F%2Foss.sonatype.org&style=for-the-badge)

This repository contains various sample applications for the PayPal Checkout SDK for Android. If you have questions, comments, or ideas related to the Android Checkout SDK or the sample apps please create a new [issue](https://github.com/paypal/paypalcheckout-samples-android/issues) if one related to your question does not already exist.

## Sample App Preparation

The sample project is intended to be as hands off as possible. With that in mind, there are only two
values which are required to this sample app and they include:

1. An app client ID. This is used by the CheckoutConfig and ensures your application can authorize
customers to place orders.
2. A corresponding app secret. This is required for generating payment tokens. This is not required
for your own implementation of the PayPal Checkout SDK and is only used to illustrate how you could
generate tokens for customer orders with a backend system.
3. Setting a return URL.

Please reference our [developer documentation](https://developer.paypal.com/docs/business/native-checkout/android/)
overview to learn about how to create a new PayPal application as well as how to find those details. **At this time, the SDK is in limited release so please be sure to follow all of the steps outlined.**
Once you have the credentials available you will want to add them to `QuickStartConstants.kt`.

```kotlin
// QuickStartConstants.kt
const val PAYPAL_CLIENT_ID = "YOUR-CLIENT-ID-HERE"
const val PAYPAL_SECRET = "ONLY-FOR-QUICKSTART-DO-NOT-INCLUDE-SECRET-IN-CLIENT-SIDE-APPLICATIONS"
```

### Setting a Return URL

A return URL is required for redirecting users back to the sample app after authenticating. For more details on setting a return URL please see our [developer documentation](https://developer.paypal.com/docs/business/native-checkout/android/#know-before-you-code),
however instead of setting the Live App Settings you want to ensure you are setting your Sandbox App Settings. The return URL you should use is `com.paypal.checkoutsamples://paypalpay`.

## Releases

New versions of the Android Checkout SDK are published via MavenCentral. Please refer to the badge at the top of this repository for the latest version of the SDK. Please see our [change log](CHANGELOG.md) to understand what changed from one version to the next.

### Adding Dependency via Gradle Groovy DSL
```groovy
implementation 'com.paypal.checkout:android-sdk:<CURRENT-VERSION>'
```

### Adding Dependency via Gradle Kotlin DSL
```kotlin
implementation("com.paypal.checkout:android-sdk:<CURRENT-VERSION>")
```

### Snapshots

Snapshot builds are available [through Sonatype](https://oss.sonatype.org/content/repositories/snapshots/) and can be used for early testing of new features or validating a reported issue has been resolved. **Snapshots should not be considered stable or production ready**. Please use the latest stable release of the Android Checkout SDK for production builds.
