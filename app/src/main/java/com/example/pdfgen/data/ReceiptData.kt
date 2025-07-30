package com.example.pdfgen.data

data class ReceiptData(
    val studentName: String = "Student Name",
    val registrationNo: String = "REG-XXXX-XXXX",
    val feeAmount: String = "0.00",
    val date: String = "DD/MM/YYYY"
)