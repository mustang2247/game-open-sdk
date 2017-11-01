@echo off 
cd %CD%
echo =========== starting build packge.... ==============

apktool b newfishgame-android

echo =========== ren.... ==============

ren newfishgame-android\dist\newfishgame-android.apk newfishgame-android_pack.apk

echo =========== move.... ==============
move newfishgame-android\dist\newfishgame-android_pack.apk

echo =========== remove ==============
rmdir newfishgame-android\dist

jarsigner -verbose -keystore fishing.keystore -storepass ```111qqq -keypass ```111qqq -signedjar newfishgame-android-release.apk newfishgame-android_pack.apk 1 -digestalg SHA1 -sigalg MD5withRSA

del newfishgame-android_pack.apk

echo =========== over ==============
echo click Q exit
pause