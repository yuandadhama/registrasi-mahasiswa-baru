package com.example.registrasi.nim0420230007.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.registrasi.nim0420230007.model.CalonMahasiswa;
import com.example.registrasi.nim0420230007.model.CalonMahasiswa.StatusPendaftaran;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface CalonMahasiswaRepository extends JpaRepository<CalonMahasiswa, Long> {

        Page<CalonMahasiswa> findAllByOrderByTanggalDaftarDesc(Pageable pageable);

        /**
         * Cek apakah email sudah digunakan (mencegah double submission).
         */
        boolean existsByEmail(String email);

        /**
         * Cek apakah NIM sudah digunakan.
         */
        boolean existsByNim(String nim);

        /**
         * Cari calon mahasiswa berdasarkan NIM (exact match).
         */
        Optional<CalonMahasiswa> findByNim(String nim);

        /**
         * Pencarian berdasarkan nama (mengandung keyword, case-insensitive) atau NIM.
         * Bisa difilter berdasarkan status.
         */
        @Query("SELECT c FROM CalonMahasiswa c WHERE " +
                        "(LOWER(c.namaLengkap) LIKE LOWER(CONCAT('%', :keyword, '%')) OR c.nim LIKE CONCAT('%', :keyword, '%')) "
                        +
                        "AND (:status IS NULL OR c.status = :status)")
        List<CalonMahasiswa> searchByNamaOrNimAndStatus(
                        @Param("keyword") String keyword,
                        @Param("status") StatusPendaftaran status);

        /**
         * Hitung jumlah pendaftar berdasarkan status.
         */
        long countByStatus(StatusPendaftaran status);

        /**
         * Ambil semua pendaftar diurutkan dari terbaru.
         */
        List<CalonMahasiswa> findAllByOrderByTanggalDaftarDesc();
}
