package com.example.pdfgen.ui.screens

// In com/yourcompany/yourproject/ui/screens/ReceiptInputDialog.kt

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pdfgen.data.ReceiptData
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptInputDialog(
    onDismiss: () -> Unit,
    onGenerate: (ReceiptData) -> Unit
) {
    // State for each text field in the dialog
    var studentName by remember { mutableStateOf("") }
    var registrationNo by remember { mutableStateOf("") }
    var feeAmount by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Enter Receipt Details") },
        text = {
            Column {
                // Text field for Student Name
                OutlinedTextField(
                    value = studentName,
                    onValueChange = { studentName = it },
                    label = { Text("Student Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Text field for Registration Number
                OutlinedTextField(
                    value = registrationNo,
                    onValueChange = { registrationNo = it },
                    label = { Text("Registration No.") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Text field for Fee Amount
                OutlinedTextField(
                    value = feeAmount,
                    onValueChange = { feeAmount = it },
                    label = { Text("Fee Amount") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            // Button to confirm and generate the receipt
            Button(onClick = {
                // Get current date
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val currentDate = sdf.format(Date())

                // Create a ReceiptData object from the input
                val data = ReceiptData(
                    studentName = studentName,
                    registrationNo = registrationNo,
                    feeAmount = feeAmount,
                    date = currentDate
                )
                // Pass the data to the generation lambda
                onGenerate(data)
            }) {
                Text("Generate")
            }
        },
        dismissButton = {
            // Button to cancel and close the dialog
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}