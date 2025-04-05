package com.basilalasadi.iti.kotlin.weatherwatcher.ui.savedlocations.view.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.basilalasadi.iti.kotlin.weatherwatcher.R
import com.basilalasadi.iti.kotlin.weatherwatcher.data.city.model.City
import com.basilalasadi.iti.kotlin.weatherwatcher.data.common.model.Result
import com.basilalasadi.iti.kotlin.weatherwatcher.ui.savedlocations.view.CityCardData
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce

@OptIn(FlowPreview::class)
@Composable
fun SearchDialog(
    modifier: Modifier = Modifier.Companion,
    onDismissRequest: (afterSelectFromMap: Boolean) -> Unit,
    onSelectFromMap: (() -> Unit)? = null,
    searchCities: (query: String) -> Flow<Result<List<CityCardData>>>,
    onSelect: (City) -> Unit
) {
    val queryFlow = remember { MutableStateFlow("") }
    val query = queryFlow.collectAsStateWithLifecycle()
    val resultsFlow = MutableStateFlow<Result<List<CityCardData>>>(Result.Initial())
    val results = resultsFlow.collectAsStateWithLifecycle()
    
    LaunchedEffect(queryFlow) {
        queryFlow
            .debounce(500)
            .collect { q ->
                searchCities(q).collect {
                    resultsFlow.emit(it)
                }
            }
    }
    
    Dialog(
        onDismissRequest = { onDismissRequest(false) },
    ) {
        Card(
            shape = MaterialTheme.shapes.large,
            modifier = Modifier.Companion
                .fillMaxWidth()
                .imePadding()
                .padding(vertical = 16.dp)
        ) {
            TextField(
                value = query.value,
                onValueChange = {
                    queryFlow.value = it
                },
                label = { Text(stringResource(R.string.search)) },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.Companion
                    .fillMaxWidth()
            )
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier.Companion
                    .fillMaxWidth()
            ) {
                if (onSelectFromMap != null) {
                    item {
                        TextButton(
                            onClick = {
                                onSelectFromMap()
                                onDismissRequest(true)
                            },
                            modifier = Modifier.Companion
                                .align(Alignment.Companion.CenterHorizontally)
                        ) {
                            Text(stringResource(R.string.choose_from_map))
                        }
                    }
                }
                
                results.value.let {
                    if (it is Result.Loading) {
                        item {
                            CircularProgressIndicator(
                                modifier = Modifier.Companion
                                    .padding(vertical = 8.dp)
                                    .align(Alignment.Companion.CenterHorizontally)
                            )
                        }
                    } else if (it is Result.Failure) {
                        item {
                            Card(
                                colors = CardDefaults.elevatedCardColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer,
                                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                                )
                            ) {
                                Text(it.error.message!!)
                            }
                        }
                    }
                    
                    it.value?.let {
                        itemsIndexed(items = it) { _, item ->
                            CityCard(
                                data = item,
                                onClick = {
                                    onSelect(it)
                                    onDismissRequest(false)
                                },
                                modifier = Modifier.Companion
                            )
                        }
                    }
                }
            }
        }
    }
}