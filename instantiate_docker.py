import docker
import sys
import os

# TODO add cli documentation
# TODO make sure ubuntu image is pulled: docker image pull ubuntu

docker_logs = os.getcwd() + '/docker-logs/'

if not os.path.exists(docker_logs):
    os.makedirs(docker_logs)

edn_file = '/home/krj/repo/dda-pallet/dda-serverspec-crate/' + sys.argv[1]
jar_file = '/home/krj/repo/dda-pallet/dda-serverspec-crate/' + sys.argv[2]

execute_command = 'java -jar /app/uberjar.jar /app/config.edn'

debug_map = {'edn_file':edn_file, 'jar_file':jar_file, 'docker_logs':docker_logs, 'execute_command':execute_command}
print(debug_map)

client = docker.APIClient()

container = client.create_container(
    image='ubuntu-test:latest',
    command='java -jar /app/uberjar.jar /app/config.edn',
    volumes=['/app/config.edn', '/app/uberjar.jar', '/logs'],

    host_config=client.create_host_config(binds={
        edn_file: {
            'bind': '/app/config.edn',
            'mode': 'rw',
        },
        jar_file: {
            'bind': '/app/uberjar.jar',
            'mode': 'rw',
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

