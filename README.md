# Customer Service

Customer Service är en fristående Spring Boot-mikrotjänst som äger all
data om kunder. Den exponerar ett REST-API som **Booking Service**
använder för att visa och hantera kunder, och den frågar i sin tur
**Booking Service** innan en kund får raderas.

Tjänsten har ingen egen webb-vy – den är en ren backend/REST-tjänst.
Webb-gränssnittet för kunder finns i Booking Service, som anropar detta
API.

## Vad tjänsten gör

- Lagrar kunder (`Customer`: förnamn, efternamn, e-post, telefon) i sin
  egen MySQL-databas (`customerdb`).
- REST-API under `/api/customers`:
  - `GET /api/customers` – lista alla kunder
  - `GET /api/customers/{id}` – hämta en kund (404 om den inte finns)
  - `POST /api/customers` – skapa kund
  - `PUT /api/customers/{id}` – uppdatera kund
  - `DELETE /api/customers/{id}` – ta bort kund
- Validerar inkommande data (`@Valid`) och svarar med tydliga
  felmeddelanden (`GlobalExceptionHandler`) vid valideringsfel (400) eller
  när en resurs saknas/konflikt uppstår (404/409).
- **Skyddar mot att en kund med aktiv bokning raderas**: innan en kund tas
  bort frågar tjänsten Booking Service om kunden har en aktiv bokning.
  - Om ja → `409 Conflict`.
  - Om Booking Service inte går att nå → `503 Service Unavailable`
    (så att en kund aldrig raderas "av misstag" om det inte går att
    verifiera).

## Hur tjänsten pratar med de andra

```
+------------------+   GET/POST/PUT/DELETE    +--------------------+
| Booking Service  | -----------------------> |  Customer Service  |
|  (port 8080)     |     /api/customers       |    (port 8081)     |
+------------------+                          +----------+---------+
                                                          |
                                        GET /api/bookings/customer/{id}/active
                                                          v
                                               +--------------------+
                                               |  Booking Service   |
                                               |    (port 8080)     |
                                               +--------------------+
```

- **Inkommande**: Booking Service (via `CustomerClient`) anropar detta
  API för att lista/hämta/skapa/uppdatera/ta bort kunder samt för att
  slå upp en kund när en bokning skapas.
- **Utgående**: `BookingClient` i den här tjänsten anropar Booking
  Service (`${booking.service.url}/api/bookings/customer/{id}/active`)
  för att kontrollera om kunden har en aktiv bokning innan radering
  tillåts.
- Kommunikationen sker via **REST/JSON över HTTP**.
- Egen databas – delar inte databas med de andra tjänsterna.

## Konfiguration (miljövariabler)

| Variabel | Standardvärde (lokalt) | Beskrivning |
|---|---|---|
| `CUSTOMER_DB_URL` | `jdbc:mysql://localhost:3306/customerdb...` | JDBC-URL till kunddatabasen |
| `CUSTOMER_DB_USERNAME` | `root` | DB-användare |
| `CUSTOMER_DB_PASSWORD` | *(krävs, inget default)* | DB-lösenord |
| `BOOKING_SERVICE_URL` | `http://localhost:8080` | Bas-URL till Booking Service |

Tjänsten körs på **port 8081**.

## Så här startar du hela systemet

Den här tjänsten är tänkt att köras tillsammans med `booking-service` och
`review-service` via `docker-compose.yml`, som ligger i
**booking-service**-repot. Klona alla tre repon som syskonkataloger:

```
projekt/
├── booking-service/    <- docker-compose.yml ligger här
├── customer-service/
└── review-service/
```

```bash
git clone https://github.com/AlexanderJava11/booking-service.git
git clone https://github.com/AlexanderJava11/customer-service.git
git clone https://github.com/AlexanderJava11/review-service.git

cd booking-service
docker compose up --build
```

Det startar bland annat:

- `customer-db` – MySQL på värdport `3308` (databas `customerdb`)
- `customer-service` – detta API på port `8081`

Testa att det fungerar:

```bash
curl http://localhost:8081/api/customers
```

Stoppa hela systemet igen från `booking-service`-katalogen:

```bash
docker compose down
```

## Köra bara den här tjänsten lokalt (utan Docker)

```bash
./mvnw spring-boot:run
```

Kräver en lokal MySQL med databasen `customerdb` samt att
`CUSTOMER_DB_PASSWORD` (och ev. övriga miljövariabler ovan) är satta,
t.ex.:

```bash
export CUSTOMER_DB_PASSWORD=customerpassword
export BOOKING_SERVICE_URL=http://localhost:8080
./mvnw spring-boot:run
```

Om Booking Service inte körs går fortfarande skapa/lista/hämta/uppdatera
kunder bra – det är bara radering av kunder med aktiva bokningar som då
inte kan verifieras (och nekas med 503).

## Testning

```bash
./mvnw test
```

Inkluderar bland annat `CustomerIntegrationTest`.
