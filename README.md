# RTO Report (Android)

Android app version of **Report_Template_by_GrpE.xlsm** (RTO Report Generator Tool, GRP E).

- 10 reports, same fields and message wording as the Excel user forms
- Copy / Share the message (share goes straight to Viber)
- Save to log on the phone, search and filter it, export it as an Excel file (to Downloads)

## Getting the APK

Every push to `main` builds the app on GitHub Actions. When the **Build APK** run finishes (about 5 minutes):

1. On the phone, open this repository's **Releases** → **latest** → tap **RTO-Report.apk**.
2. Open the downloaded file and allow "Install unknown apps" for your browser if Android asks.

New builds install over the old one and keep the saved log.

## Changing the app

The whole app is one file: `app/src/main/assets/index.html`. Edit it (e.g. the operator list near the top of the script), commit, and a new APK is built.

## Notes

- The log is stored on each phone. Use **Export Excel** on the Log screen to collect it.
- `app/debug.keystore` is a non-secret signing key kept in the repo so every build has the same signature. Fine for sharing inside the team; use a private key stored as a GitHub secret before publishing on Play Store.
