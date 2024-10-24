package com.example.changecurrency

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import java.text.NumberFormat
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var firstCurrencyText: TextView
    lateinit var firstUnitCurrency: TextView
    lateinit var secondCurrencyText: TextView
    lateinit var secondUnitCurrency: TextView
    lateinit var exchangeRateText: TextView
    lateinit var firstCurrencySpinner: Spinner
    lateinit var secondCurrencySpinner: Spinner

    var inputValue: String = "0"
    var inputText: Int = 1      //Biến kiểm tra text nào đang được chỉnh

    val currencyType = arrayOf(
        "Vietnam - Dong",
        "Europe - Euro",
        "Japan - Yen",
        "United Kingdom - Pound",
        "United States - Dollar"
    )

    val currencyUnit = mapOf(
        "Vietnam - Dong" to "đ",
        "Europe - Euro" to "€",    // Chỉnh lại lỗi chính tả
        "Japan - Yen" to "¥",
        "United Kingdom - Pound" to "£",
        "United States - Dollar" to "$"
    )

    val exchangeRates = mapOf(
        "Vietnam - Dong" to 1.0,
        "Europe - Euro" to 0.00003644,
        "Japan - Yen" to 0.00598,
        "United Kingdom - Pound" to 0.00003035,
        "United States - Dollar" to 0.00003937
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()


        val adapter = ArrayAdapter(this, R.layout.spinner_item, currencyType)
        adapter.setDropDownViewResource(R.layout.spinner_item)
        firstCurrencySpinner.adapter = adapter
        secondCurrencySpinner.adapter = adapter

        firstCurrencySpinner.onItemSelectedListener = CurrencySelectedListener()
        secondCurrencySpinner.onItemSelectedListener = CurrencySelectedListener()

        firstCurrencySpinner.setSelection(0)  // Default to "Vietnam - Dong"
        secondCurrencySpinner.setSelection(1) // Default to "Europe - Euro"
        updateUnitCurrencyText()

        setupClickListeners()
    }

    inner class CurrencySelectedListener : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            updateUnitCurrencyText()
            updateDisplay()
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }

    private fun initializeViews() {
        firstCurrencyText = findViewById(R.id.firstCurrencyText)
        secondCurrencyText = findViewById(R.id.secondCurrencyText)
        firstUnitCurrency = findViewById(R.id.firstUnitCurrency)
        secondUnitCurrency = findViewById(R.id.secondUnitCurrency)
        firstCurrencySpinner = findViewById(R.id.firstCurrencySpinner)
        secondCurrencySpinner = findViewById(R.id.secondCurrencySpinner)
        exchangeRateText = findViewById(R.id.exchangeRateText)

        firstCurrencyText.typeface = Typeface.create("sans-serif", Typeface.BOLD)
        secondCurrencyText.typeface = Typeface.create("sans-serif", Typeface.NORMAL)
    }

    private fun setupClickListeners() {
        val buttons = listOf(
            R.id.num0_btn, R.id.num1_btn, R.id.num2_btn, R.id.num3_btn,
            R.id.num4_btn, R.id.num5_btn, R.id.num6_btn, R.id.num7_btn,
            R.id.num8_btn, R.id.num9_btn, R.id.CE_btn, R.id.point_btn
        )

        buttons.forEach { findViewById<View>(it).setOnClickListener(this) }

        findViewById<ImageView>(R.id.BS_btn).setOnClickListener(this)
        firstCurrencyText.setOnClickListener(this)
        secondCurrencyText.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.firstCurrencyText -> {
                firstCurrencyText.typeface = Typeface.create("sans-serif", Typeface.BOLD)
                secondCurrencyText.typeface = Typeface.create("sans-serif", Typeface.NORMAL)
                inputValue = "0"
                inputText = 1
            }
            R.id.secondCurrencyText -> {
                firstCurrencyText.typeface = Typeface.create("sans-serif", Typeface.NORMAL)
                secondCurrencyText.typeface = Typeface.create("sans-serif", Typeface.BOLD)
                inputValue = "0"
                inputText = 2
            }
            R.id.num0_btn -> addDigit(0)
            R.id.num1_btn -> addDigit(1)
            R.id.num2_btn -> addDigit(2)
            R.id.num3_btn -> addDigit(3)
            R.id.num4_btn -> addDigit(4)
            R.id.num5_btn -> addDigit(5)
            R.id.num6_btn -> addDigit(6)
            R.id.num7_btn -> addDigit(7)
            R.id.num8_btn -> addDigit(8)
            R.id.num9_btn -> addDigit(9)
            R.id.BS_btn -> removeLastDigit()
            R.id.CE_btn -> clearText()
            R.id.point_btn -> addPoint()
        }
    }

    private fun addDigit(num: Int) {
        if (inputValue == "0") inputValue = ""   // Đảm bảo khi nhấn số, xóa đi giá trị mặc định là "0"
        inputValue += num.toString()
        updateDisplay()
    }

    private fun removeLastDigit() {
        if (inputValue.isNotEmpty()) {
            inputValue = inputValue.dropLast(1)
            if (inputValue.isEmpty()) {
                inputValue = "0"
            }
            updateDisplay()
        }
    }

    private fun updateDisplay() {
        val numberFormat = NumberFormat.getInstance(Locale.US)  // Định dạng số với dấu phẩy theo chuẩn US

        if (inputText == 1) {
            firstCurrencyText.text = numberFormat.format(inputValue.toDoubleOrNull() ?: 0.0)
            val converted = convertCurrency(
                firstCurrencySpinner.selectedItem.toString(),
                secondCurrencySpinner.selectedItem.toString(),
                inputValue.toDoubleOrNull() ?: 0.0
            )
            secondCurrencyText.text = numberFormat.format(converted)
        } else {
            secondCurrencyText.text = numberFormat.format(inputValue.toDoubleOrNull() ?: 0.0)
            val converted = convertCurrency(
                secondCurrencySpinner.selectedItem.toString(),
                firstCurrencySpinner.selectedItem.toString(),
                inputValue.toDoubleOrNull() ?: 0.0
            )
            firstCurrencyText.text = numberFormat.format(converted)
        }
    }

    private fun updateUnitCurrencyText() {
        val firstCurrency = firstCurrencySpinner.selectedItem.toString()
        val secondCurrency = secondCurrencySpinner.selectedItem.toString()
        firstUnitCurrency.text = currencyUnit[firstCurrency]
        secondUnitCurrency.text = currencyUnit[secondCurrency]
        updateExchangeRateText(firstCurrency, secondCurrency)
        updateDisplay()  // Cập nhật lại hiển thị khi thay đổi đơn vị tiền tệ
    }

    private fun updateExchangeRateText(firstCurrency: String, secondCurrency: String) {
        val fromRate = exchangeRates[firstCurrency]
        val toRate = exchangeRates[secondCurrency]

        if (fromRate != null && toRate != null) {
            val exchangeRate = toRate / fromRate
            exchangeRateText.text = "1 $firstCurrency = ${String.format("%.8f", exchangeRate)} $secondCurrency"
        } else {
            exchangeRateText.text = "N/A"
        }
    }

    private fun clearText() {
        inputValue = "0"
        firstCurrencyText.text = "0"
        secondCurrencyText.text = "0"
    }

    private fun addPoint() {
        if (!inputValue.contains(".")) {
            inputValue += "."
        }
        updateDisplay()
    }

    private fun convertCurrency(firstCurrency: String, secondCurrency: String, amount: Double): Double {
        val fromRate = exchangeRates[firstCurrency]
        val toRate = exchangeRates[secondCurrency]
        return if (fromRate != null && toRate != null) {
            val result = amount * toRate / fromRate
            String.format("%.2f", result).toDouble()
        } else {
            0.0
        }
    }
}
