# Google Access Token

Get access token available for 3600s, type Bearer.

## Required

### Google service account
From developers console google:
- Create service account
- Generate key and save json file with credentials about this service account

Make sure you have the privateKey and privateID with clientEmail in this file.

## How to install

### From maven:

``` mvn install ```

### From eclipse:

- Download dependecies from maven:
``` mvn install ```

- Export to runnable jar

## How to run

### From maven:

```mvn exec:java /path/to/file.json http://googleapi.com...```

### From java 

```java -jar jarName-jar-with-dependencies.jar /path/to/file.json http://googleapi.com...```