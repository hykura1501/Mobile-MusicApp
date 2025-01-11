package com.example.mobile_musicapp.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.mobile_musicapp.databinding.FragmentPremiumBinding
import com.example.mobile_musicapp.services.payment.Api.CreateOrder
import kotlinx.coroutines.launch
import org.json.JSONObject
import vn.zalopay.sdk.Environment
import vn.zalopay.sdk.ZaloPayError
import vn.zalopay.sdk.ZaloPaySDK
import vn.zalopay.sdk.listeners.PayOrderListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PremiumFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PremiumFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentPremiumBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.createOrder.setOnClickListener {
            val orderApi = CreateOrder()
            try {
                lifecycleScope.launch {
                    val data: JSONObject = orderApi.createOrder("20000000")
                    val code = data.getString("returncode")

                    if (code == "1") {
                        binding.zlpToken.setText(data.getString("zptranstoken"))
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireActivity(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnPay.setOnClickListener(View.OnClickListener {
            val token: String = binding.zlpToken.getText().toString()
            ZaloPaySDK.getInstance()
                .payOrder(requireActivity(), token, "demozpdk://app", object : PayOrderListener {
                    override fun onPaymentSucceeded(
                        transactionId: String,
                        transToken: String,
                        appTransID: String
                    ) {
                        requireActivity().runOnUiThread(Runnable {
                            AlertDialog.Builder(requireActivity())
                                .setTitle("Payment Success")
                                .setMessage(
                                    String.format(
                                        "TransactionId: %s - TransToken: %s",
                                        transactionId,
                                        transToken
                                    )
                                )
                                .setPositiveButton(
                                    "OK"
                                ) { dialog, which -> }
                                .setNegativeButton("Cancel", null).show()
                        })
                    }

                    override fun onPaymentCanceled(zpTransToken: String, appTransID: String) {
                        AlertDialog.Builder(requireActivity())
                            .setTitle("User Cancel Payment")
                            .setMessage(String.format("zpTransToken: %s \n", zpTransToken))
                            .setPositiveButton(
                                "OK"
                            ) { dialog, which -> }
                            .setNegativeButton("Cancel", null).show()
                    }

                    override fun onPaymentError(
                        zaloPayError: ZaloPayError,
                        zpTransToken: String,
                        appTransID: String
                    ) {
                        AlertDialog.Builder(requireActivity())
                            .setTitle("Payment Fail")
                            .setMessage(
                                String.format(
                                    "ZaloPayErrorCode: %s \nTransToken: %s",
                                    zaloPayError.toString(),
                                    zpTransToken
                                )
                            )
                            .setPositiveButton(
                                "OK"
                            ) { dialog, which -> }
                            .setNegativeButton("Cancel", null).show()
                    }
                })
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPremiumBinding.inflate(inflater, container, false)
        // ZaloPay SDK Init
        ZaloPaySDK.init(553, Environment.SANDBOX)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PremiumFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PremiumFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}