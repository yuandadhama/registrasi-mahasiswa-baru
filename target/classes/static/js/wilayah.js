/**
 * wilayah.js
 * Fetch provinsi & kota melalui proxy backend Spring Boot
 * sehingga tidak ada masalah CORS.
 * Endpoint: /api/wilayah/provinsi dan /api/wilayah/kota/{id}
 */

const provinsiSelect = document.getElementById("provinsi");
const kotaSelect = document.getElementById("kota");
const kotaHint = document.getElementById("kotaHint");

// =========================
// LOAD PROVINSI
// =========================
const loadProvinsi = async () => {
  try {
    provinsiSelect.innerHTML = `<option value="">-- Memuat data... --</option>`;
    provinsiSelect.disabled = true;

    const response = await fetch("/api/wilayah/provinsi");

    if (!response.ok) throw new Error("Gagal fetch provinsi");

    const data = await response.json();

    provinsiSelect.innerHTML = `<option value="">-- Pilih Provinsi --</option>`;

    data.forEach((prov) => {
      const opt = document.createElement("option");
      opt.value = prov.id;
      opt.textContent = prov.name;
      provinsiSelect.appendChild(opt);
    });

    provinsiSelect.disabled = false;
  } catch (err) {
    console.error("Error memuat provinsi:", err);
    provinsiSelect.innerHTML = `<option value="">-- Gagal memuat, coba refresh --</option>`;
    provinsiSelect.disabled = false;
  }
};

// =========================
// LOAD KOTA
// =========================
const loadKota = async (provinsiId) => {
  try {
    kotaSelect.disabled = true;
    kotaSelect.innerHTML = `<option value="">-- Memuat kota... --</option>`;
    kotaHint.textContent = "⏳ Sedang memuat data kota...";

    const response = await fetch(`/api/wilayah/kota/${provinsiId}`);

    if (!response.ok) throw new Error("Gagal fetch kota");

    const data = await response.json();

    kotaSelect.innerHTML = `<option value="">-- Pilih Kabupaten/Kota --</option>`;

    data.forEach((kota) => {
      const opt = document.createElement("option");
      opt.value = kota.name;
      opt.textContent = kota.name;
      kotaSelect.appendChild(opt);
    });

    kotaSelect.disabled = false;
    kotaHint.textContent = `ℹ ${data.length} kabupaten/kota tersedia`;
  } catch (err) {
    console.error("Error memuat kota:", err);
    kotaSelect.innerHTML = `<option value="">-- Gagal memuat data --</option>`;
    kotaHint.textContent = "⚠ Gagal memuat data kota";
    kotaSelect.disabled = false;
  }
};

// =========================
// EVENT: PROVINSI BERUBAH
// =========================
provinsiSelect.addEventListener("change", () => {
  const id = provinsiSelect.value;

  if (!id) {
    kotaSelect.disabled = true;
    kotaSelect.innerHTML = `<option value="">-- Pilih provinsi dulu --</option>`;
    kotaHint.textContent = "ℹ Pilih provinsi terlebih dahulu";
    return;
  }

  loadKota(id);
});

// =========================
// INIT
// =========================
loadProvinsi();
