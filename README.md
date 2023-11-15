# Finch
Finch offers a customizable debug menu for Android app development. It does not affect production code. Developers can easily add their own custom debugging features with simple steps.

<img src="assets/finch.gif" width="282" height="518"/>

## Gradle Dependency

Add it in your root build.gradle at the end of repositories:

````java
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
````

Pick a UI implementation and add the dependency:
* **ui-activity** - The debug menu as a new screen.
* **ui-bottom-sheet** - The debug menu as a modal bottom sheet.
* **ui-dialog** - The debug menu as a modal dialog.
* **ui-drawer** - The debug menu as a side navigation drawer.
* **ui-view** - The debug menu as a view.
* **noop** - For release build.

````java
dependencies {
    debugImplementation 'com.github.kernel0x.finch:ui-drawer:2.1.2'
    releaseImplementation 'com.github.kernel0x.finch:noop:2.1.2'
    debugImplementation 'com.github.kernel0x.finch:log-okhttp:2.1.2'
    releaseImplementation 'com.github.kernel0x.finch:log-okhttp-noop:2.1.2'
    debugImplementation 'com.github.kernel0x.finch:log:2.1.2'
    releaseImplementation 'com.github.kernel0x.finch:log-noop:2.1.2'
}
````

## How to works

Initialize an instance of Finch (preferably in the Application's onCreate() method)
````java
Finch.initialize(this)
````

Next, you need to add which components you want to display in the debug menu. Optionally, you can additionally configure logging and interception network events (with OkHttp).

### Logging

To add log messages in Debug Menu simple calling Finch.log() and add FinchLogger to Configuration object.

### OkHttp

Add FinchOkHttpLogger.logger to the method addInterceptor in building OkHttp Client and add FinchOkHttpLogger to Configuration object.

```java
OkHttpClient.Builder()
    .addInterceptor(FinchOkHttpLogger.logger as Interceptor)
    .build()
```

### Example initialize

Here is a minimal example that should work for most projects

```java
Finch.initialize(
    application = this,
    configuration = Configuration(
        logger = FinchLogger,
        networkLoggers = listOf(FinchOkHttpLogger)
    ),
    components = arrayOf(
        Header(
            title = getString(R.string.app_name),
            subtitle = BuildConfig.APPLICATION_ID,
            text = "${BuildConfig.BUILD_TYPE} v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
        ),
        Padding(),
        Label("Tools", Label.Type.HEADER),
        DesignOverlay(),
        AnimationSpeed(),
        ScreenCaptureToolbox(),
        Divider(),
        Label("Logs", Label.Type.HEADER),
        LifecycleLogs(),
        NetworkLogs(),
        Logs(),
        Divider(),
        Label("Other", Label.Type.HEADER),
        DeviceInfo(),
        AppInfo(),
        DeveloperOptions(),
        ForceCrash()
    )
)
```

## Releases

Checkout the [Releases](https://github.com/kernel0x/finch/releases) tab for all release info.
