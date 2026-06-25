# SubScribe Master

SubScribe Master — obunalar (subscriptions) va foydalanuvchilar to'lov davrlarini samarali boshqarish uchun mo'ljallangan backend ilovasi.

## Ilovaning Texnologik Steki
* **Backend:** Java 17 / Spring Boot 3.x (JPA, Hibernate)
* **Ma'lumotlar bazasi:** PostgreSQL 15
* **Migratsiya:** Flyway (Database Migration)
* **Konteynerizatsiya:** Docker & Docker Compose
* **Hujjatlashtirish:** Swagger UI (Springdoc OpenAPI)

---

## Ishga Tushirish Qo'llanmasi

### Prerekvizitlar (Talablar)
Tizimingizda quyidagi dasturlar o'rnatilgan va ishlayotgan bo'lishi shart:
1. **Docker Desktop** (Windows uchun yoqilgan va yashil holatda bo'lishi kerak)
2. **Git**
3. **Java 17 / Maven** (Agar lokal build qilmoqchi bo'lsangiz)

### Qadamlar:

1. **Loyihani klon qiling va papkaga kiring:**
```bash
   cd D:\SubScribeMasterProject\"SubScribe Master"
Docker Desktop dasturini yoqing:

Agarda terminalda Docker daemon API bilan bog'liq xatolik chiqsa, Docker Desktop dasturini administrator nomidan qayta ishga tushiring.

Loyihani Docker orqali build qiling va yurgizing:

Bash
   docker compose up --build -d
Bu buyruq PostgreSQL bazasini va Spring Boot ilovasini konteyner ichida avtomat sozlaydi va orqa fonda yurgizadi.

Konteynerlar holatini tekshirish:

Bash
   docker compose ps
Xatoliklar va Loglarni kuzatish:

Bash
   docker compose logs -f
API Hujjatlari (Swagger UI)
Ilova to'liq ishga tushgandan so'ng, brauzeringiz orqali barcha API endpointlar, ularning izohlari va sxemalari bilan tanishishingiz hamda test qilishingiz mumkin:

Swagger URL: http://localhost:8080/swagger-ui/index.html

Loyihani To'xtatish
Konteynerlarni o'chirish va resurslarni bo'shatish uchun:

Bash
docker compose down