const API_BASE = "http://localhost:8080/api";

let mapaClientes = {};  // { idCliente: nombre }
let mapaPedidos = {};   // { idPedido: { descripcion, idCliente } }

function actualizarFecha() {
  const ahora = new Date();
  const opciones = { weekday: "short", day: "2-digit", month: "short", year: "numeric" };
  document.getElementById("fechaHoy").textContent = ahora.toLocaleDateString("es-PE", opciones);
}

function mostrarError(mensaje) {
  const nota = document.getElementById("statusNote");
  nota.textContent = mensaje;
  nota.classList.add("visible");
}

function ocultarError() {
  document.getElementById("statusNote").classList.remove("visible");
}

function escapeHtml(str) {
  const div = document.createElement("div");
  div.textContent = str ?? "";
  return div.innerHTML;
}

function formatearFecha(fechaIso) {
  if (!fechaIso) return "—";
  const d = new Date(fechaIso);
  if (isNaN(d)) return fechaIso;
  return d.toLocaleDateString("es-PE", { day: "2-digit", month: "short", year: "numeric" });
}

function formatearSoles(monto) {
  if (monto === null || monto === undefined) return "—";
  return `S/ ${Number(monto).toFixed(2)}`;
}

// ===== Cargar clientes y pedidos para resolver nombres/descripciones por id =====
async function cargarMapas() {
  try {
    const [resClientes, resPedidos] = await Promise.all([
      fetch(`${API_BASE}/clientes`),
      fetch(`${API_BASE}/pedidos`)
    ]);
    if (!resClientes.ok || !resPedidos.ok) throw new Error("mapas");

    const clientes = await resClientes.json();
    const pedidos = await resPedidos.json();

    mapaClientes = {};
    clientes.forEach(c => { mapaClientes[c.idCliente] = c.nombre; });

    mapaPedidos = {};
    pedidos.forEach(p => { mapaPedidos[p.idPedido] = { descripcion: p.descripcion, idCliente: p.idCliente }; });
  } catch (err) {
    // Si esto falla, las tablas igual se intentan pintar pero sin nombre/descripcion resuelta.
  }
}

function nombreClientePorPedido(idPedido) {
  const pedido = mapaPedidos[idPedido];
  if (!pedido) return "—";
  return mapaClientes[pedido.idCliente] ?? "—";
}

function descripcionPedido(idPedido) {
  return mapaPedidos[idPedido]?.descripcion ?? "—";
}

// ===== Pedidos sin cotizar =====
async function cargarPorCotizar() {
  const tbody = document.getElementById("tablaPorCotizar");
  try {
    const res = await fetch(`${API_BASE}/pedidos/sin-cotizar`);
    if (!res.ok) throw new Error("sin-cotizar");
    const pedidos = await res.json();

    if (pedidos.length === 0) {
      tbody.innerHTML = `<tr><td colspan="4" class="table-empty">No hay pedidos pendientes de cotizar.</td></tr>`;
      return;
    }

    tbody.innerHTML = pedidos.map(p => `
      <tr data-id="${p.idPedido}">
        <td>${escapeHtml(mapaClientes[p.idCliente] ?? "—")}</td>
        <td>${escapeHtml(p.descripcion)}</td>
        <td class="cell-mono">${p.cantidad}</td>
        <td>
          <div class="cell-actions">
            <button class="btn btn-sm btn-primary btn-generar" data-id="${p.idPedido}">Generar cotización</button>
          </div>
        </td>
      </tr>
    `).join("");

    ocultarError();
  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="4" class="table-empty">No se pudo cargar la lista.</td></tr>`;
    mostrarError("No se pudo conectar con el backend (¿está corriendo en el puerto 8080?)");
  }
}

// ===== Cotizaciones generadas =====
async function cargarCotizaciones() {
  const tbody = document.getElementById("tablaCotizaciones");
  try {
    const res = await fetch(`${API_BASE}/cotizaciones`);
    if (!res.ok) throw new Error("cotizaciones");
    const cotizaciones = await res.json();

    if (cotizaciones.length === 0) {
      tbody.innerHTML = `<tr><td colspan="4" class="table-empty">Aún no hay cotizaciones generadas.</td></tr>`;
      return;
    }

    tbody.innerHTML = cotizaciones.map(c => `
      <tr>
        <td>${escapeHtml(nombreClientePorPedido(c.idPedido))}</td>
        <td>${escapeHtml(descripcionPedido(c.idPedido))}</td>
        <td class="cell-mono">${formatearSoles(c.precioTotal)}</td>
        <td class="cell-mono">${formatearFecha(c.fecha)}</td>
      </tr>
    `).join("");
  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="4" class="table-empty">No se pudo cargar la lista.</td></tr>`;
  }
}

// ===== Generar cotización =====
document.getElementById("tablaPorCotizar").addEventListener("click", async (e) => {
  const btn = e.target.closest(".btn-generar");
  if (!btn) return;

  const pedidoId = btn.dataset.id;
  btn.disabled = true;
  btn.textContent = "Generando…";

  try {
    const res = await fetch(`${API_BASE}/cotizaciones/generar/${pedidoId}`, { method: "POST" });
    if (!res.ok) throw new Error("generar");

    await Promise.all([cargarPorCotizar(), cargarCotizaciones()]);
  } catch (err) {
    mostrarError("No se pudo generar la cotización.");
    btn.disabled = false;
    btn.textContent = "Generar cotización";
  }
});

document.addEventListener("DOMContentLoaded", async () => {
  actualizarFecha();
  await cargarMapas();
  cargarPorCotizar();
  cargarCotizaciones();
});