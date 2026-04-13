@echo off
call mvn clean install -DskipTests assembly:single -q
if %errorlevel% neq 0 (
    echo Build failed!
    pause
    exit /b %errorlevel%
)

java -jar target\geektrust.jar sample_input\input1.txt > output.txt

