/* ==========================
   CONSTANTS & HELPERS
========================== */
const $ = (s) => document.querySelector(s);
const $$ = (s) => Array.from(document.querySelectorAll(s));
const clamp = (n, min, max) => Math.max(min, Math.min(max, n));

function setVisible(el, yes, className = "is-visible") {
  if (!el) return;
  el.classList.toggle(className, !!yes);
}

function setRowDisplay(el, yes) {
  if (!el) return;
  el.style.display = yes ? "flex" : "none";
}

function setDividerDisplay(el, yes) {
  if (!el) return;
  el.style.display = yes ? "block" : "none";
}

function clearChildren(el) {
  if (!el) return;
  while (el.firstChild) el.removeChild(el.firstChild);
}

/* ==========================
   API
========================== */
const API_BASE = "http://localhost:8080/api/catalog";
const API_ORIGIN = new URL(API_BASE).origin;

function toFileUrl(u) {
  const s = String(u || "").trim();
  if (!s) return "";
  if (/^https?:\/\//i.test(s)) return s;
  if (s.startsWith("/")) return `${API_ORIGIN}${s}`;
  return `${API_ORIGIN}/${s}`;
}

function downloadByUrl(url, filename) {
  const href = toFileUrl(url);
  const a = document.createElement("a");
  a.href = href;
  a.target = "_blank";
  a.rel = "noopener";
  if (filename) a.download = filename;
  document.body.appendChild(a);
  a.click();
  a.remove();
}


async function apiGetJson(path) {
  const res = await fetch(`${API_BASE}${path}`);
  if (!res.ok) throw new Error(`HTTP ${res.status} ${path}`);
  return res.json();
}

/* ==========================
   STATE
========================== */
const state = {
  kitId: null,
  kit: null,
  currentVariantId: null,
  currentViewId: null,
  selectedGlassId: null,
};

let allKits = [];

function getKit() {
  return state.kit;
}

function getCurrentVariant() {
  const kit = getKit();
  if (!kit || !state.currentVariantId) return null;
  return kit.variants?.find((v) => v.id === state.currentVariantId) || null;
}

function getCurrentView() {
  const variant = getCurrentVariant();
  if (!variant || !state.currentViewId) return null;
  return variant.views?.find((v) => v.id === state.currentViewId) || null;
}

function getGlassById(glassId) {
  const kit = getKit();
  return kit?.glasses?.find((g) => g.id === glassId) || null;
}

/* ==========================
   RENDERING
========================== */
function renderVariantSelector() {
  const kit = getKit();
  const filterType = $("#filterType");
  if (!filterType || !kit) return;

  clearChildren(filterType);

  // Opção "Todas"
  const defaultOpt = document.createElement("option");
  defaultOpt.value = "";
  defaultOpt.textContent = "Todas";
  filterType.appendChild(defaultOpt);

  // Variantes dinâmicas (Padrão, Ambulância, Escolar, etc.)
  (kit.variants || []).forEach((v) => {
    const opt = document.createElement("option");
    opt.value = v.id;
    opt.textContent = v.name;
    filterType.appendChild(opt);
  });

  // Seleciona a primeira variante automaticamente
  if (kit.variants && kit.variants.length > 0) {
    state.currentVariantId = kit.variants[0].id;
    filterType.value = state.currentVariantId;
    selectVariant(state.currentVariantId);
  } else {
    // Se não tem variantes, limpa tudo
    state.currentVariantId = null;
    state.currentViewId = null;
    renderCurrentView();
  }
}

function selectVariant(variantId) {
  state.currentVariantId = variantId;

  const variant = getCurrentVariant();
  if (!variant) {
    state.currentViewId = null;
    renderViewSelector();
    renderCurrentView();
    return;
  }

  state.currentViewId = null; // começa em Apresentação
  renderViewSelector();       // popula: Apresentação + views do admin
  renderCurrentView();
}


function renderViewSelector() {
  const kit = getKit();
  const variant = getCurrentVariant();
  const sel = document.getElementById("filterView");
  if (!sel) return;

  sel.innerHTML = "";

  // opção "Apresentação"
  const opt0 = document.createElement("option");
  opt0.value = "";
  opt0.textContent = "Apresentação";
  sel.appendChild(opt0);

  // views da variante
  (variant?.views || []).forEach(vw => {
    const opt = document.createElement("option");
    opt.value = vw.id;
    opt.textContent = vw.name;
    sel.appendChild(opt);
  });

  // default: apresentação
  sel.value = state.currentViewId || "";
}



function renderCurrentView() {
  const kit = getKit();
  if (!kit) return;

  const variant = getCurrentVariant();
  const view = getCurrentView();

  const vehicleImg = $("#vehicleImg");
  const vehicleLabel = $("#vehicleLabel");

  if (vehicleLabel) {
    vehicleLabel.textContent = kit.vehicleLabel || "Selecione um modelo";
  }

  // Se não tem view selecionada => modo Apresentação
  const isPresentation = !state.currentViewId;

  // Prioridade:
  // apresentação: variant.presentationImage > kit.defaultImage
  // view 2D: view.image > variant.presentationImage > kit.defaultImage
  const imageSrc = isPresentation
    ? (variant?.presentationImage || kit.defaultImage || "")
    : (view?.image || variant?.presentationImage || kit.defaultImage || "");

  if (vehicleImg) {
    vehicleImg.src = imageSrc;
    vehicleImg.alt = kit.vehicleLabel
      ? `${kit.vehicleLabel} - ${state.currentVariantId || ""}`
      : "";
  }

  hideTooltip();
  renderHotspots(); // seu renderHotspots já sai fora se não tiver view [file:22]
}


function syncHotspotsToImage() {
  const imageWrap = $("#imageWrap");
  const vehicleImg = $("#vehicleImg");
  const hotspotsEl = $("#hotspots");
  if (!imageWrap || !vehicleImg || !hotspotsEl) return;

  const wrapRect = imageWrap.getBoundingClientRect();
  const imgRect = vehicleImg.getBoundingClientRect();

  const left = imgRect.left - wrapRect.left;
  const top = imgRect.top - wrapRect.top;

  hotspotsEl.style.inset = "auto";
  hotspotsEl.style.left = `${left}px`;
  hotspotsEl.style.top = `${top}px`;
  hotspotsEl.style.width = `${imgRect.width}px`;
  hotspotsEl.style.height = `${imgRect.height}px`;
}

function pxToPercent(pxBox) {
  const vehicleImg = $("#vehicleImg");
  const nw = vehicleImg?.naturalWidth || 1080;
  const nh = vehicleImg?.naturalHeight || 1080;

  return {
    l: (pxBox.x / nw) * 100,
    t: (pxBox.y / nh) * 100,
    w: (pxBox.w / nw) * 100,
    h: (pxBox.h / nh) * 100,
  };
}

function renderHotspots() {
  const hotspotsEl = $("#hotspots");
  clearChildren(hotspotsEl);

  const view = getCurrentView();
  const kit = getKit();
  if (!view || !kit) return;

  const map = view.coordsPxByGlassId || {};

  Object.entries(map).forEach(([glassId, pxBox]) => {
    const glass = getGlassById(glassId);
    if (!glass) return;

    const p = pxToPercent(pxBox);

    const btn = document.createElement("button");
    btn.type = "button";
    btn.className = "hotspot";
    btn.dataset.glassId = glassId;
    btn.setAttribute("aria-label", glass.model);

    btn.style.left = `${p.l}%`;
    btn.style.top = `${p.t}%`;
    btn.style.width = `${p.w}%`;
    btn.style.height = `${p.h}%`;

    btn.addEventListener("mouseenter", (ev) =>
      showTooltip(glass.model, ev.clientX, ev.clientY)
    );
    btn.addEventListener("mousemove", (ev) =>
      showTooltip(glass.model, ev.clientX, ev.clientY)
    );
    btn.addEventListener("mouseleave", hideTooltip);

    btn.addEventListener("click", (ev) => {
      ev.stopPropagation();
      state.selectedGlassId = glassId;
      renderPanel();
      $$(".hotspot").forEach((h) =>
        h.classList.toggle("is-active", h.dataset.glassId === glassId)
      );
    });

    hotspotsEl.appendChild(btn);
  });
}

/* ==========================
   TOOLTIP
========================== */
function showTooltip(text, clientX, clientY) {
  const tooltip = $("#tooltip");
  const tooltipTitle = $("#tooltipTitle");
  const imageWrap = $("#imageWrap");
  if (!tooltip || !tooltipTitle || !imageWrap) return;

  tooltipTitle.textContent = text || "Modelo do vidro";

  const rect = imageWrap.getBoundingClientRect();
  const x = clamp(clientX - rect.left + 12, 10, rect.width - 260);
  const y = clamp(clientY - rect.top + 12, 10, rect.height - 70);

  tooltip.style.transform = `translate(${x}px, ${y}px)`;
  setVisible(tooltip, true);
}

function hideTooltip() {
  const tooltip = $("#tooltip");
  if (!tooltip) return;
  setVisible(tooltip, false);
  tooltip.style.transform = "translate(-9999px,-9999px)";
}

/* ==========================
   PANEL
========================== */
function renderPanel() {
  const kit = getKit();
  const infoTitle = $("#infoTitle");
  const placeholder = $("#placeholder");

  if (!kit) {
    // Limpa lado esquerdo quando não há kit
    const vehicleLabel = $("#vehicleLabel");
    const vehicleImg = $("#vehicleImg");
    const hotspotsEl = $("#hotspots");

    if (vehicleLabel) vehicleLabel.textContent = "Selecione um modelo";
    if (vehicleImg) {
      vehicleImg.src = "";
      vehicleImg.alt = "";
    }
    if (hotspotsEl) clearChildren(hotspotsEl);

    if (infoTitle) infoTitle.textContent = "Selecione um kit";
    setVisible(placeholder, true, "is-visible");

    // Esconde linhas de detalhes
    setRowDisplay($("#rowKit"), false);
    setRowDisplay($("#rowCode"), false);
    setRowDisplay($("#rowDesc"), false);
    setRowDisplay($("#rowPos"), false);
    setRowDisplay($("#rowOptions"), false);
    setDividerDisplay($("#divA"), false);
    setDividerDisplay($("#divB"), false);
    clearChildren($("#options"));
    updateFilesButtonsVisibility(null);
    return;
  }

  const glass = state.selectedGlassId
    ? getGlassById(state.selectedGlassId)
    : null;

  const kitPill = $("#kitPill");
  const kitCodeEl = $("#kitCode");
  const kitDescEl = $("#kitDesc");

  if (kitPill) kitPill.textContent = `Kit ${kit.kit?.code || kit.id}`;
  if (kitCodeEl) kitCodeEl.textContent = kit.kit?.code || kit.id;
  if (kitDescEl) kitDescEl.textContent = kit.kit?.desc || "";

  setRowDisplay($("#rowKit"), true);
  setDividerDisplay($("#divA"), true);

  if (!glass) {
    if (infoTitle) infoTitle.textContent = "Nenhum vidro selecionado";
    setVisible(placeholder, true, "is-visible");
    setRowDisplay($("#rowCode"), false);
    setRowDisplay($("#rowDesc"), false);
    setRowDisplay($("#rowPos"), false);
    setDividerDisplay($("#divB"), false);
    setRowDisplay($("#rowOptions"), false);
    clearChildren($("#options"));
    updateFilesButtonsVisibility(null);
    return;
  }

  if (infoTitle) infoTitle.textContent = glass.model;
  setVisible(placeholder, false, "is-visible");

  setRowDisplay($("#rowCode"), true);
  setRowDisplay($("#rowDesc"), true);
  setRowDisplay($("#rowPos"), true);
  setDividerDisplay($("#divB"), true);
  setRowDisplay($("#rowOptions"), true);

  const codeEl = $("#code");
  const descEl = $("#desc");
  const positionEl = $("#position");
  const sideEl = $("#side");
  const dimEl = $("#dim");

  if (codeEl) codeEl.textContent = glass.code || "";
  if (descEl) descEl.textContent = glass.desc || "";
  if (positionEl) positionEl.textContent = glass.position || "";
  if (sideEl) sideEl.textContent = glass.side || "";
  if (dimEl) dimEl.textContent = glass.dim || "";

  const optionsEl = $("#options");
  clearChildren(optionsEl);
  (glass.options || []).forEach((t) => {
    const span = document.createElement("span");
    span.className = "tag";
    span.textContent = t;
    optionsEl.appendChild(span);
  });

  updateFilesButtonsVisibility(glass);
}

/* ==========================
   FILTERS
========================== */
function getFilteredKits() {
  const vehicleSel = $("#filterVehicle")?.value || "";
  return allKits.filter((k) => {
    if (vehicleSel && k.vehicleLabel !== vehicleSel) return false;
    return true;
  });
}

function renderSelectFromFilters() {
  const kitSelect = $("#kitSelect");
  if (!kitSelect) return;

  const currentId = state.kitId;
  const filtered = getFilteredKits();

  kitSelect.innerHTML = "";

  filtered.forEach((k) => {
    const opt = document.createElement("option");
    opt.value = k.id;
    opt.textContent = `${k.vehicleLabel} — ${k.kit?.code || k.id} — ${k.kit?.desc || ""}`;
    kitSelect.appendChild(opt);
  });

  let targetId =
    currentId && filtered.some((k) => k.id === currentId) ? currentId : null;

  if (targetId) {
    kitSelect.value = targetId;
    kitSelect.dispatchEvent(new Event("change", { bubbles: true }));
  } else {
    state.kitId = null;
    state.kit = null;
    renderPanel();

    // Reseta select de configuração para "Todas"
    const filterType = $("#filterType");
    if (filterType) {
      filterType.innerHTML = "";
      const opt = document.createElement("option");
      opt.value = "";
      opt.textContent = "Todas";
      filterType.appendChild(opt);
    }
  }
}

function populateVehicleFilter() {
  const sel = $("#filterVehicle");
  if (!sel) return;

  const values = Array.from(new Set(allKits.map((k) => k.vehicleLabel))).sort();

  sel.innerHTML = `<option value="">Todos</option>`;
  values.forEach((v) => {
    const opt = document.createElement("option");
    opt.value = v;
    opt.textContent = v;
    sel.appendChild(opt);
  });
}

async function selectKit(id) {
  state.kitId = id;

  const raw = await apiGetJson(`/kits/${encodeURIComponent(id)}`);
  state.kit = raw;

  state.selectedGlassId = null;

  // define variante padrão
  const firstVariantId = state.kit?.variants?.[0]?.id || null;
  state.currentVariantId = firstVariantId;

  // começa em apresentação
  state.currentViewId = null;

  renderVariantSelector();
  renderViewSelector();   // novo
  renderPanel();
  renderCurrentView();

  const vehicleImg = $("#vehicleImg");
  if (vehicleImg && vehicleImg.complete) {
    syncHotspotsToImage();
    renderHotspots();
  }
}


/* ==========================
   EVENTS
========================== */
function bindEvents() {
  const kitSelect = $("#kitSelect");
  const filterVehicle = $("#filterVehicle");
  const filterType = $("#filterType");
  const filterView = $("#filterView");
  const imageWrap = $("#imageWrap");
  const vehicleImg = $("#vehicleImg");

  kitSelect?.addEventListener("change", async () => {
    const id = kitSelect.value;
    if (!id) return;
    await selectKit(id);
  });

  filterVehicle?.addEventListener("change", () => {
    renderSelectFromFilters();
  });

  filterType?.addEventListener("change", (e) => {
    const variantId = e.target.value;
    if (variantId) {
      selectVariant(variantId);
      renderViewSelector();
    }
  });

  filterView?.addEventListener("change", (e) => {
    const id = e.target.value;
    state.currentViewId = id || null;
    state.selectedGlassId = null;
    renderPanel();
    renderCurrentView();
  });

  imageWrap?.addEventListener("click", () => {
    state.selectedGlassId = null;
    renderPanel();
    $$(".hotspot").forEach((h) => h.classList.remove("is-active"));
  });

  window.addEventListener("resize", syncHotspotsToImage);

  vehicleImg?.addEventListener("load", () => {
    syncHotspotsToImage();
    renderHotspots();
  });

  // Downloads (NOVO)
  document.getElementById("btnDownloadPdf")?.addEventListener("click", (e) => {
    e.preventDefault();
    baixarPDF();
  });

  document.getElementById("btnDownloadXt")?.addEventListener("click", (e) => {
    e.preventDefault();
    baixarXT();
  });

  document.getElementById("btnDownloadXb")?.addEventListener("click", (e) => {
    e.preventDefault();
    baixarXB();
  });
}


/* ==========================
   AUTH & PDF
========================== */
let usuarioLogado = null;

function verificarAutenticacao() {
  let user = null;
  try {
    user = JSON.parse(localStorage.getItem("user"));
  } catch (_) {
    user = null;
  }

  const btnEntrar = $("#btnEntrar");
  const btnCadastrar = $("#btnCadastrar");
  const userMenu = $("#userMenu");
  const userName = $("#userName");
  const btnPdf = $("#btnDownloadPdf");

  if (user && user.status === "APPROVED") {
    usuarioLogado = user;
    if (btnEntrar) btnEntrar.style.display = "none";
    if (btnCadastrar) btnCadastrar.style.display = "none";
    if (userMenu) userMenu.style.display = "flex";
    if (userName) userName.textContent = user.nome || "";

    const glass = state.selectedGlassId
      ? getGlassById(state.selectedGlassId)
      : null;
    if (btnPdf) btnPdf.style.display = glass ? "block" : "none";
  } else {
    usuarioLogado = null;
    if (btnEntrar) btnEntrar.style.display = "inline-block";
    if (btnCadastrar) btnCadastrar.style.display = "inline-block";
    if (userMenu) userMenu.style.display = "none";
    if (btnPdf) btnPdf.style.display = "none";
  }
}

function updateFilesButtonsVisibility(glass) {
  const box = document.getElementById("filesBox");
  const btnPdf = document.getElementById("btnDownloadPdf");
  const btnXt = document.getElementById("btnDownloadXt");
  const btnXb = document.getElementById("btnDownloadXb");

  const canShow = !!usuarioLogado && !!glass;

  const hasPdf = !!glass?.pdfUrl;
  const hasXt = !!glass?.parasolidXtUrl;
  const hasXb = !!glass?.parasolidXbUrl;

  if (btnPdf) btnPdf.style.display = (canShow && hasPdf) ? "block" : "none";
  if (btnXt) btnXt.style.display = (canShow && hasXt) ? "block" : "none";
  if (btnXb) btnXb.style.display = (canShow && hasXb) ? "block" : "none";

  if (box) box.style.display = (canShow && (hasPdf || hasXt || hasXb)) ? "block" : "none";
}


function logout() {
  if (confirm("Deseja realmente sair?")) {
    localStorage.removeItem("user");
    window.location.reload();
  }
}

function irParaLogin() {
  window.location.href = "login.html";
}

function irParaCadastro() {
  window.location.href = "cadastro.html";
}

function getSelectedGlass() {
  return state.selectedGlassId ? getGlassById(state.selectedGlassId) : null;
}

async function baixarPDF() {
  if (!usuarioLogado) { alert("Você precisa estar logado"); window.location.href = "login.html"; return; }

  const glass = getSelectedGlass();
  if (!glass?.pdfUrl) { alert("Este vidro não tem PDF cadastrado."); return; }

  downloadByUrl(glass.pdfUrl, `${state.kitId || "kit"}-${glass.id || "glass"}.pdf`);
}

async function baixarXT() {
  if (!usuarioLogado) { alert("Você precisa estar logado"); window.location.href = "login.html"; return; }

  const glass = getSelectedGlass();
  if (!glass?.parasolidXtUrl) { alert("Este vidro não tem Parasolid (.x_t) cadastrado."); return; }

  downloadByUrl(glass.parasolidXtUrl, `${state.kitId || "kit"}-${glass.id || "glass"}.x_t`);
}

async function baixarXB() {
  if (!usuarioLogado) { alert("Você precisa estar logado"); window.location.href = "login.html"; return; }

  const glass = getSelectedGlass();
  if (!glass?.parasolidXbUrl) { alert("Este vidro não tem Parasolid (.x_b) cadastrado."); return; }

  downloadByUrl(glass.parasolidXbUrl, `${state.kitId || "kit"}-${glass.id || "glass"}.x_b`);
}


/* ==========================
   BURGER MENU
========================== */
(function () {
  const btn = document.querySelector("[data-burger]");
  const backdrop = document.querySelector("[data-backdrop]");
  const nav = document.querySelector("[data-nav]");
  if (!btn || !backdrop || !nav) return;

  const setOpen = (open) => {
    document.body.classList.toggle("vf-navOpen", open);
    btn.setAttribute("aria-expanded", String(open));
    backdrop.hidden = !open;
    btn.classList.toggle("is-open", open);
  };

  const close = () => setOpen(false);
  const toggle = () =>
    setOpen(!document.body.classList.contains("vf-navOpen"));

  btn.addEventListener("click", toggle);
  backdrop.addEventListener("click", close);

  nav.addEventListener("click", (e) => {
    const link = e.target.closest("a");
    if (link) close();
  });

  document.addEventListener("keydown", (e) => {
    if (e.key === "Escape") close();
  });

  const style = document.createElement("style");
  style.textContent = `
   .vf-burger.is-open .vf-burger__lines{ background: transparent; }
   .vf-burger.is-open .vf-burger__lines::before{ top: 0; transform: rotate(45deg); }
   .vf-burger.is-open .vf-burger__lines::after{ top: 0; transform: rotate(-45deg); }
 `;
  document.head.appendChild(style);
})();

/* ==========================
   CUSTOM SELECT
========================== */
/* ==========================
   CUSTOM SELECT
========================== */
(function () {
  const customSelect = document.querySelector("[data-custom-select]");
  const nativeSelect = document.querySelector("#kitSelect");
  if (!customSelect || !nativeSelect) return;

  const trigger = customSelect.querySelector("[data-trigger]");
  const searchInput = customSelect.querySelector("[data-search]");
  const optionsList = customSelect.querySelector("[data-options]");
  const makerSelect = customSelect.querySelector("[data-maker]");
  const valueDisplay = trigger?.querySelector(".vf-select-value");
  if (!trigger || !searchInput || !optionsList || !valueDisplay) return;

  let allOptions = [];

  function getMakerFromLabel(label) {
    // Seu option começa com: "Renault Master ... — código — desc"
    // Então a montadora normalmente é a 1ª palavra.
    const t = (label || "").trim();
    if (!t) return "";
    return t.split(/\s+/)[0].trim();
  }

  function populateMakers() {
    if (!makerSelect) return;

    const current = makerSelect.value || "";
    const makers = Array.from(
      new Set(allOptions.map((o) => o.maker).filter(Boolean))
    ).sort((a, b) => a.localeCompare(b));

    makerSelect.innerHTML =
      `<option value="">Todas</option>` +
      makers.map((m) => `<option value="${m}">${m}</option>`).join("");

    makerSelect.value = makers.includes(current) ? current : "";
  }

  function populateOptions() {
    optionsList.innerHTML = "";
    allOptions = [];

    Array.from(nativeSelect.options).forEach((option) => {
      if (!option.value) return;

      const label = option.textContent || "";
      const maker = getMakerFromLabel(label);

      const li = document.createElement("li");
      li.className = "vf-option";
      li.textContent = label;
      li.dataset.value = option.value;

      li.addEventListener("click", () => selectOption(option.value, label));

      optionsList.appendChild(li);
      allOptions.push({
        element: li,
        text: label.toLowerCase(),
        maker,
      });
    });

    populateMakers();
    filterOptions(searchInput.value); // reaplica filtro quando repopula
  }

  function syncFromNativeSelect() {
    const opt = nativeSelect.selectedOptions?.[0];
    if (!opt) return;

    valueDisplay.textContent = opt.textContent || "";
    valueDisplay.classList.remove("placeholder");

    optionsList
      .querySelectorAll(".vf-option")
      .forEach((el) => el.classList.remove("selected"));

    const escaped =
      window.CSS && CSS.escape ? CSS.escape(opt.value) : opt.value;

    const selectedEl = optionsList.querySelector(`[data-value="${escaped}"]`);
    if (selectedEl) selectedEl.classList.add("selected");
  }

  nativeSelect.addEventListener("change", syncFromNativeSelect);
  syncFromNativeSelect();

  function filterOptions(query) {
    const lowerQuery = (query || "").toLowerCase().trim();
    const maker = makerSelect ? (makerSelect.value || "") : "";
    let hasResults = false;

    allOptions.forEach(({ element, text, maker: optMaker }) => {
      const okText = !lowerQuery || text.includes(lowerQuery);
      const okMaker = !maker || optMaker === maker;
      const ok = okText && okMaker;

      element.classList.toggle("hidden", !ok);
      if (ok) hasResults = true;
    });

    let noResultsMsg = optionsList.querySelector(".vf-no-results");
    if (!hasResults && (lowerQuery || maker)) {
      if (!noResultsMsg) {
        noResultsMsg = document.createElement("li");
        noResultsMsg.className = "vf-no-results";
        noResultsMsg.textContent = "Nenhum resultado encontrado";
        optionsList.appendChild(noResultsMsg);
      }
    } else if (noResultsMsg) {
      noResultsMsg.remove();
    }
  }

  function handleClickOutside(e) {
    if (!customSelect.contains(e.target)) {
      customSelect.classList.remove("open");
      searchInput.value = "";
      filterOptions("");
      document.removeEventListener("click", handleClickOutside);
    }
  }

  function toggleDropdown() {
    customSelect.classList.toggle("open");

    if (customSelect.classList.contains("open")) {
      searchInput.focus();
      document.addEventListener("click", handleClickOutside);
    } else {
      document.removeEventListener("click", handleClickOutside);
      searchInput.value = "";
      filterOptions("");
    }
  }

  function selectOption(value, text) {
    nativeSelect.value = value;
    valueDisplay.textContent = text;
    valueDisplay.classList.remove("placeholder");

    optionsList
      .querySelectorAll(".vf-option")
      .forEach((opt) => opt.classList.remove("selected"));

    const selectedEl = optionsList.querySelector(`[data-value="${value}"]`);
    if (selectedEl) selectedEl.classList.add("selected");

    customSelect.classList.remove("open");
    searchInput.value = "";
    filterOptions("");

    nativeSelect.dispatchEvent(new Event("change", { bubbles: true }));
  }

  trigger.addEventListener("click", (e) => {
    e.stopPropagation();
    toggleDropdown();
  });

  searchInput.addEventListener("input", (e) => filterOptions(e.target.value));
  searchInput.addEventListener("click", (e) => e.stopPropagation());

  makerSelect?.addEventListener("change", () => {
    filterOptions(searchInput.value);
  });

  searchInput.addEventListener("keydown", (e) => {
    if (e.key === "Escape") {
      customSelect.classList.remove("open");
      searchInput.value = "";
      filterOptions("");
    }
  });

  const observer = new MutationObserver(() => {
    if (nativeSelect.options.length > 0) populateOptions();
  });
  observer.observe(nativeSelect, { childList: true });

  if (nativeSelect.options.length > 0) populateOptions();
})();


/* ==========================
   BOOT
========================== */
(function () {
  bindEvents();
  verificarAutenticacao();

  window.addEventListener("storage", (e) => {
    if (e.key === "user") {
      verificarAutenticacao();
      renderPanel();
    }
  });

  async function bootFromApi() {
    const list = await apiGetJson("/kits");
    allKits = list;

    populateVehicleFilter();
    renderSelectFromFilters();
  }

  bootFromApi().catch((err) => {
    console.error(err);
    renderPanel();
  });
})();
