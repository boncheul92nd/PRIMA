package com.okdevtv.fileupload;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;

    private ImageButton imageButton1;
    private ImageButton imageButton2;
    private ImageButton imageButton3;
    private ImageButton imageButton4;
    private ImageButton imageButton5;

    private TextView script;
    private TextView messageText;

    private Uri mImageCaptureUri;
    private int id_view;
    private String absolutePath;

    private int serverResponseCode = 0;
    private String upLoadServerUri = "http://kairas.iptime.org:8083/input";
    private ProgressDialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageButton1 = (ImageButton) this.findViewById(R.id.imageButton1);
        imageButton2 = (ImageButton) this.findViewById(R.id.imageButton2);
        imageButton3 = (ImageButton) this.findViewById(R.id.imageButton3);
        imageButton4 = (ImageButton) this.findViewById(R.id.imageButton4);
        imageButton5 = (ImageButton) this.findViewById(R.id.imageButton5);

        script = (TextView) this.findViewById(R.id.script);
        messageText = (TextView) this.findViewById(R.id.messageText);

    }

    public void onClick(View v) {
        id_view = v.getId();

        if (id_view == R.id.imageButton1 ||
                id_view == R.id.imageButton2 ||
                id_view == R.id.imageButton3 ||
                id_view == R.id.imageButton4 ||
                id_view == R.id.imageButton5) {
            new AlertDialog.Builder(this)
                    .setTitle("업로드할 이미지 선택")
                    .setPositiveButton("사진촬영", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            doTakePhotoAction();
                        }
                    })
                    .setNeutralButton("앨범선택", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                            doTakeAlbumAction();
                }
            })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();

        } else if (id_view == R.id.cancleButton1) {
            imageButton1.setImageBitmap(null);
        } else if (id_view == R.id.cancleButton2) {
            imageButton2.setImageBitmap(null);
        } else if (id_view == R.id.cancleButton3) {
            imageButton3.setImageBitmap(null);
        } else if (id_view == R.id.cancleButton4) {
            imageButton4.setImageBitmap(null);
        } else if (id_view == R.id.cancleButton5) {
            imageButton5.setImageBitmap(null);
        }
    }

    public void doTakePhotoAction() // 카메라 촬영 후 이미지 가져오기
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }

    public void doTakeAlbumAction( ) // 앨범에서 이미지 가져오기
    {
        // 앨범 호출
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK)
            return;

        mImageCaptureUri = data.getData();
        absolutePath = getRealPathFromURI(mImageCaptureUri);

        ExifInterface exif = null;
        try {
            exif = new ExifInterface(absolutePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        int exifDegree = exifOrientationToDegrees(exifOrientation);

        Bitmap bitmap = BitmapFactory.decodeFile(absolutePath);//경로를 통해 비트맵으로 전환

        Log.d("******PRIMA******", absolutePath);

        if (id_view == R.id.imageButton1) {
            imageButton1.setImageBitmap(rotate(bitmap, exifDegree));//이미지 뷰에 비트맵 넣기
        } else if (id_view == R.id.imageButton2) {
            imageButton1.setImageBitmap(rotate(bitmap, exifDegree));//이미지 뷰에 비트맵 넣기
        } else if (id_view == R.id.imageButton3) {
            imageButton1.setImageBitmap(rotate(bitmap, exifDegree));//이미지 뷰에 비트맵 넣기
        } else if (id_view == R.id.imageButton4) {
            imageButton1.setImageBitmap(rotate(bitmap, exifDegree));//이미지 뷰에 비트맵 넣기
        } else if (id_view == R.id.imageButton5) {
            imageButton1.setImageBitmap(rotate(bitmap, exifDegree));//이미지 뷰에 비트맵 넣기
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public void Posting(View v) {

        if (imageButton1.getDrawable() == null) {
            Toast.makeText(MainActivity.this, "메인 사진을 등록하셔야 합니다", Toast.LENGTH_SHORT).show();
            return;
        }

        runOnUiThread(new Runnable() {
            public void run() {
                messageText.setText("uploading started.....");
                dialog = ProgressDialog.show(MainActivity.this, "", "Uploading file...", true);
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {

                if (uploadFile(absolutePath) == 200) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(MainActivity.this, "사진 전송이 완료되었습니다", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(MainActivity.this, "사진 전송이 실패하였습니다", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                dialog.dismiss();
            }
        }).start();
    }

    public int uploadFile(String sourceFileUri) {

        final String fileName = sourceFileUri;
        final File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {

            Log.e("uploadFile", "Source File not exist :"
                    + absolutePath);

            runOnUiThread(new Runnable() {
                public void run() {
                    messageText.setText("Source File not exist :"
                            + absolutePath);
                }
            });

            return 0;

        } else {
            try {
                HttpURLConnection conn = null;
                DataOutputStream dos = null;
                String lineEnd = "\r\n";
                String twoHyphens = "--";
                String boundary = "*****";
                int bytesRead, bytesAvailable, bufferSize;
                byte[] buffer;
                int maxBufferSize = 1 * 1024 * 1024;

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(upLoadServerUri);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + fileName + "\"" + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if (serverResponseCode == 200) {
                    BufferedReader input = new BufferedReader(new InputStreamReader(conn.getInputStream()), 8192);
                    final StringBuilder response = new StringBuilder();
                    String strLine = null;
                    while ((strLine = input.readLine()) != null) {
                        response.append(strLine);
                    }
                    input.close();

                    runOnUiThread(new Runnable() {
                        public void run() {

                            messageText.setText("File Upload Completed.");

                            String msg = "File Upload Completed.\n See uploaded file here : \n"
                                    + response.toString();

                            script.setText(msg);
                        }
                    });
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                ex.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        messageText.setText("MalformedURLException Exception : check script url.");
                        Toast.makeText(MainActivity.this, "MalformedURLException",
                                Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    public void run() {
                        messageText.setText("Got Exception : see logcat ");
                        Toast.makeText(MainActivity.this, "Got Exception : see logcat ",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload file Exception", "Exception : " + e.getMessage(), e);
            }

            return serverResponseCode;
        } // End else block
    }

    public int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }
    public Bitmap rotate(Bitmap src, float degree) {

        // Matrix 객체 생성
        Matrix matrix = new Matrix();
        // 회전 각도 셋팅
        matrix.postRotate(degree);
        // 이미지와 Matrix 를 셋팅해서 Bitmap 객체 생성
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(),
                src.getHeight(), matrix, true);
    }
}
