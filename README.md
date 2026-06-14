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

### Prerequisites
- Java 25+
- Maven 3.9+

### Langkah Instalasi

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

### Konfigurasi Database
Database H2 berjalan otomatis saat backend distart. H2 Console tersedia di:
```
http://localhost:8080/h2-console
JDBC URL : jdbc:h2:file:./data/taskly
Username : sa
Password : (kosong)
```

---

## Video Presentasi

[Tambahkan link YouTube di sini]

---

## Tim Pengembang

| Nama | Role |
|------|------|
| Edric Roland Li | Backend (Security, Auth, Task API, Frontend Service) |
| Muhammad Rizky Fadhillah | Backend (Models, Repository) + Frontend (Dashboard, Forms) |
| Yasmin Assyifa | Frontend (UI/UX, Login, Register, CSS) |
| Najla Az Zahra Tanjung | Backend (DTO, Exception Handler) + Frontend (Helpers) |
