package com.example.registrasi.nim0420230007.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.registrasi.nim0420230007.model.CalonMahasiswa;
import com.example.registrasi.nim0420230007.model.CalonMahasiswa.StatusPendaftaran;
import com.example.registrasi.nim0420230007.repository.CalonMahasiswaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

@Service
public class CalonMahasiswaService {

  @Autowired
  private CalonMahasiswaRepository repository;

  /**
   * Menyimpan data pendaftaran calon mahasiswa baru.
   * Validasi: email unik, NIM unik, usia minimal 18 tahun.
   *
   * @param calon data calon mahasiswa
   * @throws IllegalArgumentException jika data tidak valid
   */
  @Transactional
  public CalonMahasiswa daftar(CalonMahasiswa calon) {
    // Validasi email unik (mencegah double submission)
    if (repository.existsByEmail(calon.getEmail())) {
      throw new IllegalArgumentException(
          "Email sudah digunakan untuk mendaftar. Setiap email hanya dapat digunakan satu kali.");
    }

    // Validasi NIM unik
    if (repository.existsByNim(calon.getNim())) {
      throw new IllegalArgumentException("NIM sudah terdaftar dalam sistem.");
    }

    // Validasi usia minimal 18 tahun
    if (!isAgeValid(calon.getTanggalLahir())) {
      throw new IllegalArgumentException("Calon mahasiswa harus berusia minimal 18 tahun.");
    }

    // Set status awal PENDING
    calon.setStatus(StatusPendaftaran.PENDING);

    return repository.save(calon);
  }

  /**
   * Mengambil semua data calon mahasiswa, diurutkan dari terbaru.
   */
  public List<CalonMahasiswa> getAll() {
    return repository.findAllByOrderByTanggalDaftarDesc();
  }

  /**
   * Mencari calon mahasiswa berdasarkan ID.
   */
  public Optional<CalonMahasiswa> findById(Long id) {
    return repository.findById(id);
  }

  /**
   * Melakukan verifikasi pendaftaran: ubah status menjadi VERIFIED atau REJECTED.
   *
   * @param id     ID calon mahasiswa
   * @param status status baru (VERIFIED atau REJECTED)
   */
  @Transactional
  public CalonMahasiswa verifikasi(Long id, StatusPendaftaran status) {
    CalonMahasiswa calon = repository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Data calon mahasiswa tidak ditemukan."));
    calon.setStatus(status);
    return repository.save(calon);
  }

  /**
   * Mengambil statistik jumlah pendaftar berdasarkan status.
   */
  public long countPending() {
    return repository.countByStatus(StatusPendaftaran.PENDING);
  }

  public long countVerified() {
    return repository.countByStatus(StatusPendaftaran.VERIFIED);
  }

  public long countAll() {
    return repository.count();
  }

  /**
   * Validasi usia: hitung apakah calon mahasiswa sudah berumur >= 18 tahun.
   *
   * @param tanggalLahir tanggal lahir calon mahasiswa
   * @return true jika usia >= 18 tahun
   */
  public boolean isAgeValid(LocalDate tanggalLahir) {
    if (tanggalLahir == null)
      return false;
    int usia = Period.between(tanggalLahir, LocalDate.now()).getYears();
    return usia >= 18;
  }

  public List<CalonMahasiswa> search(
      String keyword,
      StatusPendaftaran status,
      String jurusan) {

    List<CalonMahasiswa> all = repository.findAllByOrderByTanggalDaftarDesc();

    String kw = normalize(keyword);
    String jr = normalize(jurusan);

    return all.stream()
        .filter(c -> {

          // =====================
          // KEYWORD
          // =====================
          boolean matchKeyword = kw.isBlank()
              || normalize(c.getNamaLengkap()).contains(kw)
              || normalize(c.getNim()).contains(kw);

          // =====================
          // STATUS
          // =====================
          boolean matchStatus = status == null
              || c.getStatus() == status;

          // =====================
          // JURUSAN
          // =====================
          boolean matchJurusan = jr.isBlank()
              || jr.equals("semua")
              || normalize(c.getProgramStudi()).equals(jr);

          return matchKeyword && matchStatus && matchJurusan;
        })
        .toList();
  }

  public Page<CalonMahasiswa> getAllPaginated(Pageable pageable) {
    return repository.findAllByOrderByTanggalDaftarDesc(pageable);
  }

  private String normalize(String text) {
    if (text == null)
      return "";
    return text
        .trim()
        .toLowerCase()
        .replaceAll("\\s+", " "); // hilangkan spasi ganda
  }
}
