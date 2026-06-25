## 2. ARCHITECTURE.md

```markdown
# Architectural Decision Records (ADR) - SubScribe Master

Ushbu hujjat loyihada qo'llanilgan arxitektura tuzilishi va qabul qilingan texnik qarorlarning sabablarini tushuntiradi.

## 1. Loyiha Arxitekturasi: Layered Architecture (Qatlamli Arxitektura)
Loyiha klassik **Qatlamli arxitektura (Controller-Service-Repository)** modeliga asoslangan.

* **Controller Layer (API):** Mijozdan (frontend/mobile) kelayotgan so'rovlarni qabul qiladi, validatsiya qiladi va Swagger orqali hujjatlashtiriladi.
* **Service Layer (Business Logic):** Loyihaning barcha asosiy biznes qoidalari, hisob-kitoblari va mantiqiy jarayonlari shu yerda yoziladi.
* **Repository Layer (Data Access):** Spring Data JPA yordamida ma'lumotlar bazasi (PostgreSQL) bilan to'g'ridan-to'g'ri bog'lanadi.

**Sababi:** Qatlamlarning bir-biridan ajratilganligi (Separation of Concerns) kodni testlashni osonlashtiradi va kelajakda biznes mantiqni o'zgartirganda boshqa qatlamlarga zarar yetishining oldini oladi.

---

## 2. Ma'lumotlar Bazasi va Migratsiya Strategiyasi

### Flyway Migration (`V1__init.sql`)
Ma'lumotlar bazasi sxemasini boshqarish uchun **Flyway** tanlangan. `ddl-auto=update` kabi avtomatik Hibernate generatsiyalaridan ishlab chiqarish (production) muhitida foydalanilmaydi.

### Enumlarni saqlash strategiyasi (Status va Billing Cycle)
* **Qaror:** `status` va `billing_cycle` kabi cheklangan qiymatlar bazada `VARCHAR` (matn) yoki muvofiqlashtirilgan tartibda saqlanadi. 
* **Tuzatish:** Kod darajasida `@Enumerated(EnumType.STRING)` ishlatilganda, bazada `SMALLINT` emas, `VARCHAR` ishlatilishi qat'iy belgilandi. Bu bazani to'g'ridan-to'g'ri o'qiganda ma'lumotlarning tushunarli bo'lishini (human-readable) ta'minlaydi.

---

## 3. Konteynerizatsiya (Docker & Compose)
Loyiha mutlaqo **Docker-first** muhitiga moslashtirilgan.
* `Dockerfile` Spring Boot ilovasini build qiladi.
* `docker-compose.yml` fayli esa dasturning ishlashi uchun zarur bo'lgan `postgres:15-alpine` bazasini va Spring ilovasini bitta tarmoqqa (network) bog'lab, lokal muhitni tezda sozlash imkonini beradi.

---

## 4. API Dokumentatsiya va Guruhlash
Backend endpointlarini tushunishni osonlashtirish uchun `springdoc-openapi` o'rnatilgan.
* Controller darajasida `@Tag` annotatsiyasi orqali API guruhlanadi.
3* Metod darajasida `@Operation(summary, description)` yordamida frontend dasturchilar va testerlar uchun har bir endpointning aniq vazifasi yozib boriladi.

##
