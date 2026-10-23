package com.dayuno.app;
import android.app.Activity;
import android.os.Bundle;
import android.Manifest;
import android.os.Build;
import android.content.pm.PackageManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.graphics.Color;

public class MainActivity extends Activity {
 private WebView web;
 public final class AndroidBridge {
  @JavascriptInterface public void syncSchedule(long mealAt,long fastAt) {
   runOnUiThread(()->{
    ReminderReceiver.schedule(MainActivity.this,1,mealAt);
    ReminderReceiver.schedule(MainActivity.this,2,fastAt);
   });
  }
 }
 @Override public void onCreate(Bundle b) {
  super.onCreate(b);
  getWindow().setStatusBarColor(Color.rgb(8,12,10));
  getWindow().setNavigationBarColor(Color.rgb(8,12,10));
  if(Build.VERSION.SDK_INT>=33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED)
    requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS},100);
  web=new WebView(this);
  web.setBackgroundColor(Color.rgb(8,12,10));
  WebSettings st=web.getSettings();
  st.setJavaScriptEnabled(true);st.setDomStorageEnabled(true);st.setAllowFileAccess(true);
  web.addJavascriptInterface(new AndroidBridge(),"DayunoAndroid");
  web.setWebViewClient(new WebViewClient());
  web.setWebChromeClient(new WebChromeClient());
  setContentView(web);web.loadUrl("file:///android_asset/index.html");
 }
 @Override public void onBackPressed(){if(web.canGoBack())web.goBack();else super.onBackPressed();}
 @Override protected void onDestroy(){if(web!=null)web.destroy();super.onDestroy();}
}
