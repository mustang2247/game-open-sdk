echo =========== start sign ==============
@echo off
cd %CD%
jarsigner -verbose -keystore fishing.keystore -storepass com.hoolai.jjby -keypass com.hoolai.jjby -signedjar newfishgame-android-release.apk newfishgame-android_pack.apk com.hoolai.jjby -digestalg SHA1 -sigalg MD5withRSA

echo =========== over ==============

del newfishgame-android_pack.apk

echo click Q exit
pause