@echo off
echo Parando e removendo containers...
docker stop nginx-proxy-estapar garage-sim 2>nul
docker rm nginx-proxy-estapar garage-sim 2>nul
echo Containers parados com sucesso.
pause