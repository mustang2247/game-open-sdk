del newfishgame-android
del newfishgame-android_pack.apk
del signed.apk

echo =========== starting Unpacking packge.... ==============
@echo off
cd %CD%

set apkName=newfishgame-android

apktool d -f *.apk

echo =========== over ==============
echo 再点一下就结束了--小Q
pause