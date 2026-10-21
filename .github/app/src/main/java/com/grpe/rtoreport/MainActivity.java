package com.grpe.rtoreport;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.OutputStream;

/**
 * Hosts the RTO Report Generator page (assets/index.html) in a WebView.
 * The page calls window.AndroidBridge for things a web page can't do well on Android:
 * copying to the clipboard, sharing to Viber, and saving the Excel export to Downloads.
 */
public class MainActivity extends Activity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        webView = new WebView(this);
        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);   // keeps the message log and drafts on the phone
        s.setDatabaseEnabled(true);
        s.setTextZoom(100);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("file:///android_asset/")) return false;
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                } catch (Exception ignored) { }
                return true;
            }
        });
        webView.addJavascriptInterface(new Bridge(), "AndroidBridge");

        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState);
        } else {
            webView.loadUrl("file:///android_asset/index.html");
        }
        setContentView(webView);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }

    /** Back button: return to the report list first, then leave the app. */
    @Override
    @SuppressWarnings("deprecation")
    public void onBackPressed() {
        webView.evaluateJavascript("window.appBack ? window.appBack() : false", value -> {
            if (!"true".equals(value)) {
                MainActivity.super.onBackPressed();
            }
        });
    }

    private void toast(final String msg) {
        runOnUiThread(() -> Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show());
    }

    private class Bridge {

        @JavascriptInterface
        public void copyText(final String text) {
            runOnUiThread(() -> {
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                if (cm != null) cm.setPrimaryClip(ClipData.newPlainText("RTO message", text));
            });
        }

        @JavascriptInterface
        public void shareText(final String text) {
            runOnUiThread(() -> {
                Intent send = new Intent(Intent.ACTION_SEND);
                send.setType("text/plain");
                send.putExtra(Intent.EXTRA_TEXT, text);
                startActivity(Intent.createChooser(send, "Send message via"));
            });
        }

        /** Saves the exported .xlsx to the phone's Downloads folder. Returns a message for the page to show. */
        @JavascriptInterface
        public String saveFile(String fileName, String base64, String mime) {
            try {
                byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
                ContentResolver resolver = getContentResolver();
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                values.put(MediaStore.MediaColumns.MIME_TYPE, mime);
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
                Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                if (uri == null) return "Couldn't create the file in Downloads.";
                try (OutputStream out = resolver.openOutputStream(uri)) {
                    if (out == null) return "Couldn't write the file in Downloads.";
                    out.write(bytes);
                }
                final Uri fileUri = uri;
                final String fileMime = mime;
                runOnUiThread(() -> {
                    Intent send = new Intent(Intent.ACTION_SEND);
                    send.setType(fileMime);
                    send.putExtra(Intent.EXTRA_STREAM, fileUri);
                    send.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    try {
                        startActivity(Intent.createChooser(send, "Send Message Log via"));
                    } catch (Exception ignored) { }
                });
                return "Saved to Downloads: " + fileName;
            } catch (Exception e) {
                return "Export failed: " + e.getMessage();
            }
        }
    }
}
