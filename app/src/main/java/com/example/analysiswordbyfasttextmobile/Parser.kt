package com.example.analysiswordbyfasttextmobile

import java.io.BufferedReader
import java.io.File
import java.io.FileReader

/**
 * モデルファイル(Word2Vecで学習した素性ベクトル)をパースするクラス
 */
class Parser {
    /**
     * 入力した単語の素性ベクトルの情報を素性ベクトルファイルからサーチ
     */
    fun searchInputWordVector(inputWord: LinkedHashMap<String, String>): LinkedHashMap<String, ArrayList<Double>> {
        val vectorMap = LinkedHashMap<String, ArrayList<Double>>()
        val searchBr = BufferedReader(FileReader(File(Setting.model)))
        var searchStr = searchBr.readLine()
        searchStr = searchBr.readLine()
        while (searchStr != null) {
            val split = searchStr.split(" ")
            if (inputWord.containsKey(split[0])) {
                val scoreList = ArrayList<Double>()
                for (i in 1 until split.size - 1) {
                    scoreList.add(split.get(i).toDouble())
                }
                vectorMap.put(split[0], scoreList)
            }
            searchStr = searchBr.readLine()
        }
        println(vectorMap)
        searchBr.close()
        return vectorMap
    }
}