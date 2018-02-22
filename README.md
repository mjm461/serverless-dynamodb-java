# Sample Project for AWS Lambda with DynamoDB

## Development 

## Prerequisites

- Install [Docker](https://github.com/awslabs/aws-sam-local#prerequisites)
- Install [AWS SAM local](https://github.com/awslabs/aws-sam-local) from 
  [here](https://github.com/awslabs/aws-sam-local/releases)

### Jars that are built
game-core: Includes only the Spring endpoints and the DynamodBD storage
game-boot: Runs the project in Spring Boot
game-lambda: Builds the jar that is deployable to AWS Lambda (SAM Local in this case)

### Build
For deployment we follow [the SAM documentation](https://github.com/awslabs/aws-sam-local#package-and-deploy-to-lambda)

To build the project use gradlew

```bash
./gradlew build
```

### Start an instance of DynamoDB Local
- Get it [here](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.html)

```bash
java -Djava.library.path=./DynamoDBLocal_lib -jar DynamoDBLocal.jar -sharedDb
```

### Test with Spring Boot
```bash
./gradlew bootRun
```

### Run with SAM local and environment variables
```bash
sam local start-api -n env.json
```

### Curl examples
```bash
$ curl localhost:3000/game?create=true
[]

$ curl localhost:3000/game -X POST  -H "Content-Type: application/json" -d '{"playerId": "mark", "opponentId": "dave", "winnerId": "mark"}' | python -m json.tool0 
{
    "gameModelId": {
        "playerId": "mark",
        "createdTime": "2018-02-22T16:42:33.966Z"
    },
    "opponentId": "dave",
    "winnerId": "mark",
    "notes": null,
    "createdTime": "2018-02-22T16:42:33.966Z",
    "playerId": "mark"
}

$ curl localhost:3000/game -X POST  -H "Content-Type: application/json" -d '{"playerId": "mark", "opponentId": "dave", "winnerId": "dave"}' | python -m json.tool
{
    "gameModelId": {
        "playerId": "mark",
        "createdTime": "2018-02-22T16:42:48.781Z"
    },
    "opponentId": "dave",
    "winnerId": "dave",
    "notes": null,
    "createdTime": "2018-02-22T16:42:48.781Z",
    "playerId": "mark"
}

$ curl localhost:3000/game -X POST  -H "Content-Type: application/json" -d '{"playerId": "mark", "opponentId": "bob", "winnerId": "mark"}' | python -m json.tool
{
    "gameModelId": {
        "playerId": "mark",
        "createdTime": "2018-02-22T16:43:06.422Z"
    },
    "opponentId": "bob",
    "winnerId": "mark",
    "notes": null,
    "createdTime": "2018-02-22T16:43:06.422Z",
    "playerId": "mark"
}

$ curl localhost:3000/game/mark?opponentId=dave | python -m json.tool
[
    {
        "gameModelId": {
            "playerId": "mark",
            "createdTime": "2018-02-22T16:42:48.781Z"
        },
        "opponentId": "dave",
        "winnerId": "dave",
        "notes": null,
        "createdTime": "2018-02-22T16:42:48.781Z",
        "playerId": "mark"
    },
    {
        "gameModelId": {
            "playerId": "mark",
            "createdTime": "2018-02-22T16:42:33.966Z"
        },
        "opponentId": "dave",
        "winnerId": "mark",
        "notes": null,
        "createdTime": "2018-02-22T16:42:33.966Z",
        "playerId": "mark"
    }
]

$ curl http://localhost:3000/game/mark?winner=true | python -m json.tool
[
    {
        "gameModelId": {
            "playerId": "mark",
            "createdTime": "2018-02-22T16:42:33.966Z"
        },
        "opponentId": "dave",
        "winnerId": "mark",
        "notes": null,
        "createdTime": "2018-02-22T16:42:33.966Z",
        "playerId": "mark"
    },
    {
        "gameModelId": {
            "playerId": "mark",
            "createdTime": "2018-02-22T16:43:06.422Z"
        },
        "opponentId": "bob",
        "winnerId": "mark",
        "notes": null,
        "createdTime": "2018-02-22T16:43:06.422Z",
        "playerId": "mark"
    }
]

```


### Remote debbugging SAM Local
* Start SAM Local with a debug port
```bash
sam local start-api -n -d 5005 env.json
```
* Connect to port 5005 with your remote debugger
