# Instalist #
[![Build Status](https://travis-ci.org/InstaList/instalist-android.png)](https://travis-ci.org/InstaList/instalist-android)

InstaList. ShoppingList for Android.

## Project Download Order ##
1. git clone git@bitbucket.org:fhnoorg/einkaufsliste.git
2. git submodule update --init --recursive
3. In each submodule execute git checkout master  (prevents the detached head state)

## How to compile ##

The repository contains a minimal Android Studio Project. Just clone and build the "app"-Module in Android Studio. The first build may need a internet connection as some libraries get downloaded by gradle.

# Git submodule (Please read) #
## Notes ##

* Always publish (push) the submodule change before publishing (push) the change to the superproject that references it. [SO](http://stackoverflow.com/questions/1979167/git-submodule-update)

## Dependencies ##

See app/build.gradle for an always up to date dependency-information.

* EventBus by GreenRobot ([Source Code](https://github.com/greenrobot/EventBus), Apache2-license)
* Android Support Library by Google ([Source Code](https://android.googlesource.com/platform/frameworks/support.git/), Apache2-license)
    * RecyclerView-library
    * Design-library