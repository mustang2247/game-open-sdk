#! /bin/bash
echo =========== starting jarsigner.... ==============

apkName=newfishgame-android
echo $apkName

echo =========== start sign ==============
echo jarsigner -verbose -keystore ./fishing.keystore -storepass '```111qqq' -keypass '```111qqq' -signedjar ${apkName}-release.apk ${apkName}_pack.apk 1 -digestalg SHA1 -sigalg MD5withRSA

jarsigner -verbose -keystore ./fishing.keystore -storepass '```111qqq' -keypass '```111qqq' -signedjar ${apkName}-release.apk ${apkName}_pack.apk 1 -digestalg SHA1 -sigalg MD5withRSA

echo =========== over ==============

rm -rf ${apkName}_pack.apk

echo =========== over ==============