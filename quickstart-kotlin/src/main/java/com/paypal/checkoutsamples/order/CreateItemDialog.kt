package com.paypal.checkoutsamples.order

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputLayout
import com.paypal.checkout.createorder.ItemCategory
import com.paypal.checkoutsamples.R
import kotlinx.android.synthetic.main.dialog_create_item.view.*

/**
 * CreateItemDialog provides an entry point for the user to create a new item and for the host view
 * to be notified when the item is created along with it's contents.
 *
 * @see [CreatedItem]
 */
class CreateItemDialog : DialogFragment() {

    var onItemCreated: ((createdItem: CreatedItem) -> Unit)? = null

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { nonNullActivity ->
            val dialogBuilder = AlertDialog.Builder(nonNullActivity)
            val layoutInflater = nonNullActivity.layoutInflater
            val dialogView = layoutInflater.inflate(R.layout.dialog_create_item, null)

            with(dialogView) {
                selectItemCategory.setOnCheckedChangeListener { _, _ -> errorTextView.text = "" }

                createItemButton.setOnClickListener {
                    if (!canSaveItem(dialogView)) return@setOnClickListener

                    onItemCreated?.invoke(
                        CreatedItem(
                            name = itemNameInput.text,
                            quantity = itemQuantityInput.text,
                            amount = itemAmountInput.text,
                            taxAmount = itemTaxInput.text,
                            itemCategory = selectedItemCategory(selectItemCategory.checkedRadioButtonId)
                        )
                    )

                    dismiss()
                }
            }

            dialogBuilder.setView(dialogView).create()
        } ?: throw IllegalStateException("Activity cannot be null.")
    }

    private fun canSaveItem(view: View): Boolean = with(view) {
        itemNameInput.validateField()
        itemQuantityInput.validateField()
        itemAmountInput.validateField()
        itemTaxInput.validateField()

        if (!itemCategoryPhysicalGoods.isChecked && !itemCategoryDigitalGoods.isChecked) {
            errorTextView.text = getString(R.string.dialog_create_error_item_category)
        }

        return itemNameInput.text.isNotEmpty() && itemQuantityInput.text.isNotEmpty()
                && itemAmountInput.text.isNotEmpty() && itemTaxInput.text.isNotEmpty()
                && (itemCategoryPhysicalGoods.isChecked || itemCategoryDigitalGoods.isChecked)
    }

    private fun TextInputLayout.validateField() {
        if (text.isEmpty()) error = getString(R.string.dialog_create_error_required)
    }

    private fun selectedItemCategory(selectedId: Int): ItemCategory {
        return when (selectedId) {
            R.id.itemCategoryPhysicalGoods -> ItemCategory.PHYSICAL_GOODS
            R.id.itemCategoryDigitalGoods -> ItemCategory.DIGITAL_GOODS
            else -> {
                throw IllegalArgumentException(
                    "Expected one of the following ids: ${R.id.itemCategoryPhysicalGoods}, or " +
                            "${R.id.itemCategoryDigitalGoods} but was $selectedId"
                )
            }
        }
    }

    private val TextInputLayout.text: String
        get() {
            return editText?.run { text.toString() } ?: ""
        }

}

/**
 * CreatedItem is a simple data class which is used for sending [CreatedItem] details from one input
 * screen to another.
 */
data class CreatedItem(
    val name: String,
    val quantity: String,
    val amount: String,
    val taxAmount: String,
    val itemCategory: ItemCategory
)
