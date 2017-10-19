package com.acgow.steamauth;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodSession;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    EditText inputinfo;
    TextView ev;
    Button get, stat, neko ;
    WebView wb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String str = "正在连接..";
        ev = (TextView)findViewById(R.id.linedit);
        inputinfo = (EditText)findViewById(R.id.editText);
        get = (Button)findViewById(R.id.button);
        wb = (WebView)findViewById(R.id.webpage);
        ev.setText(str);
        ev.setVisibility(View.GONE);
        stat = (Button)findViewById(R.id.button2);
        neko = (Button)findViewById(R.id.button3);

        final Handler h = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==0x123){
                    ev.setText(msg.obj.toString());
                    wb.loadData(msg.obj.toString(),"text/html","utf-8");
                }
            }
        };
        new Thread(new AccessNetwork("GET", "http://127.0.0.1:1242/IPC", "command=status", h)).start();

        get.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int count = 0;
                String regEx = "([0-9A-Z]{5}-[0-9A-Z]{5}-[0-9A-Z]{5})";
                String mstr = inputinfo.getText().toString();
                String allkey = "r ";
                Pattern p = Pattern.compile(regEx);
                Matcher m = p.matcher(mstr);
                while (m.find()) {
                    count = count + 1;
                    System.out.println(m.groupCount());
                    System.out.println(m.group());
                    allkey += m.group().toString();
                    allkey +=",";
                }
                Log.i("reg","count:"+count);
                if(count<1) {
                   allkey = mstr;
                }

                new Thread(new AccessNetwork("GET", "http://127.0.0.1:1242/IPC", "command=" + allkey, h)).start();
            }
        });
        stat.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new Thread(new AccessNetwork("GET", "http://127.0.0.1:1242/IPC", "command=status", h)).start();
            }
        });
        neko.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new Thread(new AccessNetwork("GET", "http://127.0.0.1:1242/IPC", "command=play 420110", h)).start();
            }
        });
    }

}
class AccessNetwork implements Runnable{
    private String op ;
    private String url;
    private String params;
    private Handler h;

    public AccessNetwork(String op, String url, String params,Handler h) {
        super();
        this.op = op;
        this.url = url;
        this.params = params;
        this.h = h;
    }

    @Override
    public void run() {
        Message m = new Message();
        m.what = 0x123;
        if(op.equals("GET")){
            Log.i("iiiiiii","发送GET请求");
            m.obj = GetPostUtil.sendGet(url, params);
            Log.i("iiiiiii",">>>>>>>>>>>>url:"+url);
            Log.i("iiiiiii",">>>>>>>>>>>>parms:"+params);
            Log.i("iiiiiii",">>>>>>>>>>>>"+m.obj);
        }
        if(op.equals("POST")){
            Log.i("iiiiiii","发送POST请求");
            m.obj = GetPostUtil.sendPost(url, params);
            Log.i("gggggggg",">>>>>>>>>>>>"+m.obj);
        }
        h.sendMessage(m);
    }
}