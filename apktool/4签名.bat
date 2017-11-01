echo =========== start sign ==============
@echo off
cd %CD%
jarsigner -verbose -keystore fishing.keystore -storepass ```111qqq -keypass ```111qqq -signedjar newfishgame-android-release.apk newfishgame-android_pack.apk 1 -digestalg SHA1 -sigalg MD5withRSA

echo =========== over ==============

del newfishgame-android_pack.apk

echo click Q exit
pause