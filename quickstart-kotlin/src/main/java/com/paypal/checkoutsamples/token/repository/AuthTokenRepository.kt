package com.paypal.checkoutsamples.token.repository

import android.util.Log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date

/**
 * AuthTokenRepository provides a way to retrieve a valid [AuthToken]. If you encounter errors with
 * this repository please ensure you have a valid client id and secret set in QuickStartConstants.
 */
class AuthTokenRepository(
    private val checkoutApi: CheckoutApi,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val tag = this::class.java.toString()

    /**
     * Retrieves a valid [AuthToken]. If one does not exist or has expired a new one will be created
     * and returned.
     */
    suspend fun retrieve(): AuthToken {
        val currentAuthToken = authToken
        return if (currentAuthToken == null || currentAuthToken.expiresAt < Date()) {
            Log.i(tag, "Creating a new OAuth Token...")
            val oAuthToken = withContext(dispatcher) {
                checkoutApi.postOAuthToken()
            }
            Log.i(tag, "New token created...")
            Log.d(tag, "Token: $oAuthToken")

            val expiresAt: Long = (oAuthToken.expiresIn * expirationFactor).toLong()
            AuthToken(
                accessToken = oAuthToken.accessToken,
                expiresAt = Date().add(expiresAt)
            )
                .also { authToken = it }
                .also { Log.d(tag, "New token cached: $it") }
        } else {
            Log.i(tag, "Valid token exists, returning to caller.")
            currentAuthToken
        }
    }

    private fun Date.add(seconds: Long): Date = apply { time += seconds }

    companion object {
        /**
         * The expirationFactor is applied to our OAuth Token expiration time. The primary goal is
         * to avoid using a token just as it's expiring, so instead of using the full time returned
         * by the API we only use 95% of that time to avoid making requests with an expired token.
         */
        private const val expirationFactor = 0.95

        private var authToken: AuthToken? = null
    }
}

data class AuthToken(
    val accessToken: String,
    val expiresAt: Date
)
