# Developer notes

This document is meant for the developers and should not be important to the users of Katarina.

## Keeping the dependencies up-to-date
To keep the bot working (e.g. with the Discord API), dependency updates should be updated continuously.
Run `gradle dependencyUpdates` to search for updates.

The gradle wrapper can be updated with something like `./gradlew wrapper --gradle-version 6.7.1`. Running this command twice will also replace the existing jar and more stuff. See https://docs.gradle.org/current/userguide/gradle_wrapper.html#sec:upgrading_wrapper for more information.

## Danbooru module maintenance
Now and then, the page limits for some clients need to be updated with new values to increase the randomness.
