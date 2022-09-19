## Goalbox Hybrid-DApp
Simple POC of a Hybrid-DApp using **GoQuorum**, **Webflux** ("Reactive Spring Boot") and **Vue.js**. The POC consists on a Goalbox management application. A ***Goalbox*** is a similar to a "Savings Account" with some constraints:
- They are assigned a "goal" (hence the name).
- They can be **funded** from the owner's personal account or from another *Goalbox*.
- Money can't be withdrawn from them except at the time of closing them or to **fund** another *Goalbox*.
- When a *Goalbox* is settled, it can't be operated again and will be labeled (depending on if the goal was accomplished or not).

**Goalboxes** are managed as "dynamic smart contracts". Every Goalbox has its own deployed contract, deployed from the metadata server as needed through a **Rest API**.

The application requires MetaMask browser's plugin to be installed in the browser.

> **Note**: MetaMask caches the account's nonce. When the blockchain network is reset, any new transaction with a previously used account will end up stuck. To avoid this, reset the account on MetaMask: **Settings -> Advanced -> Reset Account**

> **Disclaimer**: Goalbox, in this context, is a completely invented name and not a real trademark nor widely used concept.

## The Project

The project is composed of 5 modules:
- **Metadata Server (Webflux)**: manages non-transactional data around *Goalboxes*, such as name, or goal value.
- **DB (PostgreSQL)**: metadata are stored in here.
- **Blockchain Network (GoQuorum)**: ethereum client composed of 2 member nodes, it serves as a *transactional database*.
- **DApp (Vuejs)**: the visual application itself. 
- **Smart-Contracts (Solidity)**: contains our Smart-Contract. The module itself is used by the Metadata Server and the DApp to know how to communicate with the Blockchain Network. 

> **Note**: although we are storing our "metadata" in a **Webflux** + **PostgreSQL** application, they could be stored in the Blockchain network too, transforming our Hybrid-DApp in a pure one.  

## Try it yourself

The different modules can be run independently with their respective development tools, or using containerized as pseudo-production versions using Docker.

### Option 1: Docker Run
The project comes with two different docker compose files, once specifically for GoQuorum and another one for the Hybrid-DApp modules.

##### Run GoQuorum:
```
$ docker compose -f ./quorum-network/docker-compose-quorum-network.yml up -d
```
Which exposes:
- **Geth RPC-API**: `http://localhost:8545` + `ws://localhost:8546` (mapped to member 1)
  - Member 1: `http://localhost:20000` + `ws://localhost:20001`
  - Member 2: `http://localhost:20002` + `ws://localhost:20003`


##### Run the Hybrid-DApp:
```
$ docker compose up -d
```

Which exposes:
- Web application: `http://localhost:4000`
- Metadata API: `http://localhost:8080/docs/api-docs-ui`
- Metadata DB: `postgresql://postgres:postgres@localhost:5432/goalbox`

---

### Option 2: Dev Run

- **Metadata DB**:
  - The schema can be found in `./goalbox-db/goalbox-metadata-schema.sql`.
  - Env:
    - POSTGRES_USER=postgres
    - POSTGRES_PASSWORD=postgres
    - POSTGRES_DB=goalbox
- **Metadata Server**:
  - Module path: `./goalbox-server`
  - Run it: from module path `$ ./gradlew bootRun`
- **Blockchain**:
  - Module path: `./quorum-network`
- **DApp**:
  - Module path: `./goalbox-dapp`
  - Run it: from module path: `$ npx vite --port=8000`
- **Smart-Contracts**: they are automatically compiled as part of the **Metadata Server** and the **Dapp** build pipeline.






