# Taskly

Taskly adalah aplikasi manajemen tugas akademik berbasis desktop yang membantu mahasiswa mengorganisir, memantau, dan menyelesaikan tugas dengan lebih efisien. Dibangun menggunakan Java dengan arsitektur client-server (JavaFX frontend + Spring Boot backend).

---

## Fitur Utama

- **Autentikasi Pengguna** — Register dan login dengan keamanan JWT.
- **Manajemen Tugas** — Tambah, edit, hapus, dan lihat tugas (Assignment, Exam, Group Task).
- **Prioritas Tugas** — Atur prioritas tugas: Low, Medium, High.
- **Status Tugas** — Pantau progress tugas: Pending, In Progress, Completed.
- **Dashboard** — Tampilan ringkasan semua tugas aktif dengan filter dan reminder.
- **Kategori Tugas** — Kelompokkan tugas berdasarkan mata kuliah atau kategori custom.

---

## Teknologi

| Komponen | Teknologi |
|----------|-----------|
| Backend  | Java 25, Spring Boot, Spring Security, JWT |
| Frontend | JavaFX, FXML |
| Database | H2 (embedded) |
| Build    | Maven (multi-module) |

---

## Cara Menjalankan

### Download & Install (Direkomendasikan)

1. Buka halaman [Releases](https://github.com/edricrolandli/UAS-PBO_Taskly_Chimera-DinoHappySekaliSepanjangMasa/releases/tag/v1.0.0)
2. Download **Taskly.zip**
3. Extract zip ke folder mana saja
4. Buka folder hasil extract, jalankan **Taskly.exe**

Tidak perlu install Java atau Maven — semua sudah tersedia di dalam paket.

---

### Menjalankan dari Source Code (Opsional)

Untuk developer yang ingin menjalankan dari kode sumber:

#### Prerequisites
- Java 25+
- Maven 3.9+

**1. Clone repositori**
```bash
git clone https://github.com/edricrolandli/UAS-PBO_Taskly_Chimera-DinoHappySekaliSepanjangMasa.git
cd UAS-PBO_Taskly_Chimera-DinoHappySekaliSepanjangMasa
```

**2. Jalankan Backend**
```bash
cd backend
mvn spring-boot:run
```
Backend berjalan di `http://localhost:8080`

**3. Jalankan Frontend** (terminal baru)
```bash
cd frontend
mvn javafx:run
```

#### Konfigurasi Database
Database H2 berjalan otomatis saat backend distart. H2 Console tersedia di:
```
http://localhost:8080/h2-console
JDBC URL : jdbc:h2:file:./data/taskly
Username : sa
Password : (kosong)
```

---

## Video Presentasi

[![Video Presentasi](https://img.shields.io/badge/YouTube-Tonton%20Presentasi-red?logo=youtube)](https://youtu.be/KK2zdouqNBQ)

---

## Tim Pengembang

| Nama | Role |
|------|------|
| Edric Roland Li | Backend (Security, Auth, Task API, Frontend Service) |
| Muhammad Rizky Fadhillah | Backend (Models, Repository) + Frontend (Dashboard, Forms) |
| Yasmin Assyifa | Frontend (UI/UX, Login, Register, CSS) |
| Najla Az Zahra Tanjung | Backend (DTO, Exception Handler) + Frontend (Helpers) |
