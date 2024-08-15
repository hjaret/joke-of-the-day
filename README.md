# Joke of the Day

## Description
A RESTful service to CRUD on a Joke of the Day. The service runs an embedded H2 database to manage JOTD data. The service also relies on a Fusion Auth authentication server which can be spun up using docker compose.

## Installation
### Clone project
`git clone`

### Run Docker Compose
`docker compose up -d`

### Run Spring Service
`./gradlew run`

## Using the service
The service is using Fusion Auth for authentication and authorization on the API endpoints. Two users have been created, using the kickstart.json file, one with admin level access and one with user level access. User level access only gives authorization for the GET endpoints while admin can access all of the endpoints giving an admin level user full CRUD functionality.

### Access Tokens
Retrieval of an admin access token can be accomplished with:

```
curl -v --location 'http://localhost:9011/api/login' \
--header 'Authorization: this_really_should_be_a_long_random_alphanumeric_value_but_this_still_works' \
--header 'Content-Type: application/json' \
--data-raw '{
"loginId": "jotd_admin@example.com",
"password": "password",
"applicationId": "e9fdb985-9173-4e01-9d73-ac2d60d1dc8e"
}'
```

while a user level token can be retrieved using:

```
curl -v --location 'http://localhost:9011/api/login' \
--header 'Authorization: this_really_should_be_a_long_random_alphanumeric_value_but_this_still_works' \
--header 'Content-Type: application/json' \
--data-raw '{
  "loginId": "user@example.com",
  "password": "password",
  "applicationId": "e9fdb985-9173-4e01-9d73-ac2d60d1dc8e"
}'
```

### Hitting REST endpoints

```
curl --location 'http://localhost:8080/jotd' \
--header 'Authorization: Bearer <auth token>'
```