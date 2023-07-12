## Splashdown

An image searching app similar to unsplash, but different.

### How to Build

This project uses a typical Android Gradle build.

`./gradlew assembleDebug` builds a debug apk
`./gradlew lint` runs android lint
`./gradlew testDebugUnitTest` runs the unit tests

Other available tasks can be listed with `./gradlew tasks`

### Secrets

You must specify a Flickr API key via a gradle property named `FLICKR_API_KEY`. This can be stored in
a global gradle properties file `~/.gradle/gradle.properties` or be passed as a command line argument
to the build via `-PFLICKR_API_KEY=<value>`. This secret should not be committed to source control!

The Gradle build will fail to assemble or bundle the app if you do not specify this secret because
the app will not function without it. However, unit tests will run without the value specified.

### Notes

- There's a good deal of NSFW content coming back from the API. I discovered the `safe_search` option
  on the search endpoint, but it appears the classification system doesn't catch much. I saw several
  photos with a `safety_level` of 0 (via adding the `safety_level` extra to the get recents request)
  but were not SFW. I tried to work around this in the recording in this document, but it wasn't perfect.
  If you run the app, be prepared for the content the service returns.
- I used an RC version of AGP, which I typically wouldn't do in a production environment.
  I keep Beta and Canary AS builds on my personal machine so I can try out new features. AGP
  support for Gradle's jvm toolchains is a nice new feature that is only recently supported.
  If you have issues building via Android Studio, building an apk from the command line should work.
- Searching happens with the keyboard action (search button on the keyboard)
- The error ack strategy accommodates configuration changes that might occur during a snackbar presentation.
  The main value is that if an error occurs just before a configuration change, a new snackbar will be
  shown after the configuration change so the user is more likely to see the error message.
- When an image is tapped in the grid, images crossfade between the low resolution image found
  in the grid (already in memory) and a high resolution image that is loaded from the network.
- r8 rules are tightly tuned — a release apk is 2.6MB and a 1.3MB download size.
- The ImageSearchScreen's preview uses a fake view model to avoid crashing layout previews that
  the real (network dependant) version would induce.

### Room for Improvement

- Search automatically via a debounce mechanism on text input
- A better empty state in the case of an initial network failure
- Move com.jzbrooks.splashdown.data into its own gradle module
- A modal sheet was used to show a higher resolution version of the image upon selection in the grid.
  This may not be the best option if the app grew to be more complex, but it avoids subtleties of arg
  passing in compose-navigation (since it lacks parcelable support) and back stack handling.
- UI Tests via espresso andor UI snapshot testing via
- Log calls are no-ops in release builds, but some mechanism for observability should be put in place
- Paging is simple, but solid. It might be beneficial to consider jetpack paging's compose integration.
- Figure out some way for the grid to be previewable with sample images in the layout inspector

[A quick demo of the basics](https://drive.google.com/file/d/1ypGlD7MDLoH4l_NBPPQWIxuxi-u3pXsI/view?usp=sharing)
