package com.kanzankazu.kanzanwidget.compose.widget

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.kanzankazu.R
import com.kanzankazu.kanzanwidget.compose.extension.initMod
import com.kanzankazu.kanzanwidget.compose.ui.PrimaryDarkItungItungan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KanzanStandardToolbar(
    title: String,
    modifier: Modifier = Modifier,
    contentDescription: String = "Kembali",
    canNavigateBack: Boolean = true,
    containerColor: Color = PrimaryDarkItungItungan,
    navigateUp: () -> Unit,
) {
    TopAppBar(
        title = { Text(title, initMod().fillMaxWidth(), color = Color.Black) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = containerColor
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_head_left),
                        contentDescription = contentDescription,
                        tint = Color.Black
                    )
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun KanzanStandardToolbarPreview() {
    KanzanStandardToolbar("StandardToolbar") {}
}

