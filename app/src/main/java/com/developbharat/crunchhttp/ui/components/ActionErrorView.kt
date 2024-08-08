package com.developbharat.crunchhttp.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.developbharat.crunchhttp.common.ResourceStatus
import com.developbharat.crunchhttp.ui.theme.CrunchHTTPTheme


@Composable
fun ActionErrorView(
    status: ResourceStatus = ResourceStatus(
        isError = true,
        statusText = "Unknown error occurred."
    )
) {
    return Text(text = "Status: ${status.statusText}")
}

@Preview(showBackground = true)
@Composable
fun ActionErrorViewPreview() {
    CrunchHTTPTheme {
        ActionErrorView()
    }
}