const API_BASE = "http://localhost:8080/api";

let mapaClientes = {}; // { idCliente: nombre }

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

function formatearSoles(monto) {
  if (monto === null || monto === undefined) return "—";
  return `S/ ${Number(monto).toFixed(2)}`;
}

async function cargarClientes() {
  try {
    const res = await fetch(`${API_BASE}/clientes`);
    if (!res.ok) throw new Error("clientes");
    const clientes = await res.json();
    mapaClientes = {};
    clientes.forEach(c => { mapaClientes[c.idCliente] = c.nombre; });
  } catch (err) {
    // si falla, los nombres se muestran como "—"
  }
}

// ===== Pedidos listos para entregar =====
async function cargarListos() {
  const tbody = document.getElementById("tablaListos");
  try {
    const res = await fetch(`${API_BASE}/pedidos`);
    if (!res.ok) throw new Error("pedidos");
    const pedidos = await res.json();
    const listos = pedidos.filter(p => p.estado === "Listo");

    if (listos.length === 0) {
      tbody.innerHTML = `<tr><td colspan="4" class="table-empty">No hay pedidos listos para entregar.</td></tr>`;
      return;
    }

    tbody.innerHTML = listos.map(p => `
      <tr data-id="${p.idPedido}">
        <td>${escapeHtml(mapaClientes[p.idCliente] ?? "—")}</td>
        <td>${escapeHtml(p.descripcion)}</td>
        <td class="cell-mono">${p.cantidad}</td>
        <td>
          <div class="cell-actions">
            <button class="btn btn-sm btn-primary btn-entrega" data-id="${p.idPedido}">Registrar entrega y cobro</button>
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

// ===== Deudas pendientes =====
async function cargarDeudas() {
  const tbody = document.getElementById("tablaDeudas");
  try {
    const res = await fetch(`${API_BASE}/entregas/deudas`);
    if (!res.ok) throw new Error("deudas");
    const deudas = await res.json();

    if (deudas.length === 0) {
      tbody.innerHTML = `<tr><td colspan="4" class="table-empty">No hay deudas pendientes.</td></tr>`;
      return;
    }

    tbody.innerHTML = deudas.map(d => `
      <tr data-id="${d.id}">
        <td>${escapeHtml(d.clienteNombre ?? d.pedido?.cliente?.nombre ?? "—")}</td>
        <td>${escapeHtml(d.pedidoDescripcion ?? d.pedido?.descripcion ?? "—")}</td>
        <td class="cell-mono"><span class="badge badge-deuda">${formatearSoles(d.saldo)}</span></td>
        <td>
          <div class="cell-actions">
            <button class="btn btn-sm btn-abono" data-id="${d.id}">Registrar abono</button>
          </div>
        </td>
      </tr>
    `).join("");
  } catch (err) {
    tbody.innerHTML = `<tr><td colspan="4" class="table-empty">No se pudo cargar la lista.</td></tr>`;
  }
}

// ===== Modal entrega y cobro =====
const modalEntrega = document.getElementById("modalEntrega");
const formEntrega = document.getElementById("formEntrega");

function abrirModalEntrega(pedidoId) {
  formEntrega.reset();
  document.getElementById("entregaPedidoId").value = pedidoId;
  document.getElementById("errEntregaMonto").classList.remove("visible");
  modalEntrega.classList.add("visible");
}

function cerrarModalEntrega() {
  modalEntrega.classList.remove("visible");
}

formEntrega.addEventListener("submit", async (e) => {
  e.preventDefault();

  const monto = Number(document.getElementById("entregaMonto").value);
  if (!monto || monto < 0) {
    document.getElementById("errEntregaMonto").classList.add("visible");
    return;
  }

  const payload = {
    idPedido: Number(document.getElementById("entregaPedidoId").value),
    montoRecibido: monto,
    tipoComprobante: document.getElementById("entregaComprobante").value
};

  try {
    const res = await fetch(`${API_BASE}/entregas`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    });
    if (!res.ok) throw new Error("entrega");

    cerrarModalEntrega();
    await Promise.all([cargarListos(), cargarDeudas()]);
  } catch (err) {
    mostrarError("No se pudo registrar la entrega y el cobro.");
  }
});

// ===== Modal abono =====
const modalAbono = document.getElementById("modalAbono");
const formAbono = document.getElementById("formAbono");

function abrirModalAbono(entregaId) {
  formAbono.reset();
  document.getElementById("abonoEntregaId").value = entregaId;
  document.getElementById("errAbonoMonto").classList.remove("visible");
  modalAbono.classList.add("visible");
}

function cerrarModalAbono() {
  modalAbono.classList.remove("visible");
}

formAbono.addEventListener("submit", async (e) => {
  e.preventDefault();

  const monto = Number(document.getElementById("abonoMonto").value);
  if (!monto || monto < 0) {
    document.getElementById("errAbonoMonto").classList.add("visible");
    return;
  }

  const entregaId = document.getElementById("abonoEntregaId").value;

  try {
    const res = await fetch(`${API_BASE}/entregas/${entregaId}/abono`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ monto })
    });
    if (!res.ok) throw new Error("abono");

    cerrarModalAbono();
    cargarDeudas();
  } catch (err) {
    mostrarError("No se pudo registrar el abono.");
  }
});

// ===== Listeners generales =====
document.getElementById("btnCancelarEntrega").addEventListener("click", cerrarModalEntrega);
document.getElementById("btnCancelarAbono").addEventListener("click", cerrarModalAbono);

document.getElementById("tablaListos").addEventListener("click", (e) => {
  const btn = e.target.closest(".btn-entrega");
  if (btn) abrirModalEntrega(btn.dataset.id);
});

document.getElementById("tablaDeudas").addEventListener("click", (e) => {
  const btn = e.target.closest(".btn-abono");
  if (btn) abrirModalAbono(btn.dataset.id);
});

[modalEntrega, modalAbono].forEach(modal => {
  modal.addEventListener("click", (e) => {
    if (e.target === modal) modal.classList.remove("visible");
  });
});

document.addEventListener("DOMContentLoaded", () => {
  actualizarFecha();
  cargarListos();
  cargarDeudas();
});