# Revive — Notification Cacher

Revive is a privacy-focused Android app built with **Kotlin + Jetpack Compose** that captures, stores, and organizes your WhatsApp notifications — even if messages are deleted later.

## ✨ Features
- 📨 Automatically logs WhatsApp notifications (including deleted messages)
- 💬 Displays messages grouped by sender
- 🧹 Periodic cleanup of old messages (configurable)
- 🔒 Works fully offline, all data stored locally
- 🛠️ Built with Hilt, Room, WorkManager, and Compose

## 🧩 Tech Stack
- **Kotlin**
- **Jetpack Compose**
- **Room Database**
- **Hilt Dependency Injection**
- **WorkManager** (for background cleanup)
- **Coil 3** (for image handling)

## 📱 Screens
- **Home Screen** — list of senders
- **Message Screen** — view, select, and delete messages
- **Splash Screen** — app startup
- **Permission Dialog** — request notification access

## 🔐 Permissions
To function properly, Revive needs **Notification Access** permission.  
When you open the app, it will prompt you to grant access.

## ⚙️ Build
1. Clone the repo  
   ```bash
   git clone https://github.com/rohanughade/Revive.git
