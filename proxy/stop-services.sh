#!/bin/bash
echo "Parando e removendo containers..."
docker stop nginx-proxy-estapar garage-sim 2>/dev/null
docker rm nginx-proxy-estapar garage-sim 2>/dev/null
echo "Containers parados com sucesso."