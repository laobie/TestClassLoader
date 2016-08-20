package com.jaeger.testclassloader;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.jaeger.ISayHello;
import dalvik.system.DexClassLoader;
import java.io.File;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "TestClassLoader";
    private TextView mTvInfo;
    private Button mBtnLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTvInfo = (TextView) findViewById(R.id.tv_info);
        mBtnLoad = (Button) findViewById(R.id.btn_load);
        mBtnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 获取到包含 class.dex 的 jar 包文件
                final File jarFile =
                    new File(Environment.getExternalStorageDirectory().getPath() + File.separator + "sayhello_dex.jar");

                // 如果没有读权限,确定你在 AndroidManifest 中是否声明了读写权限
                Log.d(TAG, jarFile.canRead() + "");

                if (!jarFile.exists()) {
                    Log.e(TAG, "sayhello_dex.jar not exists");
                    return;
                }

                // getCodeCacheDir() 方法在 API 21 才能使用,实际测试替换成 getExternalCacheDir() 等也是可以的
                // 只要有读写权限的路径均可
                DexClassLoader dexClassLoader =
                    new DexClassLoader(jarFile.getAbsolutePath(), getExternalCacheDir().getAbsolutePath(), null, getClassLoader());
                try {
                    // 加载 HelloAndroid 类
                    Class clazz = dexClassLoader.loadClass("com.jaeger.HelloAndroid");
                    // 强转成 ISayHello, 注意 ISayHello 的包名需要和 jar 包中的
                    ISayHello iSayHello = (ISayHello) clazz.newInstance();
                    mTvInfo.setText(iSayHello.say());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
