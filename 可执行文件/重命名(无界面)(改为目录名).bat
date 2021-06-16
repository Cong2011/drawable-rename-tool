@Echo Off
For /f "tokens=*" %%i in ('dir /ad /b /s "%~dp0"') do (
For /f "tokens=*" %%j in ('dir /ad /b /s "%%i"') do (
For /f "tokens=*" %%k in ('dir /a-d /b /s "%%j\*.png"') do (
Ren "%%k" "%%~nxi.png"
)))
Pause