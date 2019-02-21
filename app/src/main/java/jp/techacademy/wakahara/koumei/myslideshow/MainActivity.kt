package jp.techacademy.wakahara.koumei.myslideshow

import android.media.MediaPlayer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {

    // スライドショーで使用する画像をリストに格納
    // 今回は曲の長さに合わせて20枚を準備
    private val resources = listOf(
        R.drawable.cat02, R.drawable.cat01, R.drawable.cat03, R.drawable.cat04,
        R.drawable.cat05, R.drawable.cat06, R.drawable.cat07, R.drawable.cat08,
        R.drawable.cat09, R.drawable.cat12, R.drawable.cat11, R.drawable.cat19,
        R.drawable.cat13, R.drawable.cat14, R.drawable.cat15, R.drawable.cat16,
        R.drawable.cat17, R.drawable.cat18, R.drawable.cat20, R.drawable.cat10
    )

    private var position = 0         //resourceが示す画像のうち、どの画像を表示しているかを記憶するプロパティ
    private var isSlideshow = false // スライドショーが実行中かどうかのフラグ
    private val handler = Handler()   // timerを使って画面処理を行うために必要
    private lateinit var player: MediaPlayer //初期化をonCreate内で行いたいのでlateinit修飾子をつける

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /*
        ImageSwitcherは内部に複数の画像を持ちこれを切り替える。setFactoryメソッドは、切り替える画像を生成する方法を指定する。
        setFactoryの引数はfactory:ViewSwitcher.VieFactory。
        ViewSwitcherはImageSwitcherの親クラスで内部にビューを作成するためのファクトリー用インターフェイスViewSwitcher.ViewFactoryを持つ。
        ViewSwitcher.ViewFactory は makeViewというメソッドだけをもつSAMインターフェイスなのでSAM変換が使える。
        */
        imageSwitcher.setFactory {
            ImageView(this) // イメージビューのインスタンスを作成（makeViewメソッドの戻り値)
        }
        imageSwitcher.setImageResource(resources[0]) // 最初の画像をImageSwitcherに表示

        // ひとつ前の画像へ移動
        prevButton.setOnClickListener {
            imageSwitcher.setInAnimation(this, android.R.anim.fade_in)   //アニメーション効果：フェードイン (その他アニメーション効果の一覧はp.211)
            // 第一引数：context、第二引数：アニメーション効果のリソースID（プロジェクトに属するRではなくAndroid SDKに含まれるアニメーションリソースファイルのID）
            imageSwitcher.setOutAnimation(this, android.R.anim.fade_out) //アニメーション効果：フェードアウト
            movePosition(-1)
        }
        // ひとつ先の画像へ移動
        nextButton.setOnClickListener {
            imageSwitcher.setInAnimation(this, android.R.anim.slide_in_left) //アニメーション効果：左からスライドしながら入ってくる
            imageSwitcher.setOutAnimation(this, android.R.anim.slide_out_right) //アニメーション効果：右へスライドしながら出ていく
            movePosition(1) }

        // タイマーを使う、引数periodで実行間隔をミリ秒で指定、ラムダ式
        // Androidでは「画面に対する処理はすべてメインスレッドから行わなければならない」(p.212)ため、Handlerクラスを使って
        // 別スレッドで行いたい処理を委譲する

        // val handler = Handler()
        // timer(period = 5000) {
        //     handler.post(object:Runnable{
        //        override fun run() {
        //           TODO("定期的に実行したい処理を記述")
        //        }
        //     }/
        // }
        /*
        以上をSAM変換して
        val handler = Handler()
        timer(period = 5000) {
            handler.post {
                TODO("定期的に実行したい処理を記述")
            }
         }
         として実装する。プロパティはonCreateの上で定義
         */

        timer(period = 5000) {
            handler.post {
                if (isSlideshow) movePosition(1) // スライドショー実行中だけ５秒で次の画像に移動する
            }
        }
        slideshowButton.setOnClickListener {
            isSlideshow = !isSlideshow   // ボタンを押すたびにisSlideshowのtrueとfalseを切り替える

            // スライドショーと音楽再生を連動させる
            when (isSlideshow) {
                true -> player.start()
                false -> player.apply {
                    pause()   // スライドショーを中止したら音楽も中止して...
                    seekTo(0) // ...曲の頭出し
                }
            }
        }
        player = MediaPlayer.create(this, R.raw.promnade)
        // 第一引数：context, アクティビティを指定
        // 第二引数：サウンドファイルのリソースID, 事前にresのrawディレクトリに入れた曲(ACCも使える)ファイル名は大文字NGっぽい＊＊拡張子なしで
        player.isLooping = true
    }

    //画像を変更する 移動量を示すInt型の数値を受け取る
    private fun movePosition(move: Int) {
        position += move
        if (position >= resources.size) {           //リストの要素数はsizeプロパティで取得する
            position = 0
        } else if (position < 0) {
            position = resources.size -1
        }
        imageSwitcher.setImageResource(resources[position])
    }
}
