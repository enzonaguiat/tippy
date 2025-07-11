package com.example.android.tippy

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import android.widget.ToggleButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.vectordrawable.graphics.drawable.ArgbEvaluator
import android.view.View

private const val TAG = "MainActivity"
private const val INITIAL_TIP_PERCENT = 15
class MainActivity : AppCompatActivity() {
    private lateinit var etBaseAmount: EditText
    private lateinit var seekBarTip: SeekBar
    private lateinit var tvTipPercent: TextView
    private lateinit var tvTipAmount: TextView
    private lateinit var tvTotalAmount: TextView
    private lateinit var tvTipPercentDescription: TextView
    private lateinit var toggleButtonSplitBill: ToggleButton
    private lateinit var etSplitBillNumber: EditText
    private lateinit var tvBillPerPersonLabel: TextView
    private lateinit var tvTotalPerPerson: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        etBaseAmount = findViewById(R.id.etBaseAmount)
        seekBarTip = findViewById(R.id.seekBarTip)
        tvTipPercent = findViewById(R.id.tvTipPercentLabel)
        tvTipAmount = findViewById(R.id.tvTipAmount)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        tvTipPercentDescription = findViewById(R.id.tvTipPercentDescription)
        toggleButtonSplitBill = findViewById((R.id.toggleButtonSplitBill))
        etSplitBillNumber = findViewById(R.id.etSplitBillNumber)
        tvBillPerPersonLabel = findViewById((R.id.tvBillPerPersonLabel))
        tvTotalPerPerson = findViewById(R.id.tvTotalPerPerson)

        seekBarTip.progress = INITIAL_TIP_PERCENT
        tvTipPercent.text = "$INITIAL_TIP_PERCENT%"
        updateTipDescription(INITIAL_TIP_PERCENT)
        seekBarTip.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                Log.i(TAG, "onProgressChanged: $p1")
                tvTipPercent.text = "$p1%"
                computeTipAndTotal()
                updateTipDescription(p1)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}

        })
        etBaseAmount.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                Log.i(TAG, "after text change: $p0")
                computeTipAndTotal()
            }
        })
        etSplitBillNumber.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                Log.i(TAG, "Changed split bill number $p0")
                computeSplitBill()
            }
        })
        toggleButtonSplitBill.setOnCheckedChangeListener {_, isChecked ->
            val visibility = if (isChecked) View.VISIBLE else View.GONE
            etSplitBillNumber.visibility = visibility
            tvBillPerPersonLabel.visibility = visibility
            tvTotalPerPerson.visibility = visibility
        }
    }

    private fun updateTipDescription(tipPercent: Int) {
        val tipDescription = when (tipPercent) {
            in 0..9 -> "Poor \u2639\uFE0F"
            in 10..14 -> "Acceptable \uD83D\uDE10"
            in 15..19 -> "Good \uD83D\uDE42"
            in 20..24 -> "Great \uD83D\uDE04"
            else -> "Amazing \uD83D\uDE0E"
        }
        tvTipPercentDescription.text = tipDescription
        val color = ArgbEvaluator().evaluate(
            tipPercent.toFloat()/seekBarTip.max,
            ContextCompat.getColor(this, R.color.worst_tip_color),
            ContextCompat.getColor(this, R.color.best_tip_color)
        ) as Int
        tvTipPercentDescription.setTextColor(color)
    }

    private fun computeTipAndTotal() {
        if (etBaseAmount.text.isEmpty()){
            tvTipAmount.text = ""
            tvTotalAmount.text = ""
            return
        }
        var baseAmount = etBaseAmount.text.toString().toDouble()
        var tipPercent = seekBarTip.progress
        var tipAmount = baseAmount * tipPercent / 100
        var totalAmount = baseAmount + tipAmount

        tvTipAmount.text = "%.2f".format(tipAmount)
        tvTotalAmount.text = "%.2f".format(totalAmount)
    }

    private fun computeSplitBill() {
        if (tvTotalAmount.text.isEmpty()) {
            Log.i(TAG, "no Total Amount")
            tvTotalPerPerson.text = ""
            return
        }
        if (etSplitBillNumber.text.isEmpty() || etSplitBillNumber.text.toString() == "0") {
            return
        }
        var totalAmount = tvTotalAmount.text.toString().toDouble()
        var totalAmountPerPerson = tvTotalAmount.text.toString().toDouble() / etSplitBillNumber.text.toString().toInt()
        tvTotalPerPerson.text = "%.2f".format(totalAmountPerPerson)
    }
}