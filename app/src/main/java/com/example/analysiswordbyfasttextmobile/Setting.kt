package com.example.analysiswordbyfasttextmobile

/**
 * 実行モード(高性能モード・高速モード)によって変動するパラメータのクラス
 */
class Setting {
    companion object {
        // 高性能モード・高速モードを区別する
        var mode = ""
        // 読み込む素性ベクトルのファイルパス
        var model = ""
        // 読み込む素性ベクトルの次元数
        var vectorSize = 0
    }
}
