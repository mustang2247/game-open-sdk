#! /bin/bash
echo =========== delete files.... ==============

rm -rf newfishgame-android
rm -rf newfishgame-android-release.apk
rm -rf signed.apk

echo =========== starting Unpacking packge.... ==============

apkName=newfishgame-android.apk
echo $apkName
./apktool d -f $apkName

echo =========== over ==============
echo 再点一下就结束了--小Q