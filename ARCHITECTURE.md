# SubScribe Master — Arxitektura

## 1. Qatlamli arxitektura (Layered Architecture)

Loyiha klassik 4 qatlamga ajratilgan:

```
Controller  →  Service  →  Repository  →  Entity (DB)
   (REST)      (biznes)     (JPA)          (PostgreSQL)
```

- **Controller** — faqat HTTP so'rovni qabul qiladi, `Authentication` ni service'ga uzatadi,
  `@RequirePermission` orqali ruxsatni tekshiradi va `ResponseEntity` qaytaradi.
- **Service** — barcha biznes mantiq shu yerda (`@Transactional`, valyuta konvertatsiyasi,
  hisob-kitoblar, ownership tekshiruvi).
- **Repository** — Spring Data JPA interfeyslari. Native SQL ishlatilmagan; faqat derived
  query'lar va bitta JPQL + constructor expression (`findMostUsedServices`).
- **Entity** — `AbstractEntity` dan meros oladi (`id`, `createdAt`, `updatedAt`).

Cross-cutting komponentlar: `config` (Security, JWT filter, Swagger, RestClient, Cache),
`exception` (global handler), `security` (utils, aspect), `extra` (umumiy `ApiResponse`, builder bazasi).

---

## 2. Modullar va ularning javobgarligi

| Modul | Javobgarligi |
|---|---|
| **auth** | Ro'yxatdan o'tish, login, email-kod tasdiqlash, JWT generatsiya/tekshirish, Refresh Token boshqaruvi |
| **user** | Foydalanuvchi profili, ro'l/ruxsatlar (`Role`, `Permission`), asosiy valyuta sozlamasi |
| **subscription** | Obuna CRUD, keyingi to'lov sanasini hisoblash, soft delete, status/davriylik enum'lari |
| **payment** | `PaymentHistory` — har bir to'lovni o'z vaqtidagi kurs bilan saqlash (moliyaviy tarix) |
| **currency** | cbu.uz integratsiyasi (`CbuApiClient`), keshlash, Circuit Breaker, valyuta konvertatsiyasi |
| **notification** | Ogohlantirish yuborish (`EmailNotificationService`) |
| **report** | Excel (`Apache POI`) va CSV hisobot generatsiyasi (`ReportService` interfeysi + generatorlar) |
| **analytics** | Statistika: eng qimmat obuna, oylik xarajat, oylar dinamikasi, kategoriya bo'yicha taqsimot, admin reytingi |
| **scheduler** | `@Scheduled` cron — har kuni 09:00 da to'lovi yaqin obunalarni topib ogohlantirish (ShedLock bilan) |

---

## 3. TT'da aniq yozilmagan "noaniq" joylar va qabul qilingan qarorlar

> Bu bo'lim TT'ning 10-mezoni ("Noaniqlikni hal qilish qobiliyati") uchun muhim.
> Quyidagilar **kodda haqiqatan amalga oshirilgan** qarorlardir.

1. **Kurs qaysi sanaga olinadi.** cbu.uz ning `arkhiv-kursov-valyut/json/` endpoint'i parametrsiz
   chaqirilmoqda — bu **bugungi (eng so'nggi) kurs**ni qaytaradi. To'lov amalga oshganda esa
   `PaymentHistory.exchangeRateAtPayment` ga o'sha paytdagi kurs saqlanishi ko'zda tutilgan, shunda
   tarixiy hisobot kelajakdagi kurs o'zgarishidan ta'sirlanmaydi.

2. **Asosiy valyuta.** Har bir foydalanuvchining `preferredCurrency` maydoni bor (default `UZS`).
   Barcha konvertatsiya UZS orqali "ko'prik" qilib bajariladi: `from → UZS → to`.

3. **Oylik normallashtirish.** Turli davriylikdagi obunalarni solishtirish uchun xarajat oylikka
   keltiriladi: haftalik `× 4.33`, yillik `÷ 12`, oylik o'zgarmaydi (`AnalyticsService.normalizeToMonthly`).

4. **Ruxsat modeli.** Faqat Authentication emas, balki maydon darajasidagi ownership tekshiruvi
   qo'shilgan (har bir service obuna/foydalanuvchi egasini `username` bo'yicha solishtiradi).
   Bundan tashqari permission-based kirish `@RequirePermission` annotatsiyasi + AOP aspekt orqali.

5. **Soft delete.** Obuna jismonan o'chirilmaydi — `isDelete=true` qo'yiladi, o'qish query'lari
   `...IsDeleteFalse` bilan filtrlaydi (moliyaviy tarix saqlanadi).

6. **Ogohlantirish simulyatsiyasi.** TT "log yoki email" deydi — bu yerda email kanali tanlangan
   (`EmailNotificationService` → `JavaMailSender`).

> Quyidagilar TT'da so'ralgan, lekin kodda **hali to'liq hal qilinmagan** noaniqliklar
> (tavsiya sifatida 5-bo'limga qarang): obuna `status` qiymatlari (PAUSED yo'q), to'lovni
> `PaymentHistory` ga yozish jarayoni (`recordPayment` hech qayerdan chaqirilmaydi), va
> profil-asosli (dev/prod) konfiguratsiya.

---

## 4. Ishlatilgan dizayn pattern'lar

- **DTO + Mapper (MapStruct):** `SubscriptionMapper`, `PaymentHistoryMapper` — entity'lar tashqariga
  to'g'ridan-to'g'ri chiqmaydi.
- **Builder:** `AbstractEntity.Builder<C, B>` generik bazasi orqali `Subscription`, `Users`,
  `RefreshToken`, `ApiResponse`, `AuthResponse` builder'lari.
- **Strategy (qisman):** Hisobot generatsiyasi `ReportService` interfeysi orqali Excel/CSV
  generatorlariga ajratilgan. (Notification uchun Strategy hali to'liq emas — pastga qarang.)
- **Aspect / AOP:** `@RequirePermission` + `PermissionAspect` — ruxsatni deklarativ tekshirish.
- **Circuit Breaker + Fallback:** `CurrencyService.getRates()` Resilience4j bilan, ishlamasa
  oxirgi keshlangan kursga qaytadi.
- **Cache-aside:** `@Cacheable("currency-rates")` + Caffeine (24 soat TTL).

---

## 5. Bilingan cheklovlar / kelajakda yaxshilash kerak bo'lgan joylar

> Bular **haqiqiy kod asosida** aniqlangan; "to'ldirib yozilgan" emas.

1. **Authentication principal nomuvofiqligi.** `JwtFilter` autentifikatsiya principal'iga `String`
   (username) qo'yadi, lekin `SecurityUtils.getUsername()` uni `CustomUserDetails` deb kutadi —
   bu himoyalangan endpoint'larda muammoga sabab bo'ladi. Tuzatish shart.

2. **Login oqimi.** `AuthService.login` foydalanuvchini `findByEmail(request.username())` orqali
   qidiradi, lekin autentifikatsiya `username` bo'yicha amalga oshadi — username ≠ email bo'lsa nomuvofiqlik.

3. **`PaymentHistory` to'ldirilmaydi.** `recordPayment` metodi mavjud, lekin hech qayerdan
   (jumladan scheduler'dan ham) chaqirilmaydi. Natijada to'lovga asoslangan analitika va
   hisobotlar bo'sh chiqadi. To'lov simulyatsiyasini scheduler'ga ulash kerak.

4. **MapStruct maydon nomlari.** `SubscriptionCreateRequest.price` ↔ `Subscription.amount` va
   `SubscriptionResponse.price` nomlari mos kelmaydi — `@Mapping(source/target)` qo'shilmagan,
   bu maydonlar mapping'da bo'sh qolishi mumkin.

5. **Optimistic Locking yo'q.** Hech bir entity'da `@Version` yo'q (TT 4-mezon).

6. **Filtering (Specification API) yo'q.** Obunalar faqat pagination bilan qaytadi; status/valyuta/narx
   oralig'i bo'yicha filtrlash amalga oshirilmagan (TT 2.2).

7. **Notification Strategy to'liq emas.** TT `NotificationService` interfeysi + `LogNotifier`/`EmailNotifier`
   ni so'raydi; hozir faqat bitta `EmailNotificationService` bor (interfeyssiz).

8. **ShedLock konfiguratsiyasi to'liq emas.** `@EnableSchedulerLock` va `@SchedulerLock` bor, lekin
   `LockProvider` bean'i va `shedlock` jadvali migratsiyada yo'q.

9. **Circuit Breaker nomi mos emas.** `@CircuitBreaker(name = "debugApi")`, lekin
   `application.properties` da instance `cbuApi` deb sozlangan — konfiguratsiya qo'llanmaydi.

10. **Auditing.** `createdAt/updatedAt` Hibernate `@CreationTimestamp/@UpdateTimestamp` orqali
    ishlaydi, lekin `PaymentHistory` dagi `@CreatedDate` uchun `@EnableJpaAuditing` yoqilmagan —
    o'sha maydon to'ldirilmaydi.

11. **Maxfiy ma'lumotlar hardcode.** `application.properties` da real Gmail app-parol va DB parol bor;
    `application-dev/prod` profillariga ajratish kerak (TT 8-mezon).

12. **Test qoplami.** Faqat `contextLoads()` testi bor; TT biznes mantiqning ≥30% test bilan
    qoplanishini talab qiladi (JUnit 5 + AssertJ).

13. **Lombok.** TT Lombok'ni tavsiya etmaydi, lekin u hali 28+ faylda ishlatilmoqda.

14. **Kod dublikatsiyasi.** `extra.RequirePermission` ↔ `valid.RequirePermission` va
    `security.PermissionAspect` ↔ `valid.PermissionAspect` — bir xil maqsadli takror klasslar.

15. **Docker Compose yo'q.** TT "bitta buyruq bilan ishga tushirish" ni so'raydi.
