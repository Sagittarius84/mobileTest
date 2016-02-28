# Git submodule (Please read) #
## Notes ##

* Always publish (push) the submodule change before publishing (push) the change to the superproject that references it. [SO](http://stackoverflow.com/questions/1979167/git-submodule-update)

* Eingabe im masterproject root(git erkennt die submodule aus der .gitmodules):  
    * *git submodule init*
* Dann zum Update:  
    * *git submodule update*       (wahlweise auch --remote --rebase)
* Späteres updaten dann über:
    * normales pullen aus dem Subprojekt oder
    * *git submodule foreach git pull*

# README #
InstaList. ShoppingList for Android.

## Reihenfolge ##
1. git clone git@bitbucket.org:fhnoorg/einkaufsliste.git
2. git submodule update --init --recursive
3. In jedem submodule ordner einmal git checkout master ausführen (löst den detached head auf)

## Ablauf Gruppe erstellen/Member hinzufügen ##
[YouTube Link](https://www.youtube.com/watch?v=fwmNDBMlAns)
##[Letzer Commit vor Abgabe](https://bitbucket.org/fhnoorg/einkaufsliste/get/51ca908.tar.gz) (Direkter Download Link) oder Commithash: 51ca908##

## Documentation##
[Documentation](https://bitbucket.org/fhnoorg/einkaufsliste/wiki/Dokumentation)
## How to compile ##

The repository contains a minimal Android Studio Project. Just clone and build the "app"-Module in Android Studio. The first build may need a internet connection as some libraries get downloaded by gradle.

## Dependencies ##

See app/build.gradle for an always up to date dependency-information.

* EventBus by GreenRobot ([Source Code](https://github.com/greenrobot/EventBus), Apache2-license)
* SugarORM by "Satyan" ([Website](https://satyan.github.io/sugar/index.html), [License](https://github.com/satyan/sugar/blob/master/LICENSE))
* Android Support Library by Google ([Source Code](https://android.googlesource.com/platform/frameworks/support.git/), Apache2-license)
    * RecyclerView-library
    * Design-library

* Note that Autobahn Android library is added manually and not via gradle