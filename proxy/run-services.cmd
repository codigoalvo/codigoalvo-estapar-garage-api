@echo off
setlocal enabledelayedexpansion

:: Configuracoes
set PROXY_NAME=nginx-proxy-estapar
set GARAGE_SIM_NAME=garage-sim
set NGINX_CONFIG_DIR=%~dp0nginx-config
set GARAGE_SIM_IMAGE=cfontes0estapar/garage-sim:1.0.0
set DEFAULT_IP=192.168.1.16

:: Deteccao de IP alternativo (se o padrao nao estiver disponivel)
for /f "tokens=2 delims=:" %%a in ('ipconfig ^| findstr "IPv4" ^| findstr /i "Ethernet Wi-Fi"') do (
    set ALT_IP=%%a
    set ALT_IP=!ALT_IP: =!
)

:: Verifica se o IP padrao esta nas interfaces
ipconfig | findstr "%DEFAULT_IP%" >nul
if %errorlevel% neq 0 (
    if defined ALT_IP (
        set DEFAULT_IP=!ALT_IP!
    ) else (
        echo Nenhuma interface com o IP %DEFAULT_IP% encontrada.
        echo IPs disponiveis:
        ipconfig | findstr "IPv4"
        echo.
    )
)

:: Confirmacao do IP
echo.
echo IP detectado: %DEFAULT_IP%
set /p HOST_IP="Pressione ENTER para usar %DEFAULT_IP% ou digite outro IP: "
if "!HOST_IP!"=="" set HOST_IP=%DEFAULT_IP%

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

:: Verificacao
timeout /t 2 >nul
echo.
echo Containers em execucao:
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

echo.
echo URLs disponiveis:
echo - Garage Simulator: http://localhost:3000
echo - Proxy NGINX:      http://localhost:3003
echo - IP configurado:   %HOST_IP%
echo.