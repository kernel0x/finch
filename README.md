# Finch
Finch is an android library built on top of OkHttp3, which is responsible for intercepting all the network calls and for display in UI.

Can be plugged in to any app which uses okhttp in their networking stack.

![Finch](assets/finch.gif)

## Gradle Dependency

Add it in your root build.gradle at the end of repositories:

````java
	allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }
		}
	}
````

Add the dependency:

````java
	dependencies {
		debugImplementation 'com.github.kernel0x.finch:library:1.0.1'
		releaseImplementation 'com.github.kernel0x.finch:library-no-op:1.0.1'
	}
````

## How to works

Create an instance of FinchInterceptor and add it to the method addInterceptor in building OkHttp client.

```java
	OkHttpClient okHttpClient = new OkHttpClient.Builder()
		.addInterceptor(new FinchInterceptor(context))
		.build();
```

## Releases

Checkout the [Releases](https://github.com/kernel0x/finch/releases) tab for all release info.
