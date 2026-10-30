package com.lzy.myapplication.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.lzy.myapplication.ui.theme.Coral
import com.lzy.myapplication.ui.theme.TextSecondary

@Composable
fun ConfirmDeleteDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("確認刪除？")
        },
        text = {
            Text("確定要刪除這筆記錄嗎？")
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Coral
                )
            ) {
                Text("確認刪除")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = TextSecondary
                )
            ) {
                Text("取消")
            }
        }
    )
}
