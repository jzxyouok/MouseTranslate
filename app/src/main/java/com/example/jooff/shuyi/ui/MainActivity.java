package com.example.jooff.shuyi.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.jooff.shuyi.R;
import com.example.jooff.shuyi.adapter.HistoryAdapter;
import com.example.jooff.shuyi.api.YoudaoTranslate;
import com.example.jooff.shuyi.db.MouseTranslateDB;
import com.example.jooff.shuyi.model.RecHistoryItem;
import com.example.jooff.shuyi.utils.NetworkState;
import com.example.jooff.shuyi.utils.UTF8Format;
import com.example.jooff.shuyi.view.DividerItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText editText;
    private TextView text_result;
    private ImageView result_collect;
    private ImageView result_copy;
    private ImageView original_delete;
    private CardView cardView;
    private RequestQueue requestQueue;
    private ProgressBar progressBar;
    private TextView dic_speech;
    private CardView content_dic;
    private RecyclerView rv;
    private ArrayList<RecHistoryItem> lists = new ArrayList<>();
    private CardView content_history;
    private TextView enPhonetic;
    private TextView usPhonetic;
    private LinearLayout phonetic;
    private HistoryAdapter myAdapter;
    private ImageView fab;
    private MouseTranslateDB mouseTranslateDB;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        mouseTranslateDB = MouseTranslateDB.getInstance(this);
        requestQueue = Volley.newRequestQueue(MainActivity.this);
        lists = (ArrayList<RecHistoryItem>) mouseTranslateDB.loadHistory();
        if (lists.size() == 0 )
            content_history.setVisibility(View.GONE);
        myAdapter = new HistoryAdapter(MainActivity.this, lists);
        myAdapter.setOnItemClickListener(new HistoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                editText.setText(lists.get(position).getTextOriginal());
                text_result.setText(lists.get(position).getTextResult());
                cardView.setVisibility(View.VISIBLE);
                content_history.setVisibility(View.GONE);
            }
        });
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rv.setAdapter(myAdapter);
        rv.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL_LIST));
        result_collect.setTag("cancel");
        fab.setOnClickListener(this);
        original_delete.setOnClickListener(this);
        result_copy.setOnClickListener(this);
        result_collect.setOnClickListener(this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                //设置滑动的方向
                int swipeFlags = makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT
                );
                return makeMovementFlags(0, swipeFlags);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            /**
             *
             * @param viewHolder  滑动删除的Item
             * @param direction
             *
             */

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                String s = lists.get(position).getTextOriginal();
                mouseTranslateDB.deleteHistory(s);
                myAdapter.remove(position);
                //当历史记录清空时，将历史记录卡片设置为不可见
                if (lists.size() == 0) {
                    content_history.setVisibility(View.GONE);
                }
            }
        });
        itemTouchHelper.attachToRecyclerView(rv);

    }

    private void sendRequest(String url) {

        progressBar.setVisibility(View.VISIBLE);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {

                try {
                    switch (jsonObject.getInt("errorCode")) {

                        case 0:
                            String dic = "";
                            String speech = "";
                            if (!jsonObject.isNull("translation")) {
                                for (int i = 0; i < jsonObject.getJSONArray("translation").length(); i++) {
                                    dic = dic + jsonObject.getJSONArray("translation").getString(i);
                                }

                                dic = dic + "\n";
                            }

                            if (!jsonObject.isNull("basic")) {
                                for (int i = 0; i < jsonObject.getJSONObject("basic").getJSONArray("explains").length(); i++) {
                                    speech = speech + jsonObject.getJSONObject("basic").getJSONArray("explains").getString(i) + "\n";
                                }
                                speech = speech + "\n";
                                Log.d("speech", speech);

                                if (!jsonObject.getJSONObject("basic").isNull("us-phonetic")) {
                                    phonetic.setVisibility(View.VISIBLE);
                                    usPhonetic.setText(jsonObject.getJSONObject("basic").optString("us-phonetic"));
                                } else phonetic.setVisibility(View.GONE);

                                if (!jsonObject.getJSONObject("basic").isNull("uk-phonetic")) {
                                    enPhonetic.setText(jsonObject.getJSONObject("basic").getString("uk-phonetic"));
                                }

                                dic_speech.setText(speech);
                                content_dic.setVisibility(View.VISIBLE);

                            } else content_dic.setVisibility(View.GONE);

                            text_result.setText(dic);
                            RecHistoryItem recHistoryItem = new RecHistoryItem();
                            recHistoryItem.setTextOriginal(editText.getText().toString());
                            recHistoryItem.setTextResult(dic);
                            mouseTranslateDB.deleteHistory(editText.getText().toString());
                            mouseTranslateDB.saveHistory(recHistoryItem);
                            Log.d("存数据", "onResponse: " + mouseTranslateDB.loadHistory().size());
                            lists.add(recHistoryItem);
                            myAdapter.notifyDataSetChanged();

                            break;
                        case 20:
                            Snackbar.make(cardView, R.string.original_is_too_long, Snackbar.LENGTH_LONG).show();
                            break;
                        case 30:
                            Snackbar.make(cardView, R.string.invalid_translate, Snackbar.LENGTH_LONG).show();
                            break;
                        case 40:
                            Snackbar.make(cardView, R.string.dont_support_language, Snackbar.LENGTH_LONG).show();
                            break;
                        case 50:
                            Snackbar.make(cardView, R.string.invalid_key, Snackbar.LENGTH_LONG).show();
                        default:
                            break;
                    }
                } catch (JSONException e) {

                    e.printStackTrace();
                }

                progressBar.setVisibility(View.GONE);

                cardView.setVisibility(View.VISIBLE);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressBar.setVisibility(View.GONE);
                Snackbar.make(cardView, volleyError.toString(), Snackbar.LENGTH_LONG).show();
            }
        });

        requestQueue.add(jsonObjectRequest);

    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        phonetic = (LinearLayout) findViewById(R.id.phonetic);
        editText = (EditText) findViewById(R.id.et);
        content_history = (CardView) findViewById(R.id.content_history);
        fab = (ImageView) findViewById(R.id.floatingActionButton);
        rv = (RecyclerView) findViewById(R.id.rec_history);
        usPhonetic = (TextView) findViewById(R.id.us_phonetic);
        enPhonetic = (TextView) findViewById(R.id.en_phonetic);
        dic_speech = (TextView) findViewById(R.id.dic_speech);
        content_dic = (CardView) findViewById(R.id.content_dic);
        text_result = (TextView) findViewById(R.id.text_result);
        result_copy = (ImageView) findViewById(R.id.result_copy);
        original_delete = (ImageView) findViewById(R.id.original_delete);
        result_collect = (ImageView) findViewById(R.id.result_collect);
        cardView = (CardView) findViewById(R.id.content);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.about) {
            String test = null;
            test = UTF8Format.encode(test);
            Log.d("test", "onOptionsItemSelected: " + test);
            final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setCancelable(false);
            dialog.setTitle(R.string.give_me_five);
            dialog.setMessage(R.string.five);
            dialog.setPositiveButton(R.string.positive, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            dialog.setNegativeButton(R.string.negative, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(MainActivity.this, R.string.dislike, Toast.LENGTH_SHORT).show();
                    finish();

                }
            });
            dialog.show();

        }
        return true;

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.floatingActionButton:
                String original = editText.getText().toString();
                //先进行网络的判断
                if (!NetworkState.isConnected(MainActivity.this)) {
                    Snackbar.make(view, R.string.no_internet, Snackbar.LENGTH_SHORT)
                            .setAction(R.string.set_internet, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(Settings.ACTION_SETTINGS));
                                }
                            })
                            .show();
                } else if (TextUtils.isEmpty(original)) {
                    Snackbar.make(view, R.string.no_text, Snackbar.LENGTH_SHORT).show();

                } else {
                    content_history.setVisibility(View.GONE);
                    //先对翻译结果进行初始化
                    text_result.setText("");

                    if (Objects.equals(original, "酷安")) original = "基佬";
                    sendRequest(YoudaoTranslate.TRANSLATE_URL + UTF8Format.encode(original).replace("\n", ""));
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    //发送请求后就隐藏输入板
                    if (inputManager.isActive()) {
                        inputManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    }
                }

                break;

            case R.id.original_delete:
                //将发音清空
                phonetic.setVisibility(View.GONE);
                editText.setText("");
                //当输入为空时,将卡片隐藏
                if (lists.size() != 0)
                    content_history.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                cardView.setVisibility(View.GONE);
                content_dic.setVisibility(View.GONE);
                break;

            case R.id.result_copy:
                //复制到粘贴板上
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData result = ClipData.newPlainText("result", text_result.getText().toString());
                clipboardManager.setPrimaryClip(result);
                Snackbar.make(view, R.string.copy_success, Snackbar.LENGTH_SHORT).show();
                break;

            case R.id.result_collect:
                //设置tag,可以重复点击收藏图标,实现收藏或取消收藏
                if (result_collect.getTag() == "cancel") {
                    result_collect.setImageResource(R.drawable.ic_star_white_24dp);
                    result_collect.setTag("collect");
                    Snackbar.make(view,R.string.collect,Snackbar.LENGTH_SHORT).show();
                } else if (result_collect.getTag() == "collect") {
                    result_collect.setImageResource(R.drawable.ic_star_border_white_24dp);
                    result_collect.setTag("cancel");
                    Snackbar.make(view,R.string.cancel_collect,Snackbar.LENGTH_SHORT).show();
                }
                break;

            default:
                break;

        }
    }
}

