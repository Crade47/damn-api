#!/bin/bash

echo -n "Action? [build/run/seeddb/stop/curltest/lombokstuff]"
read -r action 

services=(
  damn-api-gateway
  damn-common
  damn-data-manager
  damn-map-service
  damn-route-manager
  damn-route-provider
  damn-route-scorer
)

seeding_files=(
    /archieve/location.sql
    /archieve/luas_route.sql
    /archieve/station.sql
    /route_luas.sql
    /shape_luas.sql
    /stop_luas.sql
    /stop_time_luas.sql
    /trip_luas.sql

)

start_service() {
  service_name=$1
  jar_path="$service_name/target/$service_name-0.0.1-SNAPSHOT.jar"

  if [ ! -f "$jar_path" ]; then
    echo "JAR not found for $service_name! Make sure you built the service."
    return
  fi

  echo "Starting $service_name..."
  nohup java -jar "$jar_path" > "logs/$service_name.log" 2>&1 &  
  echo $! > "pids/$service_name.pid"
}

stop_services() {
  echo "Stopping all services..."
  for service in "${services[@]}"; do
    if [ -f "pids/$service.pid" ]; then
      pid=$(cat "pids/$service.pid")
      echo "Stopping $service (PID: $pid)..."
      kill "$pid"
      rm "pids/$service.pid"
    fi
  done
  echo "All services stopped."
}

if [ "$action" == "build" ]; then
  echo -n "Skip tests? [y/N] "
  read -r skip_test
  for service in "${services[@]}"; do
    echo "Building $service..."
    if [ "$skip_test" == "y" ]; then
      mvn -f "$service" clean package -Dmaven.test.skip=true
    else
      mvn -f "$service" clean package
    fi
  done
elif [ "$action" == "seeddb" ]; then
  # location -> stations -> luas
  docker exec -i damn_map_postgres psql -U damn -d damn_maps < ./data/postgres/archieve/station.sql
  docker exec -i damn_map_postgres psql -U damn -d damn_maps < ./data/postgres/archieve/luas_route.sql

  for file in "${seeding_files[@]}"; do
    docker exec -i damn_map_postgres psql -U damn -d damn_maps < "./data/postgres$file"
  done

elif [ "$action" == "run" ]; then
  mkdir -p logs pids
  for service in "${services[@]}"; do
    start_service "$service"
    sleep 2  # Optional: Give some time for each service to start
  done
  echo "All services started!"
elif [ "$action" == "stop" ]; then
  stop_services
elif [ "$action" == "curltest" ]; then
  curl -X POST "http://localhost:8081/api/routes-manager/get-routes" \
       -H "Content-Type: application/json" \
       -d '{
             "startLocation": {
                 "latitude": 53.281490479443086,
                 "longitude": -6.2407756517351025
             },
             "endLocation": {
                 "latitude": 53.341746546347416,
                 "longitude": -6.2531287590420614
             }
           }' | python3 -m json.tool
elif [ "$action" == "lombokstuff" ]; then
    java -jar ~/.local/share/nvim/mason/share/jdtls/lombok.jar config -g --verbose > .
else
  echo "Invalid action. Use 'build', 'run', 'seeddb', or 'stop'."
fi
