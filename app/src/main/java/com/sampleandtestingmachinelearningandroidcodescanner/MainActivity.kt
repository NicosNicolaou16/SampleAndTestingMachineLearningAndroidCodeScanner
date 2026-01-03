package com.sampleandtestingmachinelearningandroidcodescanner

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.sampleandtestingmachinelearningandroidcodescanner.ui.theme.SampleAndTestingMachineLearningAndroidCodeScannerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SampleAndTestingMachineLearningAndroidCodeScannerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ScanButton()
                }
            }
        }
    }
}

@Composable
fun ScanButton(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val openDialog = remember { mutableStateOf(false) }
    val displayValue = remember { mutableStateOf("") }
    if (openDialog.value) AlertDialog(
        displayValue.value, openDialog,
        confirmButton = {
            openALink(context = context, displayLinkValue = displayValue.value)
        })
    Box(contentAlignment = Alignment.Center) {
        ElevatedButton(
            content = {
                Text(
                    text = stringResource(id = R.string.scanACode),
                    style = TextStyle(fontSize = 21.sp)
                )
            },
            modifier = modifier.size(height = 70.dp, width = 250.dp),
            onClick = {
                bacCodeProcess(context = context, success = {
                    displayValue.value = it
                    openDialog.value = true
                }, canceled = {
                    Toast.makeText(context, R.string.cancel, Toast.LENGTH_LONG).show()
                }, failed = {
                    Toast.makeText(context, it.message.toString(), Toast.LENGTH_LONG).show()
                })
            }
        )
    }
}

@Composable
private fun AlertDialog(
    displayValue: String,
    openDialog: MutableState<Boolean>,
    confirmButton: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = {
            openDialog.value = false
        },
        title = {
            Text(text = stringResource(id = R.string.openTheLink))
        },
        text = {
            Text(
                displayValue
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    confirmButton()
                    openDialog.value = false
                }
            ) {
                Text(stringResource(id = R.string.open))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    openDialog.value = false
                }
            ) {
                Text(stringResource(id = R.string.cancel))
            }
        }
    )
}

private fun bacCodeProcess(
    context: Context,
    success: (String) -> Unit,
    canceled: () -> Unit,
    failed: (Exception) -> Unit
) {
    GmsBarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_QR_CODE,
            Barcode.FORMAT_CODE_128,
        )
        .build().apply {
            GmsBarcodeScanning.getClient(context, this).apply {
                startScan()
                    .addOnSuccessListener { barcode ->
                        val displayValue: String? = barcode.displayValue
                        if (displayValue != null) {
                            success(displayValue)
                        }
                        //Log.d("callBack", displayValue.toString())
                    }
                    .addOnCanceledListener {
                        canceled()
                        //Log.d("callBack", "Canceled")
                    }
                    .addOnFailureListener { e ->
                        failed(e)
                        //Log.d("callBack", e.message.toString())
                    }
            }
        }
}

private fun openALink(context: Context, displayLinkValue: String) {
    try {
        Intent(Intent.ACTION_VIEW, Uri.parse(displayLinkValue)).apply {
            context.startActivity(this)
        }
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, e.message.toString(), Toast.LENGTH_LONG).show()
        e.printStackTrace()
    }
}