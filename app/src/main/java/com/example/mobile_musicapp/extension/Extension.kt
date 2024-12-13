package com.example.mobile_musicapp.extension

import androidx.appcompat.widget.SearchView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

fun SearchView.queryTextChanges(): Flow<String> = callbackFlow {
    val queryListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            trySend(newText.orEmpty())
            return true
        }
    }
    setOnQueryTextListener(queryListener)
    awaitClose { setOnQueryTextListener(null) }
}.buffer(Channel.CONFLATED)
