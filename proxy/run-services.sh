#!/bin/bash

# Configurações
PROXY_NAME="nginx-proxy-estapar"
GARAGE_SIM_NAME="garage-sim"
NGINX_CONFIG_DIR="./nginx-config"
GARAGE_SIM_IMAGE="cfontes0estapar/garage-sim:1.0.0"

# Detecção de IP
echo -e "\nInterfaces de rede disponíveis:"
ip -o -4 addr show | awk '{print $2": "$4}'

read -p "Digite o IP que deseja usar: " HOST_IP
while [[ -z "$HOST_IP" ]]; do
  echo "IP não pode ser vazio!"
  read -p "Digite o IP que deseja usar: " HOST_IP
done

# Remove containers existentes
echo -e "\nRemovendo containers antigos..."
docker rm -f $PROXY_NAME 2>/dev/null
docker rm -f $GARAGE_SIM_NAME 2>/dev/null

# Inicia o NGINX
echo -e "\nIniciando NGINX Proxy ($PROXY_NAME)..."
docker run -d \
    --name $PROXY_NAME \
    -p 3003:3003 \
    -v "$(pwd)/nginx-config:/etc/nginx/conf.d" \
    --restart unless-stopped \
    nginx

# Inicia o Garage Simulator
echo -e "\nIniciando Garage Simulator ($GARAGE_SIM_NAME)..."
docker run -d \
    --name $GARAGE_SIM_NAME \
    -p 3000:3000 \
    --add-host="localhost:$HOST_IP" \
    $GARAGE_SIM_IMAGE

# Verificação
sleep 2
echo -e "\nContainers em execução:"
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

echo -e "\nURLs disponíveis:"
echo "- Garage Simulator: http://localhost:3000"
echo "- Proxy NGINX:      http://localhost:3003"
echo "- IP configurado:   $HOST_IP"