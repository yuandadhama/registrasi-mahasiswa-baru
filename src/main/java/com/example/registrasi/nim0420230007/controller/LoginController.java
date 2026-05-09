package com.example.registrasi.nim0420230007.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller untuk halaman login admin.
 */
@Controller
public class LoginController {

    /**
     * Tampilkan halaman login admin.
     *
     * @param error  ada jika login gagal
     * @param logout ada jika user baru logout
     */
    @GetMapping("/login")
    public String login(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Username atau password salah. Silakan coba lagi.");
        }
        if (logout != null) {
            model.addAttribute("logoutMessage", "Anda berhasil logout.");
        }
        return "admin/login";
    }
}
