# bank

## go to the root directory where the build.gradle file is located and 
## execute below command

./gradlew build or you can just run in your IDE default

### run the docker compose file which is located ander the root directory 
## using following command

docker-compose up --build

## tuum-bank spring boot application expose to the  9093 port 
through this url you can access the application rest api http://localhost:9093 
## how to pass arguments to each rest api end has been mentioned in the 
##Tuum-Bank document 

## Create queues in RabbitMq 
Access raabitMQ through managmnet url http://localhost:15672
Create following quese with default exchange
ACCOUNT_CREATION, CREDIT_TRANSACTION, DEBIT_TRANSACTION
(Need to create this queue manually since the automatically queue creation
failed as mentioned in the document(section 8))
