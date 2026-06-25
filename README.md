# SubScribe Master

Foydalanuvchilarning pullik obunalarini (Netflix, Spotify, ChatGPT, Adobe va h.k.)
bitta tizimda jamlaydigan, sarf-xarajatlarni tahlil qiladigan, valyuta kursiga qarab
asosiy valyutaga konvertatsiya qiladigan va to'lov muddati yaqinlashganda ogohlantirish
yuboradigan **backend REST API**.

Asosiy imkoniyatlar:

- JWT asosida ro'yxatdan o'tish / kirish, Refresh Token va Role-based (USER / ADMIN) kirish
- Obunalar uchun CRUD, Soft Delete, Pagination
- cbu.uz orqali valyuta kursini olish, keshlash (Caffeine) va Circuit Breaker (Resilience4j) bilan himoyalash
- Har kuni 09:00 da to'lovi yaqinlashgan foydalanuvchilarga ogohlantirish (Scheduler + ShedLock)
- Yillik xarajatlar bo'yicha Excel/CSV hisobot (Apache POI)
- Statistika API: eng qimmat obuna, oylik xarajat, oylar dinamikasi, kategoriya bo'yicha taqsimot

> ⚠️ Eslatma: bu loyiha hozircha aktiv ishlab chiqilmoqda. Ma'lum cheklovlar va
> tuzatilishi kerak bo'lgan joylar [ARCHITECTURE.md](ARCHITECTURE.md) faylining
> "Bilingan cheklovlar" bo'limida sanab o'tilgan.

---

## Texnologiyalar (pom.xml dan haqiqiy versiyalar)

| Texnologiya | Versiya | Maqsad |
|---|---|---|
| Java | 21 | Asosiy til (record, sealed, pattern matching) |
| Spring Boot | 3.4.2 | Web, Data JPA, Security, Validation, Cache, Mail, AOP |
| Spring Cloud | 2024.0.0 | Circuit Breaker BOM |
| PostgreSQL | runtime (driver) | Asosiy ma'lumotlar bazasi |
| Flyway | Spring Boot boshqaruvida | DB migratsiyasi (`flyway-core`, `flyway-database-postgresql`) |
| MapStruct | 1.6.3 | Entity ↔ DTO mapping |
| Caffeine | Spring Boot boshqaruvida | Valyuta kursini keshlash |
| Resilience4j | spring-cloud-starter-circuitbreaker | Circuit Breaker / Fallback |
| Apache POI | 5.3.0 | Excel (.xlsx) hisobot |
| springdoc-openapi | 2.8.4 | Swagger / OpenAPI hujjati |
| jjwt (api/impl/jackson) | 0.12.6 | JWT generatsiya / tekshirish |
| ShedLock | 5.16.0 | Distributed scheduler lock |
| Lombok | 1.18.36 | (kodda hali ishlatilmoqda) |
| H2 | test scope | Test uchun in-memory DB |

> TT'da migratsiya uchun "Liquibase / Flyway" deyilgan — bu loyihada **Flyway** tanlangan.

---

## Lokal kompyuterda ishga tushirish

### 1. Talablar
- JDK 21
- Maven 3.9+ (yoki repo ichidagi `./mvnw`)
- PostgreSQL 14+

### 2. PostgreSQL bazasini tayyorlash
```sql
CREATE DATABASE subscribe_master;
```
Ulanish manzili kodda `jdbc:postgresql://localhost:5432/subscribe_master` deb belgilangan
(`src/main/resources/application.properties`).

### 3. Konfiguratsiya (environment variables)
Maxfiy ma'lumotlarni environment variable orqali bering (default qiymatlar `application.properties`
ichida bor, lekin **production'da ularni albatta o'zgartiring**):

```bash
export DB_USERNAME=postgres
export DB_PASSWORD=your_db_password
export JWT_SECRET=your-very-long-random-secret-key
export MAIL_USERNAME=your_gmail@gmail.com
export MAIL_PASSWORD=your_gmail_app_password
```

### 4. Build va run
```bash
./mvnw clean package
./mvnw spring-boot:run
```
yoki
```bash
java -jar target/d-0.0.1-SNAPSHOT.jar
```

Ilova sukut bo'yicha `http://localhost:8080` da ishga tushadi.
Flyway dastur startida `src/main/resources/db/migration/V1__init.sql` migratsiyasini qo'llaydi.

---

## Docker Compose

Hozircha loyihada `docker-compose.yml` yoki `Dockerfile` **yo'q**.
TT talab qilgan "bitta buyruq bilan ishga tushirish" hali amalga oshirilmagan.

---

## Asosiy API endpoint'lar

| Method | Path | Tavsif | Ruxsat |
|---|---|---|---|
| POST | `/api/auth/register` | Ro'yxatdan o'tish | Ochiq |
| POST | `/api/auth/login` | Kirish (access + refresh token) | Ochiq |
| PUT | `/api/auth/verify` | Email kodini tasdiqlash | Ochiq |
| POST | `/api/auth/refresh` | Refresh token orqali yangi access token | Ochiq |
| POST | `/api/auth/refresh/logout` | Refresh tokenni bekor qilish | Ochiq |
| POST | `/api/v1/subscription` | Obuna qo'shish | `subscription:create` |
| GET | `/api/v1/subscription?page=&size=` | Obunalar ro'yxati (pagination) | `subscription:read` |
| PUT | `/api/v1/subscription/{id}` | Obunani yangilash | `subscription:update` |
| DELETE | `/api/v1/subscription/{id}` | Obunani o'chirish (soft delete) | `subscription:delete` |
| GET | `/api/v1/payment-history/subscription/{id}` | Obuna to'lov tarixi | `payment:read` |
| GET | `/api/v1/analytics/most-expensive?period=` | Eng qimmat obuna | `analytics:read` |
| GET | `/api/v1/analytics/monthly-spending` | Oylik umumiy xarajat | `analytics:read` |
| GET | `/api/v1/analytics/trend?months=6` | Oylar bo'yicha dinamika | `analytics:read` |
| GET | `/api/v1/analytics/by-category` | Kategoriya bo'yicha taqsimot | `analytics:read` |
| GET | `/api/v1/analytics/admin/popular-services?limit=10` | Eng ko'p ishlatilgan xizmatlar | `admin:analytics:read` |
| GET | `/api/v1/report/excel` | Excel hisobot (.xlsx) | `report:export` |
| GET | `/api/v1/report/csv` | CSV hisobot | `report:export` |
| GET | `/currency/usd` | USD → UZS kursi | `currency:read` |
| GET | `/api/v1/user` | Foydalanuvchilar ro'yxati | `admin:user:read` |
| GET | `/api/v1/user/{id}` | Foydalanuvchi (id bo'yicha) | `admin:user:read` |
| PUT | `/api/v1/user/{id}` | Foydalanuvchini yangilash | `user:update` |
| DELETE | `/api/v1/user/me/{id}` | O'z hisobini o'chirish | `user:delete` |
| PATCH | `/api/v1/user/preferences/currency?currency=` | Asosiy valyutani o'zgartirish | `user:update` |

---

## Swagger / OpenAPI

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

Bearer (JWT) avtorizatsiyasi `SwaggerConfig` da sozlangan — "Authorize" tugmasi orqali
token kiritib test qilish mumkin.

---

## Environment variables

| O'zgaruvchi | Majburiymi | Default | Izoh |
|---|---|---|---|
| `DB_USERNAME` | Tavsiya etiladi | `postgres` | DB foydalanuvchi nomi |
| `DB_PASSWORD` | **Ha (prod)** | hardcoded default | DB paroli |
| `JWT_SECRET` | **Ha (prod)** | hardcoded default | JWT imzolash kaliti |
| `MAIL_USERNAME` | Email kerak bo'lsa | hardcoded fallback | SMTP login |
| `MAIL_PASSWORD` | Email kerak bo'lsa | hardcoded fallback | SMTP parol |

> ⚠️ Hozir `application.properties` ichida real Gmail app-parol va DB parol hardcode
> qilingan. Bu xavfsizlik nuqtai nazaridan tuzatilishi shart (qarang ARCHITECTURE.md).
