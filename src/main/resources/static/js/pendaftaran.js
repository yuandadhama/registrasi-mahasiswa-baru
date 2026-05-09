/**
 * pendaftaran.js
 * Script validasi frontend untuk form pendaftaran calon mahasiswa baru.
 * Mencakup: validasi real-time, captcha refresh via AJAX.
 */

// ===================================
// Refresh Captcha via AJAX
// ===================================
function refreshCaptcha() {
    fetch('/captcha/refresh')
        .then(response => response.text())
        .then(code => {
            document.getElementById('captchaText').textContent = code;
            document.getElementById('captchaInput').value = '';
        })
        .catch(err => console.error('Gagal refresh captcha:', err));
}

// ===================================
// Validasi Real-time Field
// ===================================
document.addEventListener('DOMContentLoaded', function () {

    const form = document.getElementById('formPendaftaran');
    const btnDaftar = document.getElementById('btnDaftar');

    // Field yang wajib diisi
    const namaInput       = document.getElementById('namaLengkap');
    const nimInput        = document.getElementById('nim');
    const emailInput      = document.getElementById('email');
    const teleponInput    = document.getElementById('nomorTelepon');
    const tanggalInput    = document.getElementById('tanggalLahir');
    const prodiInput      = document.getElementById('programStudi');
    const alamatInput     = document.getElementById('alamat');
    const asalInput       = document.getElementById('asalSekolah');
    const captchaInput    = document.getElementById('captchaInput');

    // Validasi nama: minimal 4 karakter
    namaInput.addEventListener('input', function () {
        if (this.value.length > 0 && this.value.length < 4) {
            showError(this, 'Inputan nama tidak valid, kurang dari empat karakter');
        } else {
            clearError(this);
        }
    });

    // Validasi NIM: hanya angka, tepat 10 digit
    nimInput.addEventListener('input', function () {
        const val = this.value.replace(/\D/g, ''); // hanya angka
        this.value = val; // hapus non-angka secara langsung
        if (val.length > 0 && val.length < 10) {
            showError(this, 'Panjang NIM tidak valid');
        } else {
            clearError(this);
        }
    });

    // Validasi email: format standar
    emailInput.addEventListener('blur', function () {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (this.value && !emailRegex.test(this.value)) {
            showError(this, 'Format email tidak valid');
        } else {
            clearError(this);
        }
    });

    // Validasi nomor telepon: diawali 08/62, panjang 10-13 digit
    teleponInput.addEventListener('input', function () {
        const val = this.value.replace(/\D/g, '');
        this.value = val;
        if (val.length > 0) {
            const validStart = val.startsWith('08') || val.startsWith('62');
            const validLength = val.length >= 10 && val.length <= 13;
            if (!validStart || !validLength) {
                showError(this, 'Nomor telepon tidak valid. Diawali 08 atau 62, panjang 10-13 digit.');
            } else {
                clearError(this);
            }
        }
    });

    // Validasi tanggal lahir: usia minimal 18 tahun
    tanggalInput.addEventListener('change', function () {
        const tglLahir = new Date(this.value);
        const today = new Date();
        const usia = today.getFullYear() - tglLahir.getFullYear();
        const bulanBeda = today.getMonth() - tglLahir.getMonth();
        const hariValid = bulanBeda > 0 || (bulanBeda === 0 && today.getDate() >= tglLahir.getDate());
        const usiaFinal = hariValid ? usia : usia - 1;

        if (this.value && usiaFinal < 18) {
            showError(this, 'Calon mahasiswa harus berusia minimal 18 tahun');
        } else {
            clearError(this);
        }
    });

    // Validasi alamat: minimal 15 karakter
    alamatInput.addEventListener('input', function () {
        if (this.value.length > 0 && this.value.length < 15) {
            showError(this, 'Alamat minimal 15 karakter');
        } else {
            clearError(this);
        }
    });

    // ===================================
    // Cek kelengkapan form untuk enable/disable tombol
    // ===================================
    const allFields = [namaInput, nimInput, emailInput, teleponInput, tanggalInput, prodiInput, alamatInput, asalInput, captchaInput];
    allFields.forEach(field => {
        field.addEventListener('input', checkFormValidity);
        field.addEventListener('change', checkFormValidity);
    });

    function checkFormValidity() {
        const namaOk    = namaInput.value.length >= 4;
        const nimOk     = nimInput.value.length === 10;
        const emailOk   = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(emailInput.value);
        const teleponOk = /^(08|62)\d{8,11}$/.test(teleponInput.value);
        const tglOk     = tanggalInput.value !== '';
        const prodiOk   = prodiInput.value !== '';
        const alamatOk  = alamatInput.value.length >= 15;
        const asalOk    = asalInput.value.length > 0;
        const captchaOk = captchaInput.value.length >= 4;

        btnDaftar.disabled = !(namaOk && nimOk && emailOk && teleponOk && tglOk && prodiOk && alamatOk && asalOk && captchaOk);
    }

    // Initial check
    checkFormValidity();
});

// ===================================
// Helper: tampilkan dan hapus error inline
// ===================================
function showError(inputEl, message) {
    inputEl.classList.add('is-invalid');
    // Cari elemen error di bawah input
    const parent = inputEl.closest('.form-group');
    if (parent) {
        let errEl = parent.querySelector('.js-error');
        if (!errEl) {
            errEl = document.createElement('div');
            errEl.className = 'field-hint error-text js-error';
            inputEl.insertAdjacentElement('afterend', errEl);
        }
        errEl.textContent = message;
    }
}

function clearError(inputEl) {
    inputEl.classList.remove('is-invalid');
    const parent = inputEl.closest('.form-group');
    if (parent) {
        const errEl = parent.querySelector('.js-error');
        if (errEl) errEl.remove();
    }
}
