# Aplikasi Registrasi Calon Mahasiswa Baru

Aplikasi web untuk pendaftaran calon mahasiswa baru berbasis **Spring Boot 3.x**, **Java 21**, **Thymeleaf**, dan **MySQL**.

**UAS Framework Based Programming — Jakarta International University**
**Tahun Akademik 2026/2027**

---

## Fitur Utama

### Halaman Publik (Calon Mahasiswa)
- Form pendaftaran dengan validasi lengkap (nama, NIM, email unik, telepon, usia min 18 tahun, alamat, asal sekolah)
- Kode keamanan CAPTCHA + tombol refresh
- Tombol submit hanya aktif jika semua field terisi dengan benar
- Halaman notifikasi pendaftaran berhasil

### Halaman Admin
- Login admin dengan autentikasi Spring Security
- Dashboard: statistik total pendaftar, menunggu verifikasi, terverifikasi
- Tabel daftar semua calon mahasiswa + status badge (PENDING, VERIFIED, REJECTED)
- Fitur pencarian berdasarkan nama/NIM + filter status
- Halaman verifikasi individual: ubah status ke VERIFIED atau REJECTED

---

## Tech Stack

| Komponen       | Versi/Detail              |
|----------------|---------------------------|
| Framework      | Spring Boot 3.2.5         |
| Java Version   | 21 (LTS)                  |
| Template       | Thymeleaf                 |
| Database       | MySQL 8.x                 |
| ORM            | Spring Data JPA (Hibernate)|
| Security       | Spring Security 6.x       |
| Build Tool     | Maven                     |

---

## Cara Install & Menjalankan

### 1. Prasyarat
Pastikan sudah terinstall:
- **Java 21** (JDK) — download di https://adoptium.net/
- **MySQL Server 8.x** — download di https://dev.mysql.com/
- **Maven 3.x** — download di https://maven.apache.org/
- IDE: IntelliJ IDEA / Eclipse / VS Code (opsional)

### 2. Buat Database MySQL

Buka MySQL client / Workbench, jalankan perintah berikut:

```sql
CREATE DATABASE db_registrasi CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Konfigurasi Koneksi Database

Edit file `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/db_registrasi?useSSL=false&serverTimezone=Asia/Jakarta&allowPublicKeyRetrieval=true
spring.datasource.username=root         # Ganti dengan username MySQL Anda
spring.datasource.password=             # Ganti dengan password MySQL Anda
```

> Tabel `calon_mahasiswa` akan dibuat otomatis oleh Hibernate (`ddl-auto=update`).

### 4. Clone / Extract Project

Jika dari GitHub:
```bash
git clone https://github.com/username/registrasi-nim00000000.git
cd registrasi-nim00000000
```

Jika dari ZIP, extract dan masuk ke folder project.

### 5. Build Project

```bash
mvn clean install
```

### 6. Jalankan Aplikasi

```bash
mvn spring-boot:run
```

Atau jalankan file JAR hasil build:
```bash
java -jar target/registrasi-nim00000000-0.0.1-SNAPSHOT.jar
```

### 7. Buka di Browser

| URL                              | Keterangan                       |
|----------------------------------|----------------------------------|
| http://localhost:8080/           | Form Pendaftaran Mahasiswa Baru  |
| http://localhost:8080/login      | Login Admin                      |
| http://localhost:8080/admin/dashboard | Dashboard Admin (setelah login) |

---

## Kredensial Admin Default

| Field    | Value     |
|----------|-----------|
| Username | `admin`   |
| Password | `admin123`|

> Untuk mengubah password, edit method `userDetailsService()` di `SecurityConfig.java`.

---

## Struktur Package

```
src/main/java/com/example/registrasi/nim00000000/
├── RegistrasiApplication.java          # Entry point aplikasi
├── config/
│   └── SecurityConfig.java             # Konfigurasi Spring Security
├── controller/
│   ├── AdminController.java            # Controller halaman admin
│   ├── LoginController.java            # Controller login admin
│   └── RegistrasiController.java       # Controller form pendaftaran publik
├── model/
│   └── CalonMahasiswa.java             # Entity JPA model
├── repository/
│   └── CalonMahasiswaRepository.java   # Spring Data JPA repository
└── service/
    ├── CalonMahasiswaService.java      # Business logic pendaftaran
    └── CaptchaService.java             # Generate & validasi CAPTCHA

src/main/resources/
├── application.properties              # Konfigurasi aplikasi
├── static/
│   ├── css/style.css                   # Stylesheet utama
│   └── js/pendaftaran.js               # Validasi JavaScript frontend
└── templates/
    ├── pendaftaran.html                # Form pendaftaran mahasiswa baru
    ├── sukses.html                     # Halaman notifikasi sukses
    └── admin/
        ├── login.html                  # Halaman login admin
        ├── dashboard.html              # Dashboard admin
        ├── pencarian.html              # Pencarian calon mahasiswa
        └── verifikasi.html             # Halaman verifikasi individual
```

---

## Aturan Validasi

| Field          | Aturan                                                   |
|----------------|----------------------------------------------------------|
| Nama Lengkap   | Wajib diisi, minimal 4 karakter                          |
| NIM            | Hanya angka, tepat 10 digit, unik                        |
| Email          | Format standar email, unik (tidak bisa daftar 2x)        |
| Nomor Telepon  | Diawali 08 atau 62, panjang 10–13 digit                  |
| Tanggal Lahir  | Wajib berusia minimal 18 tahun                           |
| Program Studi  | Wajib dipilih dari dropdown                              |
| Alamat         | Minimal 15 karakter                                      |
| Asal Sekolah   | Wajib diisi                                              |
| Captcha        | Wajib cocok (case-insensitive), ada fitur refresh        |

---

## Catatan Pengembangan

- Aplikasi menggunakan Spring Security in-memory authentication (cukup untuk development/UAS)
- Untuk production, gunakan database-based authentication dengan BCrypt
- CAPTCHA yang digunakan adalah custom captcha berbasis server-side (tanpa third-party API)
- `ddl-auto=update` akan otomatis membuat/memperbarui tabel saat aplikasi dijalankan

---

*Dibuat untuk UAS Framework Based Programming — Jakarta International University, Mei 2026*
