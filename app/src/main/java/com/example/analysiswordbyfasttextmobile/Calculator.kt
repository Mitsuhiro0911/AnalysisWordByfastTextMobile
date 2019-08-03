package com.example.analysiswordbyfasttextmobile

import net.sf.javaml.core.DenseInstance
import net.sf.javaml.distance.CosineSimilarity
import java.io.BufferedReader

/**
 * 素性ベクトルの計算を行うクラス
 */
class Calculator {
    private val cs = CosineSimilarity()
    /**
     * 2つの商品[x],[y]間のコサイン類似度を計算する
     *
     * @param[x],[y] 商品の素性ベクトル
     * @return 商品同士のコサイン類似度
     */
    fun calCosSimilarity(x: DoubleArray, y: DoubleArray): Double {
        return this.cs.measure(DenseInstance(x), DenseInstance(y))
    }

    /**
     * 入力した単語の素性ベクトルの加算・減算を行う
     */
    fun calWordVector(
        inputWord: LinkedHashMap<String, String>,
        vectorMap: LinkedHashMap<String, ArrayList<Double>>
    ): Pair<LinkedHashMap<String, ArrayList<Double>>, String> {
        val calculatedVector = LinkedHashMap<String, ArrayList<Double>>()
        var joinedWord = ""
        val calculatedScore = ArrayList<Double>()
        for (i in 0 until Setting.vectorSize) {
            calculatedScore.add(0.0)
        }
        for (vector in vectorMap) {
            // Positiveの場合、ベクトルを加算。Nevativeの場合、ベクトルを減算。
            if (inputWord.get(vector.key) == "Positive") {
                joinedWord = "${joinedWord}＋${vector.key}"
                for (i in 0 until vector.value.size) {
                    calculatedScore[i] = calculatedScore[i] + vector.value.get(i)
                }
            } else if (inputWord.get(vector.key) == "Negative") {
                joinedWord = "${joinedWord}ー${vector.key}"
                for (i in 0 until vector.value.size) {
                    calculatedScore[i] = calculatedScore[i] - vector.value.get(i)
                }
            }
        }
        calculatedVector.put(joinedWord, calculatedScore)
        println(calculatedVector)
        return calculatedVector to joinedWord
    }

    /**
     * 加算・減算した入力単語と他の単語のコサイン類似度を計算
     */
    fun getWord2Vec(
        inputWord: LinkedHashMap<String, String>,
        joinedWord: String,
        calculatedVector: LinkedHashMap<String, ArrayList<Double>>,
        cosBr: BufferedReader
    ): LinkedHashMap<String, Double> {
        var cosRank = LinkedHashMap<String, Double>()
        var cosStr = cosBr.readLine()
        cosStr = cosBr.readLine()
        while (cosStr != null) {
            val scoreList = ArrayList<Double>()
            val split = cosStr.split(" ")
            // 演算に用いた単語はコサイン類似度計算の対象から除外
            if (!inputWord.containsKey(split[0])) {
                for (i in 1 until split.size - 1) {
                    scoreList.add(split.get(i).toDouble())
                }
                val word = calculatedVector.get(joinedWord)!!.toDoubleArray()
                val compareWord = scoreList.toDoubleArray()
                val cosSimilarity = calCosSimilarity(word, compareWord)
                cosRank.put(split[0], cosSimilarity)
            }
            cosStr = cosBr.readLine()
        }
        cosBr.close()
        return cosRank
    }
}