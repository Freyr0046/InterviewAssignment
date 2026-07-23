package com.example.interview.ui.stocklist.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.interview.R
import com.example.interview.ui.model.SortOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortBottomSheet(
    selectedOption: SortOption,
    onOptionSelected: (SortOption) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalBottomSheet(onDismissRequest = onDismiss, modifier = modifier) {
        SortOption.entries.forEach { option ->
            SortOptionRow(
                label = stringResource(option.labelRes()),
                selected = option == selectedOption,
                onClick = { onOptionSelected(option) },
            )
        }
    }
}

@Composable
private fun SortOptionRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .selectable(selected = selected, onClick = onClick)
                .padding(horizontal = 24.dp, vertical = 12.dp),
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Text(text = label, modifier = Modifier.padding(start = 16.dp))
    }
}

private fun SortOption.labelRes(): Int =
    when (this) {
        SortOption.CODE_DESC -> R.string.sort_option_code_desc
        SortOption.CODE_ASC -> R.string.sort_option_code_asc
    }
