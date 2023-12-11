package com.example.todo_app



import android.content.DialogInterface
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.*


class MainActivity : AppCompatActivity() {

    //表示するリストを用意（今は空）
    private var addList = ArrayList<Todo>()

    // RecyclerViewを宣言
    private lateinit var recyclerView: RecyclerView

    // RecyclerViewのAdapterを用意
    private var recyclerAdapter = RecyclerAdapter(addList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ヘッダータイトルを非表示
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)

        // Viewをセット
        setContentView(R.layout.activity_main)

        // View要素を取得
        val btnAdd: Button = findViewById(R.id.btnAdd)
        recyclerView = findViewById(R.id.rv)

        // コンテンツを変更してもRecyclerViewのレイアウトサイズを変更しない場合はこの設定を使用してパフォーマンスを向上
        recyclerView.setHasFixedSize(true)

        // 縦スクロールリスト
        recyclerView.layoutManager = LinearLayoutManager(this)

        val itemDecoration: RecyclerView.ItemDecoration =
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(itemDecoration)

        // RecyclerViewにAdapterをセット
        recyclerView.adapter = recyclerAdapter

        // 追加ボタン押下時にAlertDialogを表示する
        btnAdd.setOnClickListener {

            // AlertDialog内の表示項目を取得
            val view = layoutInflater.inflate(R.layout.add_todo, null)
            val txtTitle: EditText = view.findViewById(R.id.title)
            val txtDetail: EditText = view.findViewById(R.id.detail)

            // AlertDialogを生成
            android.app.AlertDialog.Builder(this)
                // AlertDialogのタイトルを設定
                .setTitle(R.string.addTitle)
                // AlertDialogの表示項目を設定
                .setView(view)
                // AlertDialogのyesボタンを設定し、押下時の挙動を記述
                .setPositiveButton(R.string.yes) { _: DialogInterface?, _: Int ->
                    // ToDoを生成
                    val data = Todo(txtTitle.text.toString(), txtDetail.text.toString())
                    // 表示するリストの最後尾に追加
                    addList.add(data)
                    // 表示するリストを更新(アイテムが挿入されたことを通知)
                    recyclerAdapter.notifyItemInserted(addList.size - 1)
                }
                // AlertDialogのnoボタンを設定
                .setNegativeButton(R.string.no, null)
                // AlertDialogを表示
                .show()
        }

        // 表示しているアイテムがタッチされた時の設定
        val itemTouchHelper = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(
                // アイテムをドラッグできる方向を指定
                ItemTouchHelper.UP or ItemTouchHelper.DOWN or
                        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
                // アイテムをスワイプできる方向を指定
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {
                // アイテムドラッグ時の挙動を設定
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    // アイテム位置の入れ替えを行う
                    val fromPos = viewHolder.adapterPosition
                    val toPos = target.adapterPosition
                    recyclerAdapter.notifyItemMoved(fromPos, toPos)
                    return true
                }

                // アイテムスワイプ時の挙動を設定
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    // アイテムスワイプ時にAlertDialogを表示
                    android.app.AlertDialog.Builder(this@MainActivity)
                        // AlertDialogのタイトルを設定
                        .setTitle(R.string.removeTitle)
                        // AlertDialogのyesボタンを設定
                        .setPositiveButton(R.string.yes) { arg0: DialogInterface, _: Int ->
                            try {
                                // AlertDialogを非表示
                                arg0.dismiss()
                                // UIスレッドで実行
                                runOnUiThread {
                                    // スワイプされたアイテムを削除
                                    addList.removeAt(viewHolder.adapterPosition)
                                    // 表示するリストを更新(アイテムが削除されたことを通知)
                                    recyclerAdapter.notifyItemRemoved(viewHolder.adapterPosition)
                                }
                            } catch (ignored: Exception) {
                            }
                        }
                        .setNegativeButton(R.string.no) { _: DialogInterface, _: Int ->
                            // 表示するリストを更新(アイテムが変更されたことを通知)
                            recyclerAdapter.notifyDataSetChanged()
                        }
                        // AlertDialogを表示
                        .show()
                }
            })

        // 表示しているアイテムがタッチされた時の設定をリストに適用
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }
}
