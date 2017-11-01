#! /bin/bash

echo =========== starting build packge.... ==============
rm -rf newfishgame-android-release.apk
rm -rf signed.apk

apkName=newfishgame-android
echo apkName is $apkName

echo =========== build packge.... ==============
./apktool b $apkName

echo =========== move packge.... ==============
cp $apkName/dist/${apkName}.apk ./${apkName}_pack.apk
echo =========== delete dist .... ==============
rm -rf $apkName/dist
rm -rf $apkName/build

echo =========== starting jarsigner.... ==============
echo $apkName

echo =========== start sign ==============
echo jarsigner -verbose -keystore ./fishing.keystore -storepass '```111qqq' -keypass '```111qqq' -signedjar ${apkName}-release.apk ${apkName}_pack.apk 1 -digestalg SHA1 -sigalg MD5withRSA

jarsigner -verbose -keystore ./fishing.keystore -storepass '```111qqq' -keypass '```111qqq' -signedjar ${apkName}-release.apk ${apkName}_pack.apk 1 -digestalg SHA1 -sigalg MD5withRSA

echo =========== over ==============

rm -rf ${apkName}_pack.apk
rm -rf signed.apk

echo =========== over ==============