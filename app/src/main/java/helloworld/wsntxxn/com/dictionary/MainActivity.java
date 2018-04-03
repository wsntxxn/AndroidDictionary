package helloworld.wsntxxn.com.dictionary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText editword;
    private Button buttonsearch;
    private TextView tvword; private TextView tvphonetic;
    private TextView tvtrans;

    private String url="http://xtk.azurewebsites.net/BingDictService.aspx?Word=";
    private String word;
    private String InputText;
    private String phonetic = "获取失败";
    private String trans = "获取失败";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonsearch=(Button)findViewById(R.id.button_search);
        editword = (EditText) findViewById(R.id.edit_word);
        tvword = (TextView) findViewById(R.id.tv_word);
        tvphonetic = (TextView) findViewById(R.id.tv_phonetic);
        tvtrans = (TextView) findViewById(R.id.tv_trans);
        buttonsearch.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch(v.getId()) {
            case R.id.button_search: {
                InputText=editword.getText().toString();
                url=url+InputText;
                sendRequestWithOkHttp(url);
                url="http://xtk.azurewebsites.net/BingDictService.aspx?Word=";
            }
            break;
            default:
                break;
        }
    }

    private void sendRequestWithOkHttp(final String url){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client=new OkHttpClient();
                    Request request=new Request.Builder().url(url).build();
                    Response response=client.newCall(request).execute();
                    String responseData=response.body().string();
                    parseJSONWithJSONObject(responseData);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void parseJSONWithJSONObject(String responseData){
        try{
            JSONObject mainObject=new JSONObject(responseData);
            word=mainObject.getString("word");

            JSONObject phoneticObject=new JSONObject(mainObject.getString("pronunciation"));
            phonetic=phoneticObject.getString("BrE");

            String defsData=mainObject.getString("defs");
            JSONArray defsJsonArray=new JSONArray(defsData);
            trans="";
            for(int i=0;i<defsJsonArray.length();++i){
                JSONObject defObject=defsJsonArray.getJSONObject(i);
                trans= new StringBuilder().append(trans).append(defObject.getString("pos")).append(defObject.getString("def")).append("\n").toString();
            }

            showResponse(word,phonetic,trans);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void showResponse(final String word,final String phonetic,final String trans){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvword.setText(word);
                tvphonetic.setText(phonetic);
                tvtrans.setText(trans);
            }
        });
    }
}
