# Introduction
[![Platform](https://img.shields.io/badge/platform-android-brightgreen.svg)](https://developer.android.com/index.html)
![API](https://img.shields.io/badge/Min--SDK-21-yellowgreen)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)

Working with location is one of the most common tasks in the development process of mobile apps.
Therefore, questions about what to use to achieve more precise results, maximum stability in operation and 
simplicity of implementation frequently arise during the development.

EasyLocation is built to ease this frequent task by using just a few lines of code, But providing a powerful, wide and compact features too.

#Setup
Gradle:
```
implementation 'com.linkdev.easylocation:easylocation:1.0.0'
```
Maven:
```
<dependency>
  <groupId>com.linkdev.easylocation</groupId>
  <artifactId>easylocation</artifactId>
  <version>1.0.0</version>
  <type>pom</type>
</dependency>
```

# How to use EasyLocation
There are multiple ways to use EasyLocation depending on your business using:
1.  EasyLocationBaseFragment
2.  EasyLocation

##EasyLocationBaseFragment
* The fastest way, just make the fragment extend from the `EasyLocationBaseFragment`.
* Will handle the permissions checks and settings for you.

Just extend `EasyLocationBaseFragment` and call `getLocation` whenever you need the location
```kotlin
    getLocation(
        TimeLocationOptions(),
        LocationRequestType.ONE_TIME_REQUEST,
        500
    )
```
You will recieve the callbacks in implemented methods
```kotlin
    override fun onLocationRetrieved(location: Location) {
        // TODO Use non-null retrieved location
    }

    override fun onLocationRetrievalError(locationResultError: LocationResultError) {
        // TODO Handle location different errors
        when (locationResultError.errorCode) {
            LocationErrorCode.LOCATION_SETTING_DENIED,
            LocationErrorCode.LOCATION_PERMISSION_DENIED,
            LocationErrorCode.UNKNOWN_ERROR,
            LocationErrorCode.TIME_OUT ->
                Toast.makeText(mContext, locationResultError.errorMessage, Toast.LENGTH_LONG)
                    .show()
            LocationErrorCode.PROVIDER_EXCEPTION ->
                Toast.makeText(mContext, locationResultError.exception?.message, Toast.LENGTH_LONG)
                    .show()
        }
    }
```
If you prefer composition check out [EasyLocation](#easylocation)

## EasyLocation
* If you want to handle permissions on your own.
Using the `EasyLocation` Builder class you initialize the object and call requestLocationUpdates like below:
```kotlin
    mEasyLocation = EasyLocation.Builder(mContext, TimeLocationOptions())
        .setLocationRequestTimeout(500)
        .setLocationRequestType(LocationRequestType.ONE_TIME_REQUEST)
        .build()

    mEasyLocation.requestLocationUpdates(lifecycle)
        .observe(this, this::onLocationStatusRetrieved)
```
`requestLocationUpdates()` returns a `LiveData` object in which you will recieve future location updates based on provided `LocationOptions`.


# Contribute
Contributions and contributors are always welcome! Help us make DragDismiss better and give back to the community.

Found an issue or feel like contributing? Please use [Github][issues]
Have a question? Please use Stackoverflow with tag [DragDismissLayout][stackoverflow]

# License
    Copyright 2020-present Link Development

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

 [issues]: https://github.com/DragDismissLayout/issues
 [stackoverflow]: http://stackoverflow.com/questions/tagged/DragDismissLayout
