package com.example.registrasi.nim0420230007.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "calon_mahasiswa")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalonMahasiswa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nama lengkap wajib diisi")
    @Size(min = 4, message = "Inputan nama tidak valid, kurang dari empat karakter")
    @Column(name = "nama_lengkap", nullable = false)
    private String namaLengkap;

    /**
     * NIM: hanya angka, tepat 10 digit.
     */
    @NotBlank(message = "NIM wajib diisi")
    @Pattern(regexp = "\\d{10}", message = "Panjang NIM tidak valid")
    @Column(name = "nim", nullable = false, unique = true, length = 10)
    private String nim;

    /**
     * Email: format standar, unik (tidak boleh registrasi 2x).
     */
    @NotBlank(message = "Email wajib diisi")
    @Email(message = "Format email tidak valid")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    /**
     * Nomor telepon: diawali 08 atau 62, panjang 10-13 digit.
     */
    @NotBlank(message = "Nomor telepon wajib diisi")
    @Pattern(regexp = "(08|62)\\d{8,11}", message = "Nomor telepon tidak valid. Contoh: 081234567890 (10-13 digit, diawali 08 atau 62)")
    @Column(name = "nomor_telepon", nullable = false, length = 15)
    private String nomorTelepon;

    /**
     * Tanggal lahir: calon mahasiswa minimal berumur 18 tahun.
     */
    @NotNull(message = "Tanggal lahir wajib diisi")
    @Column(name = "tanggal_lahir", nullable = false)
    private LocalDate tanggalLahir;

    /**
     * Program studi yang dipilih.
     */
    @NotBlank(message = "Program studi wajib dipilih")
    @Column(name = "program_studi", nullable = false)
    private String programStudi;

    /**
     * Alamat lengkap, minimal 15 karakter.
     */
    @NotBlank(message = "Alamat wajib diisi")
    @Size(min = 15, message = "Alamat minimal 15 karakter. Semakin detail semakin baik")
    @Column(name = "alamat", nullable = false, columnDefinition = "TEXT")
    private String alamat;

    /**
     * Asal sekolah calon mahasiswa.
     */
    @NotBlank(message = "Asal sekolah wajib diisi")
    @Column(name = "asal_sekolah", nullable = false)
    private String asalSekolah;

    /**
     * Status pendaftaran: PENDING, VERIFIED, REJECTED.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusPendaftaran status = StatusPendaftaran.PENDING;

    /**
     * Tanggal dan waktu pendaftaran (auto-set saat insert).
     */
    @Column(name = "tanggal_daftar", nullable = false)
    private LocalDateTime tanggalDaftar;

    /**
     * Set tanggal daftar otomatis sebelum insert.
     */
    @PrePersist
    protected void onCreate() {
        this.tanggalDaftar = LocalDateTime.now();
        if (this.status == null) {
            this.status = StatusPendaftaran.PENDING;
        }
    }

    /**
     * Enum status pendaftaran calon mahasiswa.
     */
    public enum StatusPendaftaran {
        PENDING, VERIFIED, REJECTED
    }
}
