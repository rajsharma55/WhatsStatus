package com.logicalhub.whatsstatus.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import androidx.appcompat.app.AlertDialog;
import org.jsoup.Jsoup;
import java.io.IOException;

public class ForceUpdateAsync extends AsyncTask<String, String, String> {

    String newVersion;
    private Context context;

    public ForceUpdateAsync(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {

        try {
            newVersion = Jsoup.connect("https://play.google.com/store/apps/details?id=" +  context.getPackageName() + "&hl=en")
                    .timeout(30000)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get()
                    .select("div.hAyfc:nth-child(4) > span:nth-child(2) > div:nth-child(1) > span:nth-child(1)")
                    .first()
                    .ownText();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return newVersion;
    }

    @Override
    protected void onPostExecute(String latestVersion) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String currentVersion = packageInfo.versionName;
        if (latestVersion != null) {
            if (!currentVersion.equalsIgnoreCase(latestVersion)) {
                startDialog();
            }
        }
        super.onPostExecute(latestVersion);
    }

    private void startDialog() {
        final AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
                context);
        myAlertDialog.setTitle("Update Available");
        myAlertDialog.setMessage("A new version of WhatsStatus is available. Please update to version");

        myAlertDialog.setPositiveButton("Update",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        rateUsOnPlayStore();
                    }
                });

        myAlertDialog.setNegativeButton("Not Now",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });
        myAlertDialog.show();
    }

    public void rateUsOnPlayStore() {
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
        }
    }
}