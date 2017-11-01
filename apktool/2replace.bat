echo =========== starting ReplaceStr of XML.... ==============
@echo off
cd %CD%

echo.请输入原始字符串，比如：air.com.hoolai.jjby.provider

set /p input_source=

echo.请输入替换字符串，如：replaceByHoolaiPackageName.provider

set /p input_dist=

set xmlName=AndroidManifest

ReplaceStr %CD%\newfishgame-android\%xmlName%.xml %input_source% %input_dist%

echo =========== over ==============
echo 再点一下就结束了--小Q
pause