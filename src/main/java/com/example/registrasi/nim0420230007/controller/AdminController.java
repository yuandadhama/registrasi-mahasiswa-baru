package com.example.registrasi.nim0420230007.controller;

import com.example.registrasi.nim0420230007.model.CalonMahasiswa;
import com.example.registrasi.nim0420230007.model.CalonMahasiswa.StatusPendaftaran;
import com.example.registrasi.nim0420230007.service.CalonMahasiswaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private CalonMahasiswaService calonMahasiswaService;

    // =========================
    // DASHBOARD
    // =========================
    @GetMapping("/dashboard")
    public String dashboard(
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        int size = 10;

        Pageable pageable = PageRequest.of(page, size);

        Page<CalonMahasiswa> data = calonMahasiswaService.getAllPaginated(pageable);

        model.addAttribute("totalPendaftar", calonMahasiswaService.countAll());
        model.addAttribute("menungguVerifikasi", calonMahasiswaService.countPending());
        model.addAttribute("terverifikasi", calonMahasiswaService.countVerified());

        model.addAttribute("daftarCalon", data.getContent());

        // pagination tambahan (TIDAK MENGHAPUS APA PUN)
        model.addAttribute("currentPage", data.getNumber());
        model.addAttribute("totalPages", data.getTotalPages());

        return "admin/dashboard";
    }

    // =========================
    // PAGE SEARCH (THYMELEAF)
    // =========================
    @GetMapping("/pencarian")
    public String pencarian(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "SEMUA") String status,
            @RequestParam(defaultValue = "SEMUA") String jurusan,
            Model model) {

        StatusPendaftaran statusFilter = parseStatus(status);

        List<CalonMahasiswa> hasil = calonMahasiswaService.search(
                keyword,
                statusFilter,
                jurusan);

        model.addAttribute("keyword", keyword);
        model.addAttribute("statusFilter", status);
        model.addAttribute("jurusanFilter", jurusan);
        model.addAttribute("hasilPencarian", hasil);
        model.addAttribute("jumlahHasil", hasil.size());

        return "admin/pencarian";
    }

    // =========================
    // LIVE SEARCH WITH PAGINATION (AJAX)
    // =========================
    @GetMapping(value = "/pencarian/ajax", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> pencarianAjax(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "SEMUA") String status,
            @RequestParam(defaultValue = "0") int page) {

        StatusPendaftaran statusFilter = parseStatus(status);

        List<CalonMahasiswa> semua = calonMahasiswaService.search(keyword, statusFilter, "SEMUA");

        // Pagination manual
        int pageSize = 10;
        int totalItems = semua.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        int fromIndex = page * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, totalItems);

        List<CalonMahasiswa> pageData = (fromIndex >= totalItems)
                ? List.of()
                : semua.subList(fromIndex, toIndex);

        Map<String, Object> response = new HashMap<>();
        response.put("data", pageData);
        response.put("currentPage", page);
        response.put("totalPages", totalPages);
        response.put("totalItems", totalItems);

        return response;
    }

    // =========================
    // VERIFIKASI PAGE
    // =========================
    @GetMapping("/verifikasi/{id}")
    public String showVerifikasi(@PathVariable Long id, Model model) {

        Optional<CalonMahasiswa> calon = calonMahasiswaService.findById(id);

        if (calon.isEmpty()) {
            return "redirect:/admin/dashboard";
        }

        model.addAttribute("calon", calon.get());
        return "admin/verifikasi";
    }

    // =========================
    // PROCESS VERIFIKASI
    // =========================
    @PostMapping("/verifikasi/{id}")
    public String processVerifikasi(
            @PathVariable Long id,
            @RequestParam String status,
            RedirectAttributes redirectAttributes) {

        try {
            StatusPendaftaran statusBaru = StatusPendaftaran.valueOf(status.toUpperCase());

            calonMahasiswaService.verifikasi(id, statusBaru);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Status berhasil diperbarui");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Terjadi kesalahan");
        }

        return "redirect:/admin/dashboard";
    }

    // =========================
    // SAFE PARSER (ANTI ERROR)
    // =========================
    private StatusPendaftaran parseStatus(String status) {
        if (status == null || status.isBlank() || status.equalsIgnoreCase("SEMUA")) {
            return null;
        }

        try {
            return StatusPendaftaran.valueOf(status.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }
}