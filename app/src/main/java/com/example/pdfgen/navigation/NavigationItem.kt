package com.example.pdfgen.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationItem(
    val route: String,
    val title: String,
    val icon: ImageVector
){
    object Home : NavigationItem("home", "Home", Icons.Default.Home)
    object PdfGen : NavigationItem("pdfgen", "PdfGen", Icons.Default.PictureAsPdf)
    object Profile : NavigationItem("profile", "Profile", Icons.Default.Person)
}