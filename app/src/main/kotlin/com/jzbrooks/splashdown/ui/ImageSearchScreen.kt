package com.jzbrooks.splashdown.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.jzbrooks.splashdown.ui.theme.SplashdownTheme

@Composable
fun ImageSearchScreen(it: PaddingValues) {
    Column(modifier = Modifier.padding(it)) {
        TextField(value = "Test", onValueChange = {})
        LazyVerticalGrid(columns = GridCells.Fixed(3)) {

        }
    }
}

@Preview(showBackground = true)
@Composable
fun ImageSearchScreenPreview() {
    SplashdownTheme {
        ImageSearchScreen(PaddingValues.Absolute())
    }
}
