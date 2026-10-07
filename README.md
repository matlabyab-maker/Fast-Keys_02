# Fast_KK_1

**Fast_KK_1 v1.49** — پروژه مستقل کیبورد اندروید و ابزارهای کنترل ماوس/دسترسی مرتبط با آن.

## وضعیت این Repository

این مخزن از نسخهٔ فعلی پروژه برای شروع یک Repository تازه آماده شده است. تاریخچهٔ توضیحات نسخه‌های قبلی در `docs/history/` نگه‌داری شده و به کد برنامه دست زده نشده است.

### مشخصات فعلی

- `applicationId`: `com.fastkeyboard.nova`
- `versionCode`: `44`
- `versionName`: `1.49`
- `minSdk`: 23
- `targetSdk`: 35
- `compileSdk`: 35
- Java toolchain: 17
- Gradle مورد استفاده در GitHub Actions: 8.11.1

## بخش‌های اصلی پروژه

- کیبورد Fast_KK_1 و سرویس Input Method
- سرویس‌های Accessibility و کنترل ماوس
- پنجره/کنترل‌های ماوس و Quick Settings مرتبط
- منابع و تنظیمات Accessibility
- فهرست پیشنهادهای فارسی و انگلیسی در `app/src/main/assets/`
- Workflow ساخت خودکار APK در `.github/workflows/android.yml`

## ساختار

```text
Fast_KK_1/
├── app/
├── .github/workflows/android.yml
├── docs/history/
├── build.gradle
├── settings.gradle
├── gradle.properties
├── .gitignore
└── README.md
```

## Build با GitHub Actions

Workflow موجود با GitHub Actions اجرا می‌شود و به‌صورت خودکار یا دستی قابل اجراست. محیط Build از JDK 17، Android SDK 35 و Gradle 8.11.1 استفاده می‌کند.

خروجی Debug با نام Artifact زیر منتشر می‌شود:

`Fast_KK_1-debug-apk`

## نکته مهم برای Repository جدید

این بسته شامل Git history قبلی نیست. پس از ایجاد Repository جدید، فایل‌ها را به‌عنوان اولین Commit پروژه جدید Push کنید.

چیدمان، ظاهر و قابلیت‌های برنامه در این مرحله عمداً تغییر داده نشده‌اند؛ تغییرات بعدی باید فقط بر اساس درخواست مشخص و مرحله‌به‌مرحله انجام شوند.


## ریموت لمسی Wi‑Fi

### v1.49
- پنل عمودی «Close / Drag» پنجره کنترل موس با تصویر ارسالی کاربر جایگزین شد؛ عملکرد لمسی Close و Drag حفظ شده است.

از نسخه 1.43، گیرنده Wi‑Fi ریموت لمسی داخلی شده است. پورت TCP برابر `38555` است و پروتکل آن با Repository مستقل `Fast_Remote_1` یکسان است.

برای استفاده، سرویس «موس سیستمی» را در Accessibility فعال کنید. سپس در برنامه Fast Keyboard از «اطلاعات اتصال ریموت Wi‑Fi» آدرس IP و پورت را ببینید و همان IP را در `Fast_Remote_1` وارد کنید. هر دو دستگاه باید روی یک شبکه Wi‑Fi باشند.


## پنجره کنترل موس v1.49
تصویر مرجع جدید 430×287 استفاده می‌شود و فقط Touchpad، Left Click، Drag، Point Zoom و Close فعال هستند.


### v1.49
- Quick Settings now includes a dedicated mouse-window resize tile.
- Mouse control window keeps the upper-right resize key and adds an upper-left resize key.
- Predictor persistence is moved off the keyboard UI thread and suggestion debounce is slightly relaxed to reduce typing stalls.


## v1.50
- پنجره موس اجراشده از کیبورد بدون گرفتن فوکوس از IME عمل می‌کند تا لمس دکمه‌های موس باعث بسته‌شدن کیبورد نشود.
- پنجره موس Quick Settings با همان تصویر و دو دستگیره تغییر اندازه گوشه‌ای پنجره موس کیبورد هماهنگ شد.
- دکمه دوم موس در Quick Settings (تغییر اندازه) حذف شد و فقط دکمه اصلی موس باقی ماند.
- روی تاچ‌پد: لمس اول کلیک چپ است؛ لمس دوم همراه با نگه‌داشتن و کشیدن، حرکت انتخاب متن را شبیه Drag انتخاب در کامپیوتر اجرا می‌کند.


## v1.50 mouse fix
- Keyboard-launched mouse now uses the same Accessibility overlay as Quick Settings; mouse controls no longer use an IME PopupWindow, preventing mouse taps from dismissing the keyboard.
- Quick Settings mouse resize corners now resize with the fixed 430:287 aspect ratio and stable corner anchoring.
- Keyboard and Quick Settings use the same mouse window implementation.


### v1.51 — mouse button no longer dismisses keyboard
- The mouse overlay now explicitly stays above the IME using `FLAG_ALT_FOCUSABLE_IM` and in-screen layout flags.
- Opening the mouse from the keyboard requests the IME to remain visible immediately and again shortly afterward for Android builds that momentarily dismiss it.
- Mouse functionality and the existing Quick Settings mouse remain unchanged.

### v1.51 — mouse/keyboard layer fix
- When the mouse is opened from the keyboard, the IME is restored first and the mouse accessibility overlay is added afterward.
- Removed repeated delayed IME-show calls that could raise the keyboard above the mouse window or finish the IME on some Android builds.
- The mouse overlay remains the same control artwork and behavior.
## v1.52 — Remote cursor visibility
- When a Remote_For_Fast_Keys connection completes its `FKREMOTE 1` handshake, the desktop mouse cursor is now shown immediately.
- The cursor is independent of the mouse control window; it no longer waits for the first MOVE/PAGE command.
- Existing remote commands and mouse-window behavior are unchanged.


## v1.53 remote page scrolling
- Page Up/Page Down short taps now make a modest single movement.
- Holding either remote page button uses small, sequential accessibility gestures for smoother continuous scrolling.
- The remote sends an explicit stop packet on release/cancel so continuous scrolling stops immediately.


## Latest fix
- Corrected button hit areas to match the supplied 1920×1080 menu artwork.
- Remote Point Zoom and Back commands are sent on touch-down.
- Page Up/Down use smaller, slower repeated steps while held.
- Fast Keys prefers Android accessibility semantic scroll actions for smoother scrolling, with a gentle gesture fallback.
- Point Zoom on Android 7–10 uses the Accessibility magnification controller immediately when available.


## v1.55 build fix
- Fixed the Point Zoom compile error by using the correct nested `AccessibilityService.MagnificationController` type.
- No layout or Remote behavior was changed in this build-fix release.


## v1.56
- Fixed the compile error in `MouseAccessibilityService`: `MagnificationController.isMagnificationEnabled()` is not available in the Android API used by the project. Point Zoom now relies on the service's own `magnifierEnabled` state and uses the Accessibility magnification controller without calling the unavailable method.
