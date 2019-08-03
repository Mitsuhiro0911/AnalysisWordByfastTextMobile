package com.example.analysiswordbyfasttextmobile

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.util.Log
import android.view.View
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_main.view.*
import java.io.BufferedReader
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {

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
            Log.d("checked", "${inputText1.text}")
        }

//        // 入力する単語情報をセット
//        val inputWord = linkedMapOf<String, String>()
//        inputWord.put("感情", "Negative")
//        inputWord.put("人間", "Positive")
//
//        // 素性ベクトルファイルの読み込み
//        val searchBr = BufferedReader(InputStreamReader(assets.open(Setting.model)))
//        val cosBr = BufferedReader(InputStreamReader(assets.open(Setting.model)))
//
//        // 入力した単語の素性ベクトル情報をサーチ
//        val vectorMap = Parser().searchInputWordVector(inputWord, searchBr)
//        // 単語ベクトルの演算
//        val (calculatedVector, joinedWord) = cal.calWordVector(inputWord, vectorMap)
//        // 入力した単語と他の単語のコサイン類似度を計算
//        val cosRank = cal.getWord2Vec(inputWord, joinedWord, calculatedVector, cosBr)
//        // コサイン類似度の上位10位まで出力
//        Sort().sortCosRank(cosRank)
    }
}
