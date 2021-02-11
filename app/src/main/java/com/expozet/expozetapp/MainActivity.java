package com.expozet.expozetapp;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.airbnb.lottie.LottieAnimationView;
public class MainActivity extends AppCompatActivity {
    public static WebView webView;
    private String url = "https://expozet.com/";
    public static SwipeRefreshLayout swipeRefreshLayout;
    static ProgressBar progressBar;
//    static ProgressDialog progressDialog;
    LottieAnimationView lottieAnimationView;
    LinearLayout animation, main;
    @SuppressLint({"SetJavaScriptEnabled", "WrongConstant"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = findViewById(R.id.web_view);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);

        progressBar = findViewById(R.id.progress_bar);
        swipeRefreshLayout = findViewById(R.id.swipe_layout);
        animation = findViewById(R.id.animation_layout);
        main = findViewById(R.id.main_layout);
        animation.setVisibility(View.INVISIBLE);
        main.setVisibility(View.VISIBLE);

        lottieAnimationView = findViewById(R.id.animationView);
        lottieAnimationView.setVisibility(View.GONE);
        progressBar.setProgress(0);

        swipeRefreshLayout.setEnabled(true);
//        progressDialog = new ProgressDialog(this);
//        progressDialog.setCancelable(false);
//        progressDialog.setMessage("Loading Please Wait!");
        if (savedInstanceState == null) {
            check_connection();
        } else {
            webView.restoreState(savedInstanceState);
        }
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.design_default_color_on_secondary),
                getResources().getColor(R.color.design_default_color_secondary_variant), getResources().getColor(R.color.design_default_color_primary));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.reload();
            }
        });


        webView.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url == null || url.startsWith("http://") || url.startsWith("https://"))
                    return false;
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    view.getContext().startActivity(intent);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                swipeRefreshLayout.setRefreshing(false);

            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(newProgress);
//                progressDialog.show();
                if (newProgress== 100) {
//                    progressDialog.cancel();

                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    public void check_connection() {
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean connected = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
                if (connected) {
                    animation.setVisibility(View.VISIBLE);
                    main.setVisibility(View.INVISIBLE);
                    lottieAnimationView.playAnimation();
                    lottieAnimationView.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "No Internet", Toast.LENGTH_SHORT).show();
                } else {
                    animation.setVisibility(View.INVISIBLE);
                    main.setVisibility(View.VISIBLE);
                    webView.loadUrl(url);
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    public void onBackPressed() {
        if (webView.getUrl().equals(url)) {
            final AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setMessage("Are You Sure To Exit");
            b.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    b.setCancelable(true);
                }
            }).setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finishAffinity();
                }
            }).show();
        } else if (webView.canGoBack()) {
            webView.goBack();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        webView.saveState(outState);
    }
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        webView.restoreState(savedInstanceState);
    }


}


