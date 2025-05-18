@echo off
setlocal enabledelayedexpansion

:: Configurações
set PROXY_NAME=nginx-proxy-estapar
set GARAGE_SIM_NAME=garage-sim
set NGINX_CONFIG_DIR=%~dp0nginx-config
set GARAGE_SIM_IMAGE=cfontes0estapar/garage-sim:1.0.0

:: Detecção de IP com prioridade para Ethernet/Wi-Fi
for /f "tokens=1-2 delims=:" %%a in ('ipconfig ^| findstr "IPv4"') do (
    set interface=%%a
    set ip=%%b
    :: Remove espaços
    set interface=!interface: =!
    set ip=!ip: =!

    :: Filtra apenas interfaces físicas
    echo !interface! | findstr /i "Ethernet Wi-Fi" >nul
    if !errorlevel! equ 0 (
        set HOST_IP=!ip!
        goto ip_detected
    )
)

:ip_detected
echo.
echo IPs detectados:
ipconfig | findstr /i "IPv4" | findstr /i "Ethernet Wi-Fi"
echo.

:: Confirmação do IP
set /p HOST_IP="Digite o IP que deseja usar (padrão %HOST_IP%): "
if "!HOST_IP!"=="" (
    echo Usando IP detectado automaticamente: %HOST_IP%
) else (
    echo Usando IP manual: !HOST_IP!
)

:: Remove containers existentes
echo.
echo Removendo containers antigos...
docker rm -f %PROXY_NAME% 2>nul
docker rm -f %GARAGE_SIM_NAME% 2>nul

:: Inicia o NGINX
echo.
echo Iniciando NGINX Proxy (%PROXY_NAME%)...
docker run -d ^
    --name %PROXY_NAME% ^
    -p 3003:3003 ^
    -v "%NGINX_CONFIG_DIR%:/etc/nginx/conf.d" ^
    --restart unless-stopped ^
    nginx

:: Inicia o Garage Simulator
echo.
echo Iniciando Garage Simulator (%GARAGE_SIM_NAME%)...
docker run -d ^
    --name %GARAGE_SIM_NAME% ^
    -p 3000:3000 ^
    --add-host=localhost:%HOST_IP% ^
    %GARAGE_SIM_IMAGE%

:: Verificação
timeout /t 2 >nul
echo.
echo Containers em execução:
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

echo.
echo URLs disponíveis:
echo - Garage Simulator: http://localhost:3000
echo - Proxy NGINX:      http://localhost:3003
echo - IP configurado:   %HOST_IP%
echo.
pause