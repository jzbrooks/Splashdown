## Splashdown

An image searching app similar to unsplash, but different.

### Secrets

You must specify a Flickr API key via a gradle property named `FLICKR_API_KEY`. This can be stored in
a global gradle properties file `~/.gradle/gradle.properties` or be passed as a command line argument
to the build via `-PFLICKR_API_KEY=<value>`. This secret should not be committed to source control!

The Gradle build will fail to assemble or bundle the app if you do not specify this secret because
the app will not function without it. However, unit tests will run without the value specified.
