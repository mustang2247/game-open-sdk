echo =========== starting ReplaceStr of XML.... ==============
@echo off
cd %CD%

echo.������ԭʼ�ַ��������磺air.com.hoolai.jjby.provider

set /p input_source=

echo.�������滻�ַ������磺replaceByHoolaiPackageName.provider

set /p input_dist=

set xmlName=AndroidManifest

ReplaceStr %CD%\newfishgame-android\%xmlName%.xml %input_source% %input_dist%

echo =========== over ==============
echo �ٵ�һ�¾ͽ�����--СQ
pause