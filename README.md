# Azure Event Hubs Spring Boot Application

This project contains two applications:

1.  **`azure-eventhub-listener`**: A Spring Boot application that listens for messages from an Azure Event Hub. It is designed to be deployed as a containerized application and can be autoscaled using KEDA.
2.  **`event-sender`**: A simple Java console application to send messages to an Azure Event Hub.

## `azure-eventhub-listener`

This application listens to an Azure Event Hub and an Azure Service Bus queue, and logs the messages it receives.

### Configuration

Configuration is managed in `src/main/resources/application.yml`. You need to provide the following environment variables:

*   `EVENTHUBS_CONNECTION_STRING`: Your Azure Event Hubs connection string.
*   `EVENTHUBS_NAME`: The name of your Event Hub.
*   `CHECKPOINT_STORAGE_ACCOUNT`: The name of the storage account for checkpointing.
*   `CHECKPOINT_CONTAINER`: The name of the container in the storage account for checkpointing.
*   `SERVICEBUS_CONNECTION_STRING`: Your Azure Service Bus connection string.
*   `SERVICEBUS_QUEUE_NAME`: The name of your Service Bus queue.

### Building and Running

1.  **Build the application:**
    ```bash
    mvn clean package
    ```

2.  **Run the application:**
    ```bash
    java -jar target/azure-eventhub-listener-1.0.0-SNAPSHOT.jar
    ```

### Docker

A `Dockerfile` is provided to containerize the application.

1.  **Build the Docker image:**
    ```bash
    docker build -t your-registry/azure-eventhub-listener:latest .
    ```

2.  **Push the image to your registry:**
    ```bash
    docker push your-registry/azure-eventhub-listener:latest
    ```

### KEDA Autoscaling

The `keda/scaled-object.yaml` file contains the KEDA configuration for autoscaling the application based on the number of messages in the Event Hub. You will need to apply this configuration to your Kubernetes cluster where the application is deployed.

## `event-sender`

This is a command-line application to send messages to your Azure Event Hub.

### Configuration

Set the following environment variables:

*   `EVENTHUBS_CONNECTION_STRING`: Your Azure Event Hubs connection string.
*   `EVENTHUBS_NAME`: The name of your Event Hub.

### Building and Running

1.  **Navigate to the `event-sender` directory:**
    ```bash
    cd event-sender
    ```

2.  **Build the application:**
    ```bash
    mvn clean package
    ```

3.  **Run the application to send a message:**
    ```bash
    java -cp target/event-sender-1.0.0-SNAPSHOT.jar com.example.sender.EventSender "Your message here"
    
## Azure Infrastructure and Deployment

This section provides the Azure CLI commands to create the necessary infrastructure and deploy the `azure-eventhub-listener` application.

### Prerequisites

*   [Azure CLI](https://docs.microsoft.com/en-us/cli/azure/install-azure-cli)
*   [Docker](https://docs.docker.com/get-docker/)
*   An Azure subscription

### 1. Set up Variables

Replace the placeholder values with your own.

```bash
export RESOURCE_GROUP="my-resource-group"
export LOCATION="eastus"
export ACR_NAME="myacr" # Must be globally unique
export EVENTHUB_NAMESPACE="myeventhubnamespace" # Must be globally unique
export EVENTHUB_NAME="myeventhub"
export SERVICEBUS_NAMESPACE="myservicebusnamespace" # Must be globally unique
export QUEUE_NAME="myqueue"
export STORAGE_ACCOUNT_NAME="mystorageaccount" # Must be globally unique
export CONTAINER_APP_NAME="my-container-app"
export CONTAINER_APP_ENV_NAME="my-container-app-env"
```

### 2. Create Infrastructure

```bash
# Login to Azure
az login

# Create a resource group
az group create --name $RESOURCE_GROUP --location $LOCATION

# Create an Azure Container Registry
az acr create --name $ACR_NAME --resource-group $RESOURCE_GROUP --sku Basic --admin-enabled true

# Create an Event Hubs namespace
az eventhubs namespace create --name $EVENTHUB_NAMESPACE --resource-group $RESOURCE_GROUP --location $LOCATION

# Create an Event Hub
az eventhubs eventhub create --name $EVENTHUB_NAME --namespace-name $EVENTHUB_NAMESPACE --resource-group $RESOURCE_GROUP

# Create a Service Bus namespace
az servicebus namespace create --name $SERVICEBUS_NAMESPACE --resource-group $RESOURCE_GROUP --location $LOCATION

# Create a Service Bus queue
az servicebus queue create --name $QUEUE_NAME --namespace-name $SERVICEBUS_NAMESPACE --resource-group $RESOURCE_GROUP

# Create a storage account
az storage account create --name $STORAGE_ACCOUNT_NAME --resource-group $RESOURCE_GROUP --location $LOCATION --sku Standard_LRS

# Create a storage container
az storage container create --name checkpoint-container --account-name $STORAGE_ACCOUNT_NAME
```

### 3. Build and Push Docker Image

```bash
# Login to your container registry
az acr login --name $ACR_NAME

# Build the Docker image
docker build -t $ACR_NAME.azurecr.io/azure-eventhub-listener:latest .

# Push the image to the registry
docker push $ACR_NAME.azurecr.io/azure-eventhub-listener:latest
```

### 4. Deploy to Azure Container Apps

```bash
# Create a Container App Environment
az containerapp env create --name $CONTAINER_APP_ENV_NAME --resource-group $RESOURCE_GROUP --location $LOCATION

# Get connection strings
EVENTHUBS_CONNECTION_STRING=$(az eventhubs namespace authorization-rule keys list --name RootManageSharedAccessKey --namespace-name $EVENTHUB_NAMESPACE --resource-group $RESOURCE_GROUP --query primaryConnectionString -o tsv)
SERVICEBUS_CONNECTION_STRING=$(az servicebus namespace authorization-rule keys list --name RootManageSharedAccessKey --namespace-name $SERVICEBUS_NAMESPACE --resource-group $RESOURCE_GROUP --query primaryConnectionString -o tsv)

# Create the Container App
az containerapp create \
    --name $CONTAINER_APP_NAME \
    --resource-group $RESOURCE_GROUP \
    --environment $CONTAINER_APP_ENV_NAME \
    --image $ACR_NAME.azurecr.io/azure-eventhub-listener:latest \
    --registry-server $ACR_NAME.azurecr.io \
    --registry-username $ACR_NAME \
    --registry-password $(az acr credential show --name $ACR_NAME --query "passwords[0].value" -o tsv) \
    --secrets eventhubs-connection-string=$EVENTHUBS_CONNECTION_STRING servicebus-connection-string=$SERVICEBUS_CONNECTION_STRING \
    --env-vars EVENTHUBS_NAME=$EVENTHUB_NAME CHECKPOINT_STORAGE_ACCOUNT=$STORAGE_ACCOUNT_NAME CHECKPOINT_CONTAINER=checkpoint-container SERVICEBUS_QUEUE_NAME=$QUEUE_NAME
```

### 5. Apply KEDA Scaling

Update the `keda/scaled-object.yaml` file with your specific values, then apply it to your Container App's environment.

```bash
# Note: You may need to adjust the namespace in the scaled-object.yaml file.
kubeclt apply -f keda/scaled-object.yaml
```
