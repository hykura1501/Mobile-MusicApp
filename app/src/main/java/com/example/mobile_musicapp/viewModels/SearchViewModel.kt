package com.example.mobile_musicapp.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import androidx.paging.map
import com.example.mobile_musicapp.models.Playlist
import com.example.mobile_musicapp.models.Song
import com.example.mobile_musicapp.repository.SongRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    val TAG = "SearchViewModel"
    private val _query = MutableStateFlow("") // Search query
    private val songRepository = SongRepository()

    private val pagedSongs: Flow<PagingData<Song>> = songRepository.getPagedItems()
        .cachedIn(viewModelScope)

    val pagedSongsSmaller: LiveData<PagingData<Song>> = songRepository.getPagedItemsSmaller()
        .cachedIn(viewModelScope).asLiveData()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val searchedSongs = _query
        .debounce(300)
        .flatMapLatest { query ->
            if (query.isEmpty()) {
                pagedSongs
            } else {
                pagedSongs.map { pagingData ->
                    pagingData.filter { song ->
                        song.title.contains(query, ignoreCase = true)
                    }
                }
            }
        }.asLiveData().cachedIn(viewModelScope)

    fun updateQuery(newQuery: String) {
        _query.value = newQuery
    }
}