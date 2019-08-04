package com.example.analysiswordbyfasttextmobile

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.ToggleButton
import kotlinx.android.synthetic.main.table_row.view.*
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {
    //TODO: PNを画面から指定できるようにする
    //TODO: モード選択を画面からできるようにする
    //TODO: プログレスバーを追加する
    //TODO: 入力単語数を画面から増減できるようにする

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val cal = Calculator()
        // 高性能モード・高速モードを選択し、それに応じたパラメータを設定
//    Setting.mode = "HIGH_PERFORMANCE"
        Setting.mode = "HIGH_SPEED"
        if (Setting.mode == "HIGH_PERFORMANCE") {
            Setting.model = "data/corpas/model_201907.vec"
            Setting.vectorSize = 300
        } else if (Setting.mode == "HIGH_SPEED") {
            Setting.model = "model_abstract_201907.vec"
            Setting.vectorSize = 200
        }

        val executeButton = findViewById<View>(R.id.executeButton)
        executeButton.setOnClickListener {
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
            // コサイン類似度の上位10位まで出力
            sortCosRank(cosRank)
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
