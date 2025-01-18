package com.example.mobile_musicapp.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.mobile_musicapp.databinding.FragmentPremiumBinding
import com.example.mobile_musicapp.services.UserDao
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPremiumBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnPay.setOnClickListener {
            var radioWeeklyValue = binding.radioWeekly.isChecked
            var radioMonthlyValue = binding.radioMonthly.isChecked
            var radioYearlyValue = binding.radioYearly.isChecked
            val orderApi = CreateOrder()
            var amount = 0
            var day = 0
            if (radioWeeklyValue) {
                amount = 20000
                day = 7
            } else if (radioMonthlyValue) {
                amount = 60000
                day = 30
            } else if (radioYearlyValue) {
                amount = 600000
                day = 365
            }
            lifecycleScope.launch {
                try {
                    val data: JSONObject = orderApi.createOrder(amount.toString())
                    val code = data.getString("returncode")
                    if (code == "1") {
                        val token = data.getString("zptranstoken")

                        if (token.isBlank()) {
                            Toast.makeText(requireActivity(), "Please generate an order token first.", Toast.LENGTH_SHORT).show()
                        }

                        ZaloPaySDK.getInstance()
                            .payOrder(requireActivity(), token, "demozpdk://app", object : PayOrderListener {
                                override fun onPaymentSucceeded(
                                    transactionId: String,
                                    transToken: String,
                                    appTransID: String
                                ) {
                                    lifecycleScope.launch {
                                        var response = UserDao.upgradeToPremium(day)
                                    }

                                    requireActivity().runOnUiThread {
                                        AlertDialog.Builder(requireActivity())
                                            .setTitle("Payment Success")
                                            .setMessage(
                                                String.format("You have %s days of premium account", day)
                                            )
                                            .setPositiveButton("OK") { _, _ ->
                                                requireActivity().onBackPressed()
                                            }
                                            .show()

                                    }
                                }

                                override fun onPaymentCanceled(zpTransToken: String, appTransID: String) {
                                    requireActivity().runOnUiThread {
                                        AlertDialog.Builder(requireActivity())
                                            .setTitle("User Cancel Payment")
                                            .setMessage(String.format("zpTransToken: %s", zpTransToken))
                                            .setPositiveButton("OK", null)
                                            .show()
                                    }
                                }

                                override fun onPaymentError(
                                    zaloPayError: ZaloPayError,
                                    zpTransToken: String,
                                    appTransID: String
                                ) {
                                    requireActivity().runOnUiThread {
                                        AlertDialog.Builder(requireActivity())
                                            .setTitle("Payment Fail")
                                            .setMessage(
                                                String.format(
                                                    "Error: %s\nzpTransToken: %s",
                                                    zaloPayError.toString(),
                                                    zpTransToken
                                                )
                                            )
                                            .setPositiveButton("OK", null)
                                            .show()
                                    }
                                }
                            })
                    } else {
                        Toast.makeText(requireContext(), "Create order failed: $code", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(requireActivity(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
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