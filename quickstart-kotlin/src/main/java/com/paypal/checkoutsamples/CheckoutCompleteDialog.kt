package com.paypal.checkoutsamples

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_checkout_complete.view.*

class CheckoutCompleteDialog : DialogFragment() {

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { nonNullActivity ->
            val dialogBuilder = AlertDialog.Builder(nonNullActivity)
            val layoutInflater = nonNullActivity.layoutInflater
            val dialogView = layoutInflater.inflate(R.layout.dialog_checkout_complete, null)
            with(dialogView) {
                checkoutCompleteTitle.text = arguments.resultsTitle
                completionResultsText.text = arguments.resultsText
            }
            dialogBuilder.setView(dialogView).create()
        } ?: throw IllegalStateException("Activity cannot be null.")
    }

    private val Bundle?.resultsText: String
        get() = this?.getString(ARG_RESULTS_TEXT) ?: ""

    private val Bundle?.resultsTitle: String
        get() = this?.getString(ARG_RESULTS_TITLE) ?: ""

    companion object {
        private const val ARG_RESULTS_TITLE = "arg_results_title"
        private const val ARG_RESULTS_TEXT = "arg_results_text"

        fun create(resultsTitle: String, resultsText: String): CheckoutCompleteDialog {
            val bundle = Bundle()
                .apply { putString(ARG_RESULTS_TEXT, resultsText) }
                .apply { putString(ARG_RESULTS_TITLE, resultsTitle) }
            return CheckoutCompleteDialog().apply { arguments = bundle }
        }
    }
}
