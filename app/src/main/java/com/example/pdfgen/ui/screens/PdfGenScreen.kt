package com.example.pdfgen.ui.screens

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

// In com/yourcompany/yourproject/ui/screens/PdfGenScreen.kt

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pdfgen.PdfGenerator
import com.example.pdfgen.R // IMPORTANT: Import your R class
import com.example.pdfgen.data.ReceiptData
import kotlinx.coroutines.launch

// Function to view a PDF using an Intent
private fun viewPdf(context: Context, pdfUri: Uri) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(pdfUri, "application/pdf")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    try {
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "No PDF viewer app found.", Toast.LENGTH_SHORT).show()
    }
}

// Function to share a PDF using an Intent
private fun sharePdf(context: Context, pdfUri: Uri) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, pdfUri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Share PDF"))
}

@Composable
fun PdfGenScreen() {
    var receiptData by remember { mutableStateOf(ReceiptData()) }
    var showDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var generatedPdfUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current
    // Coroutine scope for running the PDF generation off the main thread
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ReceiptTemplate(data = receiptData)
        Spacer(modifier = Modifier.height(32.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Button(onClick = {
                // When generating a new receipt, clear the old URI
                generatedPdfUri = null
                showDialog = true
            }) {
                Text("Generate New Receipt")
            }
        }

        // Show View and Share buttons only if a PDF has been generated
        generatedPdfUri?.let { uri ->
            Row(
                modifier = Modifier.padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(onClick = { viewPdf(context, uri) }) {
                    Text("View PDF")
                }
                Button(onClick = { sharePdf(context, uri) }) {
                    Text("Share PDF")
                }
            }
        }
    }

    if (showDialog) {
        ReceiptInputDialog(
            onDismiss = { showDialog = false },
            onGenerate = { data ->
                receiptData = data
                showDialog = false
                // Launch a coroutine to generate the PDF
                scope.launch {
                    isLoading = true // Show progress indicator
                    // The actual PDF generation happens here
                    val uri = PdfGenerator.generatePdf(context, data)
                    generatedPdfUri = uri // Store the URI of the generated file
                    isLoading = false // Hide progress indicator
                }
            }
        )
    }
}
@Composable
fun ReceiptTemplate(data: ReceiptData) {
    // A Surface to visually group the receipt elements with an elevation shadow.
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            // Header with College Logo and Name
            Row(verticalAlignment = Alignment.CenterVertically) {
                // You need to add a logo image to your `res/drawable` folder.
                // For now, we use a placeholder.
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "College Logo",
                    modifier = Modifier.size(60.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "FutureLearn Tech College",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))

            // Title of the receipt
            Text(
                text = "FEE RECEIPT",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Receipt details using a reusable Row composable
            ReceiptInfoRow("Date:", data.date)
            ReceiptInfoRow("Student Name:", data.studentName)
            ReceiptInfoRow("Registration No:", data.registrationNo)

            Spacer(modifier = Modifier.height(24.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))

            // Fee Amount
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total Fee Paid", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("₹${data.feeAmount}", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF008000))
            }
        }
    }
}

@Composable
fun ReceiptInfoRow(label: String, value: String) {
    // This composable helps in creating consistent rows for label-value pairs.
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = label, modifier = Modifier.width(140.dp), color = Color.Gray)
        Text(text = value, fontWeight = FontWeight.SemiBold)
    }
}