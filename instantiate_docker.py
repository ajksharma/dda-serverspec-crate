import docker
import sys
import os

# Please perform the following steps in order to use this script
# 1) Install pyton 3 and pip3: sudo apt install python3-pip python3
# 2) Install the docker sdk with pip: pip3 install docker
# 3) TODO: Check image openjdk:8 for using ubuntu
# 4) TODO: Make it possible to specify docker container cmd per command line (Default java -jar...)

# TODO add cli documentation

docker_logs = os.getcwd() + '/docker-logs/'

if not os.path.exists(docker_logs):
    os.makedirs(docker_logs)

# TODO relative path (os.getcwd())
edn_file = '/home/gec/repo/dda-pallet/dda-serverspec-crate/' + sys.argv[1]
jar_file = '/home/gec/repo/dda-pallet/dda-serverspec-crate/' + sys.argv[2]

execute_command = 'java -jar /app/uberjar.jar /app/config.edn'

debug_map = {'edn_file':edn_file, 'jar_file':jar_file, 'docker_logs':docker_logs, 'execute_command':execute_command}
print(debug_map)

client = docker.APIClient()

container = client.create_container(
    image='openjdk:8',
    command='java -jar /app/uberjar.jar /app/config.edn',
    volumes=['/app/config.edn', '/app/uberjar.jar', '/logs'],

    host_config=client.create_host_config(binds={
        edn_file: {
            'bind': '/app/config.edn',
            'mode': 'ro',
        },
        jar_file: {
            'bind': '/app/uberjar.jar',
            'mode': 'ro',
        },
        docker_logs: {
            'bind': '/logs/',
            'mode': 'rw',
        }
    })
)


response = client.start(container=container)
for log in client.logs(container, stream=True, stdout=True, stderr=True):
    print(log)

