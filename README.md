# 📚 Sistem Registrasi Calon Mahasiswa Baru

Aplikasi web berbasis **Spring Boot** untuk mengelola pendaftaran calon mahasiswa baru. Dibangun dengan arsitektur MVC menggunakan Thymeleaf sebagai template engine dan Spring Security untuk autentikasi admin.

---

## 🚀 Fitur Aplikasi

### Fitur Utama

- **Form Pendaftaran Publik** — Calon mahasiswa dapat mengisi form pendaftaran dengan validasi lengkap
- **Autentikasi Admin** — Halaman admin dilindungi Spring Security (login required)
- **Dashboard Admin** — Menampilkan statistik dan daftar seluruh pendaftar
- **Verifikasi Pendaftaran** — Admin dapat mengubah status PENDING → VERIFIED / REJECTED
- **Halaman Pencarian** — Admin dapat mencari pendaftar berdasarkan nama, NIM, atau status

### ⭐ Fitur Unik (Tidak Ada di Soal)

1. **Live Search dengan Debounce** — Hasil pencarian muncul otomatis setiap user mengetik tanpa perlu klik tombol search. Dilengkapi debounce 400ms untuk menghindari spam request ke server.
2. **Paginasi Dashboard** — Daftar mahasiswa di dashboard dikelompokkan 10 data per halaman dengan navigasi halaman (Previous / Next / Nomor Halaman). Nomor urut tetap berlanjut antar halaman (tidak reset ke 1).

### 🌐 Penggunaan Open API (Non-Captcha)

- **API Wilayah Indonesia** (`emsifa.github.io/api-wilayah-indonesia`) — Dropdown Provinsi dan Kabupaten/Kota pada form pendaftaran diisi secara dinamis dari API eksternal. Fetch dilakukan melalui proxy backend Spring Boot untuk menghindari masalah CORS.

---

## 🛠️ Teknologi yang Digunakan

| Teknologi               | Versi  | Kegunaan                                |
| ----------------------- | ------ | --------------------------------------- |
| Java                    | 17+    | Bahasa pemrograman utama                |
| Spring Boot             | 3.x    | Framework backend                       |
| Spring Security         | 3.x    | Autentikasi & otorisasi                 |
| Spring Data JPA         | 3.x    | ORM & akses database                    |
| Thymeleaf               | 3.x    | Template engine (server-side rendering) |
| MySQL                   | 8.x    | Database                                |
| Lombok                  | Latest | Mengurangi boilerplate code             |
| HTML / CSS / JavaScript | -      | Frontend                                |

---

## ⚙️ Cara Install & Menjalankan

### Prasyarat

Pastikan sudah terinstall:

- **Java 17** atau lebih baru
- **Maven** 3.6+
- **MySQL** 8.0+
- **Git**

### 1. Clone Repository

```bash
git clone https://github.com/username/registrasi-mahasiswa.git
cd registrasi-mahasiswa
```

### 2. Buat Database MySQL

```sql
CREATE DATABASE db_registrasi;
```

### 3. Konfigurasi Database

Buka file `src/main/resources/application.properties` dan sesuaikan:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/db_registrasi
spring.datasource.username=root
spring.datasource.password=password_anda

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

spring.thymeleaf.cache=false
```

### 4. Jalankan Aplikasi

```bash
mvn spring-boot:run
```

### 5. (Opsional) Import Data Dummy

Untuk testing dengan 35 data mahasiswa, jalankan file SQL berikut:

```bash
mysql -u root -p registrasi_mahasiswa < seed_mahasiswa.sql
```

### 6. Akses Aplikasi

| URL                                     | Keterangan                |
| --------------------------------------- | ------------------------- |
| `http://localhost:8080/`                | Form pendaftaran (publik) |
| `http://localhost:8080/login`           | Halaman login admin       |
| `http://localhost:8080/admin/dashboard` | Dashboard admin           |
| `http://localhost:8080/admin/pencarian` | Pencarian pendaftar       |

---

## 🔐 Kredensial Admin

```
Username : admin
Password : admin123
```

## 📸 Alur Penggunaan

### Pendaftar (Publik)

1. Buka `http://localhost:8080/`
2. Isi form pendaftaran lengkap
3. Pilih provinsi → kota otomatis terisi berdasarkan provinsi
4. Masukkan kode captcha
5. Klik **Daftar Sekarang**
6. Muncul halaman sukses, status awal **PENDING**

### Admin

1. Login di `http://localhost:8080/login`
2. Dashboard menampilkan statistik dan daftar pendaftar (10 per halaman)
3. Gunakan navigasi halaman untuk berpindah antar halaman
4. Gunakan menu **Pencarian** untuk mencari pendaftar secara real-time
5. Klik **Verifikasi** pada baris dengan status PENDING
6. Pilih status **Terverifikasi** atau **Ditolak**, lalu submit
