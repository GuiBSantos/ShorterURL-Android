package com.example.shortenerapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.shortenerapp.R

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun LogoTopBar(
    isDarkTheme: Boolean,
    onBackClick: (() -> Unit)? = null
) {
    val logoRes = if (isDarkTheme) R.drawable.ic_logo_app_dark else R.drawable.ic_logo_app_light

    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        title = {
            Image(
                painter = painterResource(id = logoRes),
                contentDescription = "Logo APP",
                modifier = Modifier
                    .height(40.dp)
                    .padding(vertical = 4.dp)
            )
        },
        navigationIcon = {

            if (onBackClick != null) {
                IconButton(onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Voltar"
                    )
                }
            }
        }
    )
}