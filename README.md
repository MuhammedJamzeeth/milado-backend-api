# Milado-API

Milado-API is a Spring Boot application designed to provide a RESTful API for managing Milado services. This project includes endpoints for various functionalities and ensures smooth interaction with the backend database.

## Table of Contents

- [Getting Started](#getting-started)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Running the Application](#running-the-application)
- [Public Api Declaration](#public-api-declaration)
- [RSA Key Generation](#rsa-key-generation)



## Getting Started

These instructions will help you set up and run the Milado-API project on your local machine for development and testing purposes.

## Prerequisites

Make sure you have the following installed on your system:

- Java Development Kit (JDK) 11 or higher
- Apache Maven
- MySQL or any other SQL database

## Installation

1. Clone the repository:

    ```sh
    git clone https://github.com/yourusername/milado-api.git
    ```

2. Navigate to the project directory:

    ```sh
    cd milado-api
    ```

3. Configure the database connection in `src/main/resources/application.yml`:

    ```yaml
    spring:
      datasource:
        url: jdbc:mysql://localhost:3306/miladodb
        username: yourUsername
        password: yourPassword
      jpa:
        hibernate:
          ddl-auto: update
    ```

4. Build the project using Maven:

    ```sh
    mvn clean install
    ```

## Running the Application

To run the application, use the following command:

```sh
mvn spring-boot:run
```

Any request to a secured endpoint is intercepted by the [JwtAuthenticationFilter](), which is added to the security filter chain and configured in the [SecurityConfiguration](). The custom filter holds the responsibility for verifying the authenticity of the incoming access token and populating the security context.

## Public API declaration

Any API that needs to be made public can be annotated with [@PublicEndpoint](). Requests to the configured API paths will not evaluated by the custom security filter with the logic being governed by [ApiEndpointSecurityInspector]().

Below is a sample controller method declared as public which will be exempted from authentication checks:

```java
@PublicEndpoint
@GetMapping(value = "/api/v1/something")
public ResponseEntity<Something> getSomething() {
    var something = someService.fetch();
    return ResponseEntity.ok(something);
}
```
# RSA Key Generation

This guide provides steps to generate an RSA private key and derive its corresponding public key using OpenSSL.

## Prerequisites

Ensure you have OpenSSL installed on your machine. You can check the installation by running:

```bash
openssl version
```

If OpenSSL is not installed, follow the instructions [here](https://www.openssl.org/source/) to install it.

## Steps to Generate RSA Keys

### 1. Generate RSA Private Key

Open your command-line interface (CMD on Windows, Terminal on macOS/Linux), and execute the following OpenSSL command to generate an RSA private key:

```bash
openssl genpkey -algorithm RSA -out private.pem -pkeyopt rsa_keygen_bits:2048
```

This command generates an RSA private key with 2048 bits and saves it to a file named `private.pem`.

### 2. Generate RSA Public Key from Private Key

After generating the private key, you can derive the public key from it using the following command:

```bash
openssl rsa -pubout -in private.pem -out public.pem
```

This command takes the `private.pem` file (generated in the previous step) and outputs its corresponding public key in PEM format to a file named `public.pem`.

## Usage

1. **Private Key (`private.pem`):** Keep this key secure and do not share it. It will be used for signing JWT tokens.
2. **Public Key (`public.pem`):** This key can be shared and will be used to verify the JWT tokens.

## Example

```bash
# Generate RSA Private Key
openssl genpkey -algorithm RSA -out private.pem -pkeyopt rsa_keygen_bits:2048

# Generate RSA Public Key from Private Key
openssl rsa -pubout -in private.pem -out public.pem
```

## Notes

- The private key file (`private.pem`) contains sensitive information and should be stored securely.
- The public key file (`public.pem`) can be shared publicly and used for verifying signatures.
