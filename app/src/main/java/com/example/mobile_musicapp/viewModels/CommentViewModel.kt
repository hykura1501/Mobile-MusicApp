package com.example.mobile_musicapp.viewModels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobile_musicapp.models.CommentModel
import com.example.mobile_musicapp.models.CommentResponse
import com.example.mobile_musicapp.models.Playlist
import com.example.mobile_musicapp.models.UserInfo
import com.example.mobile_musicapp.repository.CommentRepository
import kotlinx.coroutines.launch

class CommentViewModel : ViewModel() {
    val TAG = "CommentViewModel"
    private val _commentMutableLiveData = MutableLiveData<List<CommentModel>>(null)
    private val commentLiveData : LiveData<List<CommentModel>> = _commentMutableLiveData

    private val _commentAddMutableLiveData = MutableLiveData(Pair(0, CommentModel(UserInfo("",""),"")))
    private val commentAddLiveData : LiveData<Pair<Int, CommentModel>> = _commentAddMutableLiveData


    private val commentRepository = CommentRepository()
    fun getCommentLiveData() = commentLiveData
    fun getCommentAddLiveData() = commentAddLiveData

    fun addComment(songId : String , data : String) {
        viewModelScope.launch {
           commentRepository.addComment(songId, data)?.let {
               Log.d(TAG, "addComment: ===> ${it.code}")
               _commentAddMutableLiveData.postValue(Pair(it.code,it.data))
           }?: run {
               Log.d(TAG, "addComment: NULL")
               _commentAddMutableLiveData.postValue(Pair(-1, CommentModel(UserInfo("",""),"")))
           }
        }
    }

    fun getAllCommentsById(songId: String){
        viewModelScope.launch {
            commentRepository.getAllCommentsById(songId)?.let {
                Log.d(TAG, "getAllCommentsById: ===> ${it.code}")
                _commentMutableLiveData.postValue(it.data)
            }?: run {
                Log.d(TAG, "addComment: NULL")
            }
        }
    }
}