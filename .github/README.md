# RTO Reports (Android)

Android version of **Report_Template_by_GrpE.xlsm**. The same 10 report templates, the same dropdowns,
operators and message wording, plus the Message_Log and "Export to Excel".

The app is one page (`app/src/main/assets/index.html`) shown in a WebView. `MainActivity.java` adds
the phone features: Copy, Share (to Viber), and saving the Excel export to the Downloads folder.

## Option A: build the APK in Android Studio (about 10 minutes the first time)

1. Install Android Studio (free): https://developer.android.com/studio
2. Unzip this folder, then in Android Studio choose **File > Open** and pick the `RTOReportApp` folder.
   Wait for "Gradle sync" to finish. It downloads what it needs the first time.
3. Menu **Build > Build App Bundle(s) / APK(s) > Build APK(s)**.
4. Click **locate** in the popup. The file is `app/build/outputs/apk/debug/app-debug.apk`.
5. Send that APK to your phone (Viber, Drive or USB), open it, and allow "Install unknown apps" when asked.

## Option B: let GitHub build it (no Android Studio)

1. Create a new GitHub repository and upload the contents of this folder.
2. Open the **Actions** tab. The "Build APK" workflow runs on every push (or press "Run workflow").
3. When it finishes, download **RTO-Reports-apk** from the run's page, unzip it, and install `app-debug.apk` on the phone.

## Changing things

- Operators, dropdown lists, report fields: edit the `OPERATORS`, `RUNS`, `REGIONS` and `REPORTS` lists
  near the top of the `<script>` in `app/src/main/assets/index.html`, then rebuild.
- App name: `app/src/main/res/values/strings.xml`.
- Each new APK you share should have a higher `versionCode` in `app/build.gradle`, so phones accept it as an update.

Requires Android 10 or newer. The message log is stored on the phone; uninstalling the app deletes it,
so export it to Excel first.
