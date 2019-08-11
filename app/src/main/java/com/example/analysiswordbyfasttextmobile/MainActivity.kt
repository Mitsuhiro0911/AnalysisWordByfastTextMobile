package com.example.analysiswordbyfasttextmobile

import android.app.ProgressDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.Contacts
import android.util.Log
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.table_row.view.*
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {
    //TODO: 入力単語数を画面から増減できるようにする
    private var mHandler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // ハンドラを生成
        mHandler = Handler()
        val cal = Calculator()
        // モード選択のラジオボタングループの中身を動的生成
        val radio1 = RadioButton(this)
        radio1.text = "高速モード"
        radio1.id = 0
        radio1.isChecked = true
        val radio2 = RadioButton(this)
        radio2.text = "高性能モード(現在使用不可)"
        radio2.id = -1
        radioGroup.addView(radio1)
        radioGroup.addView(radio2)

        val executeButton = findViewById<View>(R.id.executeButton)
        executeButton.setOnClickListener {
            // プログレスバーで処理の進捗を表示
//            val progressBar = findViewById<View>(R.id.progressBar) as ProgressBar
//            progressBar.visibility = ProgressBar.VISIBLE
//            progressBar.max = 100
//            progressBar.progress = 80

            val progressDialog = ProgressDialog(this)
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
            progressDialog.setMessage("処理を実行しています")
            progressDialog.setCancelable(true)
            progressDialog.show()
            progressDialog.invalidateOptionsMenu()

            // プログレスダイアログの表示と別スレッドで内部処理を行う
            GlobalScope.async {
                // 高性能モード・高速モードを選択し、それに応じたパラメータを設定
                val checkedId = radioGroup.getCheckedRadioButtonId()
                Log.d("check", "${checkedId}")
                if (checkedId == 0) {
                    Setting.mode = "HIGH_SPEED"
                }
//            else if(checkedId == 1) {
//                // ファイルが大きすぎるため、現状読み込めない。対策が必要。
//                Setting.mode = "HIGH_PERFORMANCE"
//            }
                if (Setting.mode == "HIGH_SPEED") {
                    Setting.model = "model_abstract_201907.vec"
                    Setting.vectorSize = 200
                }
//            else if (Setting.mode == "HIGH_PERFORMANCE") {
//                Setting.model = "model_201907.vec"
//                Setting.vectorSize = 300
//            }
                val inputText1 = findViewById<View>(R.id.inputText1) as EditText
                val inputText2 = findViewById<View>(R.id.inputText2) as EditText
                Log.d("input_text", "${inputText1.text}")
                Log.d("input_text", "${inputText2.text}")
                // 入力する単語・PNをセット
                val inputWord = linkedMapOf<String, String>()
                val toggle1 = findViewById<View>(R.id.toggleButton) as ToggleButton
                val toggle2 = findViewById<View>(R.id.toggleButton2) as ToggleButton
                inputWord.put("${inputText1.text}", "${toggle1.text}")
                inputWord.put("${inputText2.text}", "${toggle2.text}")

                // 素性ベクトルファイルの読み込み
                val searchBr = BufferedReader(InputStreamReader(assets.open(Setting.model)))
                val cosBr = BufferedReader(InputStreamReader(assets.open(Setting.model)))
                // 入力した単語の素性ベクトル情報をサーチ
                val vectorMap = Parser().searchInputWordVector(inputWord, searchBr)
                // 単語ベクトルの演算
                val (calculatedVector, joinedWord) = cal.calWordVector(inputWord, vectorMap)
                // 入力した単語と他の単語のコサイン類似度を計算
                val cosRank = cal.getWord2Vec(inputWord, joinedWord, calculatedVector, cosBr)
                // プログレスダイアログを非表示にする
                progressDialog.dismiss()
                // UIへの操作を行う処理のため、メインスレッドに投げる
                Thread(object : Runnable {
                    override fun run() {
                        mHandler!!.post(object : Runnable {
                            override fun run() {
                                // コサイン類似度の上位10位まで出力
                                sortCosRank(cosRank)
                            }
                        })
                    }
                }).start()
            }
        }
    }

    fun sortCosRank(cosRank: LinkedHashMap<String, Double>): Unit {
        val sortedAnswer = cosRank.toList().sortedByDescending { it.second }.toMap()
        val tableLayout = findViewById<View>(R.id.tableLayout) as TableLayout
        var i = 0
        for (ans in sortedAnswer) {
            val tableRow = layoutInflater.inflate(R.layout.table_row, null) as TableRow
            // 順位をセット
            tableRow.getVirtualChildAt(0).rowtext1.text = "${i + 1}位"
            tableRow.getVirtualChildAt(0).rowtext1.height = 100
            // 類似単語をセット
            tableRow.getVirtualChildAt(1).rowtext2.text = "${ans.key}"
            tableRow.getVirtualChildAt(1).rowtext2.height = 100
            // スコアをセット
            tableRow.getVirtualChildAt(2).rowtext3.text = "${ans.value}"
            tableRow.getVirtualChildAt(2).rowtext3.height = 100
            i = i.plus(1)
            println("${i}位：${ans}")
            tableLayout.addView(tableRow)
            if (i > 9) {
                break
            }
        }
    }
}

