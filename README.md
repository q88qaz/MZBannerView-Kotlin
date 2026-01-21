# MZBannerView-Kotlin

A Kotlin implementation of the popular MZBannerView Android banner carousel library. This library provides a smooth, customizable banner view with automatic scrolling, manual swiping, and indicator support.

## Features

- Smooth horizontal scrolling with ViewPager2
- Automatic playback with configurable interval
- Manual swipe support
- Customizable indicators (dots, numbers, etc.)
- Support for various view types (ImageView, custom layouts)
- Lifecycle-aware (handles pause/resume automatically)
- Memory efficient with view recycling
- Highly customizable appearance and behavior

## Download

[![](https://jitpack.io/v/yourusername/MZBannerView-Kotlin.svg)](https://jitpack.io/#yourusername/MZBannerView-Kotlin)

Add it to your build.gradle with:
```gradle
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

And:

```gradle
dependencies {
    implementation 'com.github.yourusername:MZBannerView-Kotlin:Tag'
}
```

Replace `yourusername` with your GitHub username and `Tag` with the latest release tag.

## Usage

### XML Layout

```xml
<com.yourpackage.MZBannerView
    android:id="@+id/banner_view"
    android:layout_width="match_parent"
    android:layout_height="200dp" />
```

### Kotlin Code

```kotlin
val bannerView = findViewById<MZBannerView>(R.id.banner_view)

// Setup with data
bannerView.apply {
    setPages({ position ->
        // Create your view for each page
        ImageView(this@MainActivity).apply {
            scaleType = ImageView.ScaleType.CENTER_CROP
            // Load your image here
        }
    }, data) { position ->
        // On page click listener
        Toast.makeText(context, "Page $position clicked", Toast.LENGTH_SHORT).show()
    }
    
    // Configure auto play
    isAutoPlay(true)
    setDelayedTime(3000) // 3 seconds interval
    
    // Set indicator
    setIndicatorAlign(IndicatorAlign.CENTER)
    setIndicatorStyle(IndicatorStyle.DOT)
    
    // Start banner
    start()
}
```

## Customization

### Auto Play Configuration
```kotlin
bannerView.isAutoPlay(true)           // Enable/disable auto play
bannerView.setDelayedTime(5000)       // Set scroll delay in ms
```

### Indicator Configuration
```kotlin
bannerView.setIndicatorStyle(IndicatorStyle.NUMBER)  // Number indicator
bannerView.setIndicatorAlign(IndicatorAlign.RIGHT)   // Align to right
bannerView.setIndicatorColor(Color.RED, Color.GRAY)  // Selected/unselected colors
```

### Page Transformer
```kotlin
bannerView.setPageTransformer { page, position ->
    // Custom page transformation effect
    page.rotationY = position * 30
}
```

## License

MIT License

Copyright (c) 2026

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.