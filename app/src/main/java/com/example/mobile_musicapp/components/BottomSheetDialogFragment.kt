package com.example.mobile_musicapp.components

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobile_musicapp.R
import com.example.mobile_musicapp.adapters.CommentsAdapter
import com.example.mobile_musicapp.models.CommentModel
import com.example.mobile_musicapp.models.Song
import com.example.mobile_musicapp.services.UserManager
import com.example.mobile_musicapp.singletons.Queue
import com.example.mobile_musicapp.viewModels.CommentViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class CommentsBottomSheet : BottomSheetDialogFragment() {
    val TAG = "CommentsBottomSheet"
    private lateinit var commentsAdapter: CommentsAdapter
    private lateinit var commentViewModel: CommentViewModel
    private val commentsList = mutableListOf<CommentModel>()
    private lateinit var viewTreeObserver: OnGlobalLayoutListener
    private lateinit var layoutMain: LinearLayout
    private lateinit var rlMessage: RelativeLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_comments, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rvComments: RecyclerView = view.findViewById(R.id.rvComments)
        val ivSend: ImageView = view.findViewById(R.id.btn_send)
        val edtContent: EditText = view.findViewById(R.id.edt_message)
        val tvError :TextView = view.findViewById(R.id.tvError)
        val pbLoading :ProgressBar = view.findViewById(R.id.pb_loading)
      //  layoutMain = view.findViewById(R.id.main)
        rlMessage = view.findViewById(R.id.ll_message)
        commentViewModel = ViewModelProvider(requireActivity())[CommentViewModel::class.java]
        commentsAdapter = CommentsAdapter()

        commentViewModel.getCommentLiveData().observe(requireActivity()){
            pbLoading.visibility = View.GONE
            if (it != null){
                Log.d(TAG, "commentViewModel: ====> $it")
                if (it.isEmpty()) {
                    tvError.visibility = View.VISIBLE
                    rvComments.visibility = View.GONE
                }else {
                    tvError.visibility = View.GONE
                    rvComments.visibility = View.VISIBLE
                }
                commentsList.addAll(it.sortedBy {data -> data.createdAt })
                commentsAdapter.submitList(it.sortedBy { data -> data.createdAt })
            }

        }
        Queue.getCurrentSong()?.let {
            commentViewModel.getAllCommentsById(it._id)
        }
        commentViewModel.getCommentAddLiveData().observe(requireActivity()){
            if (it.first == 201) {
                tvError.visibility = View.GONE
                rvComments.visibility = View.VISIBLE
                commentsList.add(it.second)
                edtContent.setText("")
                hideKeyboard(requireActivity())
                commentsAdapter.submitList(commentsList.sortedBy { data -> data.createdAt })
                rvComments.scrollToPosition(commentsList.size - 1)
            }else {
                if (it.first != 0 ) {
                    Toast.makeText(requireContext(), "Lỗi", Toast.LENGTH_SHORT).show()
                    if (commentsList.isEmpty()){
                        tvError.visibility = View.VISIBLE
                        rvComments.visibility = View.GONE
                    }else {
                        tvError.visibility = View.GONE
                        rvComments.visibility = View.VISIBLE
                    }
                }
            }
        }

        rvComments.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvComments.adapter = commentsAdapter

        ivSend.setOnClickListener {
            if (edtContent.text.toString().trim().isNotEmpty()){
                Queue.getCurrentSong()?.let {
                    commentViewModel.addComment(it._id, UserManager._id, UserManager.fullName, edtContent.text.toString().trim())
                }
            }
        }
    }
    override fun onStop() {
      //  layoutMain.viewTreeObserver.removeOnGlobalLayoutListener(viewTreeObserver)
        super.onStop()
    }
    fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
    override fun onResume() {
        super.onResume()
       // layoutMain.viewTreeObserver.addOnGlobalLayoutListener(viewTreeObserver)
    }

}
