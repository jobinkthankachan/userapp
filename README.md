# Docker Compose -> Spring boot App and MySql

## Prerequistes 
Maven , Docker 

## Installation
Clone the repo -> https://github.com/jobinkthankachan/userapp.git
navigate to the local directory where you had cloned repo , go to userapp/ dir

## Start the Program
To Run the project , execute the command below:
```bash
docker-compose up -d
```
## Run Unit Test
navigate to the local directory where you had cloned repo , go to userapp/ dir and execute the below command
```bash
mvn test
```

## Stop the Program
To stop all running containers execute the command below:
```bash
docker-compose down
```
