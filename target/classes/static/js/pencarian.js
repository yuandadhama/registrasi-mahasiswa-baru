document.addEventListener("DOMContentLoaded", () => {
  const keywordInput = document.getElementById("searchKeyword");
  const statusSelect = document.getElementById("statusFilter");
  const resultsContainer = document.getElementById("searchResults");
  const loadingIndicator = document.getElementById("loadingIndicator");

  let debounceTimer;
  let controller;
  let currentPage = 0; // ← track halaman aktif

  const debounceDelay = 400;

  const showLoading = () => {
    if (loadingIndicator) loadingIndicator.style.display = "block";
  };
  const hideLoading = () => {
    if (loadingIndicator) loadingIndicator.style.display = "none";
  };

  const escapeHtml = (text = "") => {
    return String(text)
      .replaceAll("&", "&amp;")
      .replaceAll("<", "&lt;")
      .replaceAll(">", "&gt;")
      .replaceAll('"', "&quot;")
      .replaceAll("'", "&#039;");
  };

  // =========================
  // RENDER PAGINATION
  // =========================
  const renderPagination = (current, total) => {
    if (total <= 1) return "";

    let html = `<div class="pagination">`;

    // Tombol Previous
    if (current > 0) {
      html += `<a class="page-btn" onclick="goToPage(${current - 1})">&laquo; Sebelumnya</a>`;
    } else {
      html += `<span class="page-btn page-btn-disabled">&laquo; Sebelumnya</span>`;
    }

    // Nomor halaman
    for (let i = 0; i < total; i++) {
      if (i === current) {
        html += `<span class="page-btn page-btn-active">${i + 1}</span>`;
      } else {
        html += `<a class="page-btn" onclick="goToPage(${i})">${i + 1}</a>`;
      }
    }

    // Tombol Next
    if (current < total - 1) {
      html += `<a class="page-btn" onclick="goToPage(${current + 1})">Selanjutnya &raquo;</a>`;
    } else {
      html += `<span class="page-btn page-btn-disabled">Selanjutnya &raquo;</span>`;
    }

    html += `</div>`;
    return html;
  };

  // =========================
  // RENDER EMPTY
  // =========================
  const renderEmpty = () => {
    resultsContainer.innerHTML = `
      <p class="result-count">Hasil Pencarian: <strong>0 data ditemukan</strong></p>
      <table class="data-table">
        <thead><tr><th>Nama</th><th>NIM</th><th>Prodi</th><th>Status</th><th>Aksi</th></tr></thead>
        <tbody><tr><td colspan="5" class="text-center">Tidak ada data.</td></tr></tbody>
      </table>`;
  };

  // =========================
  // RENDER RESULTS
  // =========================
  const renderResults = (data, currentPage, totalPages, totalItems) => {
    if (!Array.isArray(data) || data.length === 0) {
      renderEmpty();
      return;
    }

    const rows = data
      .map((calon) => {
        const badgeClass = {
          PENDING: "badge-pending",
          VERIFIED: "badge-verified",
          REJECTED: "badge-rejected",
        }[calon.status];
        return `
        <tr>
          <td>${escapeHtml(calon.namaLengkap)}</td>
          <td>${escapeHtml(calon.nim)}</td>
          <td>${escapeHtml(calon.programStudi)}</td>
          <td><span class="badge ${badgeClass}">${calon.status}</span></td>
          <td>${calon.status === "PENDING" ? `<a href="/admin/verifikasi/${calon.id}" class="btn-verifikasi">Verifikasi</a>` : "-"}</td>
        </tr>`;
      })
      .join("");

    resultsContainer.innerHTML = `
      <p class="result-count">Hasil Pencarian: <strong>${totalItems} data ditemukan</strong></p>
      <table class="data-table">
        <thead><tr><th>Nama</th><th>NIM</th><th>Prodi</th><th>Status</th><th>Aksi</th></tr></thead>
        <tbody>${rows}</tbody>
      </table>
      ${renderPagination(currentPage, totalPages)}`;
  };

  // =========================
  // FETCH DATA
  // =========================
  const fetchResults = async (page = 0) => {
    currentPage = page;

    const keyword = keywordInput.value.trim();
    const status = statusSelect.value;

    if (controller) controller.abort();
    controller = new AbortController();

    showLoading();

    try {
      const params = new URLSearchParams();
      params.append("keyword", keyword);
      params.append("status", status);
      params.append("page", page); // ← kirim nomor halaman

      const response = await fetch(
        `/admin/pencarian/ajax?${params.toString()}`,
        {
          signal: controller.signal,
          headers: { "X-Requested-With": "XMLHttpRequest" },
        },
      );

      if (!response.ok) throw new Error("Request gagal");

      const result = await response.json();

      renderResults(
        result.data,
        result.currentPage,
        result.totalPages,
        result.totalItems,
      );
    } catch (error) {
      if (error.name !== "AbortError") console.error(error);
    } finally {
      hideLoading();
    }
  };

  // Expose goToPage ke global scope untuk onclick di pagination
  window.goToPage = (page) => fetchResults(page);

  const debounceSearch = () => {
    clearTimeout(debounceTimer);
    debounceTimer = setTimeout(() => fetchResults(0), debounceDelay); // ← reset ke page 0 saat search baru
  };

  keywordInput.addEventListener("input", debounceSearch);
  statusSelect.addEventListener("change", () => fetchResults(0));

  fetchResults(0);
});
