package kr.kairas.mkdir;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GetFilePath();
    }

    public static synchronized String GetFilePath()
    {
        String sdcard = Environment.getExternalStorageState();
        File file = null;

        if ( !sdcard.equals(Environment.MEDIA_MOUNTED))
        {
            // SD카드가 마운트되어있지 않음
            file = Environment.getRootDirectory();
        }
        else
        {
            // SD카드가 마운트되어있음
            file = Environment.getExternalStorageDirectory();
        }


        String dir = file.getAbsolutePath() + "/mytestdata/test";

        file = new File(dir);
        if ( !file.exists() )
        {
            // 디렉토리가 존재하지 않으면 디렉토리 생성
            file.mkdirs();
        }

        // 파일 경로 반환
        return dir;
    }
}
