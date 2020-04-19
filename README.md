# SumUp code challenge variant 2

This is Camel-cdi application which solves the challenge in non-recursive way

### build

>mvn clean install

### start the application

> java -jar target/task1b-1.0-SNAPSHOT.jar

> Note: task1b-1.0-SNAPSHOT.jar is not an uber jar; it relies on folder 'target/dependency-jars'.

### Test the app

From the main folder of the application, execute this in a shell:
 >curl -H 'Content-Type: application/json' -d @src/test/resources/example.json http://localhost:8080/api/job/tasksAsBash|bash
 
Currently 'Content-Type: application/x-www-form-urlencoded' is not supported from this solution.
You need to specify specifically 'Content-Type: application/json'.
 
 
## Rest services
 
 
### Order tasks

```
POST http://localhost:8080/api/job/tasksInOrder
Accept: */*
Cache-Control: no-cache
Content-Type: application/json
```
payload:

```json
 {
   "tasks":[
     {
       "name":"task-1",
       "command":"touch /tmp/file1"
     },
     {
       "name":"task-2",
       "command":"cat /tmp/file1",
       "requires":[
         "task-3"
       ]
     },
     {
       "name":"task-3",
       "command":"echo 'Hello World!' > /tmp/file1",
       "requires":[
         "task-1"
       ]
     },
     {
       "name":"task-4",
       "command":"rm /tmp/file1",
       "requires":[
         "task-2",
         "task-3"
       ]
     }
   ]
 
 }
```

Response example:
```json
[
  {
    "name": "task-1",
    "command": "touch /tmp/file1"
  },
  {
    "name": "task-3",
    "command": "echo 'Hello World!' > /tmp/file1"
  },
  {
    "name": "task-2",
    "command": "cat /tmp/file1"
  },
  {
    "name": "task-4",
    "command": "rm /tmp/file1"
  }
]
```
 
### Tasks as bash script
```
POST http://localhost:8080/api/job/tasksAsBash
Accept: */*
Cache-Control: no-cache
Content-Type: application/json
```  
payload:

```json
 {
   "tasks":[
     {
       "name":"task-1",
       "command":"touch /tmp/file1"
     },
     {
       "name":"task-2",
       "command":"cat /tmp/file1",
       "requires":[
         "task-3"
       ]
     },
     {
       "name":"task-3",
       "command":"echo 'Hello World!' > /tmp/file1",
       "requires":[
         "task-1"
       ]
     },
     {
       "name":"task-4",
       "command":"rm /tmp/file1",
       "requires":[
         "task-2",
         "task-3"
       ]
     }
   ]
 
 }
```

Response example:
```
#!/usr/bin/env bash

touch /tmp/file1
echo 'Hello World!' > /tmp/file1
cat /tmp/file1
rm /tmp/file1

```
