package com.shaho.tradingview.ui.first

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.shaho.tradingview.util.Resource

@Composable
fun FirstFragmentComposable() {
    val viewModel = hiltViewModel<FirstViewModel>()
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Magenta),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "HOME",
            fontSize = MaterialTheme.typography.h3.fontSize,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }

    viewModel.getAccounts().observeForever {
        when (it) {
            is Resource.Failure -> Toast.makeText(context, "Failure: ${it.message}", Toast.LENGTH_SHORT).show()
            Resource.Loading -> Toast.makeText(context, "Loading", Toast.LENGTH_SHORT).show()
            is Resource.Success -> Toast.makeText(context, "Success: ${it.data}", Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
@Preview
fun HomeScreenPreview() {
    FirstFragmentComposable()
}
