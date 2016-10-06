package android.lyiue.com.and11_getpost;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    private EditText et_username;
    private EditText et_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_username = (EditText) findViewById(R.id.et_username);
        et_password = (EditText) findViewById(R.id.et_password);
    }

    /**
     * get提交方式
     *
     * @param view
     */
    public void getCommit(View view) throws IOException {
        final String username = et_username.getText().toString();
        final String password = et_password.getText().toString();
        new Thread() {
            @Override
            public void run() {
                URL url = null;
                try {
                    url = new URL("http://127.0.0.1:8080/Login/LoginServlet?username=" + username + "&password=" + password + "");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    final int responseCode = connection.getResponseCode();
                    if (responseCode == 200) {
                        InputStream inputStream = connection.getInputStream();
                        final String res = readStream(inputStream);
                        runOnUiThread(new Thread() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, res, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        runOnUiThread(new Thread() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "get访问服务器失败！" + responseCode, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                } catch (IOException e) {
                    System.out.println("..." + e.toString());
                }
            }
        }.start();
    }

    /**
     * post提交方式
     *
     * @param view
     */
    public void postCommit(View view) {
        final String username = et_username.getText().toString();
        final String password = et_password.getText().toString();

        new Thread() {
            @Override
            public void run() {
                URL url = null;
                try {
                    url = new URL("http://127.0.0.1:8080/Login/LoginServlet");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(5000);
                    //请求体,重要，url编码
                    String data = "username=" + URLEncoder.encode(username,"UTF-8") + "&password=" + URLEncoder.encode(password,"UTF-8");
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setRequestProperty("Content-Length", data.length() + "");

                    connection.setDoOutput(true);
                    connection.getOutputStream().write(data.getBytes());

                    final int responseCode = connection.getResponseCode();
                    if (responseCode == 200) {
                        InputStream inputStream = connection.getInputStream();
                        final String res = readStream(inputStream);
                        runOnUiThread(new Thread() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, res, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        runOnUiThread(new Thread() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this, "post访问服务器失败！" + responseCode, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                } catch (IOException e) {
                    System.out.println("..." + e.toString());
                }
            }
        }.start();
    }

    private String readStream(InputStream inputStream) throws IOException {
        //创建ByteArrayOutputStream一个内存输出流
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int len = -1;
        byte[] buffer = new byte[1024];
        while ((len = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, len);
        }
        inputStream.close();
        return new String(byteArrayOutputStream.toByteArray());
    }
}
