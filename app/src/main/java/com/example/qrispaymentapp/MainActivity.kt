package com.example.qrispaymentapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.midtrans.sdk.corekit.callback.TransactionFinishedCallback
import com.midtrans.sdk.corekit.core.MidtransSDK
import com.midtrans.sdk.corekit.core.TransactionRequest
import com.midtrans.sdk.corekit.core.themes.CustomColorTheme
import com.midtrans.sdk.corekit.models.BillingAddress
import com.midtrans.sdk.corekit.models.CustomerDetails
import com.midtrans.sdk.corekit.models.ItemDetails
import com.midtrans.sdk.corekit.models.ShippingAddress
import com.midtrans.sdk.corekit.models.snap.TransactionResult
import com.midtrans.sdk.uikit.SdkUIFlowBuilder


// from
//https://sutisnaasep323.medium.com/tutorial-midtrans-menggunakan-kotlin-e9119079a7a9

class MainActivity : ComponentActivity(), TransactionFinishedCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            // A surface container using the 'background' color from the theme
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                Arrangement.Center
            ) {
                Text(text = "Payment Gateway", fontSize = 25.sp)
                Text(text = "MidTrans Sand Box", fontSize = 25.sp)
                Text(text = "Check Out Product", fontSize = 18.sp)

                Image(
                    painter = painterResource(id = R.drawable.productimage),
                    contentDescription = "Product ",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp),
                    contentScale = ContentScale.Crop
                )
                Text(text = "Firemouth Cichlid", fontSize = 16.sp)
                Text(text = "Quantity: 1", fontSize = 14.sp)
                Text(text = "Price: 45.000", fontSize = 14.sp)
                Button(onClick = { goToPayment() }) {
                    Text(text = "Pilih Pembayaran")
                }
            }
        }

        initMidtransSDK()
    }

    private fun initMidtransSDK() {
        val sdkUIFlowBuilder: SdkUIFlowBuilder = SdkUIFlowBuilder.init()
            // how to get??
            .setClientKey("CLIENT_KEY") // client_key
            .setContext(this)
            .setTransactionFinishedCallback(this)
            .setMerchantBaseUrl("URL_SERVER") //URL Server
            .enableLog(true)
            .setColorTheme(
                CustomColorTheme(
                    "#002855",
                    "#FF6200EE",
                    "#FF018786"
                )
            )
            .setLanguage("id")
        sdkUIFlowBuilder.buildSDK()
    }

    private fun goToPayment() {
        val qty = 1
        val harga = 45000.0
        val amount = qty * harga
        val transactionRequest =
            TransactionRequest("ORDER_ID" + System.currentTimeMillis().toShort(), amount)
        val detail = ItemDetails("ID", harga, qty, "NAME")// If need detail transaction

        val itemDetails = ArrayList<ItemDetails>()
        itemDetails.add(detail)

        uiKitDetails(transactionRequest)
        transactionRequest.itemDetails = itemDetails

        MidtransSDK.getInstance().transactionRequest = transactionRequest
        MidtransSDK.getInstance().startPaymentUiFlow(this)
    }

    private fun uiKitDetails(transactionRequest: TransactionRequest) {
        val customerDetails = CustomerDetails()
        customerDetails.customerIdentifier = ""
        customerDetails.phone = ""
        customerDetails.firstName = ""
        customerDetails.lastName = ""
        customerDetails.email = ""

        val shippingAddress = ShippingAddress()
        shippingAddress.address = ""
        shippingAddress.city = ""
        shippingAddress.postalCode = ""
        customerDetails.shippingAddress = shippingAddress

        val billingAddress = BillingAddress()
        billingAddress.address = ""
        billingAddress.city = ""
        billingAddress.postalCode = ""
        customerDetails.billingAddress = billingAddress

        transactionRequest.customerDetails = customerDetails
    }

    override fun onTransactionFinished(result: TransactionResult) {
        if (result.response != null) {
            when (result.status) {
                TransactionResult.STATUS_SUCCESS -> Toast.makeText(
                    this,
                    "Transaction Finished. ID: " + result.response.transactionId,
                    Toast.LENGTH_LONG
                ).show()

                TransactionResult.STATUS_PENDING -> Toast.makeText(
                    this,
                    "Transaction Pending. ID: " + result.response.transactionId,
                    Toast.LENGTH_LONG
                ).show()

                TransactionResult.STATUS_FAILED -> Toast.makeText(
                    this,
                    "Transaction Failed. ID: " + result.response.transactionId.toString() + ". Message: " + result.response.statusMessage,
                    Toast.LENGTH_LONG
                ).show()
            }
        } else if (result.isTransactionCanceled) {
            Toast.makeText(this, "Transaction Canceled", Toast.LENGTH_LONG).show()
        } else {
            if (result.status.equals(TransactionResult.STATUS_INVALID, true)) {
                Toast.makeText(this, "Transaction Invalid", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Transaction Finished with failure.", Toast.LENGTH_LONG).show()
            }
        }
    }
}