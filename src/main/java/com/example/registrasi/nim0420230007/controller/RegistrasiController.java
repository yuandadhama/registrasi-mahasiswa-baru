package com.example.registrasi.nim0420230007.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.example.registrasi.nim0420230007.model.CalonMahasiswa;
import com.example.registrasi.nim0420230007.service.CalonMahasiswaService;
import com.example.registrasi.nim0420230007.service.CaptchaService;

import java.util.Arrays;
import java.util.List;

@Controller
public class RegistrasiController {

    @Autowired
    private CalonMahasiswaService calonMahasiswaService;

    @Autowired
    private CaptchaService captchaService;

    // Daftar program studi yang tersedia
    private static final List<String> PROGRAM_STUDI = Arrays.asList(
            "Teknik Informatika",
            "Sistem Informasi",
            "Rekayasa Perangkat Lunak",
            "Bahasa Inggris",
            "Manajemen",
            "Akuntansi");

    @GetMapping("/")
    public String showForm(Model model, HttpSession session) {
        // Generate captcha baru
        String captchaCode = captchaService.generateCaptcha();
        session.setAttribute("captchaCode", captchaCode);

        model.addAttribute("calonMahasiswa", new CalonMahasiswa());
        model.addAttribute("programStudiList", PROGRAM_STUDI);
        model.addAttribute("captchaCode", captchaCode);
        model.addAttribute("tahunAkademik", "2026/2027");

        return "pendaftaran";
    }

    /**
     * Refresh captcha tanpa reload halaman (AJAX endpoint).
     * Mengembalikan kode captcha baru sebagai plain text.
     */
    @GetMapping("/captcha/refresh")
    @ResponseBody
    public String refreshCaptcha(HttpSession session) {
        String captchaCode = captchaService.generateCaptcha();
        session.setAttribute("captchaCode", captchaCode);
        return captchaCode;
    }

    /**
     * Proses submit form pendaftaran.
     * Validasi field, captcha, usia, email unik, NIM unik.
     */
    @PostMapping("/daftar")
    public String processDaftar(
            @Valid @ModelAttribute("calonMahasiswa") CalonMahasiswa calon,
            BindingResult bindingResult,
            @RequestParam(value = "captchaInput", defaultValue = "") String captchaInput,
            HttpSession session,
            Model model) {
        String sessionCaptcha = (String) session.getAttribute("captchaCode");

        // Tambahkan list prodi & captcha baru ke model (untuk re-render form jika ada
        // error)
        model.addAttribute("programStudiList", PROGRAM_STUDI);
        model.addAttribute("tahunAkademik", "2026/2027");

        // Validasi captcha
        boolean captchaValid = captchaService.validate(captchaInput, sessionCaptcha);
        if (!captchaValid) {
            // Generate captcha baru setelah gagal
            String newCaptcha = captchaService.generateCaptcha();
            session.setAttribute("captchaCode", newCaptcha);
            model.addAttribute("captchaCode", newCaptcha);
            model.addAttribute("captchaError", "Kode captcha tidak sesuai. Silakan coba lagi.");
            return "pendaftaran";
        }

        // Validasi usia minimal 18 tahun
        if (calon.getTanggalLahir() != null && !calonMahasiswaService.isAgeValid(calon.getTanggalLahir())) {
            bindingResult.rejectValue("tanggalLahir", "usia.invalid", "Calon mahasiswa harus berusia minimal 18 tahun");
        }

        // Jika ada error validasi, kembalikan ke form
        if (bindingResult.hasErrors()) {
            String newCaptcha = captchaService.generateCaptcha();
            session.setAttribute("captchaCode", newCaptcha);
            model.addAttribute("captchaCode", newCaptcha);
            return "pendaftaran";
        }

        // Proses simpan ke database
        try {
            calonMahasiswaService.daftar(calon);
            // Invalidate session captcha setelah berhasil
            session.removeAttribute("captchaCode");
            return "redirect:/sukses";
        } catch (IllegalArgumentException e) {
            // Error business logic (email duplikat, NIM duplikat, dll)
            String newCaptcha = captchaService.generateCaptcha();
            session.setAttribute("captchaCode", newCaptcha);
            model.addAttribute("captchaCode", newCaptcha);
            model.addAttribute("errorMessage", e.getMessage());
            return "pendaftaran";
        }
    }

    /**
     * Halaman sukses setelah pendaftaran berhasil.
     */
    @GetMapping("/sukses")
    public String sukses() {
        return "sukses";
    }
}
