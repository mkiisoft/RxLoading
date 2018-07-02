# RxLoading - Material Design
_______________
[![Awesome](https://cdn.rawgit.com/sindresorhus/awesome/d7305f38d29fed78fa85652e3a63e154dd8e8829/media/badge.svg)](https://github.com/mkiisoft/RxLoading) [![GitHub version](https://d25lcipzij17d.cloudfront.net/badge.svg?id=gh&type=6&v=1.0b&x2=0)](https://github.com/mkiisoft/JokeGenerator) [![Android](https://img.shields.io/badge/language-Android-blue.svg)](https://github.com/mkiisoft/JokeGenerator) ![API](https://img.shields.io/badge/API-22%2B-brightgreen.svg?style=flat)

Loading screen using RxJava with full customization using colors and orientations.

[![RxLoading](https://image.ibb.co/ktepbd/Rx_Loading.gif)](https://github.com/mkiisoft/RxLoading)

# Install
_______________

## Android Studio:

Create proyect > Clone library > File > New > Import Module... > Finish

Open Project "settings.gradle"

include ':app', ':rxloading'

Open App "build.gradle"

implementation project(':rxloading')

# Examples
_______________

- Default (transparent background - white dots)

```
<com.mkiisoft.rxloading.RxLoading
        android:id="@+id/progress"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />
```

- Match Design (select any color - dots select any color)

```
    <com.mkiisoft.rxloading.RxLoading
        android:id="@+id/progress"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="@android:color/holo_orange_dark"
        app:color="@android:color/black" />
```

- Orientation - Opacity (orientation: vertical | horizontal) (opacity: changes dots opacity)
```
    <com.mkiisoft.rxloading.RxLoading
        android:id="@+id/progress"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:orientation="vertical"
        app:opacity="0.5" />
```

- Use android:alpha to change the entire view opacity

# Changelog
_______________

- First Version v1.0

# Features
_______________

* Fully Customizable
* Change opacity
* Change loading color
* Change background color
* Change loading orientation
* Wrap and Match parent

# © 2018 Mariano Zorrilla
