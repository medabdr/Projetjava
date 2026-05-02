@echo off
echo Compilation des fichiers Java...
javac -cp "lib/*;." *.java

if %ERRORLEVEL% neq 0 (
    echo.
    echo Erreur lors de la compilation.
    pause
    exit /b %ERRORLEVEL%
)

echo Lancement de l'application...
java -cp "lib/*;." Main

pause
