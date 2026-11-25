# COMP3330_CAFE_G11
Hello! We are Nick Ze Kun Lei, Karina Qiu, Tony Junxiang Wu, Nicole Xie, Rig Ved Gautam and Hadi Baher Sayed Mohamed. We are building CAFE - Cool App for Events: a mobile application allowing event hosts to manage participants and events logistics!

# COMP3330 CAFE

## Getting Started

### Prerequisites

Before running the project, ensure the following are installed:

- Android Studio (latest stable version recommended)
- JDK 17
- Android SDK
- Git

---

## Clone the Repository

```bash
git clone https://github.com/Nicole-Xie-HKU/COMP3330_CAFE_G11.git
cd COMP3330_CAFE_G11
```

---

## Switch to the Latest Main Branch

```bash
git checkout main
git pull origin main
```

---

## Open the Project in Android Studio

1. Open Android Studio.
2. Click **Open**.
3. Select the project root folder:

```text
COMP3330_CAFE_G11
```

4. Verify that the selected folder contains:

```text
app/
gradle/
build.gradle.kts
settings.gradle.kts
gradlew
gradlew.bat
```

5. Wait for Android Studio to import the project.

---

## Sync Gradle

If Android Studio displays:

```text
Gradle files have changed
```

Click:

```text
Sync Now
```

Alternatively, manually sync through:

```text
File → Sync Project with Gradle Files
```

---

## Configure JDK

1. Open:

```text
File → Settings → Build, Execution, Deployment → Build Tools → Gradle
```

2. Set **Gradle JDK** to:

```text
JDK 17
```

or Android Studio's **Embedded JDK**.

---

## Install Android SDK Components

1. Open:

```text
File → Settings → Android SDK
```

2. Install any missing:
    - Android SDK Platform
    - Android SDK Build Tools
    - Android SDK Command-line Tools

---

## Build the Project

Using Android Studio:

```text
Build → Make Project
```

Or from a terminal:

### Windows

```bash
gradlew.bat build
```

### macOS/Linux

```bash
./gradlew build
```

---

## Run the Application

1. Connect an Android device with USB debugging enabled or start an Android Emulator.
2. Click the **Run** button in Android Studio.

---

## How to Try CAFE

Use two phones (or a phone and an emulator). Register one account per role.

1. Organizer: press "Create Event" and enter a date. Open My Events, the event, Sessions, then "Add Session". Tick "Fixed time (never shifted)" for sessions that must not move.
2. Organizer: open Members and read the crew invite code.
3. Crew: open Team and enter the invite code. Organizer: on Members, type a team name and press "Save Team".
4. Organizer: open a session, then Tasks, and add a task for the crew member.
5. Crew: open My Tasks and tick the task when it is done.
6. Participant: open an event, then a session, for directions, calendar and share.
7. Participant: show the QR code. Crew: open the session and press "Scan Check-ins" (needs a real camera).
8. Organizer: open a session, press "Running Late or Early", check the preview and confirm. The other phones update by themselves.
9. Participant: press "Need Help" or shake the phone. Crew: open the event, then Help Requests, and press "I Will Go".

Task reminders need the notification permission and may arrive a few minutes late.

---

## Working on New Features

Before starting new work:

```bash
git checkout main
git pull origin main
```

Create a new branch:

```bash
git checkout -b your-name-feature
```

Example:

```bash
git checkout -b nicole-shift-management
```

After committing your changes:

```bash
git push -u origin your-name-feature
```

Create a Pull Request to merge your branch into `main`.

---

## Keeping Your Local Repository Updated

To get the latest changes:

```bash
git checkout main
git pull origin main
```

---

## Troubleshooting

### "Code insight unavailable (related Gradle project not linked)"

Ensure that you opened the project root folder containing:

```text
settings.gradle.kts
build.gradle.kts
```

Then either:

```text
File → Sync Project with Gradle Files
```

or click:

```text
Link Gradle Project
```

and select the project's `settings.gradle.kts` file.

---

### Gradle Sync Fails

Try:

```bash
gradlew clean
gradlew build
```

Then reopen Android Studio and sync Gradle again.

---

### Build Fails After Cloning

Make sure:

- Android Studio is up to date.
- JDK 17 is selected.
- Required Android SDK components are installed.
- Gradle sync completes successfully before running the app.

---

## Repository Workflow

- `main` contains the latest stable version of the project.
- Create a separate feature branch for new work.
- Submit Pull Requests to merge changes into `main`.
- Do not commit directly to `main`.