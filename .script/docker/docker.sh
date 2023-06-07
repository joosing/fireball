docker run -d --name=nginx -p 58080:58080 -p 50711:50711 -v files:/app/files nginx
docker stop nginx
docker start nginx
docker exec -it nginx bash

docker compose up --build -d
docker compose down

docker stop $(docker ps -aq)
docker rm $(docker ps -aq) -f

docker cp files/local-1000.dat nginx:/app/files/local-1000.dat # 파일 복사
