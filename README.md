# CS6018-Group-Project

## Project Description

[Client Requirements: Drawing App](https://github.com/UtahMSD/CS6018_2023/blob/main/projectDescription.md)

## Team 10 Members

- Sonia Sun
- Jo Song
- Lydia Yuan

## Phase 2 Code Review Reports

- Sonia Sun: [Code Review Report](https://docs.google.com/document/d/1uVCAY4ODb6tR8fNQdl9Jm8aaSMdw0Sa1V-Y5BzL5yp0/edit#heading=h.jv43cx3t9ago)
- Jo Song: [Code Review Report](https://docs.google.com/document/d/1bVUHNTDen-8i1168dBOqMSsxz3w45VWQ5C0rgraXtXI/edit?usp=sharing)
- Lydia Yuan: [Code Review Report](https://docs.google.com/document/d/1fUeUmIba0xmmRzI8FkZSU2MKtpPa9NG-4kUTBftLcW4/edit?usp=sharing)

## How to Set Up Firebase Config

Setting up Firebase configuration in your project is an essential step to enable Firebase services. Here's a straightforward guide on how to do it:

1. **Go to Firebase Console Project Settings:**
   - Navigate to the Firebase Console (https://console.firebase.google.com/).
   - Select your project or create a new one if you haven't already.

2. **Download the config file `google-services.json`:**
   - In the Firebase Console, locate and click on your project.
   - Access Project Settings by clicking the gear icon (⚙️) on the top left.
   - Scroll down to the "Your apps" section and select the platform (e.g., Android, iOS) for which you want to configure Firebase.
   - Follow the on-screen instructions to download the `google-services.json` configuration file specific to your platform. This file contains essential settings and keys for your project.

3. **Put the file in the `androidApp/app` directory of the project:**
   - In your project's file structure, place the downloaded `google-services.json` file in the `androidApp/app` directory. This is typically where your Android app's configuration files reside.
   - Your project structure should look like this:

```
└── androidApp
    ├── app
    │   ├── build
    │   ├── build.gradle.kts
    │   ├── google-services.json
    │   ├── proguard-rules.pro
    │   └── src
```

4. **You are all set!**
   - With the `google-services.json` file in place, your project is now configured to use Firebase services. You can start integrating Firebase features into your app.

## How to Set Up Client Secrets

- Navigate to the GCP Credentials (https://console.cloud.google.com/apis/credentials?project=view-capstone-project-918de).
- Download the client secrets file.
- Rename the file to `client_secrets.json`.
- Place the file in the `androidApp/app/src/main/assets/client_secret` directory of the project.