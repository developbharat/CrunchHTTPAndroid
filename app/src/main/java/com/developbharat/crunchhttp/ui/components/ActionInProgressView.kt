package com.developbharat.crunchhttp.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.developbharat.crunchhttp.common.ResourceStatus
import com.developbharat.crunchhttp.ui.theme.CrunchHTTPTheme


@Composable
fun ActionInProgressView(
    status: ResourceStatus = ResourceStatus(
        isInProgress = true,
        statusText = "Loading..."
    )
) {
    return Text(text = "Status: ${status.statusText}")
}

@Preview(showBackground = true)
@Composable
fun ActionInProgressViewPreview() {
    CrunchHTTPTheme {
        ActionInProgressView()
    }
}